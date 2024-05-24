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
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.core.standard.Next;
import org.nextframework.exception.NextException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.NotWritablePropertyException;
import org.springframework.beans.NullValueInNestedPathException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.context.MessageSourceResolvable;

/**
 * @author rogelgarcia
 * @since 22/01/2006
 * @version 1.1
 */
public class BeanUtils {

	public Object getId(Object obj) {
		BeanDescriptor beanDescriptor = BeanDescriptorFactory.forBean(obj);
		return beanDescriptor.getId();
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

	public Set<Method> getPropertyGetters(Class<?> clazz) {
		Set<Method> getters = new HashSet<Method>();
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (isGetter(method)) {
				getters.add(method);
			}
		}
		return getters;
	}

	public boolean isGetter(Method method) {
		return (method.getName().startsWith("get") || method.getName().startsWith("is"))
				&& !method.getReturnType().isAssignableFrom(Void.TYPE)
				&& method.getParameterTypes().length == 0;
	}

	public boolean isSetter(Method method) {
		return (method.getName().startsWith("set"))
				&& method.getReturnType().isAssignableFrom(Void.TYPE)
				&& method.getParameterTypes().length == 1;
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

	public Object getPropertyValueNullSafe(Object bean, String property) {
		try {
			return getPropertyValue(bean, property);
		} catch (NullValueInNestedPathException e) {
			//Se o caminho está incompleto até o valor, considera nulo mesmo. 
			return null;
		}
	}

	public Object getPropertyValue(Object bean, String property) {
		return PropertyAccessorFactory.forBeanPropertyAccess(bean).getPropertyValue(property);
	}

	public void setPropertyValueNullSafe(Object bean, String property, Object value) {
		BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(bean);
		try {
			bw.setPropertyValue(property, value);
		} catch (NullValueInNestedPathException e) {
			//Se o caminho está incompleto, mas existe valor a ser definido, completa.
			if (value != null) {
				bw = PropertyAccessorFactory.forBeanPropertyAccess(bean); //buscar instancia novamente para limpar o cache de 'setAutoGrowNestedPaths' 
				bw.setAutoGrowNestedPaths(true);
				bw.setPropertyValue(property, value);
			}
		}
	}

	public void setPropertyValue(Object bean, String property, Object value) {
		PropertyAccessorFactory.forBeanPropertyAccess(bean).setPropertyValue(property, value);
	}

	public <B> void copyAttributes(B bOrigem, B bDestivo, String... atributos) {
		for (String attributo : atributos) {
			Object valor = getPropertyValueNullSafe(bOrigem, attributo);
			setPropertyValueNullSafe(bDestivo, attributo, valor);
		}
	}

	@SuppressWarnings("all")
	public <E> E clone(E o) {
		BeanDescriptor baseDescriptor = BeanDescriptorFactory.forBean(o);
		E o2;
		try {
			o2 = (E) o.getClass().newInstance();
			BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(o2);
			PropertyDescriptor[] propertyDescriptors = baseDescriptor.getPropertyDescriptors();
			for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
				if (propertyDescriptor.getName().equals("class")) {
					continue;
				}
				Object value = propertyDescriptor.getValue();
				try {
					wrapper.setPropertyValue(propertyDescriptor.getName(), value);
				} catch (NotWritablePropertyException e) {
					//if the property is not writable.. do not write
				}
			}
		} catch (InstantiationException e1) {
			throw new NextException("Não foi possível clonar o bean. Não foi possível criar outra instancia.", e1);
		} catch (IllegalAccessException e1) {
			throw new NextException("Não foi possível clonar o bean. Acesso ilegal.", e1);
		} catch (BeansException e) {
			throw new NextException("Não foi possível clonar o bean. ", e);
		}
		return o2;
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
		String displayName = propertyDescriptor.getDisplayName();
		if (prop.contains("[")) {
			prop = prop.split("\\[")[0];
			displayName = displayName.split("\\[")[0];
		}

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

		return Util.objects.newMessage(codes, null, displayName);
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