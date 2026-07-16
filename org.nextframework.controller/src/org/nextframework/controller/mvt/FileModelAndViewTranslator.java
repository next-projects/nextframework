package org.nextframework.controller.mvt;

import java.io.File;
import java.lang.reflect.Method;

import org.nextframework.controller.FileModelAndView;
import org.springframework.web.servlet.ModelAndView;

public class FileModelAndViewTranslator implements ModelAndViewTranslator<File> {

	@Override
	public ModelAndView translateActionResultToModelAndView(File result, Method actionMethod) {
		return new FileModelAndView(result);
	}

}
