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

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SelecionarCadastrarServlet extends HttpServlet {

	public static final String INSELECTONE = "INSELECTONE";
	private static final long serialVersionUID = 1L;

	public static final String SELECIONAR_CADASTRAR_PATH = "/selectcreate";

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String requestURI = request.getRequestURI();
		String encodedURI = requestURI
				.replaceAll(SELECIONAR_CADASTRAR_PATH, "")
				.replaceAll(SELECIONAR_CADASTRAR_PATH.toLowerCase(), "");
		request.setAttribute("IMPRIMIRSELECIONAR", true);
		if (encodedURI.contains("?")) {
			encodedURI += "&IMPRIMIRSELECIONAR=true&INSELECTONE=true";
		} else {
			encodedURI += "?IMPRIMIRSELECIONAR=true&INSELECTONE=true";
		}
		String queryString = request.getQueryString();
		if (queryString != null && !queryString.trim().equals("")) {
			queryString = "&" + queryString;
		} else {
			queryString = "";
		}

		StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Frameset//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd\">");
		builder.append("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		builder.append("<frameset border='0' rows='0px, *'>");
		builder.append("<frame SRC='about:blank'>");
		builder.append("</frame>");
		builder.append("<frame SRC='" + encodedURI + queryString + "'>");
		builder.append("</frame>");
		builder.append("</frameset>");
		builder.append("</html>");

//		Html html = new Html();
//		Frameset frameset = new Frameset();
//		frameset.addAttribute("border", "0");
//		frameset.addAttribute("rows", "0px, *");
//		
//		Frame frameJS = new Frame();
//		frameJS.setSrc("about:blank");
//		Frame frameMain = new Frame();
//		frameMain.setSrc(encodedURI);
//		
//		frameset.addSubTag(frameJS);
//		frameset.addSubTag(frameMain);
//		html.addSubTag(frameset);
//		
//		HtmlRenderer renderer = new HtmlRenderer();
		response.setContentType("text/html");
		response.getWriter().println(builder.toString());

	}

}
