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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nextframework.classmanager.ClassManager;
import org.nextframework.classmanager.ClassManagerFactory;
import org.nextframework.context.ResourceHandlerMap;
import org.nextframework.controller.json.NextMappingJackson2HttpMessageConverter;
import org.nextframework.web.service.ServletContextServiceProvider;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;
import org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter;

/**
 * @author rogelgarcia
 * @since 22/01/2006
 * @version 1.1
 */
public class NextDispatcherServlet extends org.springframework.web.servlet.DispatcherServlet {

	private static final long serialVersionUID = 1L;

	public NextDispatcherServlet() {
	}

//	@Override
//	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		request.setAttribute("CONFIG", getServletConfig());
//		super.doService(request, response);
//	}

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

	static class DispatcherServletResourceHandlerMap implements ResourceHandlerMap {

		List<NextDispatcherServlet> servlets = new ArrayList<NextDispatcherServlet>();

		Map<String, Object> handlerMap = new HashMap<String, Object>();

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

		Map<String, Boolean> authenticationModuleCache = new HashMap<String, Boolean>();

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

	@SuppressWarnings("all")
	@Override
	protected List getDefaultStrategies(ApplicationContext context, Class strategyInterface) throws BeansException {
		List defaultStrategies = super.getDefaultStrategies(context, strategyInterface);
		if (HandlerMapping.class.isAssignableFrom(strategyInterface)) {
			NextAnnotationHandlerMapping handlerMapping = new NextAnnotationHandlerMapping();
			handlerMapping.setModule(getServletName());
			handlerMapping.setInterceptors(getInterceptors());
			handlerMapping.setApplicationContext(context);
			defaultStrategies.add(0, handlerMapping);
		}
		if (ViewResolver.class.isAssignableFrom(strategyInterface)) {
			CompositeViewResolver compositeViewResolver = new CompositeViewResolver();
			compositeViewResolver.setApplicationContext(context);
			compositeViewResolver.setParameterName("bodyPage");
			compositeViewResolver.setPrefix("/WEB-INF/jsp/" + getServletName() + "/");
			compositeViewResolver.setSuffix(".jsp");
			compositeViewResolver.setBaseViews("/WEB-INF/jsp/" + getServletName() + "/base.jsp", "/WEB-INF/jsp/base.jsp");
			defaultStrategies.add(0, compositeViewResolver);
		}
		return defaultStrategies;
	}

	@SuppressWarnings("all")
	protected HandlerInterceptor[] getInterceptors() {
		List<HandlerInterceptor> interceptorsList = new ArrayList<HandlerInterceptor>();
		try {
			//TODO REMOVE THIS DEPENDENCY
			if (isSecured()) {
				interceptorsList.add(new AuthenticationHandlerInterceptor());
			}
			interceptorsList.add(new AuthorizationHandlerInterceptor());
//			ClassManager classManager = ClassManagerFactory.getClassManager(getServletContext());//WebClassRegister.getClassManager(getServletContext(), "org.nextframework");
			ClassManager classManager = ClassManagerFactory.getClassManager();
			Class<HandlerInterceptor>[] interceptorsClasses = classManager.getAllClassesOfType(HandlerInterceptor.class);
			for (Class<HandlerInterceptor> class1 : interceptorsClasses) {
				interceptorsList.add((HandlerInterceptor) BeanUtils.instantiate(class1));
			}
		} catch (Exception e) {
			//se ocorrer alguma exceção apenas logar o erro
			throw new RuntimeException(e);
		}
		HandlerInterceptor[] interceptors = interceptorsList.toArray(new HandlerInterceptor[interceptorsList.size()]);
		return interceptors;
	}

	boolean isSecured() {
		return "true".equalsIgnoreCase(this.getInitParameter("secured"));
	}

	@Override
	@SuppressWarnings("deprecation")
	protected Object createDefaultStrategy(ApplicationContext context, Class<?> clazz) {
		Object strategy = super.createDefaultStrategy(context, clazz);
		if (strategy instanceof AnnotationMethodHandlerAdapter) {
			AnnotationMethodHandlerAdapter handlerAdapter = (AnnotationMethodHandlerAdapter) strategy;
			//Para @Controller padrão do Spring fazer mapeamento completo declarado em @RequestMapping
			handlerAdapter.setAlwaysUseFullPath(true);
			//Para converter para json
			List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
			messageConverters.addAll(Arrays.asList(handlerAdapter.getMessageConverters()));
			messageConverters.add(new NextMappingJackson2HttpMessageConverter());
			handlerAdapter.setMessageConverters(messageConverters.toArray(new HttpMessageConverter<?>[messageConverters.size()]));
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

//	@Override
//	public Class<?> getContextClass() {
//		return AnnotationsXmlWebApplicationContext.class;
//	}

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
