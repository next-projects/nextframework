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
package org.nextframework.rtf;

import java.util.HashMap;
import java.util.Map;

public class RTF {

	String name;
	String fileName;
	Map<String, String> parameterMap = new HashMap<String, String>();
	
	public RTF(String name) {
		super();
		this.name = name;
		fileName = name;
	}
	
	public void addParameter(String key, String value){
		parameterMap.put(key, value);
	}
	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		if(fileName == null){
			fileName = name;
		}
		this.name = name;
	}

	public Map<String, String> getParameterMap() {
		return parameterMap;
	}
}
