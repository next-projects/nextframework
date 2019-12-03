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
package org.nextframework.core.web;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nextframework.context.UserPersistentDataProvider;
import org.nextframework.core.standard.Message;
import org.nextframework.core.standard.MessageType;
import org.nextframework.message.MessageResolver;
import org.nextframework.message.MessageResolverFactory;
import org.nextframework.service.ServiceFactory;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

/**
 * @author rogelgarcia | marcusabreu
 * @since 21/01/2006
 * @version 1.1
 */
public class DefaultWebRequestContext implements WebRequestContext {

	public static final String ACTION_PARAMETER = "ACTION";

	@Deprecated
	public static final String USER_ATTRIBUTE = "USER"; //TODO REFACTOR USE USER LOCATOR

	protected HttpServletRequest httpServletRequest;
	protected HttpServletResponse httpServletResponse;
	private WebApplicationContext applicationContext;

	private String servletPath;
	private String pathInfo;
	protected String requestQuery;
	protected String lastAction;

	private TimeZone timeZone;
	private Locale locale;

	protected List<Message> messages = null;

	protected BindException bindException = new BindException(new Object(), "");

	public DefaultWebRequestContext(HttpServletRequest request, HttpServletResponse response, WebApplicationContext applicationContext) {

		this.httpServletRequest = request;
		this.httpServletResponse = response;
		this.applicationContext = applicationContext;

		this.servletPath = httpServletRequest.getServletPath();
		this.pathInfo = httpServletRequest.getPathInfo();
		if (this.pathInfo == null) {
			this.pathInfo = "";
		}

		initRequestQuery();

		initLocation(request);

		this.messages = getSessionMessages();

	}

	public void initRequestQuery() {
		requestQuery = httpServletRequest.getServletPath() + (httpServletRequest.getPathInfo() != null ? httpServletRequest.getPathInfo() : "");
	}

	public void initLocation(HttpServletRequest request) {
		LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
		if (localeResolver instanceof LocaleContextResolver) {
			LocaleContext localeContext = ((LocaleContextResolver) localeResolver).resolveLocaleContext(request);
			this.locale = localeContext.getLocale();
			if (localeContext instanceof TimeZoneAwareLocaleContext) {
				this.timeZone = ((TimeZoneAwareLocaleContext) localeContext).getTimeZone();
			}
		} else if (localeResolver != null) {
			this.locale = localeResolver.resolveLocale(request);
		}
	}

	@SuppressWarnings("unchecked")
	private List<Message> getSessionMessages() {
		HttpSession session = httpServletRequest.getSession();
		List<Message> attribute = (List<Message>) session.getAttribute("_MESSAGES");
		if (attribute == null) {
			attribute = new ArrayList<Message>();
			session.setAttribute("_MESSAGES", attribute);
		}
		return attribute;
	}

	@Override
	public String getParameter(String parameter) {
		return httpServletRequest.getParameter(parameter);
	}

	@Override
	public void setAttribute(String name, Object value) {
		httpServletRequest.setAttribute(name, value);
	}

	@Override
	public Principal getUserPrincipal() {
		return httpServletRequest.getUserPrincipal();
	}

	public boolean hasRole(String role) {
		return httpServletRequest.getSession().getAttribute("ROLE_" + role.toUpperCase()) != null;
	}

	@Override
	public HttpServletRequest getServletRequest() {
		return httpServletRequest;
	}

	@Override
	public HttpServletResponse getServletResponse() {
		return httpServletResponse;
	}

	@Override
	public Object getAttribute(String name) {
		return httpServletRequest.getAttribute(name);
	}

	@Override
	public String getFirstRequestUrl() {
		return servletPath + pathInfo;
	}

	@Override
	public WebApplicationContext getWebApplicationContext() {
		return NextWeb.getWebApplicationContext(httpServletRequest.getSession().getServletContext());
	}

	@Override
	public HttpSession getSession() {
		return httpServletRequest.getSession();
	}

	@Override
	public BindException getBindException() {
		return bindException;
	}

	public void setBindException(BindException bindException) {
		this.bindException = bindException;
	}

	@Override
	public TimeZone getTimeZone() {
		return timeZone;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public MessageResolver getMessageResolver() {
		return MessageResolverFactory.get(locale);
	}

	@Override
	public void addMessage(Object source) {
		messages.add(new Message(MessageType.INFO, source));
	}

	@Override
	public void addMessage(Object source, MessageType type) {
		messages.add(new Message(type, source));
	}

	@Override
	public void addError(Object source) {
		messages.add(new Message(MessageType.ERROR, source));
	}

	@Override
	public void addMessage(Message message) {
		messages.add(message);
	}

	@Override
	public void addAllMessages(List<Message> messageList) {
		messages.addAll(messageList);
	}

	@Override
	public Message[] getMessages() {
		return messages.toArray(new Message[messages.size()]);
	}

	@Override
	public void clearMessages() {
		messages.clear();
	}

	@Override
	public String getLastAction() {
		if (lastAction == null) {
			lastAction = httpServletRequest.getParameter(ACTION_PARAMETER);
		}
		return lastAction;
	}

	@Override
	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
	}

	@Override
	public WebApplicationContext getApplicationContext() {
		return applicationContext;
	}

	@Override
	public Object getUserAttribute(String name) {
		return httpServletRequest.getSession().getAttribute(name);
	}

	@Override
	public void setUserAttribute(String name, Object value) {
		httpServletRequest.getSession().setAttribute(name, value);
	}

	@Override
	public String getContextPath() {
		return getServletRequest().getContextPath();
	}

	@Override
	public String getServletPath() {
		return servletPath;
	}

	@Override
	public String getPathInfo() {
		return pathInfo;
	}

	@Override
	public String getRequestQuery() {
		if (requestQuery == null) {
			initRequestQuery();
		}
		return requestQuery;
	}

	@Override
	public void setUserPersistentAttribute(String name, String value) {
		getUserMap().put(name, value);
	}

	@Override
	public String getUserPersistentAttribute(String name) {
		return getUserMap().get(name);
	}

	public Map<String, String> getUserMap() {
		String username = getUserName();
		if (username == null) {
			return new HashMap<String, String>(); //TODO.. when the user is not logged in.. persistent map does not work
		}
		return ServiceFactory.getService(UserPersistentDataProvider.class).getUserMap(username);
	}

	public String getUserName() {
		//TODO CREATE A SERVICE TO PROVIDE THIS INFORMATION
		Object userObject = getSession().getAttribute(USER_ATTRIBUTE);
		if (userObject == null) {
			return null;
		}
		String username = (String) PropertyAccessorFactory.forBeanPropertyAccess(userObject).getPropertyValue("username");
		return username;
	}

}
