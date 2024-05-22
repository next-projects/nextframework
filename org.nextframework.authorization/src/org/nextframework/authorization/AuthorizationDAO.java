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

import java.util.Map;

/**
 * Interface that represents access to authorization data.
 * 
 * All persistent data the framework requires will be loaded through 
 * an implementation of this class.
 *  
 * @author rogelgarcia
 * @since 2006-01-22
 * @version 1.1
 */
public interface AuthorizationDAO {

	public User findUserByUsername(String username);

	public Role[] findUserRoles(User user);

	public Permission findPermission(Role role, String controlName);

	public Permission savePermission(String controlName, Role role, Map<String, String> permissionMap);

	/**
	 * Returns all the available roles on the system
	 * @return
	 */
	public Role[] findAllRoles();

	/**
	 * Returns the last time the role authorization has been updated.<BR>
	 * This information can be used to clear cache of data based on the role authorization.<BR>
	 * The implementation of role must implement equals and hashcode.
	 * @param role
	 * @return
	 */
	long getLastUpdateTime(Role role);

	/**
	 * Returns the last time the some authorization has been modified.<BR>
	 * This information can be used to clear cache of data based on authorization.
	 * @return
	 */
	long getLastUpdateTime();

}
