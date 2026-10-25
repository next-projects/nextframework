/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2009 the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * You may obtain a copy of the license at
 * 
 *     http://www.gnu.org/copyleft/lesser.html
 * 
 */
package org.nextframework.core.web;

import java.io.File;

import org.nextframework.core.standard.AbstractApplicationContext;

import jakarta.servlet.ServletContext;

/**
 * @author rogelgarcia
 * @since 21/01/2006
 * @version 1.1
 */
public class DefaultWebApplicationContext extends AbstractApplicationContext implements WebApplicationContext {

	private static final String APPLICATION_DIR = "application.data.dir";
	private static final String APPLICATION_NAME = "application.name";
	private static final String DEFAULT_APPLICATION_NAME = "ROOT";

	private ServletContext servletContext;

	public DefaultWebApplicationContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public String getApplicationName() {
		String contextPath = getServletContext().getContextPath();
		if (contextPath != null && contextPath.length() > 1) {
			return contextPath.substring(1);
		}
		String applicationName = getServletContext().getInitParameter(APPLICATION_NAME);
		if (applicationName != null && !applicationName.trim().isEmpty()) {
			return applicationName.trim();
		}
		String servletContextName = getServletContext().getServletContextName();
		if (servletContextName != null && !servletContextName.trim().isEmpty()) {
			return servletContextName.trim();
		}
		return DEFAULT_APPLICATION_NAME;
	}

	private String cachedApplicationDir = null;

	@Override
	public String getApplicationDir() {
		if (cachedApplicationDir != null) {
			return cachedApplicationDir;
		}
		String appDirInitParameter = getServletContext().getInitParameter(APPLICATION_DIR);
		if (appDirInitParameter != null && !appDirInitParameter.isEmpty()) {
			cachedApplicationDir = appDirInitParameter;
			return appDirInitParameter;
		}
		cachedApplicationDir = System.getProperty("user.home")
				+ File.separator + ".appData"
				+ File.separator + getApplicationName();
		return cachedApplicationDir;
	}

}
