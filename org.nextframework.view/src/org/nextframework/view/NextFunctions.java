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
package org.nextframework.view;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collection;

import javax.persistence.Id;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ELException;

import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.annotation.DescriptionProperty;
import org.nextframework.core.web.NextWeb;
import org.nextframework.persistence.HibernateUtils;
import org.nextframework.util.ReflectionCache;
import org.nextframework.util.ReflectionCacheFactory;
import org.nextframework.util.Util;

/**
 * @author rogelgarcia
 * @since 05/02/2006
 * @version 1.1
 */
@SuppressWarnings("deprecation")
public class NextFunctions {
	
	public static Integer size(Collection<?> collection){
		if(collection == null){
			return null;
		}
		collection = HibernateUtils.getLazyValue(collection);
		return collection.size();
	}

	public static boolean contains(Collection<?> collection, Object o) {
		return collection.contains(o);
	}

	public static String hierarchy(Class<?> clazz) {
		StringBuilder sb = new StringBuilder();
		Class<?> parent = clazz;
		while(!Object.class.equals(parent)){
			sb.append("'"+parent.getName()+"', ");
			parent = parent.getSuperclass();
		}
		return sb.substring(0, sb.length()-2).toString();
	}
	
	public static Boolean isId(Annotation[] annotations){
		for (Annotation annotation : annotations) {
			if(annotation instanceof Id){
				return true;
			}
		}
		return false;
	}
	
	public static Object chooseDefault(Object def, Object opt){
		if(opt != null){
			if(opt instanceof String && ((String)opt).length() == 0){
				return def;
			} else {
				return opt;
			}
		}
		return def;
	}


	public static Object reevaluate(String expr, PageContext context) throws ELException{
		return ViewUtils.evaluate("${"+expr+"}", context, Object.class);
	}

	public static String descriptionToString(Object value){
		try {
			if (value == null)
				return "";
			if (hasDescriptionProperty(value.getClass())) {
				return Util.strings.toStringDescription(value);
			}
			return value.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Erro ao ler a descri��o do objeto. Talvez o problema esteja na propriedade com @DescriptionProperty.", e);
		}
	}
	
	public static String valueToString(Object value){
		if(value == null) return "";
		if(hasId(value.getClass())){
			return Util.strings.toStringIdStyled(value, true);
		}
		return value.toString();
	}
	
	public static String valueToString(Object value, Boolean includeDescription){
		if(includeDescription == null){
			includeDescription = true;
		}
		if(value == null) return "";
		if(hasId(value.getClass())){
			return Util.strings.toStringIdStyled(value, includeDescription);
		}
		return value.toString();
	}
	
	public static Object id(Object value){
		if(value == null){
			return null;
		}
		if(hasId(value.getClass())){
			return BeanDescriptorFactory.forBean(value).getId();
		} else {
			return null;
		}
	}
	
	public static String idProperty(Object value){
		if(value == null){
			return null;
		}
		if(hasId(value.getClass())){
			return BeanDescriptorFactory.forBean(value).getIdPropertyName();
		} else {
			return null;
		}
	}
	
	private static boolean hasDescriptionProperty(Class<? extends Object> class1) {
		ReflectionCache reflectionCache = ReflectionCacheFactory.getReflectionCache();
		Method[] methods = reflectionCache.getMethods(class1);
		for (Method method : methods) {
			if(reflectionCache.isAnnotationPresent(method, DescriptionProperty.class)){
				return true;
			}
		}
		return false;
	}
	
	private static boolean hasId(Class<? extends Object> class1) {
		ReflectionCache reflectionCache = ReflectionCacheFactory.getReflectionCache();
		Method[] methods = reflectionCache.getMethods(class1);
		for (Method method : methods) {
			if(reflectionCache.isAnnotationPresent(method, Id.class)){
				return true;
			}
		}
		return false;
	}
	
	public static Object ognl(String expression) {
		WebContextMap contextMap = new WebContextMap(NextWeb.getRequestContext().getServletRequest());			
		Object value = OgnlExpressionParser.parse(expression, contextMap);
		return value;
	}
	
	public static String escape(String s){
		if(s==null) return null;
		
		return s.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
	}
	
	public static Boolean isDetailProperty(String name){
		if(name == null){
			return false;
		}
		return name.contains("[");
	}
	
	public static Object printStackTrace(Throwable t){
		if(t != null){
			t.printStackTrace();
		}
		return null;
	}
}
