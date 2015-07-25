package org.nextframework.controller.json;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nextframework.service.ServiceFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

public class JsonModelAndView extends ModelAndView {

	private static final String APPLICATION_JSON = "application/json";
	
	private Object object;

	public JsonModelAndView(Object res){
		this.object = res;
		setView(new View(){
			
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
				response.setContentType(APPLICATION_JSON);
				String jsonText = ServiceFactory.getService(JsonTranslator.class).toJson(object);
				response.getWriter().print(jsonText);
			}

			public String getContentType() {
				return APPLICATION_JSON;
			}

		});
	}
}
