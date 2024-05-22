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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ReflectionCacheImpl implements ReflectionCache {

	// Classes devem ser indexadas pelo nome: clazz.getName().
	// Métodos devem ser indexados por eles mesmos: method.
	// Vetores de classes devem ser indexados por sua representação em String: classArrayToString(vetor).

	protected Map<String, Annotation[]> hashGetAnnotationsClass = Collections.synchronizedMap(new HashMap<String, Annotation[]>());
	protected Map<Method, Annotation[]> hashGetAnnotationsMethod = Collections.synchronizedMap(new HashMap<Method, Annotation[]>());
	protected Map<String, HashMap<String, Annotation>> hashGetAnnotation = Collections.synchronizedMap(new HashMap<String, HashMap<String, Annotation>>());
	protected Map<String, HashMap<String, Boolean>> hashIsAnnotationPresentClass = Collections.synchronizedMap(new HashMap<String, HashMap<String, Boolean>>());
	protected Map<Method, HashMap<String, Boolean>> hashIsAnnotationPresentMethod = Collections.synchronizedMap(new HashMap<Method, HashMap<String, Boolean>>());
	protected Map<String, Method[]> hashGetMethods = Collections.synchronizedMap(new HashMap<String, Method[]>());
	protected Map<String, HashMap<String, HashMap<String, Method>>> hashGetMethod = Collections.synchronizedMap(new HashMap<String, HashMap<String, HashMap<String, Method>>>());

	public Annotation[] getAnnotations(Class<?> clazz) {
		Annotation[] resultado = hashGetAnnotationsClass.get(clazz.getName());
		if (resultado == null) {
			resultado = clazz.getAnnotations();
			hashGetAnnotationsClass.put(clazz.getName(), resultado);
		}
		return resultado;
	}

	public Annotation[] getAnnotations(Method method) {
		Annotation[] resultado = hashGetAnnotationsMethod.get(method);
		if (resultado == null) {
			resultado = method.getAnnotations();
			hashGetAnnotationsMethod.put(method, resultado);
		}
		return resultado;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Annotation getAnnotation(Class clazz, Class annotationClazz) {

		HashMap<String, Annotation> hashInterno = hashGetAnnotation.get(clazz.getName());
		if (hashInterno == null) {
			hashInterno = new HashMap<String, Annotation>();
			hashGetAnnotation.put(clazz.getName(), hashInterno);
		}

		Annotation resultado = hashInterno.get(annotationClazz.getName());
		if (resultado == null) {
			resultado = clazz.getAnnotation(annotationClazz);
			hashInterno.put(annotationClazz.getName(), resultado);
		}

		return resultado;
	}

	public boolean isAnnotationPresent(Class<?> clazz, Class<? extends Annotation> annotationClazz) {

		HashMap<String, Boolean> hashInterno = hashIsAnnotationPresentClass.get(clazz.getName());
		if (hashInterno == null) {
			hashInterno = new HashMap<String, Boolean>();
			hashIsAnnotationPresentClass.put(clazz.getName(), hashInterno);
		}

		Boolean resultado = hashInterno.get(annotationClazz.getName());
		if (resultado == null) {
			resultado = clazz.isAnnotationPresent(annotationClazz);
			hashInterno.put(annotationClazz.getName(), resultado);
		}

		return resultado;
	}

	public boolean isAnnotationPresent(Method method, Class<? extends Annotation> annotationClazz) {

		HashMap<String, Boolean> hashInterno = hashIsAnnotationPresentMethod.get(method);
		if (hashInterno == null) {
			hashInterno = new HashMap<String, Boolean>();
			hashIsAnnotationPresentMethod.put(method, hashInterno);
		}

		Boolean resultado = hashInterno.get(annotationClazz.getName());
		if (resultado == null) {
			resultado = method.isAnnotationPresent(annotationClazz);
			hashInterno.put(annotationClazz.getName(), resultado);
		}

		return resultado;
	}

	public Method[] getMethods(Class<?> clazz) throws SecurityException {
		Method[] resultado = hashGetMethods.get(clazz.getName());
		if (resultado == null) {
			resultado = clazz.getMethods();
			hashGetMethods.put(clazz.getName(), resultado);
		}
		return resultado;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Method getMethod(Class clazz, String name, Class... parameterTypes) throws NoSuchMethodException, SecurityException {

		HashMap<String, HashMap<String, Method>> hashInterno1 = hashGetMethod.get(clazz.getName());
		if (hashInterno1 == null) {
			hashInterno1 = new HashMap<String, HashMap<String, Method>>();
			hashGetMethod.put(clazz.getName(), hashInterno1);
		}

		HashMap<String, Method> hashInterno2 = hashInterno1.get(name);
		if (hashInterno2 == null) {
			hashInterno2 = new HashMap<String, Method>();
			hashInterno1.put(name, hashInterno2);
		}

		String parameterTypesString = classArrayToString(parameterTypes);
		Method resultado = hashInterno2.get(parameterTypesString);
		if (resultado == null) {
			resultado = clazz.getMethod(name, parameterTypes);
			hashInterno2.put(parameterTypesString, resultado);
		}

		return resultado;
	}

	private String classArrayToString(Class<?>... parameterTypes) {
		String parameterTypesString = "[ ";
		for (int i = 0; i < parameterTypes.length; i++) {
			parameterTypesString += parameterTypes[i].getName();
			if (i != parameterTypes.length - 1) {
				parameterTypesString += ", ";
			}
		}
		parameterTypesString += " ]";
		return parameterTypesString;
	}

	@SuppressWarnings("unchecked")
	public <E extends Annotation> E getAnnotation(Method method, Class<E> annotation) {
		ReflectionCache reflectionCache = ReflectionCacheFactory.getReflectionCache();
		Annotation[] annotations = reflectionCache.getAnnotations(method);
		for (Annotation a : annotations) {
			if (a.annotationType().equals(annotation)) {
				return (E) a;
			}
		}
		return null;
	}

}
