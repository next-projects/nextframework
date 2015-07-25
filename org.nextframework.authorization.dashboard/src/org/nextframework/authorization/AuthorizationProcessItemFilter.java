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
package org.nextframework.authorization;

import java.util.HashMap;
import java.util.Map;

public class AuthorizationProcessItemFilter {

	String path;
	String description;
	AuthorizationModule authorizationModule;
	Map<String, String> permissionMap = new HashMap<String, String>();
	
	public Map<String, String> getPermissionMap() {
		return permissionMap;
	}
	public void setPermissionMap(Map<String, String> authorizationItemMap) {
		this.permissionMap = authorizationItemMap;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public AuthorizationModule getAuthorizationModule() {
		return authorizationModule;
	}
	public void setAuthorizationModule(AuthorizationModule authorizationModule) {
		this.authorizationModule = authorizationModule;
	}
}
