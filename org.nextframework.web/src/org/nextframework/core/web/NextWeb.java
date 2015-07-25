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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.context.NotInNextContextException;
import org.nextframework.core.standard.Next;

/**
 * @author rogelgarcia
 * @since 21/01/2006
 * @version 1.1
 */
public class NextWeb extends Next {
	
	protected static final Log log = LogFactory.getLog(NextWeb.class);
	
	private static WebRequestFactory webRequestFactory = new WebRequestFactory();
	
	public static void setWebRequestFactory(WebRequestFactory webRequestFactory) {
		NextWeb.webRequestFactory = webRequestFactory;
	}

	public static void createRequestContext(HttpServletRequest request, HttpServletResponse response){
		if(request == null || response == null){
			throw new NullPointerException();
		}
		ServletContext servletContext = request.getSession().getServletContext();
		
		getWebApplicationContext(servletContext);

		getRequestContext(request, response);
	}
	
	public static WebRequestContext getRequestContext() throws NotInNextContextException{
		WebRequestContext context = (WebRequestContext) requestContext.get();
		if(context == null){
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

			String msg = "The code is not running in a NEXT context!";
			if (stackTrace.length>=4) {
				msg += "\nClass: "+stackTrace[3].getClassName()+" " +
						"\nMethod: "+stackTrace[3].getMethodName()+" " +
						"\nLine: "+stackTrace[3].getLineNumber();
			}
			
			throw new NotInNextContextException(msg);
		}
		return context;
	}
	
	public static WebApplicationContext getApplicationContext() throws NotInNextContextException{
		WebApplicationContext nextApplicationContext = (WebApplicationContext) applicationContext.get();
		if(nextApplicationContext == null){
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			throw new NotInNextContextException("The code is not running in a NEXT context!" +
							"\nClass: "+stackTrace[3].getClassName()+" " +
							"\nMethod: "+stackTrace[3].getMethodName()+" " +
							"\nLine: "+stackTrace[3].getLineNumber());
		}
		return nextApplicationContext;
	}
	
	
	public static WebApplicationContext createApplicationContext(ServletContext servletContext){
	
		WebApplicationContext applicationContext;
		applicationContext = new DefaultWebApplicationContext(servletContext);
		servletContext.setAttribute(WebApplicationContext.APPLICATION_CONTEXT_ATTRIBUTE, applicationContext);
		
		Next.applicationContext.set(applicationContext);
		
		return applicationContext;
	}

	public static WebRequestContext getRequestContext(HttpServletRequest request, HttpServletResponse response) {
		//verificar se j� existe um request context
		createApplicationContext(request.getSession().getServletContext());
		WebRequestContext requestContext = (WebRequestContext) request.getAttribute(WebRequestContext.REQUEST_CONTEXT_ATTRIBUTE);
		if(requestContext == null){
			log.trace("Creating NEXT request context... ");
			//requestContext = new DefaultWebRequestContext(request, response, getWebApplicationContext(request.getSession().getServletContext()));
			requestContext = webRequestFactory.createWebRequestContext(request, response, getWebApplicationContext(request.getSession().getServletContext()));
			request.setAttribute(WebRequestContext.REQUEST_CONTEXT_ATTRIBUTE, requestContext);
			Next.requestContext.set(requestContext);
		} else {
			//atualizar o request por causa do multipart
			//requestContext = new DefaultWebRequestContext(request, response, getWebApplicationContext(request.getSession().getServletContext()));
			requestContext = webRequestFactory.createWebRequestContext(request, response, getWebApplicationContext(request.getSession().getServletContext()));
			request.setAttribute(WebRequestContext.REQUEST_CONTEXT_ATTRIBUTE, requestContext);
			Next.requestContext.set(requestContext);
		}
		return requestContext;
	}

	public static WebApplicationContext getWebApplicationContext(ServletContext servletContext) {
		//verificar se j� existe um applicationContext
		WebApplicationContext applicationContext = (WebApplicationContext) servletContext.getAttribute(WebApplicationContext.APPLICATION_CONTEXT_ATTRIBUTE);
		if(applicationContext == null){
			applicationContext = createApplicationContext(servletContext);
//			throw new NotInNextContextException("O contexto de aplica��o NEXT ainda n�o foi criado. Verifique se o listener ContextLoaderListener est� configurado no web.xml");
		}
		Next.applicationContext.set(applicationContext);
		return applicationContext;
	}
}
