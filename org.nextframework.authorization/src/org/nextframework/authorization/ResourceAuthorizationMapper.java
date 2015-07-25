package org.nextframework.authorization;

/**
 * Maps resources to authorization modules.
 * 
 * @author rogelgarcia
 */
public interface ResourceAuthorizationMapper {

//	void setResourceAuthorizationModule(String resource, AuthorizationModule authorizationModule);
	
	AuthorizationModule getAuthorizationModule(String resource);
}
