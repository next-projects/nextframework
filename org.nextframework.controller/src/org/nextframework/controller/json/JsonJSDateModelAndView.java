package org.nextframework.controller.json;

import java.util.Date;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class JsonJSDateModelAndView extends ModelAndView {
	
	public JsonJSDateModelAndView(Object obj) {
		MappingJackson2JsonView view = new MappingJackson2JsonView();
		view.setExtractValueFromSingleKeyModel(true);
		SimpleModule nextModule = new SimpleModule("NextModule", new Version(1, 0, 0, null, "org.nextframework", "next-controller"));
		nextModule.addSerializer(Date.class, new JsonJSDateSerializer());
		view.getObjectMapper().registerModule(nextModule);
		setView(view);
		this.addObject("jsonObject", obj);
	}

}