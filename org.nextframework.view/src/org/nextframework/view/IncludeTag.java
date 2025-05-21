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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.nextframework.core.config.ViewConfig;
import org.nextframework.service.ServiceFactory;

/**
 * @author fabricio
 */
public class IncludeTag extends TagSupport {

	private static final long serialVersionUID = 1L;

	protected String url;

	public int doEndTag() throws JspException {

		JspWriter saida = pageContext.getOut();
		HttpURLConnection urlConnection = null;

		try {

			URL requisicao = new URL(((HttpServletRequest) pageContext.getRequest()).getRequestURL().toString());
			URL link = new URL(requisicao, url);

			urlConnection = (HttpURLConnection) link.openConnection();
			ViewConfig viewConfig = ServiceFactory.getService(ViewConfig.class);
			BufferedReader entrada = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), viewConfig.getJSPDefaultCharset()));

			String linha = entrada.readLine();
			while (linha != null) {
				saida.write(linha + "\n");
				linha = entrada.readLine();
			}

			entrada.close();

		} catch (Exception e) {
			try {
				saida.write("Erro ao incluir o conteúdo da URL \"" + url + "\"");
			} catch (IOException e1) {
			}
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}

		return super.doEndTag();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String string) {
		url = string;
	}

}
