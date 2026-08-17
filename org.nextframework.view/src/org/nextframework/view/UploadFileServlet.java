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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.nextframework.controller.TempFileTokenSupport;
import org.nextframework.types.File;
import org.nextframework.util.Util;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@MultipartConfig
public class UploadFileServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static final String UPLOAD_FILE_PATH = "/uploadfile";
	public static final String FILE_PARAM = "file";
	public static final String FILE_CLASS_NAME_PARAM = "fileClassName";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {

			Part filePart = req.getPart(FILE_PARAM);
			if (filePart == null || filePart.getSize() <= 0) {
				sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Nenhum arquivo foi informado.");
				return;
			}

			String fileClassName = req.getParameter(FILE_CLASS_NAME_PARAM);
			if (Util.strings.isEmpty(fileClassName)) {
				sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "Classe concreta do arquivo não informada.");
				return;
			}

			File uploadData = TempFileTokenSupport.newFile(fileClassName);
			uploadData.setName(extractFileName(filePart));
			uploadData.setContenttype(filePart.getContentType());
			uploadData.setSize(filePart.getSize());
			uploadData.setContent(readBytes(filePart.getInputStream()));

			String token = TempFileTokenSupport.createToken();
			TempFileTokenSupport.persist(uploadData, token);

			resp.setStatus(HttpServletResponse.SC_OK);
			resp.setContentType("text/plain");
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().write(token);

		} catch (Exception e) {
			sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
		}

	}

	private void sendError(HttpServletResponse resp, int status, String message) throws IOException {
		resp.setStatus(status);
		resp.setContentType("text/plain");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().write(message != null ? message : "Erro no upload do arquivo.");
	}

	private String extractFileName(Part filePart) {
		String name = filePart.getSubmittedFileName();
		if (name == null) {
			return null;
		}
		name = name.replace('\\', '/');
		if (name.contains("/")) {
			name = name.substring(name.lastIndexOf('/') + 1);
		}
		return name;
	}

	private byte[] readBytes(InputStream inputStream) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int read;
		try {
			while ((read = inputStream.read(buffer)) >= 0) {
				outputStream.write(buffer, 0, read);
			}
			return outputStream.toByteArray();
		} finally {
			inputStream.close();
		}
	}

}
