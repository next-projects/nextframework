package org.nextframework.web.context;

import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.nextframework.service.ServiceException;
import org.nextframework.service.ServiceFactory;
import org.nextframework.web.WebContext;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;

/**
 * Automatically detected by {@link org.springframework.web.SpringServletContainerInitializer}.
 * 
 * @author rogelgarcia
 */
@Order(LOWEST_PRECEDENCE - 1000)
public class NextWebApplicationInitializer implements WebApplicationInitializer {

	private ServletContext servletContext;

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		this.servletContext = servletContext;
		registerContextLoaderListener();
	}

	protected void registerContextLoaderListener() {
		WebContext.setServletContext(servletContext);
		try {
			org.springframework.web.context.ContextLoaderListener springLoader = ServiceFactory.getService(org.springframework.web.context.ContextLoaderListener.class);
			servletContext.log("Using custom ContextLoaderListener: " + springLoader);
			servletContext.addListener(springLoader);
		} catch (ServiceException e) {
			servletContext.addListener(new ContextLoaderListener());
		}
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

}
