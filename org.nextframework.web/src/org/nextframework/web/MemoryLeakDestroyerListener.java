package org.nextframework.web;

import java.beans.Introspector;

import org.apache.commons.logging.LogFactory;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

/**
 * Releases the commons logging and Introspector references, avoiding memory leaks.
 * @author rogelgarcia
 */
public class MemoryLeakDestroyerListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
		LogFactory.release(contextClassLoader);
		Introspector.flushCaches();
	}

}
