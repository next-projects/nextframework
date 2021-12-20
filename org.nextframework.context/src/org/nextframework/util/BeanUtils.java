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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.core.standard.Next;
import org.nextframework.exception.NextException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.MessageSourceResolvable;

/**
 * @author rogelgarcia
 * @since 22/01/2006
 * @version 1.1
 */
public class BeanUtils {

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

	public Enum<?>[] getEnumItems(Class<Enum<?>> enumClass) {
		try {
			Method method = enumClass.getMethod("values");
			Enum<?>[] enumValues = (Enum[]) method.invoke(null);
			return enumValues;
		} catch (Exception ex) {
			throw new NextException("Erro ao obter itens do enum " + enumClass + ".");
		}
	}

	public <T extends Enum<T>> T getEnumItem(Class<T> enumClass, String name, T defaultValue) {
		if (Util.strings.isEmpty(name)) {
			return defaultValue != null ? defaultValue : null;
		}
		try {
			return Enum.valueOf(enumClass, name);
		} catch (IllegalStateException ex) {
			if (defaultValue != null) {
				return defaultValue;
			}
			throw ex;
		}
	}

	public String getDisplayName(Class<?> beanClass) {
		return getDisplayName(BeanDescriptorFactory.forClass(beanClass), null, null);
	}

	public String getDisplayName(Class<?> beanClass, Locale locale) {
		return getDisplayName(BeanDescriptorFactory.forClass(beanClass), null, locale);
	}

	public String getDisplayName(BeanDescriptor beanDescriptor, Locale locale) {
		return getDisplayName(beanDescriptor, null, locale);
	}

	public String getDisplayName(BeanDescriptor beanDescriptor, String optionalPrefix, Locale locale) {
		MessageSourceResolvable resolvable = getDisplayNameResolvable(beanDescriptor, optionalPrefix);
		return Next.getMessageSource().getMessage(resolvable, locale);
	}

	public MessageSourceResolvable getDisplayNameResolvable(Class<?> beanClass) {
		return getDisplayNameResolvable(BeanDescriptorFactory.forClass(beanClass), null);
	}

	public MessageSourceResolvable getDisplayNameResolvable(BeanDescriptor beanDescriptor) {
		return getDisplayNameResolvable(beanDescriptor, null);
	}

	public MessageSourceResolvable getDisplayNameResolvable(BeanDescriptor beanDescriptor, String optionalPrefix) {

		Class<?> beanClass = Util.objects.getRealClass(beanDescriptor.getTargetClass());

		boolean usePrefix = optionalPrefix != null && optionalPrefix.length() > 0;
		String[] codes = new String[usePrefix ? 4 : 2];

		int index = 0;
		if (usePrefix) {
			//Fully-qualified class name with prefix (Ex: prefix.com.app.pack.ClassName)
			codes[index++] = optionalPrefix + "." + beanClass.getName();
			//Simple class name with prefix (Ex: prefix.ClassName)
			codes[index++] = optionalPrefix + "." + beanClass.getSimpleName();
		}

		//Fully-qualified class name (Ex: com.app.pack.ClassName)
		codes[index++] = beanClass.getName();
		//Simple class name (Ex: ClassName)
		codes[index++] = beanClass.getSimpleName();

		return Util.objects.newMessage(codes, null, beanDescriptor.getDisplayName());
	}

	public String getDisplayName(Class<?> beanClass, String property, Locale locale) {
		BeanDescriptor bd = BeanDescriptorFactory.forClass(beanClass);
		PropertyDescriptor propertyDescriptorBegin = bd.getPropertyDescriptor(property);
		return getDisplayName(propertyDescriptorBegin, null, locale);
	}

	public String getDisplayName(PropertyDescriptor propertyDescriptor, Locale locale) {
		return getDisplayName(propertyDescriptor, null, locale);
	}

	public String getDisplayName(PropertyDescriptor propertyDescriptor, String optionalPrefix, Locale locale) {
		MessageSourceResolvable resolvable = getDisplayNameResolvable(propertyDescriptor, optionalPrefix);
		return Next.getMessageSource().getMessage(resolvable, locale);
	}

	public MessageSourceResolvable getDisplayNameResolvable(Class<?> beanClass, String property) {
		BeanDescriptor bd = BeanDescriptorFactory.forClass(beanClass);
		PropertyDescriptor propertyDescriptorBegin = bd.getPropertyDescriptor(property);
		return getDisplayNameResolvable(propertyDescriptorBegin, null);
	}

	public MessageSourceResolvable getDisplayNameResolvable(PropertyDescriptor propertyDescriptor) {
		return getDisplayNameResolvable(propertyDescriptor, null);
	}

	public MessageSourceResolvable getDisplayNameResolvable(PropertyDescriptor propertyDescriptor, String optionalPrefix) {

		Class<?> ownerClass = Util.objects.getRealClass(propertyDescriptor.getOwnerClass());
		String prop = propertyDescriptor.getName();

		boolean usePrefix = optionalPrefix != null && optionalPrefix.length() > 0;
		String[] codes = new String[usePrefix ? 4 : 2];

		int index = 0;
		if (usePrefix) {
			//Fully-qualified class name and property with prefix (Ex: prefix.com.app.pack.ClassName.name)
			codes[index++] = optionalPrefix + "." + ownerClass.getName() + "." + prop;
			//Simple class name and property  with prefix (Ex: prefix.ClassName.name)
			codes[index++] = optionalPrefix + "." + ownerClass.getSimpleName() + "." + prop;
		}

		//Fully-qualified class name and property (Ex: com.app.pack.ClassName.name)
		codes[index++] = ownerClass.getName() + "." + prop;
		//Simple class name and property (Ex: ClassName.name)
		codes[index++] = ownerClass.getSimpleName() + "." + prop;

		return Util.objects.newMessage(codes, null, propertyDescriptor.getDisplayName());
	}

	public MessageSourceResolvable getCustomFieldResolvable(Class<?> beanClass, String field) {

		beanClass = Util.objects.getRealClass(beanClass);

		String[] codes = new String[2];
		//Fully-qualified class name and property (Ex: com.app.pack.ClassName.name)
		codes[0] = beanClass.getName() + "." + field;
		//Simple class name and property (Ex: ClassName.name)
		codes[1] = beanClass.getSimpleName() + "." + field;

		return Util.objects.newMessage(codes, null, field);
	}

	public MessageSourceResolvable getEnumResolvable(Enum<?> enumItem, String property) {
		String[] codes = new String[2];
		//Fully-qualified class name and property (Ex: com.app.pack.ClassName.name)
		codes[0] = enumItem.getClass().getName() + "." + enumItem.name() + "." + property;
		//Simple class name and property (Ex: ClassName.name)
		codes[1] = enumItem.getClass().getSimpleName() + "." + enumItem.name() + "." + property;
		return Util.objects.newMessage(codes);
	}

	public MessageSourceResolvable getEnumResolvable(Enum<?> enumItem, String property, Object[] arguments) {
		String code = enumItem.getClass().getName() + "." + enumItem.name() + "." + property;
		return Util.objects.newMessage(code, arguments);
	}

}