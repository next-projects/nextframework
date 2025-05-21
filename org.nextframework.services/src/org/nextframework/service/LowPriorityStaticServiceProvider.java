package org.nextframework.service;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This has the same functionality as StaticServiceProvider but with lower priority.
 * 
 * This LowPriorityStaticServiceProvider is registered automatically with the ServiceFactory using Java Service Loader mechanism.<BR>
 * s
 * @see StaticServiceProvider
 * @author rogel
 */
public class LowPriorityStaticServiceProvider implements ServiceProvider {

	private static Log log = LogFactory.getLog(StaticServiceProvider.class.getSimpleName());

	public static int PRIORITY = 100;

	//does the value need to be weakreference?
	private static Map<Class<?>, Object> defaultServices = new WeakHashMap<Class<?>, Object>();

	/**
	 * Register a service with an interface. 
	 * @param interfaceService
	 * @param service
	 */
	public static <E, X extends E> void registerService(Class<E> interfaceService, X service) {
		synchronized (defaultServices) {
			if (defaultServices.get(interfaceService) != service) {
				log.debug("Registering service for " + interfaceService + " with object " + service);
				defaultServices.put(interfaceService, service);
			}
		}
	}

	/**
	 * Unregister a service with all the service's implemented interfaces.
	 * @param service
	 */
	public static void unregisterService(Object service) {
		synchronized (defaultServices) {
			Class<?>[] interfaces = service.getClass().getInterfaces();
			for (Class<?> class1 : interfaces) {
				defaultServices.remove(class1);
			}
		}
	}

	/**
	 * Clean this service provider. That is, unregister all services.<BR>
	 * This service provider remains functional after this call.
	 */
	public static void clean() {
		defaultServices = new HashMap<Class<?>, Object>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E getService(Class<E> serviceInterface) {
		return (E) defaultServices.get(serviceInterface);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E> E[] loadServices(Class<E> serviceInterface) {
		E service = getService(serviceInterface);
		if (service == null) {
			return (E[]) Array.newInstance(serviceInterface, 0);
		} else {
			E[] result = (E[]) Array.newInstance(serviceInterface, 1);
			result[0] = service;
			return result;
		}
	}

	@Override
	public int priority() {
		return PRIORITY;
	}

	@Override
	public void release() {
		clean();
	}

}
