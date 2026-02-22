package org.nextframework.web;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.core.web.NextWeb;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

public class WebContextFilter implements Filter, ServletContextListener {

	static Log log = LogFactory.getLog("WebContext");

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		if (log.isInfoEnabled()) {
			log.info("Initialized");
		}
		WebContext.setServletContext(sce.getServletContext());
		NextWeb.createApplicationContext(sce.getServletContext());
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		if (log.isDebugEnabled()) {
			log.debug("Filter Initialized");
		}
		WebContext.setServletContext(filterConfig.getServletContext());
		NextWeb.createApplicationContext(filterConfig.getServletContext());
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		WebContext.setServletRequest(request);
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
