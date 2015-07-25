package org.nextframework.service;


public interface ServiceProvider {

	/**
	 * Return a service registered with this interface.<BR>
	 * Or null if this ServiceProvider does not have a service for the interface.
	 * @param <E>
	 * @param serviceInterface
	 * @return
	 */
	<E> E getService(Class<E> serviceInterface);
	
	/**
	 * Priority of this ServiceProvider. <BR>
	 * Services providers with higher priority (ie lower priority number)
	 * will be checked first for services in the ServiceFactory.
	 * @return
	 */
	int priority();
	
	/**
	 * Release all the resources associated with this provider.<BR>
	 * After this call the ServiceProvider must not be used.
	 */
	void release();

	<E> E[] loadServices(Class<E> serviceInterface);
	
}
