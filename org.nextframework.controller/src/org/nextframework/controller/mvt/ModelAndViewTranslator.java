package org.nextframework.controller.mvt;

import java.lang.reflect.Method;

import org.springframework.web.servlet.ModelAndView;

//TODO: Use ModelAndViewResolver from Spring
public interface ModelAndViewTranslator<T> {

	public ModelAndView translateActionResultToModelAndView(T result, Method actionMethod);
}
