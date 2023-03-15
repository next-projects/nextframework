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

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nextframework.service.ServiceFactory;
import org.nextframework.web.WebContext;

public class ResourceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private ResourceProvider delegate;

	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);

		//some containers do not use the same Thread for filter and servlet initialization
		WebContext.setServletContext(config.getServletContext());

		delegate = ServiceFactory.getService(ResourceProvider.class);
		delegate.init(config);

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		delegate.doGet(req, resp);
	}

	@Override
	protected long getLastModified(HttpServletRequest req) {
		return delegate.getLastModified(req);
	}

}
