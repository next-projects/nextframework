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
package org.nextframework.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//http://tutorials.jenkov.com/java-servlets/gzip-servlet-filter.html
public class GZIPFilter implements Filter {

	public void init(FilterConfig filterConfig) {

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

		if (request instanceof HttpServletRequest) {

			HttpServletRequest httpRequest = (HttpServletRequest) request;
			HttpServletResponse httpResponse = (HttpServletResponse) response;

			if (acceptsGZipEncoding(httpRequest)) {
				httpResponse.addHeader("Content-Encoding", "gzip");
				GZIPResponseWrapper gzipResponse = new GZIPResponseWrapper(httpResponse);
				chain.doFilter(request, gzipResponse);
				gzipResponse.close();
			} else {
				chain.doFilter(request, response);
			}

		}

	}

	private boolean acceptsGZipEncoding(HttpServletRequest request) {
		//Se for IE, não rola o gzip em .js e .css
		//Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; Touch; rv:11.0) like Gecko
		//Mozilla/5.0 (Windows NT 6.0; Win64; x64; Trident/7.0; rv:11.0) like Gecko
		//Mozilla/5.0 (Windows NT 6.2; Trident/7.0; rv:11.0) like Gecko
		//Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; zsuser; rv:11.0) like Gecko
		//Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0; i-Macros-Web-Automation) like Gecko
		//Mozilla/5.0 (Windows NT 7; WOW64; Trident/7.0; LCTE; rv:11.0) like Gecko
		String useragent = request.getHeader("user-agent");
		String requestURI = request.getRequestURI();
		if (useragent != null &&
				(useragent.indexOf("MSIE") != -1 || useragent.indexOf("WOW64") != -1 && useragent.indexOf("Trident/7.0") != -1) &&
				(requestURI.endsWith(".js") || requestURI.endsWith(".css"))) {
			return false;
		}
		//Verifica se quem solicitou aceita gzip
		String acceptEncoding = request.getHeader("Accept-Encoding");
		return acceptEncoding != null && acceptEncoding.indexOf("gzip") != -1;
	}

	public void destroy() {

	}

}
