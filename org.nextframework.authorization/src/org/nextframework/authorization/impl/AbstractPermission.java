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
import java.util.Iterator;
import java.util.Map;

import javax.persistence.Transient;

import org.nextframework.authorization.Permission;

public abstract class AbstractPermission implements Permission {

	@Transient
	public Map<String, String> getPermissionMap() {
		String permissionString = getPermissionString();
		if(permissionString == null){
			throw new NullPointerException("getPermissionString returned null. Verify implementation: "+this.getClass().getName());
		}
		Map<String, String> permissionMap = new HashMap<String, String>();
		String[] permissoes = permissionString.split(";");
		for (int i = 0; i < permissoes.length; i++) {
			if(permissoes[i] != null && permissoes[i].length() > 0){
				String[] map = permissoes[i].split("=");
				String key = map[0];
				String value = map[1];
				permissionMap.put(key, value);
			}
		}
		return permissionMap;
	}

	public void setPermissionMap(Map<String, String> map) {
		//this.permissionMap = map;
		StringBuilder builder = new StringBuilder();
		for (Iterator<String> iter = map.keySet().iterator(); iter.hasNext();) {
			String id = iter.next();
			builder.append(id);
			builder.append("=");
			builder.append(map.get(id));
			if (iter.hasNext()) {
				builder.append(";");
			}
		}
		setPermissionString(builder.toString());
	}
	
	@Transient
	public String getPermissionValue(String id) {
		return (String) getPermissionMap().get(id);
	}
		
	@Override
	public String toString() {
		return "Abstract Permission: "+getPermissionString();
	}
	
	public abstract void setPermissionString(String string);
	public abstract String getPermissionString();

	
}
