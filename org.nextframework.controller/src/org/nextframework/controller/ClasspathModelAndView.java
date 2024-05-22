package org.nextframework.controller;

import org.springframework.web.servlet.ModelAndView;

public class ClasspathModelAndView extends ModelAndView {

	public ClasspathModelAndView(String path) {
		super("classpath:" + path);
	}

}
