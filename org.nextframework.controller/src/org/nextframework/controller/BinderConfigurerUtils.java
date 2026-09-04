package org.nextframework.controller;

import java.util.Map;

import org.nextframework.core.standard.Next;
import org.springframework.web.bind.ServletRequestDataBinder;

import jakarta.servlet.ServletRequest;

public class BinderConfigurerUtils {

	public static void configureBinder(ServletRequestDataBinder binder, ServletRequest request, Object command) {
		Map<String, BinderConfigurer> binderConfigurersMap = Next.getBeanFactory().getBeansOfType(BinderConfigurer.class);
		if (binderConfigurersMap != null) {
			for (BinderConfigurer binderConfigurer : binderConfigurersMap.values()) {
				binderConfigurer.configureBinder(binder, request, command);
			}
		}
	}

}