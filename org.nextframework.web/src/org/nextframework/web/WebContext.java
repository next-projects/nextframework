package org.nextframework.web;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

public class WebContext {

	private static ThreadLocal<ServletContext> sctl = new InheritableThreadLocal<ServletContext>();
	private static ThreadLocal<HttpServletRequest> srtl = new InheritableThreadLocal<HttpServletRequest>();

	public static void setServletContext(ServletContext servletContext) {
		sctl.set(servletContext);
	}

	public static void setServletRequest(ServletRequest request) {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		setServletContext(httpServletRequest.getServletContext());
		srtl.set(httpServletRequest);
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
