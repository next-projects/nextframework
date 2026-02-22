package org.nextframework.controller.mvt;

import java.lang.reflect.Method;
import java.util.Map;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StringBuilderModelAndViewTranslator implements ModelAndViewTranslator<StringBuilder> {

	@Override
	public ModelAndView translateActionResultToModelAndView(final StringBuilder result, Method actionMethod) {

		View view = new View() {

			@Override
			public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
				response.setContentType(getContentType());
				response.getWriter().print(result.toString());
			}

			@Override
			public String getContentType() {
				return "text/plain";
			}

		};

		return new ModelAndView(view);
	}

}
