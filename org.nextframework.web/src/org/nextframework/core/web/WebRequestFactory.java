package org.nextframework.core.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class WebRequestFactory {

	public WebRequestContext createWebRequestContext(HttpServletRequest request, HttpServletResponse response, WebApplicationContext webApplicationContext) {
		return new DefaultWebRequestContext(request, response, webApplicationContext);
	}

}
