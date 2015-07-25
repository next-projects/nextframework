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
package org.nextframework.core.standard;

import java.io.File;

import org.nextframework.exception.NextException;

/**
 * @author rogelgarcia
 * @since 21/01/2006
 * @version 1.1
 */
public class DefaultApplicationContext extends AbstractApplicationContext {
//	
//	Map<String, Object> attributes = new HashMap<String, Object>();
//	private ClassManager classManager;
//	
	private static String applicationName;
//
//	public DefaultApplicationContext() {
//	}
//
//	public ClassManager getClassManager() {
//		if(classManager == null){
//			//classManager = StandardClassRegister.getClassManager();
//			classManager = ClassManagerFactory.getClassManager();
//		}
//		return classManager;
//	}
//
//	public void setAttribute(String s, Object value) {
//		attributes.put(s, value);
//	}
//
//	public Object getAttribute(String s) {
//		return attributes.get(s);
//	}
//
	public String getApplicationName() {
		if(applicationName == null){
			throw new NextException("Application name is null. Use DefaultApplicationContext.setApplicationName(...)");
		}
		return applicationName;
	}

	@Override
	public String getApplicationDir() {
		return System.getProperty("user.home") 
				+ File.separator + ".appData" 
				+ File.separator + getApplicationName();
	}

	public static void setApplicationName(String appName){
		applicationName = appName;
	}

}
