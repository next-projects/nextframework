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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nextframework.core.standard.Next;
import org.nextframework.service.ServiceFactory;
import org.nextframework.types.File;
import org.nextframework.web.WebContext;

public class DownloadFileServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String DOWNLOAD_FILE_MAP = "NEXT_DOWNLOAD_FILE_MAP";

	public static final String DOWNLOAD_FILE_PATH = "/downloadfile";

	DownloadFileProvider delegate;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		//some containers do not use the same Thread for filter and servlet initialization
		WebContext.setServletContext(config.getServletContext());

		delegate = ServiceFactory.getService(DownloadFileProvider.class);
	}

	private static long tempFileId = -1;

	public synchronized static long getNewTempFileId() {
		return tempFileId--;
	}

	public static void persist(File value, long tempFileId) throws IOException {
		//TODO UNIFICAR O LOCAL DE SALVAR E LER OS ARQUIVOS TEMPORARIOS
		java.io.File tempFile = new java.io.File(System.getProperty("java.io.tmpdir"), Next.getApplicationName() + "_tempFileObject" + tempFileId + ".next");
		System.out.println("TEMPORARY FILE    " + tempFile.getAbsolutePath());
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempFile));
		out.writeObject(value);
		out.flush();
		out.close();
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
		getMap(session).put(cdfile, cdfile);
	}

	public static boolean checkCdfile(HttpSession session, Long cdfile) {
		return getMap(session).containsKey(cdfile);
	}

	private static HashMap<Long, Long> getMap(HttpSession session) {
		@SuppressWarnings("unchecked")
		HashMap<Long, Long> map = (HashMap<Long, Long>) session.getAttribute(DOWNLOAD_FILE_MAP);

		if (map == null) {
			map = new HashMap<Long, Long>();
			session.setAttribute(DOWNLOAD_FILE_MAP, map);
		}

		return map;
	}

}
