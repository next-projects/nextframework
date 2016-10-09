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
package org.nextframework.view;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.nextframework.core.config.ViewConfig;
import org.nextframework.core.web.NextWeb;
import org.nextframework.service.ServiceFactory;

public class HeadTag extends BaseTag {
	
	protected String charset;
	
	protected boolean includeNormalizeCss = true;
	protected boolean includeSystemCss = true;
	protected boolean includeDefaultCss = true;
	protected boolean includeThemeCss = true;
	protected boolean includeUtilJs = true;
	protected boolean includeNextJs = true;
	protected boolean includeNextDirectJs = false;
	protected boolean searchCssDir = true;
	protected boolean searchJsDir = true;

	@Override
	protected void doComponent() throws Exception {
		String firstRequestUrl = NextWeb.getRequestContext().getFirstRequestUrl();
		String module = firstRequestUrl.substring(0, firstRequestUrl.substring(1).indexOf('/')+1);
		
		//procurar css
		Set<String> resourcePathsCssServer = getServletContext().getResourcePaths("/css");
		Set<String> resourcePathsCss = null;
		if (resourcePathsCssServer != null) {
			resourcePathsCss = new TreeSet<String>(resourcePathsCssServer);
			if (resourcePathsCss != null) {
				for (String string : resourcePathsCss) {
					if (string.endsWith("default.css")) {
						includeDefaultCss = false;
					}
					if (string.endsWith("theme.css")) {
						includeThemeCss = false;
					}
				}
			}
		}
		
		//procurar css para modulo
		Set<String> resourcePathsCssModuleServer = getServletContext().getResourcePaths("/css"+module);
		Set<String> resourcePathsModuleCss = null;
		if (resourcePathsCssModuleServer != null) {
			resourcePathsModuleCss = new TreeSet<String>(resourcePathsCssModuleServer);
			if (resourcePathsModuleCss != null) {
				for (String string : resourcePathsModuleCss) {
					if (string.endsWith("default.css")) {
						includeDefaultCss = false;
					}
					if (string.endsWith("theme.css")) {
						includeThemeCss = false;
					}
				}
			}
		}
		//procurar JS
		Set<String> resourcePathsJsServer = getServletContext().getResourcePaths("/js");
		Set<String> resourcePathsJs = null;
		if (resourcePathsJsServer != null) {
			resourcePathsJs = new TreeSet<String>(resourcePathsJsServer);
			if (resourcePathsJs != null) {
				for (String string : resourcePathsJs) {
					if (string.endsWith("util.js")) {
						includeUtilJs = false;
					}
				}
			}
		}
		//procurar JS para modulo
		Set<String> resourcePathsJsModuleServer = getServletContext().getResourcePaths("/js"+module);
		Set<String> resourcePathsModuleJs = null;
		if (resourcePathsJsModuleServer != null) {
			resourcePathsModuleJs = new TreeSet<String>(resourcePathsJsModuleServer);
			if (resourcePathsModuleJs != null) {
				for (String string : resourcePathsJs) {
					if (string.endsWith("util.js")) {
						includeUtilJs = false;
					}
				}
			}
		}
		
		filterDirs(resourcePathsJs);
		filterDirs(resourcePathsCss);
		
		charset = charset != null ? charset : ServiceFactory.getService(ViewConfig.class).getDefaultJSPCharset();
		
		pushAttribute("jss", resourcePathsJs);
		pushAttribute("csss", resourcePathsCss);
		pushAttribute("jssModule", resourcePathsModuleJs);
		pushAttribute("csssModule", resourcePathsModuleCss);
		pushAttribute("searchJsDir", this.searchJsDir);
		pushAttribute("searchCssDir", this.searchCssDir);
		includeJspTemplate();
		popAttribute("searchJsDir");
		popAttribute("searchCssDir");
		popAttribute("csss");
		popAttribute("jss");
	}

	protected void filterDirs(Set<String> resourcePathsJs) {
		if(resourcePathsJs != null){
			for (Iterator<String> iterator = resourcePathsJs.iterator(); iterator.hasNext();) {
				String path = iterator.next();
				if(path.endsWith("/")){
					iterator.remove();
				}
			}
		}
	}

	public boolean isIncludeNextJs() {
		return includeNextJs;
	}

	public void setIncludeNextJs(boolean includeNextJs) {
		this.includeNextJs = includeNextJs;
	}

	public boolean isIncludeNextDirectJs() {
		return includeNextDirectJs;
	}

	public void setIncludeNextDirectJs(boolean includeNextDirectJs) {
		this.includeNextDirectJs = includeNextDirectJs;
	}

	public boolean isIncludeDefaultCss() {
		return includeDefaultCss;
	}

	public void setIncludeDefaultCss(boolean includeDefault) {
		this.includeDefaultCss = includeDefault;
	}

	public boolean isIncludeUtilJs() {
		return includeUtilJs;
	}

	public void setIncludeUtilJs(boolean includeUtilJs) {
		this.includeUtilJs = includeUtilJs;
	}

	public boolean isIncludeThemeCss() {
		return includeThemeCss;
	}

	public void setIncludeThemeCss(boolean includeThemeCss) {
		this.includeThemeCss = includeThemeCss;
	}

	public boolean isSearchCssDir() {
		return searchCssDir;
	}

	public boolean isSearchJsDir() {
		return searchJsDir;
	}

	public void setSearchCssDir(boolean searchCssDir) {
		this.searchCssDir = searchCssDir;
	}

	public void setSearchJsDir(boolean searchJsDir) {
		this.searchJsDir = searchJsDir;
	}

	public String getCharset() {
		return charset;
	}
	
	public void setCharset(String charset) {
		this.charset = charset;
	}

	public boolean isIncludeNormalizeCss() {
		return includeNormalizeCss;
	}

	public boolean isIncludeSystemCss() {
		return includeSystemCss;
	}

	public void setIncludeNormalizeCss(boolean includeNormalizeCss) {
		this.includeNormalizeCss = includeNormalizeCss;
	}

	public void setIncludeSystemCss(boolean includeSystemCss) {
		this.includeSystemCss = includeSystemCss;
	}
	
}
