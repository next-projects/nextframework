package org.nextframework.service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ServiceProvider that uses the Java Service Loader mechanism to load services.<BR>
 * 
 * @see ServiceLoader
 * @see ServiceFactory
 * @author rogelgarcia
 *
 */
public class JavaLoaderServiceProvider implements ServiceProvider {

	private Log log = LogFactory.getLog(JavaLoaderServiceProvider.class.getSimpleName());

	public static int PRIORITY = 50;

	private Map<Class<?>, List<Object>> javaServices = new HashMap<Class<?>, List<Object>>();

	@Override
	@SuppressWarnings("unchecked")
	public <E> E[] loadServices(Class<E> serviceInterface) {
		List<Object> list = getServicesForInterface(serviceInterface);
		E[] result = (E[]) Array.newInstance(serviceInterface, list.size());
		int i = 0;
		for (Object object : list) {
			result[i++] = (E) object;
		}
		return list.toArray(result);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E getService(Class<E> serviceInterface) {
		List<Object> list = getServicesForInterface(serviceInterface);
		if (list.size() > 1) {
			//check the priority
			Object o1 = list.get(0);
			Object o2 = list.get(1);
			if (getPriority(o1) < getPriority(o2)) {
				//if there is priority over the services choose the one with better priority
				return (E) o1;
			}
			throw ServiceException.multipleServicesFound(this.getClass().getSimpleName(), serviceInterface, list);
		}
		if (list.size() > 0) {
			return (E) list.get(0);
		}
		return null;
	}

	private List<Object> getServicesForInterface(Class<?> serviceInterface) {
		synchronized (javaServices) {
			if (!javaServices.containsKey(serviceInterface)) {
				ServiceLoader<?> loader = ServiceLoader.load(serviceInterface);
				List<Object> services = new ArrayList<Object>();
				Iterator<?> iterator = loader.iterator();
				while (iterator.hasNext()) {
					Object obj;
					try {
						obj = iterator.next();
					} catch (ServiceConfigurationError e) {
						throw ServiceException.errorLoadingService(this.getClass().getSimpleName(), serviceInterface, e);
					}
					services.add(obj);
				}
				if (services.size() == 0) {
					log.debug("Java Service Loader: No services found for interface " + serviceInterface.getName());
				} else {
					if (log.isDebugEnabled()) {
						log.debug("Loading services from Java Service Loader: " + serviceInterface.getName() + ". Found " + services);
					}
				}
				Collections.sort(services, new Comparator<Object>() {

					@Override
					public int compare(Object o1, Object o2) {
						int p1 = getPriority(o1);
						int p2 = getPriority(o2);
						return p1 - p2;
					}

				});
				javaServices.put(serviceInterface, services);
			}
			return javaServices.get(serviceInterface);
		}
	}

	public void clean() {
		javaServices = new HashMap<Class<?>, List<Object>>();
	}

	@Override
	public int priority() {
		return PRIORITY;
	}

	private int getPriority(Object o) {
		return PriorityCache.getPriority(o);
	}

	@Override
	public void release() {
		clean();
	}

}
