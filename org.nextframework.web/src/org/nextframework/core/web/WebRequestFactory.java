package org.nextframework.core.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebRequestFactory {

	public WebRequestContext createWebRequestContext(HttpServletRequest request, HttpServletResponse response, WebApplicationContext webApplicationContext) {
		return new DefaultWebRequestContext(request, response, webApplicationContext);
	}

}
