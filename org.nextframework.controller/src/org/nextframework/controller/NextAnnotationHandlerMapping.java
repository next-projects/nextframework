package org.nextframework.controller;

import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping;

public class NextAnnotationHandlerMapping extends DefaultAnnotationHandlerMapping {

	private String module;

	@Override
	protected String[] determineUrlsForHandler(String beanName) {

		String[] result;
		String[] urlsForHandler = super.determineUrlsForHandler(beanName);

		ApplicationContext context = getApplicationContext();

		Controller mapping = context.findAnnotationOnBean(beanName, Controller.class);
		if (mapping != null) {
			if (urlsForHandler != null) {
				String[] path = mapping.path();
				result = new String[path.length + urlsForHandler.length];
				System.arraycopy(path, 0, result, 0, path.length);
				System.arraycopy(urlsForHandler, 0, result, path.length, urlsForHandler.length);
			} else {
				result = mapping.path();
			}
		} else {
			result = urlsForHandler;
		}
//		System.out.println(beanName+" <> "+result);
		if (result != null && result.length > 0 && module != null) {
			for (String path : result) {
				if (path.startsWith("/" + module)) {
					for (int i = 0; i < result.length; i++) {
						//TODO this will cause conflict with the super.determineUrlsForHandler
						result[i] = result[i].substring(module.length() + 1);
					}
					return result;
				}
			}
			return null;//there's no path for this module
		}

		return result;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public String getModule() {
		return module;
	}

}
