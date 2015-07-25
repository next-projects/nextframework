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


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.authorization.AuthorizationDAO;
import org.nextframework.authorization.Permission;
import org.nextframework.authorization.Role;
import org.nextframework.authorization.User;

/**
 * @author rogelgarcia
 * @since 2006-01-22
 * @version 1.1
 */
public abstract class AbstractAuthorizationDAO implements AuthorizationDAO {
	
	protected static Log logger = LogFactory.getLog(AbstractAuthorizationDAO.class);
	
	private Map<Role, Long> updateTimes = new HashMap<Role, Long>();
	private long lastUpdateTime = System.currentTimeMillis();

	public Role[] findUserRoles(User user) {
		return new Role[0];
	}

	public Permission findPermission(Role role, String controlName) {
		return null;
	}

	public Permission savePermission(final String controlName, final Role role, final Map<String, String> permissionMap) {
		lastUpdateTime = System.currentTimeMillis();
		updateTimes.put(role, System.currentTimeMillis());
		return new Permission(){

			public Role getRole() {
				return role;
			}

			public Map<String, String> getPermissionMap() {
				return permissionMap;
			}

			public String getPermissionValue(String id) {
				return getPermissionMap().get(id);
			}};
	}

	public Role[] findAllRoles() {
		return new Role[0];
	}

	@Override
	public long getLastUpdateTime(Role role) {
		Long time;
		if((time = updateTimes.get(role)) != null){
			return time;
		}
		return System.currentTimeMillis();
	}

	@Override
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}

}
