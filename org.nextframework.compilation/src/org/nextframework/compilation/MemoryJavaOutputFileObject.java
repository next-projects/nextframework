/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2012 the original author or authors.
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
package org.nextframework.compilation;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URI;

import javax.tools.SimpleJavaFileObject;

/**
 * @author rogelgarcia
 */
public class MemoryJavaOutputFileObject extends SimpleJavaFileObject {
	
	ByteArrayOutputStream array = new ByteArrayOutputStream();
	PrintWriter printWriter;
    String className;

	public MemoryJavaOutputFileObject(URI uri, Kind kind, String className) {
		super(uri, kind);
		this.className = className;
	}
	
	public String getClassName() {
		return className;
	}
	

	@Override
	public Writer openWriter() throws IOException {
		printWriter = new PrintWriter(array);
		return printWriter;
	}
	
	@Override
	public InputStream openInputStream() throws IOException {
		return new ByteArrayInputStream(toByteArray());
	}
	
	@Override
	public OutputStream openOutputStream() throws IOException {
		return array;
	}

	public byte[] toByteArray() {
		return array.toByteArray();
	}

	public void flush() {
		try {
			array.flush();
		} catch (IOException e) {
		}
		if(printWriter != null){
			printWriter.flush();
		}
	}

}
