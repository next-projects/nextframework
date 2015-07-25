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
package org.nextframework.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

public class CompositeViewResolver extends InternalResourceViewResolver {
	

	private static final Log log = org.apache.commons.logging.LogFactory.getLog(CompositeViewResolver.class);
	
	private String baseView;
	private Boolean useBase = null;
	
	private String parameterName;

	@Override
	protected View loadView(String viewName, Locale locale) throws Exception {
		boolean direct = false;
		if(viewName.startsWith("direct:")){
			direct = true;
			viewName = viewName.substring("direct:".length());
		}
		
		String classpathView = null;
		
		if(viewName.startsWith("classpath:")){
			viewName = viewName.substring("classpath:".length());
			
			String path = viewName.substring(0, viewName.lastIndexOf('.'));
			
			
			URL resource = getServletContext().getResource("/WEB-INF/classes/"+viewName.replace('.', '/')+".jsp");
			if(resource == null){
				InputStream in = getClass().getClassLoader().getResourceAsStream(viewName.replace('.', '/')+".jsp");
				if(in == null){
					throw new RuntimeException("view not found "+viewName);
				}
				File dir = new File(getServletContext().getRealPath("/WEB-INF/classes/"+path.replace('.', '/')));
				dir.mkdirs();
				BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(getServletContext().getRealPath("/WEB-INF/classes/"+viewName.replace('.', '/')+".jsp")));
				int data;
				while(((data = in.read()) >= 0)){
					out.write(data);
				}
				out.flush();
			}
			classpathView = "/WEB-INF/classes/"+viewName.replace('.', '/')+".jsp";
		}
		
		AbstractUrlBasedView view = (AbstractUrlBasedView) super.loadView(viewName, locale);
		
		if(direct){
			return view;
		}
		
		if(classpathView != null){
			view.setUrl(classpathView);
		}
		
		if(useBase == null){
			String[] bases = baseView.split(",");
			for (String base : bases) {
				if(getServletContext().getResourceAsStream(base) == null){
					useBase = false;
				} else {
					useBase = true;
					baseView = base;
					break;
				}
			}
		}
		if(!useBase){
			return super.loadView(viewName, locale);
		}
		
		if(baseView == null){
			throw new RuntimeException(CompositeViewResolver.class.getName()+": property baseView não pode ser null");
		}
		
		if(parameterName == null){
			throw new RuntimeException(CompositeViewResolver.class.getName()+": property parameterName não pode ser null");
		}

		
		//url q seria usada
		String previousURL = view.getUrl();
		view.addStaticAttribute(parameterName, previousURL);
		//log.info(baseView+" > "+parameterName+" = "+previousURL);
		if(getServletContext().getResourceAsStream(previousURL) == null){
			log.warn("Página não existente: "+previousURL);
		}
		view.setUrl(baseView);
		
		return view;
	}

	public String getParameterName() {
		return parameterName;
	}

	public String getBaseView() {
		return baseView;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public void setBaseView(String resource) {
		this.baseView = resource;
	}
}
