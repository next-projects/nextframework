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

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author rogelgarcia
 * @since 13/07/2006
 * @version 1.0
 */
public class DefaultRequestContext implements RequestContext {

	private ApplicationContext applicationContext;

	public DefaultRequestContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public TimeZone getTimeZone() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Locale getLocale() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addMessage(Object source, MessageType type) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addMessage(Object source) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addWarn(Object source) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addError(Object source) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addMessage(Message message) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void addAllMessages(List<Message> messageList) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Message[] getMessages() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clearMessages() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getParameter(String parameter) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setAttribute(String name, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getAttribute(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getUserAttribute(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setUserAttribute(String name, Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setUserPersistentAttribute(String name, String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getUserPersistentAttribute(String name) {
		throw new UnsupportedOperationException();
	}

}
