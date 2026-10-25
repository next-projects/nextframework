package org.nextframework.test.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.After;
import org.junit.Test;
import org.nextframework.core.web.DefaultWebApplicationContext;
import org.nextframework.core.web.NextWeb;
import org.nextframework.core.web.WebApplicationContext;
import org.nextframework.core.web.WebRequestContext;
import org.nextframework.core.web.WebRequestFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class NextWebApplicationContextTest {

	@After
	public void resetContext() {
		NextWeb.remove();
		NextWeb.setWebRequestFactory(new WebRequestFactory());
	}

	@Test
	public void requestCreationReusesTheApplicationContext() {
		ServletContext servletContext = servletContext("/test", null, null);
		HttpServletRequest request = request(servletContext);
		HttpServletResponse response = proxy(HttpServletResponse.class);
		WebApplicationContext originalContext = NextWeb.getWebApplicationContext(servletContext);
		AtomicReference<WebApplicationContext> factoryContext = new AtomicReference<WebApplicationContext>();
		WebRequestContext requestContext = requestContext(request, originalContext);
		NextWeb.setWebRequestFactory(new WebRequestFactory() {

			@Override
			public WebRequestContext createWebRequestContext(HttpServletRequest request, HttpServletResponse response, WebApplicationContext applicationContext) {
				factoryContext.set(applicationContext);
				return requestContext;
			}
		});

		NextWeb.remove();
		NextWeb.createRequestContext(request, response);

		assertSame(originalContext, factoryContext.get());
		assertSame(originalContext, NextWeb.getApplicationContext());
		assertSame(originalContext, servletContext.getAttribute(WebApplicationContext.APPLICATION_CONTEXT_ATTRIBUTE));
		assertSame(requestContext, NextWeb.getRequestContext());

		NextWeb.remove();
		NextWeb.getRequestContext(request, response);
		assertSame(originalContext, NextWeb.getApplicationContext());
		assertSame(requestContext, NextWeb.getRequestContext());
	}

	@Test
	public void nonRootApplicationUsesItsContextPath() {
		assertEquals("orders", new DefaultWebApplicationContext(servletContext("/orders", "Display name", "Configured name")).getApplicationName());
	}

	@Test
	public void rootApplicationUsesConfiguredName() {
		assertEquals("Configured name", new DefaultWebApplicationContext(servletContext("", "Display name", " Configured name ")).getApplicationName());
	}

	@Test
	public void rootApplicationFallsBackToDisplayNameThenRoot() {
		assertEquals("Display name", new DefaultWebApplicationContext(servletContext("", " Display name ", null)).getApplicationName());
		assertEquals("ROOT", new DefaultWebApplicationContext(servletContext("", null, null)).getApplicationName());
	}

	private static ServletContext servletContext(String contextPath, String contextName, String applicationName) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		return (ServletContext) Proxy.newProxyInstance(ServletContext.class.getClassLoader(), new Class<?>[] { ServletContext.class }, (proxy, method, args) -> {
			if (method.getName().equals("getContextPath")) {
				return contextPath;
			}
			if (method.getName().equals("getServletContextName")) {
				return contextName;
			}
			if (method.getName().equals("getInitParameter")) {
				return "application.name".equals(args[0]) ? applicationName : null;
			}
			if (method.getName().equals("setAttribute")) {
				attributes.put((String) args[0], args[1]);
				return null;
			}
			if (method.getName().equals("getAttribute")) {
				return attributes.get(args[0]);
			}
			return defaultValue(method.getReturnType());
		});
	}

	private static HttpServletRequest request(ServletContext servletContext) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		return (HttpServletRequest) Proxy.newProxyInstance(HttpServletRequest.class.getClassLoader(), new Class<?>[] { HttpServletRequest.class }, (proxy, method, args) -> {
			if (method.getName().equals("getServletContext")) {
				return servletContext;
			}
			if (method.getName().equals("setAttribute")) {
				attributes.put((String) args[0], args[1]);
				return null;
			}
			if (method.getName().equals("getAttribute")) {
				return attributes.get(args[0]);
			}
			return defaultValue(method.getReturnType());
		});
	}

	private static WebRequestContext requestContext(HttpServletRequest request, WebApplicationContext applicationContext) {
		return (WebRequestContext) Proxy.newProxyInstance(WebRequestContext.class.getClassLoader(), new Class<?>[] { WebRequestContext.class }, (proxy, method, args) -> {
			if (method.getName().equals("getServletRequest")) {
				return request;
			}
			if (method.getName().equals("getApplicationContext") || method.getName().equals("getWebApplicationContext")) {
				return applicationContext;
			}
			return defaultValue(method.getReturnType());
		});
	}

	@SuppressWarnings("unchecked")
	private static <T> T proxy(Class<T> type) {
		return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, (proxy, method, args) -> defaultValue(method.getReturnType()));
	}

	private static Object defaultValue(Class<?> type) {
		if (!type.isPrimitive()) {
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

}
