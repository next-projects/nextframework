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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class GZIPResponseWrapper extends HttpServletResponseWrapper {

	private GZIPResponseStream gzipOutputStream = null;
	private PrintWriter printWriter = null;

	public GZIPResponseWrapper(HttpServletResponse response) throws IOException {
		super(response);
	}

	public void close() throws IOException {

		//PrintWriter.close does not throw exceptions.
		//Hence no try-catch block.
		if (this.printWriter != null) {
			this.printWriter.close();
		}

		if (this.gzipOutputStream != null) {
			this.gzipOutputStream.close();
		}

	}

	@Override
	public void flushBuffer() throws IOException {

		//PrintWriter.flush() does not throw exception
		if (this.printWriter != null) {
			this.printWriter.flush();
		}

		IOException exception1 = null;
		try {
			if (this.gzipOutputStream != null) {
				this.gzipOutputStream.flush();
			}
		} catch (IOException e) {
			exception1 = e;
		}

		IOException exception2 = null;
		try {
			super.flushBuffer();
		} catch (IOException e) {
			exception2 = e;
		}

		if (exception1 != null)
			throw exception1;
		if (exception2 != null)
			throw exception2;

	}

	@Override
	public ServletOutputStream getOutputStream() throws IOException {
		if (this.printWriter != null) {
			throw new IllegalStateException("PrintWriter obtained already - cannot get OutputStream");
		}
		if (this.gzipOutputStream == null) {
			this.gzipOutputStream = new GZIPResponseStream(getResponse().getOutputStream());
		}
		return this.gzipOutputStream;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		if (this.printWriter == null && this.gzipOutputStream != null) {
			throw new IllegalStateException("OutputStream obtained already - cannot get PrintWriter");
		}
		if (this.printWriter == null) {
			this.gzipOutputStream = new GZIPResponseStream(getResponse().getOutputStream());
			this.printWriter = new PrintWriter(new OutputStreamWriter(this.gzipOutputStream, getResponse().getCharacterEncoding()));
		}
		return this.printWriter;
	}

	@Override
	public void setContentLength(int len) {
		//ignore, since content length of zipped content
		//does not match content length of unzipped content.
	}

}
