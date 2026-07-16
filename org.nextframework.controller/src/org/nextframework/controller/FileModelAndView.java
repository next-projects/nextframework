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
import java.util.Map;

import org.nextframework.core.web.NextWeb;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FileModelAndView extends ModelAndView {

	public FileModelAndView(File file) {
		this(file, null, true);
	}

	public FileModelAndView(File file, String contentType, boolean useAttachment) {

		setView(new View() {

			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

				response.setContentType(getContentType());
				if (useAttachment) {
					response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\";");
				}
				response.setContentLengthLong(file.length());

				try (BufferedInputStream input = new BufferedInputStream(new FileInputStream(file))) {
					byte[] buffer = new byte[8192];
					int bytesRead = 0;
					while ((bytesRead = input.read(buffer)) != -1) {
						response.getOutputStream().write(buffer, 0, bytesRead);
					}
				}

				response.getOutputStream().flush();
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