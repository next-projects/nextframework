package org.nextframework.controller.json;

import org.nextframework.core.web.NextWeb;
import org.nextframework.message.MessageResolver;
import org.nextframework.service.ServiceFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonModelAndView extends ModelAndView {

	public JsonModelAndView(Object obj) {

		MappingJackson2JsonView view = new MappingJackson2JsonView();
		view.setExtractValueFromSingleKeyModel(true);

		JsonTranslator jsonTranslator = ServiceFactory.getService(JsonTranslator.class);
		if (jsonTranslator != null && jsonTranslator instanceof JacksonJsonTranslator) {
			MessageResolver messageResolver = NextWeb.getRequestContext().getMessageResolver();
			ObjectMapper objectMapper = ((JacksonJsonTranslator) jsonTranslator).createObjectMapper(messageResolver);
			view.setObjectMapper(objectMapper);
		}

		//USE JsonJSDateModelAndView
		//SimpleModule nextModule = new SimpleModule("JsonJSDateSerializer", new Version(1, 0, 0, null, "org.nextframework", "next-controller"));
		//nextModule.addSerializer(Date.class, new JsonJSDateSerializer());
		//objectMapper.registerModule(nextModule);

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