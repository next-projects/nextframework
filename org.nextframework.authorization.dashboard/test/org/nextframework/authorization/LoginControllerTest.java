package org.nextframework.authorization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;
import org.nextframework.authorization.impl.AbstractAuthorizationDAO;
import org.nextframework.core.web.WebRequestContext;
import org.nextframework.service.ServiceFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class LoginControllerTest {

	private static final PasswordEncoder TEST_PASSWORD_ENCODER = new PasswordEncoder() {

		@Override
		public String encode(CharSequence rawPassword) {
			return rawPassword.toString();
		}

		@Override
		public boolean matches(CharSequence rawPassword, String encodedPassword) {
			return encodedPassword != null && encodedPassword.contentEquals(rawPassword);
		}
	};

	@Test
	public void successfulLoginRotatesSessionBeforeStoringUser() throws Exception {
		List<String> events = new ArrayList<>();
		Map<String, Object> sessionAttributes = new HashMap<>();
		WebRequestContext request = requestContext(events, sessionAttributes);
		LoginController.UserForm persistedUser = user("user", "secret");
		TestLoginController controller = controller(request, persistedUser);

		String result = controller.doLogin(user("user", "secret"));

		assertEquals("redirect:/", result);
		assertEquals(List.of("getSession", "changeSessionId", "setUserAttribute"), events);
		assertSame(persistedUser, sessionAttributes.get("USER"));
	}

	@Test
	public void nullStoredPasswordIsRejectedWithoutRotatingSession() throws Exception {
		List<String> events = new ArrayList<>();
		Map<String, Object> sessionAttributes = new HashMap<>();
		TestLoginController controller = controller(requestContext(events, sessionAttributes), user("user", null));

		assertEquals("login", controller.doLogin(user("user", "secret")));

		assertFalse(events.contains("changeSessionId"));
		assertFalse(sessionAttributes.containsKey("USER"));
	}

	@Test
	public void missingEncoderAndNullCredentialsFailClosed() {
		TestLoginController controller = new TestLoginController(null, null);

		assertFalse(controller.verify("secret", "secret"));
		assertFalse(controller.verify(null, "secret"));
		assertFalse(controller.verify("encoded", null));
	}

	@Test
	public void shippedEncoderUsesSaltedAdaptiveHashes() {
		PasswordEncoder encoder = new Pbkdf2PasswordEncoder();

		String first = encoder.encode("secret");
		String second = encoder.encode("secret");

		assertNotEquals(first, second);
		assertTrue(encoder.matches("secret", first));
		assertFalse(encoder.matches("wrong", first));
		assertFalse(encoder.matches("secret", "secret"));
		assertFalse(encoder.matches("secret", "pbkdf2-sha256$999999999$invalid$invalid"));
	}

	@Test
	public void shippedEncoderIsAvailableAsAService() {
		assertTrue(ServiceFactory.getService(PasswordEncoder.class) instanceof Pbkdf2PasswordEncoder);
	}

	@Test
	public void packedWarUsesPackagedLoginPageWithoutResolvingARealPath() throws Exception {
		URL packagedPage = new URL("jar:file:/application/WEB-INF/lib/dashboard.jar!/META-INF/resources" + LoginController.DEFAULT_LOGIN_PAGE);
		ServletContext servletContext = proxy(ServletContext.class, (proxy, method, args) -> {
			if (method.getName().equals("getRealPath")) {
				throw new AssertionError("The login page must not depend on an exploded WAR");
			}
			if (method.getName().equals("getResource") && LoginController.DEFAULT_LOGIN_PAGE.equals(args[0])) {
				return packagedPage;
			}
			return defaultValue(method.getReturnType());
		});

		assertEquals("web-resource:" + LoginController.DEFAULT_LOGIN_PAGE,
				new TestLoginController(null, null).resolveLoginView(servletContext, "app"));
	}

	@Test
	public void applicationLoginPageOverridesPackagedDefault() throws Exception {
		URL applicationPage = new URL("file:/application/WEB-INF/jsp/app/login.jsp");
		ServletContext servletContext = proxy(ServletContext.class, (proxy, method, args) -> {
			if (method.getName().equals("getResource") && "/WEB-INF/jsp/app/login.jsp".equals(args[0])) {
				return applicationPage;
			}
			return defaultValue(method.getReturnType());
		});

		assertEquals("login", new TestLoginController(null, null).resolveLoginView(servletContext, "app"));
	}

	@Test
	public void explodedDeploymentUsesTheLoginPageFromWebInfClasses() throws Exception {
		URL explodedPage = new URL("file:/application" + LoginController.EXPLODED_DEFAULT_LOGIN_PAGE);
		ServletContext servletContext = proxy(ServletContext.class, (proxy, method, args) -> {
			if (method.getName().equals("getRealPath")) {
				throw new AssertionError("The login page must not depend on a writable deployment");
			}
			if (method.getName().equals("getResource") && LoginController.EXPLODED_DEFAULT_LOGIN_PAGE.equals(args[0])) {
				return explodedPage;
			}
			return defaultValue(method.getReturnType());
		});

		assertEquals("web-resource:" + LoginController.EXPLODED_DEFAULT_LOGIN_PAGE,
				new TestLoginController(null, null).resolveLoginView(servletContext, "app"));
	}

	@Test
	public void missingPackagedLoginPageIsRetriedOnTheNextRequest() throws Exception {
		URL packagedPage = new URL("jar:file:/application/WEB-INF/lib/dashboard.jar!/META-INF/resources" + LoginController.DEFAULT_LOGIN_PAGE);
		AtomicInteger defaultPageLookups = new AtomicInteger();
		ServletContext servletContext = proxy(ServletContext.class, (proxy, method, args) -> {
			if (method.getName().equals("getResource") && LoginController.DEFAULT_LOGIN_PAGE.equals(args[0])) {
				return defaultPageLookups.incrementAndGet() == 1 ? null : packagedPage;
			}
			return defaultValue(method.getReturnType());
		});
		TestLoginController controller = new TestLoginController(null, null);

		try {
			controller.resolveLoginView(servletContext, "app");
			throw new AssertionError("A missing packaged login page must fail explicitly");
		} catch (IllegalStateException expected) {
			assertTrue(expected.getMessage().contains(LoginController.DEFAULT_LOGIN_PAGE));
		}
		assertEquals("web-resource:" + LoginController.DEFAULT_LOGIN_PAGE,
				controller.resolveLoginView(servletContext, "app"));
		assertEquals(2, defaultPageLookups.get());
	}

	private TestLoginController controller(WebRequestContext request, User persistedUser) throws Exception {
		TestLoginController controller = new TestLoginController(request, TEST_PASSWORD_ENCODER);
		AuthorizationDAO dao = new AbstractAuthorizationDAO() {
			@Override
			public User findUserByUsername(String username) {
				return persistedUser;
			}
		};
		Field authorizationDAO = LoginController.class.getDeclaredField("authorizationDAO");
		authorizationDAO.setAccessible(true);
		authorizationDAO.set(controller, dao);
		return controller;
	}

	private WebRequestContext requestContext(List<String> events, Map<String, Object> sessionAttributes) {
		HttpSession session = proxy(HttpSession.class, (proxy, method, args) -> defaultValue(method.getReturnType()));
		HttpServletRequest servletRequest = proxy(HttpServletRequest.class, (proxy, method, args) -> {
			if (method.getName().equals("getSession")) {
				events.add("getSession");
				return session;
			}
			if (method.getName().equals("changeSessionId")) {
				events.add("changeSessionId");
				return "rotated-session-id";
			}
			return defaultValue(method.getReturnType());
		});
		return proxy(WebRequestContext.class, (proxy, method, args) -> {
			if (method.getName().equals("getServletRequest")) {
				return servletRequest;
			}
			if (method.getName().equals("setUserAttribute")) {
				events.add("setUserAttribute");
				sessionAttributes.put((String) args[0], args[1]);
				return null;
			}
			return defaultValue(method.getReturnType());
		});
	}

	private LoginController.UserForm user(String username, String password) {
		LoginController.UserForm user = new LoginController.UserForm();
		user.setUsername(username);
		user.setPassword(password);
		return user;
	}

	private <T> T proxy(Class<T> type, InvocationHandler handler) {
		return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, handler));
	}

	private Object defaultValue(Class<?> type) {
		if (!type.isPrimitive() || type == void.class) {
			return null;
		}
		if (type == boolean.class) {
			return false;
		}
		if (type == char.class) {
			return '\0';
		}
		if (type == byte.class) {
			return (byte) 0;
		}
		if (type == short.class) {
			return (short) 0;
		}
		if (type == int.class) {
			return 0;
		}
		if (type == long.class) {
			return 0L;
		}
		if (type == float.class) {
			return 0F;
		}
		return 0D;
	}

	private static class TestLoginController extends LoginController {

		private final WebRequestContext request;
		private final PasswordEncoder passwordEncoder;

		TestLoginController(WebRequestContext request, PasswordEncoder passwordEncoder) {
			this.request = request;
			this.passwordEncoder = passwordEncoder;
		}

		@Override
		public WebRequestContext getRequest() {
			return request;
		}

		@Override
		public String doPage(UserForm user) {
			return "login";
		}

		@Override
		protected PasswordEncoder getPasswordEncoder() {
			return passwordEncoder;
		}

		boolean verify(String persisted, String provided) {
			return validPassword(persisted, provided);
		}
	}
}
