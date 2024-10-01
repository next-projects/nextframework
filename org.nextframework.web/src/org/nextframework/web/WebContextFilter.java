package org.nextframework.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.core.web.NextWeb;

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
