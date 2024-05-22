package org.nextframework.controller.mvt;

import java.lang.reflect.Method;

import org.nextframework.controller.ResourceModelAndView;
import org.nextframework.controller.resource.Resource;
import org.springframework.web.servlet.ModelAndView;

public class ResourceModelAndViewTranslator implements ModelAndViewTranslator<Resource> {

	@Override
	public ModelAndView translateActionResultToModelAndView(Resource result, Method actionMethod) {
		return new ResourceModelAndView(result);
	}

}
