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
package org.nextframework.bean.editors;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.types.File;
import org.springframework.beans.propertyeditors.ByteArrayPropertyEditor;
import org.springframework.web.multipart.MultipartFile;

/**
 * Editor para arquivo (upload/download).
 */
public class FileEditor extends ByteArrayPropertyEditor {

	protected final Log logger = LogFactory.getLog(getClass());

	public void setValue(Object value) {
		if (value instanceof MultipartFile) {
			MultipartFile multipartFile = (MultipartFile) value;
			long size = multipartFile.getSize();
			try {
				File file = createFile(value);
				String name = multipartFile.getOriginalFilename();
				name = name.replace('\\','/');
				if (name.contains("/")) {
					name = name.substring(name.indexOf('/'));
				}
				
				file.setName(name);
				file.setContenttype(multipartFile.getContentType());
				file.setContent(multipartFile.getBytes());
				file.setSize(size);
				super.setValue(file);
			} catch (IOException ex) {
				logger.error("Cannot read contents of multipart file", ex);
				throw new IllegalArgumentException("Cannot read contents of multipart file: " + ex.getMessage());
			}
		} else if(value instanceof File){
			super.setValue(value);
		}
	}

	protected File createFile(Object value) {
		throw new IllegalArgumentException("\n\nPara utilizar o editor de arquivos, " +
				"você deve extender a classe org.nextframework.spring.beans.editors.FileEditor e sobrescrever o método createFile.\n" +
				"O createFile sobrescrito deve criar um File específico da aplicacao.\n" +
				"Você deve registrar o editor criado, sobrescrevendo o método initBinder nos Controllers se quiser utilizar uploads de arquivos.\n" +
				"O método initBinder recebe um argumento binder que possui um método para registrar conversores (registerCustomEditor)");
	}

	public String getAsText() {
		byte[] value = (byte[]) getValue();
		return (value != null ? new String(value) : "");
	}

}
