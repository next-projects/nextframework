/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2009 the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * You may obtain a copy of the license at
 * 
 *     http://www.gnu.org/copyleft/lesser.html
 * 
 */
package org.nextframework.controller;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;

/**
 * @author rogelgarcia
 * @since 31/01/2006
 * @version 1.1
 */
public class CachedIntrospectionResults {

	private static final Log logger = LogFactory.getLog(CachedIntrospectionResults.class);

	/**
	 * Map keyed by class containing CachedIntrospectionResults.
	 * Needs to be a WeakHashMap with WeakReferences as values to allow
	 * for proper garbage collection in case of multiple class loaders.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static final Map classCache = Collections.synchronizedMap(new WeakHashMap());

	/**
	 * We might use this from the EJB tier, so we don't want to use synchronization.
	 * Object references are atomic, so we can live with doing the occasional
	 * unnecessary lookup at startup only.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static CachedIntrospectionResults forClass(Class clazz) throws BeansException {
		CachedIntrospectionResults results = null;
		Object value = classCache.get(clazz);
		if (value instanceof Reference) {
			Reference ref = (Reference) value;
			results = (CachedIntrospectionResults) ref.get();
		} else {
			results = (CachedIntrospectionResults) value;
		}
		if (results == null) {
			// can throw BeansException
			results = new CachedIntrospectionResults(clazz);
			boolean cacheSafe = isCacheSafe(clazz);
			if (logger.isDebugEnabled()) {
				logger.debug("Class [" + clazz.getName() + "] is " + (!cacheSafe ? "not " : "") + "cache-safe");
			}
			if (cacheSafe) {
				classCache.put(clazz, results);
			} else {
				classCache.put(clazz, new WeakReference(results));
			}
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Using cached introspection results for class [" + clazz.getName() + "]");
			}
		}
		return results;
	}

	/**
	 * Check whether the given class is cache-safe,
	 * i.e. whether it is loaded by the same class loader as the
	 * CachedIntrospectionResults class or a parent of it.
	 * <p>Many thanks to Guillaume Poirier for pointing out the
	 * garbage collection issues and for suggesting this solution.
	 * @param clazz the class to analyze
	 * @return whether the given class is thread-safe
	 */
	private static boolean isCacheSafe(Class<?> clazz) {
		ClassLoader cur = CachedIntrospectionResults.class.getClassLoader();
		ClassLoader target = clazz.getClassLoader();
		if (target == null || cur == target) {
			return true;
		}
		while (cur != null) {
			cur = cur.getParent();
			if (cur == target) {
				return true;
			}
		}
		return false;
	}

	private final BeanInfo beanInfo;

	/** Property descriptors keyed by property name */
	private final Map<String, PropertyDescriptor> propertyDescriptorCache;

	/**
	 * Create new CachedIntrospectionResults instance fot the given class.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private CachedIntrospectionResults(Class clazz) throws BeansException {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Getting BeanInfo for class [" + clazz.getName() + "]");
			}
			this.beanInfo = Introspector.getBeanInfo(clazz);
			// Immediately remove class from Introspector cache, to allow for proper
			// garbage collection on class loader shutdown - we cache it here anyway,
			// in a GC-friendly manner. In contrast to CachedIntrospectionResults,
			// Introspector does not use WeakReferences as values of its WeakHashMap!
			Class classToFlush = clazz;
			do {
				Introspector.flushFromCaches(classToFlush);
				classToFlush = classToFlush.getSuperclass();
			} while (classToFlush != null);
			if (logger.isDebugEnabled()) {
				logger.debug("Caching PropertyDescriptors for class [" + clazz.getName() + "]");
			}
			this.propertyDescriptorCache = new HashMap();
			// This call is slow so we do it once.
			PropertyDescriptor[] pds = this.beanInfo.getPropertyDescriptors();
			for (int i = 0; i < pds.length; i++) {
				if (logger.isDebugEnabled()) {
					logger.debug("Found property '" + pds[i].getName() + "'" +
							(pds[i].getPropertyType() != null ? " of type [" + pds[i].getPropertyType().getName() + "]" : "") +
							(pds[i].getPropertyEditorClass() != null ? "; editor [" + pds[i].getPropertyEditorClass().getName() + "]" : ""));
				}
				// Set methods accessible if declaring class is not public, for example
				// in case of package-protected base classes that define bean properties.
				Method readMethod = pds[i].getReadMethod();
				if (readMethod != null && !Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
					readMethod.setAccessible(true);
				}
				Method writeMethod = pds[i].getWriteMethod();
				if (writeMethod != null && !Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
					writeMethod.setAccessible(true);
				}
				this.propertyDescriptorCache.put(pds[i].getName(), pds[i]);
			}
		} catch (IntrospectionException ex) {
			throw new FatalBeanException("Cannot get BeanInfo for object of class [" + clazz.getName() + "]", ex);
		}
	}

	BeanInfo getBeanInfo() {
		return this.beanInfo;
	}

	Class<?> getBeanClass() {
		return this.beanInfo.getBeanDescriptor().getBeanClass();
	}

	PropertyDescriptor getPropertyDescriptor(String propertyName) {
		return (PropertyDescriptor) this.propertyDescriptorCache.get(propertyName);
	}

}
