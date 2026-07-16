package org.nextframework.controller.mvt;

import java.lang.reflect.Method;

import org.nextframework.controller.StringBuilderModelAndView;
import org.springframework.web.servlet.ModelAndView;

public class StringBuilderModelAndViewTranslator implements ModelAndViewTranslator<StringBuilder> {

	@Override
	public ModelAndView translateActionResultToModelAndView(StringBuilder result, Method actionMethod) {
		return new StringBuilderModelAndView(result);
	}

}
