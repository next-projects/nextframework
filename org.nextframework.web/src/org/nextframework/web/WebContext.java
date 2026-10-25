package org.nextframework.web;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;

public class WebContext {

	private static final InheritableThreadLocal<ServletContext> sctl = new InheritableThreadLocal<ServletContext>();
	private static final InheritableThreadLocal<HttpServletRequest> srtl = new InheritableThreadLocal<HttpServletRequest>();

	public static void setServletContext(ServletContext servletContext) {
		sctl.set(servletContext);
	}

	public static void setServletRequest(ServletRequest request) {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		setServletContext(httpServletRequest.getServletContext());
		srtl.set(httpServletRequest);
	}

	public static void remove() {
		srtl.remove();
		sctl.remove();
	}

	public static ServletContext getServletContext() {
		ServletContext servletContext = sctl.get();
		if (servletContext == null) {
			throw new IllegalStateException("The thread ServletContext was not previously set. " +
					" Configure " + WebContextFilter.class.getName() + " as a filter and listener for your web application. ");
		}
		return servletContext;
	}

	public static HttpServletRequest getRequest() {
		HttpServletRequest servletRequest = srtl.get();
		if (servletRequest == null) {
			throw new IllegalStateException("The thread ServletRequest was not previously set. " +
					"Configure " + WebContextFilter.class.getName() + " as a filter and listener for your web application. ");
		}
		return servletRequest;
	}

}
