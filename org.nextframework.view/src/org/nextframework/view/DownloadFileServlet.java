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

import org.nextframework.service.ServiceFactory;
import org.nextframework.web.WebContext;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class DownloadFileServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String DOWNLOAD_FILE_PATH = "/downloadfile";
	public static final String DOWNLOAD_FILE_MAP = "NEXT_DOWNLOAD_FILE_MAP";

	private DownloadFileProvider delegate;

	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);

		//some containers do not use the same Thread for filter and servlet initialization
		WebContext.setServletContext(config.getServletContext());

		delegate = ServiceFactory.getService(DownloadFileProvider.class);

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		delegate.doGet(req, resp);
	}

	@Override
	protected long getLastModified(HttpServletRequest req) {
		return delegate.getLastModified(req);
	}

	public static void addCdfile(HttpSession session, Long cdfile) {
		getMap(session).put(String.valueOf(cdfile), String.valueOf(cdfile));
	}

	public static boolean checkCdfile(HttpSession session, Long cdfile) {
		return getMap(session).containsKey(String.valueOf(cdfile));
	}

	public static void addTempFileToken(HttpSession session, String tempFileToken) {
		getMap(session).put(tempFileToken, tempFileToken);
	}

	public static boolean checkTempFileToken(HttpSession session, String tempFileToken) {
		return getMap(session).containsKey(tempFileToken);
	}

	@SuppressWarnings("unchecked")
	private static HashMap<String, String> getMap(HttpSession session) {
		synchronized (session) {
			HashMap<String, String> map = (HashMap<String, String>) session.getAttribute(DOWNLOAD_FILE_MAP);
			if (map == null) {
				map = new HashMap<String, String>();
				session.setAttribute(DOWNLOAD_FILE_MAP, map);
			}
			return map;
		}
	}

}
