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



/**
 * @author rogelgarcia
 * @since 21/01/2006
 * @version 1.1
 */
public interface RequestContext {

	ApplicationContext getApplicationContext();
	
	Message[] getMessages();
	void addMessage(Object source);
	void addMessage(Object source, MessageType type);
	void addError(Object source);
	void clearMessages();
	
	String getParameter(String parameter);

	void setAttribute(String name, Object value);
	Object getAttribute(String name);
	
	void setUserAttribute(String name, Object value);
	Object getUserAttribute(String name);
	
	/**
	 * Sets an user attribute that must be persistent.
	 * 
	 * It is advisable to create full qualified keys, that is, a key 
	 * that represents both the use case and the use case variable.
	 * Example: Instead of putting 'configuration' key, 
	 * put 'my.use.case.configuration' in
	 * order to avoid conflicts. 
	 * 
	 * There must be an user logged in.
	 * 
	 * @param name
	 * @param value
	 */
	void setUserPersistentAttribute(String name, String value);
	/**
	 * Reads a persistent attribute.
	 * 
	 * There must be an user logged in.
	 * 
	 * @param name
	 * @return
	 */
	String getUserPersistentAttribute(String name);
	
//	User getUser();
	
//	boolean hasRole(String role);
	
}
