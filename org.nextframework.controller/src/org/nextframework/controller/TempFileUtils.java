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

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.nextframework.core.standard.Next;
import org.nextframework.exception.NextException;

public class TempFileUtils {

	private TempFileUtils() {
	}

	public static java.io.File getApplicationTempFile(String subFolderName, String fileName) {
		return new java.io.File(getApplicationTempSubFolder(subFolderName), fileName);
	}

	public static java.io.File getApplicationTempSubFolder(String subFolderName) {
		return new java.io.File(getApplicationTempFolderPath(), subFolderName);
	}

	private static String getApplicationTempFolderPath() {
		return System.getProperty("java.io.tmpdir") + java.io.File.separator + Next.getApplicationName();
	}

	public static void writeObject(java.io.File file, Object value) throws IOException {
		java.io.File parentFile = file.getParentFile();
		if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs()) {
			throw new IOException("Não foi possível criar a pasta temporária: " + parentFile.getAbsolutePath());
		}
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(file));
		try {
			out.writeObject(value);
			out.flush();
		} finally {
			out.close();
		}
	}

	public static <T> T readObject(java.io.File file, Class<T> requiredType) {
		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
			try {
				return requiredType.cast(in.readObject());
			} finally {
				in.close();
			}
		} catch (IOException e) {
			throw new NextException("Não foi possível ler arquivo temporário [" + file.getAbsolutePath() + "]", e);
		} catch (ClassNotFoundException e) {
			throw new NextException("Não foi possível desserializar arquivo temporário [" + file.getAbsolutePath() + "]", e);
		}
	}

}
