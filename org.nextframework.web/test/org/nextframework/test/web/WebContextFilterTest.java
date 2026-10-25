package org.nextframework.test.web;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;
import org.nextframework.core.web.NextWeb;
import org.nextframework.core.web.WebApplicationContext;
import org.nextframework.web.WebContext;
import org.nextframework.web.WebContextFilter;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class WebContextFilterTest {

	@After
	public void removeContext() {
		NextWeb.remove();
		WebContext.remove();
	}

	@Test
	public void filterRemovesContextWhenTheChainFails() {

		ServletContext servletContext = proxy(ServletContext.class, null);
		HttpServletRequest request = proxy(HttpServletRequest.class, servletContext);
		ServletResponse response = proxy(ServletResponse.class, null);
		WebContextFilter filter = new WebContextFilter();

		assertThrows(ServletException.class, () -> filter.doFilter(request, response, (req, res) -> {
			assertSame(request, WebContext.getRequest());
			assertSame(servletContext, WebContext.getServletContext());
			throw new ServletException("expected failure");
		}));
		assertThrows(IllegalStateException.class, WebContext::getRequest);
		assertThrows(IllegalStateException.class, WebContext::getServletContext);

	}

	@Test
	public void filterInitializationReusesAndPreservesStartupContext() throws ServletException {

		ServletContext servletContext = proxy(ServletContext.class, null);
		WebContextFilter filter = new WebContextFilter();

		filter.contextInitialized(new ServletContextEvent(servletContext));
		assertSame(servletContext, WebContext.getServletContext());
		WebApplicationContext applicationContext = NextWeb.getApplicationContext();
		assertSame(servletContext, applicationContext.getServletContext());

		filter.init(proxy(FilterConfig.class, servletContext));
		assertSame(servletContext, WebContext.getServletContext());
		assertSame(applicationContext, NextWeb.getApplicationContext());

	}

	@SuppressWarnings("unchecked")
	private static <T> T proxy(Class<T> type, ServletContext servletContext) {
		Map<String, Object> attributes = new HashMap<String, Object>();
		return (T) Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, (proxy, method, args) -> {
			if (method.getName().equals("getServletContext")) {
				return servletContext;
			}
			if (type == ServletContext.class && method.getName().equals("setAttribute")) {
				attributes.put((String) args[0], args[1]);
				return null;
			}
			if (type == ServletContext.class && method.getName().equals("getAttribute")) {
				return attributes.get(args[0]);
			}
			Class<?> returnType = method.getReturnType();
			if (!returnType.isPrimitive()) {
				return null;
			}
			if (returnType == boolean.class) {
				return false;
			}
			if (returnType == char.class) {
				return '\0';
			}
			return 0;
		});
	}

}
