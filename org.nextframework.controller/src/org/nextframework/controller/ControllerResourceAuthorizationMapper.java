package org.nextframework.controller;

import org.nextframework.authorization.AuthorizationModule;
import org.nextframework.authorization.HasAccessAuthorizationModule;
import org.nextframework.authorization.RequiresAuthenticationAuthorizationModule;
import org.nextframework.authorization.ResourceAuthorizationMapper;
import org.nextframework.context.ResourceHandlerMap;
import org.nextframework.service.ServiceFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

public class ControllerResourceAuthorizationMapper implements ResourceAuthorizationMapper {

	@Override
	public AuthorizationModule getAuthorizationModule(String resource) {
		ResourceHandlerMap resourceHandlerMap = ServiceFactory.getService(ResourceHandlerMap.class);
		Object handler = resourceHandlerMap.getHandler(resource);
		if (handler != null) {
			Controller ctrlAnnotation = ClassUtils.getUserClass(handler.getClass()).getAnnotation(Controller.class);
			if (ctrlAnnotation != null) {
				AuthorizationModule authorizationModule = BeanUtils.instantiate(ctrlAnnotation.authorizationModule());
				if (authorizationModule instanceof HasAccessAuthorizationModule && resourceHandlerMap.isAuthenticationRequired(resource)) {
					authorizationModule = new RequiresAuthenticationAuthorizationModule(); //if the module is secured, change the authorization module
				}
				authorizationModule.setControllerClass(handler.getClass());
				authorizationModule.setPath(resource);
				return authorizationModule;
			}
		}
		return null;
	}

//	@Override
//	public void setResourceAuthorizationModule(String resource, AuthorizationModule authorizationModule) {
//		map.put(resource, authorizationModule);
//	}

}
