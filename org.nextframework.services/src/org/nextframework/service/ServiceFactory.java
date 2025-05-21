package org.nextframework.service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory for providing services from various sources.<BR>
 * The ServiceFactory actually delegates the loading of services to ServiceProvider objects.<BR>
 * The implementations of the ServiceProvider objects will be loaded with Java Service Loader mechanism or 
 * must be registered with method registerProvider.<BR> 
 * 
 * @see ServiceLoader
 * @author rogelgarcia
 * @since 2012-08-15
 */
public class ServiceFactory {

	private static Log log = LogFactory.getLog(ServiceFactory.class.getSimpleName());

	static Set<ServiceProvider> providers = null;

	static boolean initialized = false;

	private synchronized static void init() {

		if (initialized) {
			if (providers == null) {
				throw new IllegalStateException("The ServiceFactory is not available. The release() method must have been called. ");
			}
			return;
		}

		providers = Collections.synchronizedSet(new TreeSet<ServiceProvider>(new Comparator<ServiceProvider>() {

			public int compare(ServiceProvider o1, ServiceProvider o2) {
				return o1.priority() - o2.priority();
			}

		}));

		Iterator<ServiceProvider> iterator = ServiceLoader.load(ServiceProvider.class).iterator();
		while (iterator.hasNext()) {
			try {
				providers.add(iterator.next());
			} catch (ServiceConfigurationError e) {
				//if the provider depends on classes not available does nothing.. (allow for optional dependency, aka ServletContext)
				if (e.getMessage().contains("not found")) {
					throw e;
				}
				log.debug("Ignoring provider: " + e);
			}
		}

		initialized = true;

	}

	public static void registerProvider(ServiceProvider provider) {
		init();
		providers.add(provider);
	}

	public static Set<ServiceProvider> getProviders() {
		init();
		return Collections.unmodifiableSet(providers);
	}

	@SuppressWarnings("unchecked")
	public static <E> E[] loadServices(Class<E> serviceInterface) {
		init();
		List<E> list = new ArrayList<E>();
		for (ServiceProvider provider : providers) {
			E[] services = provider.loadServices(serviceInterface);
			list.addAll(Arrays.asList(services));
		}
		E[] services = (E[]) Array.newInstance(serviceInterface, list.size());
		return list.toArray(services);
	}

	/**
	 * Returns a service with the provided interface.<BR>
	 * It will search throught the service providers avaiable for a service of the class.<BR>
	 * The first service found is returned.<BR>
	 * The service providers are ordered by its priority() method (the lower the priority number is, the higher the priority will be).
	 * That is, if two service providers provides services for the same class, the privider A has a priority of 10 and the provider B has 
	 * a priority of 20. The provider A will provide the service as it has the lower priority number.
	 * @param <E>
	 * @param service The class or interface of the service needed.
	 * @return An object of the service class.
	 * @throws ServiceException If no services found for class.
	 */
	public static <E> E getService(Class<E> service) {
		init();
		if (service == null) {
			throw new IllegalArgumentException("Service interface must not be null");
		}
		for (ServiceProvider provider : providers) {
			E serviceObject = provider.getService(service);
			if (serviceObject != null) {
				return serviceObject;
			}
		}
		throw ServiceException.noServiceFound(service.toString(), "Using providers " + providers);
	}

	/**
	 * Release all service providers associated with this ServiceFactory.<BR>
	 * After this call the ServiceFactory release all resources, so it cannot be used anymore.<BR>
	 * Any attempt to use the ServiceFactory will result in {@link IllegalStateException}
	 */
	public static void release() {
		Set<ServiceProvider> providers = getProviders();
		for (ServiceProvider serviceProvider : providers) {
			try {
				serviceProvider.release();
			} catch (Throwable e) {
				log.info("Provider " + serviceProvider + " threw excpetion when releasing. " + e);
			}
		}
		ServiceFactory.providers = null;
	}

	public synchronized static void refresh() {
		try {
			release();
		} catch (IllegalStateException e) {
		}
		initialized = false;
		init();
	}

}
