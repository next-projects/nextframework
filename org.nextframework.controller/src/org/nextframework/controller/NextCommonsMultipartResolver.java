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

import javax.servlet.http.HttpServletRequest;

import org.nextframework.exception.NextException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

public class NextCommonsMultipartResolver extends CommonsMultipartResolver {

	public static final String MAXUPLOADEXCEEDED = "MAXUPLOADEXCEEDED";

	public static final String RESOLVED_MULTIPART_REQUEST = "RESOLVED_MULTIPART_REQUEST";

	@Override
	public MultipartHttpServletRequest resolveMultipart(HttpServletRequest request) throws MultipartException {
		try {
			MultipartHttpServletRequest resolvedMultipartRequest = super.resolveMultipart(request);
			request.setAttribute(RESOLVED_MULTIPART_REQUEST, resolvedMultipartRequest);
			return resolvedMultipartRequest;
		} catch (MaxUploadSizeExceededException e) {
			throw new NextException("O tamanho máximo de upload de arquivos (" + e.getMaxUploadSize() + ") foi excedido ");
		}
	}

}
