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
package org.nextframework.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.nextframework.core.web.NextWeb;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FileModelAndView extends ModelAndView {

	private static final int DEFAULT_BUFFER_SIZE = 64 * 1024;

	public FileModelAndView(File file) {
		this(file, null, true);
	}

	public FileModelAndView(File file, String contentType, boolean useAttachment) {

		setView(new View() {

			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

				response.reset();

				response.setContentType(getContentType());
				if (useAttachment) {
					response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\";");
				}

				response.setBufferSize(DEFAULT_BUFFER_SIZE);

				long fileLength = file.length();
				response.setContentLengthLong(fileLength);

				ServletOutputStream output = response.getOutputStream();
				try (FileInputStream fileInputStream = new FileInputStream(file)) {
					try (BufferedInputStream input = new BufferedInputStream(fileInputStream, DEFAULT_BUFFER_SIZE)) {
						byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
						long remaining = fileLength;
						while (remaining > 0) {
							int bytesToRead = (int) Math.min(buffer.length, remaining);
							int bytesRead = input.read(buffer, 0, bytesToRead);
							if (bytesRead == -1) {
								throw new IOException("Arquivo terminou antes do esperado durante o download: " + file.getAbsolutePath());
							}
							output.write(buffer, 0, bytesRead);
							remaining -= bytesRead;
						}
					}
				}

				output.flush();
				response.flushBuffer();

			}

			@Override
			public String getContentType() {
				if (contentType == null) {
					return NextWeb.getApplicationContext().getServletContext().getMimeType(file.getName());
				}
				return contentType;
			}

		});
	}

}
