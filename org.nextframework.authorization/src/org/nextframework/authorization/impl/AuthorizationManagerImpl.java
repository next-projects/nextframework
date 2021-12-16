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
package org.nextframework.authorization.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.nextframework.authorization.Authorization;
import org.nextframework.authorization.AuthorizationDAO;
import org.nextframework.authorization.AuthorizationManager;
import org.nextframework.authorization.AuthorizationModule;
import org.nextframework.authorization.HasAccessAuthorizationModule;
import org.nextframework.authorization.Permission;
import org.nextframework.authorization.PermissionLocator;
import org.nextframework.authorization.RequiresAuthenticationAuthorizationModule;
import org.nextframework.authorization.ResourceAuthorizationMapper;
import org.nextframework.authorization.Role;
import org.nextframework.authorization.User;
import org.nextframework.authorization.UserAuthorization;
import org.nextframework.authorization.UserLocator;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.AntPathMatcher;

/**
 */
public class AuthorizationManagerImpl implements AuthorizationManager {

	protected Properties authorizationProperties = new Properties();
	protected UserLocator userLocator;
	protected PermissionLocator permissionLocator;
	protected ResourceAuthorizationMapper authorizationMapper;
	protected AuthorizationDAO authorizationDAO;

	public void setAuthorizationDAO(AuthorizationDAO authorizationDAO) {
		this.authorizationDAO = authorizationDAO;
	}

	public void setPermissionLocator(PermissionLocator permissionLocator) {
		this.permissionLocator = permissionLocator;
	}

	public void setUserLocator(UserLocator userLocator) {
		this.userLocator = userLocator;
	}

	public void setAuthorizationMapper(ResourceAuthorizationMapper authorizationMapper) {
		this.authorizationMapper = authorizationMapper;
	}

	public UserLocator getUserLocator() {
		return userLocator;
	}

	public PermissionLocator getPermissionLocator() {
		return permissionLocator;
	}

	public ResourceAuthorizationMapper getAuthorizationMapper() {
		return authorizationMapper;
	}

	public boolean isAuthorized(String path, String actionParameter) {
		init();
		User user = userLocator.getUser();
		return isAuthorized(path, actionParameter, user);
	}

	public boolean isAuthorized(String path, String actionParameter, User usuario) {
		init();
		// pega o localizadorControl no contexto do framework
		AuthorizationModule authorizationModule = authorizationMapper.getAuthorizationModule(path);
		return isAuthorized(actionParameter, usuario, path, authorizationModule, false);
	}

	public boolean isAuthorized(String actionParameter, User usuario, String resource, AuthorizationModule authorizationModule) {
		init();
		return isAuthorized(actionParameter, usuario, resource, authorizationModule, false);
	}

	private boolean isAuthorized(String actionParameter, User user, String resource, AuthorizationModule authorizationModule, boolean saveAuthorization) {
		init();
		boolean isAuthorized;
		//first check static authorization
		isAuthorized = checkStaticAuthorization(user, resource, actionParameter, authorizationModule);

		if (!isAuthorized) { //if the static authorization has denied access.. let's not check the 
			return false;
		}
		//check dynamic authorization
		if (authorizationModule == null || authorizationModule instanceof HasAccessAuthorizationModule) {
			isAuthorized = true;
		} else if (authorizationModule instanceof RequiresAuthenticationAuthorizationModule && user == null) {
			isAuthorized = false;
		} else if (authorizationModule instanceof RequiresAuthenticationAuthorizationModule && user != null) {
			isAuthorized = true;
		} else {
			UserAuthorization autorizacao = createAuthorization(user, resource, authorizationModule);
			if (authorizationModule.isAuthorized(actionParameter, autorizacao)) {
				isAuthorized = true;
			} else {
				isAuthorized = false;
			}
			if (saveAuthorization) {
				//salva a autorizacao na requisicao
				//TODO FIXME NOT DOESNT WORK ANYMORE
				//Next.getRequestContext().setAttribute(Authorization.AUTHORIZATION_ATTRIBUTE, autorizacao);
			}
		}
		return isAuthorized;
	}

	protected boolean initialized = false;

	protected void init() {
		if (!initialized) {
			//get the dependencies
			if (userLocator == null) {
				userLocator = Authorization.getUserLocator();
			}
			if (permissionLocator == null) {
				permissionLocator = Authorization.getPermissionLocator();
			}
			if (authorizationMapper == null) {
				authorizationMapper = Authorization.getAuthorizationMapper();
			}
			if (authorizationDAO == null) {
				authorizationDAO = Authorization.getAuthorizationDAO();
			}
			initStaticAuthorization();
			initialized = true;
		}
	}

	private void initStaticAuthorization() {
		try {
			authorizationProperties = PropertiesLoaderUtils.loadAllProperties("authorization.properties");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected Map<String, List<String>> cacheResourceRoles = new HashMap<String, List<String>>();

	protected boolean checkStaticAuthorization(User user, String resource, String actionParameter, AuthorizationModule authorizationModule) {
		List<String> roles = cacheResourceRoles.get(resource);
		if (roles == null) {
			roles = new ArrayList<String>();
			cacheResourceRoles.put(resource, roles);
			AntPathMatcher antPathMatcher = new AntPathMatcher();
			@SuppressWarnings("all")
			Enumeration<String> keys = (Enumeration<String>) authorizationProperties.propertyNames();
			while (keys.hasMoreElements()) {
				String resourceKey = keys.nextElement();
				if (antPathMatcher.match(resourceKey, resource)) {
					String rolesValue = authorizationProperties.getProperty(resourceKey);
					roles.addAll(Arrays.asList(rolesValue.split(",")));
				}
			}
		}
		if (roles.isEmpty()) {
			return true;
		}
		if (user == null) {
			return false;
		}
		Role[] userRoles = authorizationDAO.findUserRoles(user);
		for (Role role : userRoles) {
			if (roles.contains(role.getName().toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	private UserAuthorization createAuthorization(User usuario, String resource, AuthorizationModule authorizationModule) {
		Permission[] permissoes;
		if (usuario != null) {
			permissoes = permissionLocator.getPermissions(usuario, resource);
		} else {
			permissoes = new Permission[0];
		}
		UserAuthorization autorizacao = authorizationModule.createAuthorization(permissoes);
		return autorizacao;
	}

}
