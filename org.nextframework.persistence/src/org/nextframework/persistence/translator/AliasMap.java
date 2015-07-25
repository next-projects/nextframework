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
package org.nextframework.persistence.translator;

import java.util.Set;


public class AliasMap {

	String alias;
	String path;
	Class<?> type;
	Class<?> collectionType;
	int pkPropertyIndex = -1;
	Set<AliasMap> dependencias;
	
	public AliasMap(String alias, String path, Class<?> type) {
		this.alias = alias;
		this.path = path;
		this.type = type;
	}

	public AliasMap(String alias, String path, Class<?> type, Class<?> collectionType) {
		this.alias = alias;
		this.path = path;
		this.type = type;
		this.collectionType = collectionType;
	}

	public String getAlias() {
		return alias;
	}
	
	public String getPath() {
		return path;
	}
	
	public Class<?> getType() {
		return type;
	}
	
	public void setAlias(String alias) {
		this.alias = alias;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setType(Class<?> type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "alias: "+alias+"   type:"+type.getName()+"   path: "+path;
	}

	
	public Class<?> getCollectionType() {
		return collectionType;
	}

	
	public int getPkPropertyIndex() {
		return pkPropertyIndex;
	}

	
	public void setCollectionType(Class<?> collectionType) {
		this.collectionType = collectionType;
	}

	
	public void setPkPropertyIndex(int pkPropertyIndex) {
		this.pkPropertyIndex = pkPropertyIndex;
	}
}
