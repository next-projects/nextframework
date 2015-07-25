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
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nextframework.context.UserPersistentDataProvider;
import org.nextframework.core.standard.Message;
import org.nextframework.core.standard.MessageType;
import org.nextframework.service.ServiceFactory;
import org.nextframework.web.WebContext;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.validation.BindException;

/**
 * @author rogelgarcia | marcusabreu
 * @since 21/01/2006
 * @version 1.1
 */
public class DefaultWebRequestContext implements WebRequestContext {
	
	public static final String ACTION_PARAMETER = "ACTION";
	
	@Deprecated
	public static final String USER_ATTRIBUTE = "USER"; //TODO REFACTOR USE USER LOCATOR

	protected String lastAction;
	protected HttpServletRequest httpServletRequest;
	protected HttpServletResponse httpServletResponse;
	protected String requestQuery;
	
	protected List<Message> messages = null;
	
	protected BindException bindException = new BindException(new Object(), "");

	private WebApplicationContext applicationContext;

	private String servletPath;

	private String pathInfo;
	
	public DefaultWebRequestContext(HttpServletRequest request, HttpServletResponse response, WebApplicationContext applicationContext) {
		super();
		httpServletRequest = request;
		httpServletResponse = response;
		this.applicationContext = applicationContext;
		pathInfo = httpServletRequest.getPathInfo();
		if(pathInfo == null){
			pathInfo = "";
		}
		servletPath = httpServletRequest.getServletPath();
		messages = getSessionMessages();
		getRequestQuery();//inicializar o request query
	}
	
	private List<Message> getSessionMessages() {
		HttpSession session = httpServletRequest.getSession();
		@SuppressWarnings("unchecked")
		List<Message> attribute = (List<Message>)session.getAttribute("_MESSAGES");
		if(attribute == null){
			attribute = new ArrayList<Message>();
			session.setAttribute("_MESSAGES", attribute);
		}
		return attribute;
	}

	public String getRequestQuery() {
		if(requestQuery == null){
			requestQuery = httpServletRequest.getServletPath() + (httpServletRequest.getPathInfo() != null ? httpServletRequest.getPathInfo() : "");
		}
		return requestQuery;
	}
	

	@Override
	public String getRequestModule() {
		String requestURI = getFirstRequestUrl();
		return requestURI.substring(0, requestURI.indexOf('/', 1));
	}	
	
	//TODO REFACTOR MOVE TO UTIL CLASS
	public static String getController() {
		HttpServletRequest httpServletRequest = WebContext.getRequest();
		return httpServletRequest.getServletPath() + (httpServletRequest.getPathInfo() != null ? httpServletRequest.getPathInfo() : "");
	}
	
	public static String getAction() {
		HttpServletRequest httpServletRequest = WebContext.getRequest();
		return httpServletRequest.getParameter(ACTION_PARAMETER);
	}

	public String getParameter(String parameter) {
		return httpServletRequest.getParameter(parameter);
	}

	public void setAttribute(String name, Object value) {
		httpServletRequest.setAttribute(name, value);
	}

	public Principal getUserPrincipal() {
		return httpServletRequest.getUserPrincipal();
	}

//	public User getUser() {
//		return (User) httpServletRequest.getSession().getAttribute(USER_ATTRIBUTE);
//	}
//
//	public void setUser(User user) {
//		httpServletRequest.getSession().setAttribute(USER_ATTRIBUTE, user);
//	}

	public boolean hasRole(String role) {
		return httpServletRequest.getSession().getAttribute("ROLE_" + role.toUpperCase()) != null;
	}

	public HttpServletRequest getServletRequest() {
		return httpServletRequest;
	}

	public HttpServletResponse getServletResponse() {
		return httpServletResponse;
	}

	public Object getAttribute(String name) {
		return httpServletRequest.getAttribute(name);
	}

	public String getFirstRequestUrl() {
		return servletPath+pathInfo;
	}

	public WebApplicationContext getWebApplicationContext() {
		return NextWeb.getWebApplicationContext(httpServletRequest.getSession().getServletContext());
	}

	public HttpSession getSession() {
		return httpServletRequest.getSession();
	}

	public BindException getBindException() {
		return bindException;
	}

	public void setBindException(BindException bindException) {
		this.bindException = bindException;
	}

	public Message[] getMessages() {
		return messages.toArray(new Message[messages.size()]);
	}
	
	public void addAllMessages(List<Message> messageList){
		messages.addAll(messageList);
	}

	public void addMessage(Object source) {
		messages.add(new Message(MessageType.INFO, source));
	}
	
	public void addError(Object source) {
		messages.add(new Message(MessageType.ERROR, source));
	}

	public void clearMessages(){
		messages.clear();
	}
	
	public void addMessage(Object source, MessageType type) {
		messages.add(new Message(type, source));
	}

	public String getLastAction() {
		if(lastAction == null){
			lastAction = httpServletRequest.getParameter(ACTION_PARAMETER);
		}
		return lastAction;
	}

	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
	}

	
	public WebApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public Object getUserAttribute(String name) {
		return httpServletRequest.getSession().getAttribute(name);
	}

	public void setUserAttribute(String name, Object value) {
		httpServletRequest.getSession().setAttribute(name, value);
	}

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
	public void setUserPersistentAttribute(String name, String value) {
		getUserMap().put(name, value);
	}


	@Override
	public String getUserPersistentAttribute(String name) {
		return getUserMap().get(name);
	}
	
	public Map<String, String> getUserMap() {
		String username = getUserName();
		if(username == null){
			return new HashMap<String, String>(); //TODO.. when the user is not logged in.. persistent map does not work
		}
		return ServiceFactory.getService(UserPersistentDataProvider.class)
								.getUserMap(username);
	}
	
	public String getUserName() {
		//TODO CREATE A SERVICE TO PROVIDE THIS INFORMATION
		Object userObject = getSession().getAttribute(USER_ATTRIBUTE);
		if(userObject == null){
			return null;
		}
		String username = (String) PropertyAccessorFactory
										.forBeanPropertyAccess(userObject)
										.getPropertyValue("username");
		return username;
	}	
}
