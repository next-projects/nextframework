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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nextframework.classmanager.ClassManager;
import org.nextframework.classmanager.ClassManagerFactory;
import org.nextframework.util.Util;
import org.nextframework.view.ajax.AjaxCallbackController;
import org.nextframework.view.ajax.AjaxCallbackSupport;
import org.nextframework.view.ajax.ComboCallback;
import org.nextframework.view.ajax.ProgressBarCallback;
import org.nextframework.web.WebContext;

public class AjaxServlet extends HttpServlet {
	
	Map<String, AjaxCallbackController> callbacks = new HashMap<String, AjaxCallbackController>();
	
	@Override
	public void init() throws ServletException {
		super.init();
		//some containers do not use the same Thread for filter and servlet initialization
		WebContext.setServletContext(getServletContext());
		
		callbacks.put("combo", new ComboCallback());
		callbacks.put("progressbar", new ProgressBarCallback());
		callbacks.put("callbacksupport", new AjaxCallbackSupport());
		
		ClassManager classManager = ClassManagerFactory.getClassManager();
		Class<AjaxCallbackController>[] callbackClasses = classManager.getAllClassesOfType(AjaxCallbackController.class);
		callbackClasses = Util.objects.removeInterfaces(callbackClasses);
		
		for (Class<AjaxCallbackController> class1 : callbackClasses) {
			String simpleName = class1.getSimpleName().toLowerCase();
			if(simpleName.endsWith("callback")){
				simpleName = simpleName.substring(0, simpleName.length() - "callback".length());
			}
			try {
				callbacks.put(simpleName, class1.newInstance());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestURI = request.getRequestURI();
		String servletPath = request.getServletPath();
		int s = requestURI.indexOf(servletPath)+servletPath.length()+1;
		String requestResource = requestURI.substring(s);
		String item;
		if(requestResource.indexOf('/')>=0){
			item = requestResource.substring(requestResource.indexOf('/'));	
		} else {
			item = requestResource;
		}
		
		AjaxCallbackController ajaxCallback = callbacks.get(item);
		if(ajaxCallback != null){
			response.addHeader("Content-Type", "text/html; charset=iso-8859-1");
			try {
				ajaxCallback.doAjax(request, response);
			} catch (Exception e) {
				String message = escapeSingleQuotes(e.getMessage());
				if(e != null && e.getCause() != null){
					message += "\\nCause: "+escapeSingleQuotes(e.getCause().getMessage());
				}
				response.getWriter().println("alert('"+e.getClass().getSimpleName()+": "+message+"')");
				e.printStackTrace();
			} catch(Throwable t){
				String message = escapeSingleQuotes(t.getMessage());
				if(t != null && t.getCause() != null){
					message += "\\nCause: "+escapeSingleQuotes(t.getCause().getMessage());
				}
				response.getWriter().println("alert('"+t.getClass().getSimpleName()+": "+message+"')");
				t.printStackTrace();
			}
		}
	}

	private String escapeSingleQuotes(String message) {
		if(message == null){
			return "";
		}
		return message.replace((CharSequence)"'", "\\'");
	}

	@Override
	protected void doPost(HttpServletRequest arg0, HttpServletResponse arg1) throws ServletException, IOException {
		doGet(arg0, arg1);
	}

}
