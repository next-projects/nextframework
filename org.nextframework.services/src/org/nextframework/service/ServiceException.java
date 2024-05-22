package org.nextframework.service;

import java.util.List;
import java.util.ServiceConfigurationError;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	String serviceName;

	String message;

	public ServiceException() {
		super();
	}

	public ServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}

	public static ServiceException noServiceFound(String serviceName, String message) {
		return new ServiceException("No services found for " + serviceName + ". " + (message == null ? "" : message));
	}

	public static ServiceException multipleServicesFound(String provider, Class<?> serviceInterface, List<Object> list) {
		return new ServiceException("More than one service found for " + serviceInterface + " using " + provider + ". Services found " + list);
	}

	public static ServiceException errorLoadingService(String provider, Class<?> serviceInterface, ServiceConfigurationError e) {
		return new ServiceException("Error while loading service for " + serviceInterface + " using " + provider + ". Caused by: " + e.getMessage(), e);
	}

}
