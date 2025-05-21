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
package org.nextframework.report;

import java.io.InputStream;

import javax.servlet.ServletContext;

/**
 * @author rogelgarcia
 * @since 22/01/2006
 * @version 1.1
 */
public class ReportNameResolverImpl implements ReportNameResolver {

	protected String prefix;
	protected String suffix;
	protected ServletContext servletContext;

	public ReportNameResolverImpl(String prefix, String suffix, ServletContext context) {
		this.prefix = prefix;
		this.suffix = suffix;
		servletContext = context;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public InputStream resolveName(String name) {
		InputStream resourceAsStream = servletContext.getResourceAsStream(getPrefix() + name + getSuffix());
		if (resourceAsStream == null) {
			throw new NullPointerException("Relatório não encontrado! " + name);
		}
		return resourceAsStream;
	}

}
