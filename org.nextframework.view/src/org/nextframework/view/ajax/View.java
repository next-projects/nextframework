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
package org.nextframework.view.ajax;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.nextframework.core.web.NextWeb;
import org.nextframework.core.web.WebRequestContext;
import org.nextframework.exception.NextException;
import org.nextframework.util.Util;

/**
 * Classe que representa a visão do browser. Essa classe deve ser utilizada em chamadas Ajax
 * @author rogelgarcia
 *
 */
public class View {

	private static final String CURRENT_VIEW = "CURRENT_VIEW";

	@SuppressWarnings("unused")
	private WebRequestContext request;
	HttpServletResponse response;
	PrintWriter out;

	@SuppressWarnings("unused")
	private List<String> linhas = new ArrayList<String>();

	public View(WebRequestContext request) {
		this.request = request;
		response = request.getServletResponse();
		response.setCharacterEncoding(StandardCharsets.UTF_8.name());
	}

	public static View getCurrent() {
		WebRequestContext requestContext = NextWeb.getRequestContext();
		View currentView = (View) requestContext.getAttribute(CURRENT_VIEW);
		if (currentView == null) {
			currentView = new View(requestContext);
			requestContext.setAttribute(CURRENT_VIEW, currentView);
		}
		return currentView;
	}

	public View alertMessage(String code, Object[] args, String defaultMessage) {
		return alert(Util.objects.newMessage(code, args, defaultMessage));
	}

	public View alert(Object o) {
		WebRequestContext request = NextWeb.getRequestContext();
		String oStr = Util.strings.toStringDescription(o, request.getLocale());
		println("alert('" + oStr + "');");
		return this;
	}

	public View eval(String codigo) {
		println(codigo);
		return this;
	}

	public View println(String s) {
		if (out == null) {
			try {
				out = response.getWriter();
			} catch (IOException e) {
				throw new NextException("Erro ao enviar código para o cliente na chamada ajax", e);
			}
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		}
		out.println(s);
		return this;
	}

	/**
	 * Monta e envia o código para o cliente
	 *
	 */
	public void flush() {
		if (out == null) {
			try {
				out = response.getWriter();
			} catch (IOException e) {
				throw new NextException("Erro ao enviar código para o cliente na chamada ajax", e);
			}
			response.setCharacterEncoding(StandardCharsets.UTF_8.name());
		}
		out.flush();
	}

}
