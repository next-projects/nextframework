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
package org.nextframework.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.nextframework.context.ResourceHandlerMap;
import org.nextframework.controller.json.NextMappingJackson2HttpMessageConverter;
import org.nextframework.web.service.ServletContextServiceProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author rogelgarcia
 * @since 22/01/2006
 * @version 1.1
 */
public class NextDispatcherServlet extends org.springframework.web.servlet.DispatcherServlet {

	public static final String ACTUAL_SERVLET_NAME = "ACTUAL_SERVLET_NAME";
	private static final long serialVersionUID = 1L;

	public NextDispatcherServlet() {
	}

	@Override
	protected void onRefresh(ApplicationContext context) {
		super.onRefresh(context);
		DispatcherServletResourceHandlerMap handlerMap = (DispatcherServletResourceHandlerMap) ServletContextServiceProvider.getService(getServletContext(), ResourceHandlerMap.class);
		if (handlerMap == null) {
			handlerMap = new DispatcherServletResourceHandlerMap();
			ServletContextServiceProvider.registerService(getServletContext(), ResourceHandlerMap.class, handlerMap);
		}
		handlerMap.servlets.add(this);
	}

	private static class DispatcherServletResourceHandlerMap implements ResourceHandlerMap {

		private List<NextDispatcherServlet> servlets = new ArrayList<>();
		private Map<String, Object> handlerMap = new HashMap<String, Object>();
		private Map<String, Boolean> authenticationModuleCache = new HashMap<String, Boolean>();

		@Override
		public Object getHandler(String resource) {
			init();
			return handlerMap.get(resource);
		}

		@Override
		public Set<String> getHandlerNames() {
			init();
			return handlerMap.keySet();
		}

		@SuppressWarnings("unchecked")
		public void init() {
			if (handlerMap.isEmpty()) {
				for (NextDispatcherServlet servlet : servlets) {
					List<HandlerMapping> handlers = (List<HandlerMapping>) PropertyAccessorFactory.forDirectFieldAccess(servlet).getPropertyValue("handlerMappings");
					for (HandlerMapping handlerMapping : handlers) {
						if (handlerMapping instanceof AbstractUrlHandlerMapping) {
							AbstractUrlHandlerMapping abstractUrlHandlerMapping = (AbstractUrlHandlerMapping) handlerMapping;
							Map<String, Object> handlerMap = abstractUrlHandlerMapping.getHandlerMap();
							for (Entry<String, Object> entry : handlerMap.entrySet()) {
								this.handlerMap.put("/" + servlet.getServletName() + entry.getKey(), entry.getValue());
							}
						}

					}
				}
			}
		}

		@Override
		public boolean isAuthenticationRequired(String resource) {
			String module = resource.substring(1, resource.indexOf('/', 1));
			if (!authenticationModuleCache.containsKey(module)) {
				boolean result = false;
				for (NextDispatcherServlet servlet : servlets) {
					if (servlet.getServletName().equals(module) && servlet.isSecured()) {
						result = true;
						break;
					}
				}
				authenticationModuleCache.put(module, result);
			}
			return authenticationModuleCache.get(module);
		}

	}

	private boolean isSecured() {
		return "true".equalsIgnoreCase(this.getInitParameter("secured"));
	}

	@SuppressWarnings("all")
	@Override
	protected List getDefaultStrategies(ApplicationContext context, Class strategyInterface) throws BeansException {
		List defaultStrategies = super.getDefaultStrategies(context, strategyInterface);
		if (HandlerMapping.class.isAssignableFrom(strategyInterface)) {
			Object handlerMapping = createDefaultStrategy(context, NextAnnotationHandlerMapping.class);
			defaultStrategies.add(0, handlerMapping);
		}
		if (ViewResolver.class.isAssignableFrom(strategyInterface)) {
			Object viewResolver = createDefaultStrategy(context, NextCompositeViewResolver.class);
			defaultStrategies.add(0, viewResolver);
		}
		return defaultStrategies;
	}

	@Override
	// Removido @SuppressWarnings("deprecation") pois as novas classes não estão obsoletas
	protected Object createDefaultStrategy(ApplicationContext context, Class<?> clazz) {

		getServletContext().setAttribute(ACTUAL_SERVLET_NAME, getServletName());
		Object strategy = super.createDefaultStrategy(context, clazz);
		getServletContext().removeAttribute(ACTUAL_SERVLET_NAME);

		if (strategy instanceof RequestMappingHandlerAdapter handlerAdapter) {
			List<HttpMessageConverter<?>> messageConverters = new ArrayList<>(handlerAdapter.getMessageConverters());
			messageConverters.add(new NextMappingJackson2HttpMessageConverter());
			handlerAdapter.setMessageConverters(messageConverters);
		}

		return strategy;
	}

	@Override
	protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac) {
		String configLocation = XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_PREFIX + getNamespace() + XmlWebApplicationContext.DEFAULT_CONFIG_LOCATION_SUFFIX;
		WebApplicationContext rootContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		if (rootContext.getResource(configLocation).exists()) {
			//only configure factory if xml exists
			wac.setConfigLocation(configLocation);
		} else {
			wac.setConfigLocations(new String[0]);
		}
		super.configureAndRefreshWebApplicationContext(wac);
	}

	@Override
	public String getContextAttribute() {
		String contextAttribute = super.getContextAttribute();
		if ("null".equalsIgnoreCase(contextAttribute)) {
			return null;
		}
		if (contextAttribute == null) {
			return WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;
		} else {
			return contextAttribute;
		}
	}

	@Override
	protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (mv.isReference()) {
			String viewName = mv.getViewName();
			int i = viewName.indexOf(":");
			if (i > -1) {
				viewName = viewName.substring(i + 1);
			}
			request.setAttribute("viewName", viewName); //See WebUtils.getModelAndViewName
		}
		super.render(mv, request, response);
	}

}
