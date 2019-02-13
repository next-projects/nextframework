package org.nextframework.controller.json;

import org.nextframework.service.ServiceFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonModelAndView extends ModelAndView {
	
	public JsonModelAndView(Object obj) {
		MappingJackson2JsonView view = new MappingJackson2JsonView();
		//Se o JsonTranslator é do tipo JacksonJsonTranslator, substitui as configurações padrão do Spring
		JsonTranslator jsonTranslator = ServiceFactory.getService(JsonTranslator.class);
		if (jsonTranslator != null && jsonTranslator instanceof JacksonJsonTranslator) {
			JacksonJsonTranslator jacksonJsonTranslator = (JacksonJsonTranslator) jsonTranslator;
			view.setObjectMapper(jacksonJsonTranslator.getObjectMapper());
		}
		//Utiliza a view no modo Jackson como objeto único
		view.setExtractValueFromSingleKeyModel(true);
		setView(view);
		this.addObject("jsonObject", obj);
	}
	
	public JsonModelAndView(Object obj, ObjectMapper objectMapper) {
		this(obj);
		if (objectMapper != null) {
			MappingJackson2JsonView view = (MappingJackson2JsonView) getView();
			view.setObjectMapper(objectMapper);
		}
	}
	
	public JsonModelAndView(Object obj, Module module) {
		this(obj);
		if (module != null) {
			MappingJackson2JsonView view = (MappingJackson2JsonView) getView();
			view.getObjectMapper().registerModule(module);
		}
	}

}