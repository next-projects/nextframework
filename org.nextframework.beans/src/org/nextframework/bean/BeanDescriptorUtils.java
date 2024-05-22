/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2012 the original author or authors.
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
package org.nextframework.bean;

import java.lang.reflect.Method;

import org.springframework.util.StringUtils;

public class BeanDescriptorUtils {

	public static String getPropertyFromGetter(String getterMethodName) {
		if (getterMethodName.startsWith("i")) {
			return StringUtils.uncapitalize(getterMethodName.substring(2));
		} else {
			return StringUtils.uncapitalize(getterMethodName.substring(3));
		}
	}

	public static Method getGetterMethod(Class<?> clazz, String property) {
		Method[] methods = clazz.getMethods();
		Method method = null;
		String getterName = "get" + StringUtils.capitalize(property);
		String getterName1 = "is" + StringUtils.capitalize(property);
		for (int j = 0; j < methods.length; j++) {
			if (isGetter(methods[j]) && (methods[j].getName().equals(getterName) || methods[j].getName().equals(getterName1))) {
				method = methods[j];
				return method;
			}
		}
		return null;
	}

	public static String getGetterFromProperty(String propertyName) {
		return "get" + StringUtils.capitalize(propertyName);
	}

	public static boolean isGetter(Method method) {
		return (method.getName().startsWith("is") || method.getName().startsWith("get")) && method.getParameterTypes().length == 0;
	}

}
