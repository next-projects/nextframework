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

public interface ReflectionCache {

	// TODO add methods to cache fields as well (getFields, getDeclaredFields, isAnnotationPresent for Field)

	public Annotation[] getAnnotations(Class<?> clazz);

	public Annotation[] getAnnotations(Method method);

	public <A extends Annotation> A getAnnotation(Class<?> clazz, Class<A> annotationClazz);

	public boolean isAnnotationPresent(Class<?> clazz, Class<? extends Annotation> annotationClazz);

	public boolean isAnnotationPresent(Method method, Class<? extends Annotation> annotationClazz);

	public Method[] getMethods(Class<?> clazz) throws SecurityException;

	public Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) throws NoSuchMethodException, SecurityException;

	public <A extends Annotation> A getAnnotation(Method getterMethod, Class<A> class1);

}
