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
/*
 * Criado em 22/03/2005
 *
 */
package org.nextframework.authorization.web.impl;

import java.security.Principal;

import org.nextframework.authorization.AuthorizationDAO;
import org.nextframework.authorization.User;
import org.nextframework.authorization.UserLocator;
import org.nextframework.service.ServiceFactory;
import org.nextframework.web.WebContext;

import jakarta.servlet.http.HttpServletRequest;

/**
 * User Locator Service default implementation for Web applications.<BR>
 * Save and loads the current user from the http session 'USER' attribute.<BR>
 * If a principal is found in the current request, it will try to load the respective User object with the AuthorizationDAO. 
 * 
 * @author rogelgarcia
 */
public class WebUserLocator implements UserLocator {

	private static final String USER_ATTRIBUTE = "USER";

	protected AuthorizationDAO authorizationDAO;

	public AuthorizationDAO getAuthorizationDAO() {
		if (authorizationDAO == null) {
			authorizationDAO = ServiceFactory.getService(AuthorizationDAO.class);
		}
		return authorizationDAO;
	}

	public void setAuthorizationDAO(AuthorizationDAO authorizationDAO) {
		this.authorizationDAO = authorizationDAO;
	}

	public static User getSessionUser(HttpServletRequest request) {
		return (User) request.getSession().getAttribute(USER_ATTRIBUTE);
	}

	public static void setSessionUser(HttpServletRequest servletRequest, User user) {
		servletRequest.getSession().setAttribute(USER_ATTRIBUTE, user);
	}

	public User getUser() {
		HttpServletRequest servletRequest = WebContext.getRequest();
		User user = getSessionUser(servletRequest);
		if (user == null) {
			Principal userPrincipal = servletRequest.getUserPrincipal();
			if (userPrincipal != null) {
				String username = userPrincipal.getName();
				if (username != null) {
					AuthorizationDAO localAuthorizationDAO = getAuthorizationDAO();
					if (localAuthorizationDAO != null) {
						user = localAuthorizationDAO.findUserByUsername(username);
					}
					if (user == null) {
						throw new RuntimeException("Cannot load user: " + username);
					}
				} else {
					// O CÓDIGO PROVAVELMENTE NÃO CHEGARÁ AQUI
					// userPrincipal.getName() não deve retornar null
					throw new RuntimeException("Erro inesperado: Algoritmo inválido em UserLocatorImpl");
				}
			} else {
				//NINGUEM LOGADO
				user = null;
			}
			//TODO TROCAR PARA DEFAULTREQUESTCONTEXT QUANDO TIVER
			setSessionUser(servletRequest, user);
		}
		return user;
	}

}
