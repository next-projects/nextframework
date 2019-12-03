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
package org.nextframework.core.web;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nextframework.core.standard.RequestContext;
import org.springframework.validation.BindException;

/**
 * @author rogelgarcia
 * @since 21/01/2006
 * @version 1.1
 */
public interface WebRequestContext extends RequestContext {

	String REQUEST_CONTEXT_ATTRIBUTE = WebRequestContext.class.getName();

	Principal getUserPrincipal();

	HttpServletRequest getServletRequest();

	HttpServletResponse getServletResponse();

	HttpSession getSession();

	String getContextPath();

	String getRequestQuery();

	String getFirstRequestUrl();

	WebApplicationContext getWebApplicationContext();

	BindException getBindException();

	/**
	 * Informa qual o ultimo parametro ACTION foi pedido
	 * Quando é feito um redirecionamento por qualquer motivo esse valor é atualizado
	 * @return
	 */
	String getLastAction();

	void setLastAction(String action);

	String getServletPath();

	String getPathInfo();

}