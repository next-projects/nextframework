package org.nextframework.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.authorization.AuthenticationController;
import org.nextframework.authorization.Authorization;
import org.nextframework.core.standard.MessageType;
import org.nextframework.core.web.NextWeb;
import org.nextframework.service.ServiceFactory;
import org.nextframework.util.Util;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class AuthenticationHandlerInterceptor implements HandlerInterceptor {

	private Log logger = LogFactory.getLog(AuthenticationHandlerInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		if (Authorization.getUserLocator().getUser() == null) {
			//check if there is a login page
			Collection<AuthenticationController> authenticationControllers = ServiceFactory.getService(ListableBeanFactory.class).getBeansOfType(AuthenticationController.class).values();
			if (authenticationControllers.size() > 1) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "The user must be logged in to access this resource.");
				logger.error("More than one controller " + authenticationControllers + " of type LogingController found in application! Cannot redirect to login page.");
				return false;
			} else if (authenticationControllers.size() == 0) {
				response.sendError(HttpServletResponse.SC_FORBIDDEN, "The user must be logged in to access this resource.");
				logger.error("No controller of type " + AuthenticationController.class.getSimpleName() + " found! Cannot redirect to login page. Create a controller that extends LoginController or " + AuthenticationController.class.getSimpleName() + " and configure this controller in a module (@Controller(path=\"...\")) that can be accessed publicly");
				return false;
			} else {
				//TODO VERIFY INFINITE LOOP
				logger.debug("Redirecting user to login page.");
				NextWeb.getRequestContext().addMessage(Util.objects.newMessage("next.authentication.acessDenied", null, "O recurso que deseja acessar requer autenticação."), MessageType.WARN);
				AuthenticationController authenticationController = authenticationControllers.iterator().next();
				String path = request.getContextPath() + authenticationController.getPath() + "?ar=1";
				response.sendRedirect(path);
				return false;
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

	}

}
