package org.nextframework.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.BeansException;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;

public class NextAnnotationHandlerMapping extends SimpleUrlHandlerMapping {

	private boolean secured;
	private String module;

	@Override
	public void initApplicationContext() throws BeansException {
		initAttributesFromServlet();
		super.initApplicationContext();
	}

	private void initAttributesFromServlet() {
		this.secured = "true".equalsIgnoreCase(getServletContext().getInitParameter("secured"));
		this.module = (String) getServletContext().getAttribute(NextDispatcherServlet.ACTUAL_SERVLET_NAME);
		Objects.requireNonNull(module);
	}

	@Override
	protected void extendInterceptors(List<Object> interceptors) {
		if (secured) {
			interceptors.add(new AuthenticationHandlerInterceptor());
		}
		interceptors.add(new AuthorizationHandlerInterceptor());
	}

	@Override
	protected void registerHandlers(Map<String, Object> urlMap) throws BeansException {
		if (urlMap.isEmpty()) {
			Map<String, Object> controllersPathMap = getControllersPathMap();
			urlMap.putAll(controllersPathMap);
		}
		super.registerHandlers(urlMap);
	}

	private Map<String, Object> getControllersPathMap() {
		Map<String, Object> urlMap = new HashMap<>();
		Map<String, Controller> beansMap = obtainApplicationContext().getBeansOfType(Controller.class);
		for (Controller beanName : beansMap.values()) {
			org.nextframework.controller.Controller annotation = beanName.getClass().getAnnotation(org.nextframework.controller.Controller.class);
			if (annotation != null) {
				for (String path : annotation.path()) {
					if (path.startsWith("/" + this.module) || path.startsWith(this.module)) {
						urlMap.put(path.substring(path.indexOf(module) + module.length()), beanName);
					}
				}
			}
		}
		return urlMap;
	}

}