package org.nextframework.controller.mvt;

import java.lang.reflect.Method;

import org.springframework.web.servlet.ModelAndView;

public class StringModelAndViewTranslator implements ModelAndViewTranslator<String> {

	@Override
	public ModelAndView translateActionResultToModelAndView(String result, Method actionMethod) {
		return new ModelAndView(result);
	}

}
