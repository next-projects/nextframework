package org.nextframework.authorization;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.authorization.impl.AbstractAuthorizationDAO;
import org.nextframework.core.standard.Next;
import org.nextframework.service.ServiceFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * Utility class to access Authorization objects 
 * @author rogelgarcia
 */
public abstract class Authorization {

	private static Log logger = LogFactory.getLog(Authorization.class);

	public static UserLocator getUserLocator() {
		return ServiceFactory.getService(UserLocator.class);
	}

	public static PermissionLocator getPermissionLocator() {
		return ServiceFactory.getService(PermissionLocator.class);
	}

	public static ResourceAuthorizationMapper getAuthorizationMapper() {
		return ServiceFactory.getService(ResourceAuthorizationMapper.class);
	}

	public static AuthorizationManager getAuthorizationManager() {
		return ServiceFactory.getService(AuthorizationManager.class);
	}

	public static AuthorizationDAO getAuthorizationDAO() {
		try {
			return Next.getObject(AuthorizationDAO.class);
		} catch (NoSuchBeanDefinitionException e) {
			try {
				return ServiceFactory.getService(AuthorizationDAO.class);
			} catch (Exception e1) {
				logger.warn("No " + AuthorizationDAO.class + " is registered in application. Using default. " +
						"Please implement a " + AuthorizationDAO.class);
				return new AbstractAuthorizationDAO() {

					public User findUserByUsername(final String login) {
						return new User() {

							private static final long serialVersionUID = 1L;

							public String getUsername() {
								return login;
							}

							public String getPassword() {
								return login;
							}

						};
					}

				};//fallback if no AuthorizationDAO have been found
			}
		}
	}

}
