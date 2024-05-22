package org.nextframework.authorization;

/**
 * Maps resources to authorization modules.
 * 
 * @author rogelgarcia
 */
public interface ResourceAuthorizationMapper {

	AuthorizationModule getAuthorizationModule(String resource);

}
