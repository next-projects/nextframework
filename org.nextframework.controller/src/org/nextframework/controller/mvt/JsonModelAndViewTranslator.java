package org.nextframework.controller.mvt;

import java.lang.reflect.Method;

import org.nextframework.controller.json.JsonModelAndView;
import org.springframework.web.servlet.ModelAndView;

public class JsonModelAndViewTranslator implements ModelAndViewTranslator<Object> {

	@Override
	public ModelAndView translateActionResultToModelAndView(Object result, Method actionMethod) {
		return new JsonModelAndView(result);
	}

}
