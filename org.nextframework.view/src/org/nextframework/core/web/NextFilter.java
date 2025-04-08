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
import java.util.Locale;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.authorization.User;
import org.nextframework.authorization.web.impl.WebUserLocator;
import org.nextframework.core.config.ViewConfig;
import org.nextframework.service.ServiceFactory;
import org.nextframework.web.WebUtils;

/**
 * @author rogelgarcia | marcusabreu
 * @since 21/01/2006
 * @version 1.1
 */
public class NextFilter implements Filter {

	protected Log log = LogFactory.getLog(this.getClass());

	private static final String APPLICATION_ATTRIB = "application";
	private static final String APP_ATTRIB = "app";
	private static final String BOOTSTRAP_ATTRIB = "useBootstrap";
	private static final String LOCALE_ATTRIB = "locale";
	private static final String SYSTEM_LOCALE_ATTRIB = "systemLocale";

	private static final String URL_NEXT = "/next";
	private static final String URL_LOGOUT = "/logout";
	private static final String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = WebApplicationContext.class.getName() + ".ROOT";

	//copied from SelecionarCadastrarServlet
	private static final String INSELECTONE = "INSELECTONE";

	public void init(FilterConfig config) throws ServletException {

	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		boolean nextRequest = request.getServletPath().equals(URL_NEXT);
		Exception ex = (Exception) ((HttpServletRequest) req).getSession().getServletContext().getAttribute(ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);

		if (nextRequest || ex != null) {

			printNextInfo(request, response, ex);

		} else {

			if (request.getServletPath().equals(URL_LOGOUT)) {
				request.getSession().invalidate();
				response.sendRedirect(request.getContextPath());
				return;
			}

			//cria o contexto de requisicao NEXT
			NextWeb.createRequestContext(request, response);

			boolean simpleResource = isSimpleResource(request);
			if (simpleResource) {
				chain.doFilter(request, response);
				return;
			}

			//Marca início do processo
			long beginTime = System.currentTimeMillis();
			String userProcessPrefix = getUserProcessPrefix(request);
			log.info(userProcessPrefix + "...");

			//context path como atributo
			request.setAttribute(APPLICATION_ATTRIB, request.getContextPath());
			request.setAttribute(APP_ATTRIB, request.getContextPath());

			//Uso de bootstrap
			request.setAttribute(BOOTSTRAP_ATTRIB, ServiceFactory.getService(ViewConfig.class).isUseBootstrap());

			//Locale do usuário e do sistema
			request.setAttribute(LOCALE_ATTRIB, NextWeb.getRequestContext().getLocale());
			request.setAttribute(SYSTEM_LOCALE_ATTRIB, Locale.getDefault());

			//colocar um flag na requisição indicando que esta é uma página selectone ou cadastrar
			String parameter = request.getParameter(INSELECTONE);
			if ("true".equals(parameter)) {
				request.setAttribute(INSELECTONE, true);
			}

			//Toca o barco
			chain.doFilter(request, response);

			if (request.getAttribute(org.springframework.web.servlet.DispatcherServlet.EXCEPTION_ATTRIBUTE) != null) {
				userProcessPrefix += " ERROR!";
			}

			//Marca fim do processo
			long elapsed = (System.currentTimeMillis() - beginTime);
			if (elapsed < 1000 * 10) {
				log.info(userProcessPrefix + " " + elapsed + " ms");
			} else {
				log.warn(userProcessPrefix + " " + elapsed + " ms");
			}

		}

	}

	private void printNextInfo(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException {

		response.setCharacterEncoding("ISO-8859-1");

		PrintWriter out = response.getWriter();

		out.println("<HTML>");
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=ISO-8859-1\" />");
		out.println("<link rel=\"stylesheet\" href=\"" + request.getContextPath() + "/resource/css/welcome.css\"/>");
		out.println("</head>");
		out.println("<BODY>");
		if (ex != null) {
			out.println("<h1 style=\"color: #AA0000\">There was a problem in the application initialization! Check log for more info</h1>");
			out.println("<div>" + ex.getMessage() + "</div>");
			out.println("<div style=\"padding-left: 20px\"> -> " + ex.getCause() + "</div>");
		} else {
			out.println("<h1>" + getAppName(request) + "</h1>");
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

	}

	public String getAppName(HttpServletRequest request) {
		String contextpath = request.getContextPath();
		if (contextpath != null && contextpath.length() > 1) {
			return contextpath.substring(1).toUpperCase();
		}
		String contextName = request.getServletContext().getServletContextName();
		if (contextName != null) {
			return contextName;
		}
		return new DefaultWebApplicationContext(request.getServletContext()).getApplicationName();
	}

	public boolean isSimpleResource(HttpServletRequest request) {
		String uri = request.getRequestURI();
		return uri.length() < 10 || (uri.substring(uri.length() - 10).contains(".") && !uri.contains(".jsp")) || uri.contains("/ajax/");
	}

	public String getUserProcessPrefix(HttpServletRequest request) {
		User user = WebUserLocator.getSessionUser(request);
		String userStr = user != null ? user.getUsername() : "?";
		String ip = WebUtils.getClientIpAddress(request);
		return userStr + " (" + ip + ") -> " + request.getRequestURI();
	}

	public void destroy() {

	}

}