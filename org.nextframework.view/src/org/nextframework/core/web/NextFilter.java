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

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author rogelgarcia | marcusabreu
 * @since 21/01/2006
 * @version 1.1
 */
public class NextFilter implements Filter {
	
	String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = WebApplicationContext.class.getName() + ".ROOT";
	
	//copied from SelecionarCadastrarServlet
	public static final String INSELECTONE = "INSELECTONE";
	
	private Boolean initError = null;

	public void init(FilterConfig config) throws ServletException {
		
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		long beginTime = System.currentTimeMillis();
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		//colocar um flag na requisição indicando que esta é uma página selectone ou cadastrar
		String parameter = request.getParameter(INSELECTONE);
		if("true".equals(parameter)){
			request.setAttribute(INSELECTONE, true);
		}
		
		if(initError == null){
			initError = ((HttpServletRequest)req).getSession().getServletContext().getAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) instanceof Exception;
		}
		
		//context path como atributo
		request.setAttribute("application", request.getContextPath());
		
		//cria o contexto de requisicao NEXT
		NextWeb.createRequestContext(request, response);
		
		boolean nextRequest = request.getRequestURI().equals(request.getContextPath()+"/next");
		if(nextRequest || initError){
			response.setCharacterEncoding("ISO-8859-1");
			PrintWriter out = response.getWriter();
			out.println("<HTML>");
			out.println("<head>");
			out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" />");
			out.println("<link rel=\"stylesheet\"	href=\""+request.getContextPath()+"/resource/theme/welcome.css\"/>");
			out.println("</head>");
			
			out.println("<BODY>");
			if(initError){
				Exception ex = (Exception)((HttpServletRequest)req).getSession().getServletContext().getAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
				out.println("<h1 style=\"color: #AA0000\">There was a problem in the application initialization! Check log for more info</h1>");
				out.println("<div>"+ex.getMessage()+"</div>");
				out.println("<div style=\"padding-left: 20px\"> -> "+ex.getCause()+"</div>");
			} else {
				out.println("<h1>"+getAppName(request)+"</h1>");
				out.println("<h2>Application is running</h2>");
			}
			out.println("<p style=\"font-style: italic\">powered by NEXT FRAMEWORK</p>");
			out.println("</BODY>");
			out.println("</HTML>");
			out.flush();
			//String url = "/WEB-INF/classes/org/nextframework/resource/next.jsp";
			//ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
			//PrintWriter writer = new PrintWriter(arrayOutputStream);
		
			//RequestDispatcher requestDispatcher = null;
			//requestDispatcher = request.getRequestDispatcher(url);
			//requestDispatcher.include(request, response);
			//writer.flush();
			//response.getWriter().write(arrayOutputStream.toString());
			
			
			response.setStatus(HttpServletResponse.SC_OK);
		} else {
			if(request.getServletPath().equals("/logout")){
				request.getSession().invalidate();
				response.sendRedirect(request.getContextPath());
				return;
				
			}
			String uri = request.getRequestURI();
					
//			if (uri.matches("/.+?/.+?/.*")) {
//				request.setAttribute("NEXT_MODULO", uri.split("/")[2]);
//			}
			
			chain.doFilter(request, response);

			long endTime = System.currentTimeMillis();
			long elapsed = (endTime - beginTime);
			if (uri.length() > 5 && !uri.substring(uri.length() - 5).contains(".")) {
				if (elapsed > 250) {
					System.out.println("Time: " + request.getRequestURI() + "  " + elapsed + " ms");
				}
			}
		}
	}

	public String getAppName(HttpServletRequest request) {
		String contextpath = request.getContextPath();
		if(contextpath != null && contextpath.length() > 1){
			return contextpath.substring(1).toUpperCase();
		}
		String contextName = request.getServletContext().getServletContextName();
		if(contextName != null){
			return contextName;
		}
		return new DefaultWebApplicationContext(request.getServletContext()).getApplicationName();
	}

	public void destroy() {
	}

}