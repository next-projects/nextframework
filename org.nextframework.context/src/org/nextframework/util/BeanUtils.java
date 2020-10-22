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
package org.nextframework.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.springframework.beans.PropertyAccessorFactory;

/**
 * @author rogelgarcia
 * @since 22/01/2006
 * @version 1.1
 */
public class BeanUtils {

	/**
	 * Dado um array de classes, retorna um array apenas com as classes concretas
	 * @param allClassesOfType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <E> Class<E>[] removeInterfaces(Class<E>[] allClassesOfType) {
		List<Class<E>> list = new ArrayList<Class<E>>();
		for (Class<E> class1 : allClassesOfType) {
			if (!Modifier.isAbstract(class1.getModifiers())) {
				list.add(class1);
			}
		}
		return list.toArray(new Class[list.size()]);
	}

	public Method getSetterMethod(Class<?> clazz, String property) {
		ReflectionCache cache = ReflectionCacheFactory.getReflectionCache();
		Method[] methods = cache.getMethods(clazz);
		Method method = null;
		//achar o setter TODO ESTÁ DUPLICADO
		String setterName = "set" + Util.strings.captalize(property);
		for (int j = 0; j < methods.length; j++) {
			if (methods[j].getName().equals(setterName) && methods[j].getParameterTypes().length == 1) {
				method = methods[j];
				return method;
			}
		}
		return null;
	}

//	public String getName(Class<?> clazz, AnnotatedBeanRegister[] beanRegisters){
//		ReflectionCache reflectionCache = ReflectionCacheFactory.getReflectionCache();
//		Annotation[] annotations = reflectionCache.getAnnotations(clazz);
//		for (Annotation annotation : annotations) {
//			for (AnnotatedBeanRegister beanRegister : beanRegisters) {
//				if(annotation.annotationType().equals(beanRegister.getAnnotationClass())){
//					return beanRegister.getName(clazz);
//				}
//			}
//		}
//		//throw new NextException("A classe "+clazz.getName()+" não possui uma anotação que represente um bean. Ex: @Bean, @CrudBean");
//		return Util.strings.uncaptalize(clazz.getSimpleName());
//	}

	public String getGetterFromProperty(String propertyName) {
		return "get" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
	}

	public String getPropertyFromGetter(String getterMethodName) {
		//algoritmo otimizado
		char[] toCharArray = getterMethodName.toCharArray();
		if (toCharArray[0] == 'i') { //is
			char[] prop = new char[toCharArray.length - 2];
			System.arraycopy(toCharArray, 2, prop, 0, prop.length);
			prop[0] = Character.toLowerCase(prop[0]);
			return new String(prop);
		} else { //get
			char[] prop = new char[toCharArray.length - 3];
			System.arraycopy(toCharArray, 3, prop, 0, prop.length);
			prop[0] = Character.toLowerCase(prop[0]);
			return new String(prop);
		}
//		return Util.strings.uncaptalize(getterMethodName.startsWith("is")? 
//				getterMethodName.substring("is".length(), getterMethodName.length())
//				:
//				getterMethodName.substring("get".length(), getterMethodName.length())
//				)
//				;
	}

	public Set<String> getPropertiesWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
		Set<String> properties = new HashSet<String>();
		Method[] declaredMethods = clazz.getDeclaredMethods();
		for (Method method : declaredMethods) {
			if (isGetter(method) && method.isAnnotationPresent(annotationClass)) {
				String property = getPropertyFromGetter(method.getName());
				properties.add(property);
			}
		}
		return properties;
	}

	public Set<Method> getPropertiesAsGettersWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
		Set<Method> properties = new HashSet<Method>();
		Method[] declaredMethods = clazz.getDeclaredMethods();
		for (Method method : declaredMethods) {
			if (isGetter(method) && method.isAnnotationPresent(annotationClass)) {
				properties.add(method);
			}
		}
		return properties;
	}

	public List<Method> getPropertyGetters(Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		List<Method> getters = new ArrayList<Method>();
		for (Method method : methods) {
			if (isGetter(method)) {
				getters.add(method);
			}
		}
		return getters;
	}

	public Set<String> getProperties(Class<?> clazz) {
		Set<String> properties = new HashSet<String>();
		Method[] declaredMethods = clazz.getDeclaredMethods();
		for (Method method : declaredMethods) {
			if (isGetter(method)) {
				String property = getPropertyFromGetter(method.getName());
				properties.add(property);
			}
		}
		return properties;
	}

	public boolean isGetter(Method method) {
		return (method.getName().startsWith("get") || method.getName().startsWith("is")) && method.getParameterTypes().length == 0;
	}

	public Method getGetterMethod(Class<?> clazz, String property) {
		ReflectionCache cache = ReflectionCacheFactory.getReflectionCache();
		Method[] methods = cache.getMethods(clazz);
		Method method = null;
		String getterName = "get" + Util.strings.captalize(property);
		String getterName1 = "is" + Util.strings.captalize(property);
		for (int j = 0; j < methods.length; j++) {
			if ((methods[j].getName().equals(getterName) || methods[j].getName().equals(getterName1)) && methods[j].getParameterTypes().length == 0) {
				method = methods[j];
				return method;
			}
		}
		return null;
	}

	public Object getId(Object obj) {
		BeanDescriptor beanDescriptor = BeanDescriptorFactory.forBean(obj);
		return beanDescriptor.getId();
	}

	public Collection<?> getPropertyValue(Object owner, String role) {
		return (Collection<?>) PropertyAccessorFactory.forBeanPropertyAccess(owner).getPropertyValue(role);
	}

}
