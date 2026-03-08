package org.nextframework.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;

public class NextAnnotationHandlerMapping extends SimpleUrlHandlerMapping {

	private String module;

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

	public void setModule(String module) {
		this.module = module;
	}

}