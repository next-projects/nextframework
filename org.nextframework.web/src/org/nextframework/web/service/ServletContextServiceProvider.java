package org.nextframework.web.service;

import java.lang.reflect.Array;

import javax.servlet.ServletContext;

import org.nextframework.service.ServiceProvider;
import org.nextframework.web.WebContext;

public class ServletContextServiceProvider implements ServiceProvider {

	public static int PRIORITY = 30;

	private static boolean disabled = false;

	public static void disable() {
		disabled = true;
	}

	public static void enable() {
		disabled = false;
	}

	public ServletContextServiceProvider() {
		try {
			Class.forName(ServletContext.class.getName());
		} catch (ClassNotFoundException e) {
			disable();//if no web classes disable this service provider
		}
		//verify if it was initialized in a servlet context
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		for (StackTraceElement stackTraceElement : stackTrace) {
			if (stackTraceElement.getClassName().contains("web")
					|| stackTraceElement.getClassName().contains("catalina")
					|| stackTraceElement.getClassName().contains("servlet")) {
				if (stackTraceElement.getClassName().equals(this.getClass().getName())) {
					continue;
				}
				//recognizes initialization by org.nextframework.web.context.NextWebApplicationInitializer, catalina (tomcat) or servlet 
				return;
			}
		}
		disable();
	}

	public static <E, X extends E> void registerService(ServletContext servletContext, Class<E> serviceInterface, X o) {
		servletContext.setAttribute(serviceInterface.getName(), o);
	}

	public static <E, X extends E> void registerService(Class<E> serviceInterface, X o) {
		registerService(WebContext.getServletContext(), serviceInterface, o);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E> E[] loadServices(Class<E> serviceInterface) {
		return (E[]) Array.newInstance(serviceInterface, 0);
	}

	@Override
	public <E> E getService(Class<E> serviceInterface) {
		if (disabled) {
			return null;
		}
		ServletContext servletContext = WebContext.getServletContext();
		return getService(servletContext, serviceInterface);
	}

	@SuppressWarnings("unchecked")
	public static <E> E getService(ServletContext servletContext, Class<E> serviceInterface) {
		return (E) servletContext.getAttribute(serviceInterface.getName());
	}

	@Override
	public int priority() {
		return PRIORITY;
	}

	@Override
	public void release() {
	}

}
