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
package org.nextframework.bean.internal;

import java.lang.annotation.Annotation;

import org.nextframework.bean.annotation.DisplayName;
import org.springframework.util.StringUtils;

public class DisplayNameUtils {

	
	/**
	 * Returns the displayName of a class
	 * @param clazz
	 * @return
	 */
	public static String getDisplayName(Class<?> clazz) {
		//String displayName = cacheClassDisplayName.get(clazz);
		String displayName = null;
		
		if (displayName == null) {
			if (clazz.isAnnotationPresent(DisplayName.class)) {
				displayName = clazz.getAnnotation(DisplayName.class).value();
			}
//			if(displayName == null){
//				//I18N
//				String[] names = {
//						clazz.getName(),
//						clazz.getSimpleName(),
//						StringUtils.uncapitalize(clazz.getSimpleName())
//				};
//				for (int i = 0; i < names.length; i++) {
//					displayName = locale.getBundleKey(names[i]);	
//					if(displayName != null)
//						break;	
//				}				
//			}
			if(displayName == null){
				displayName = separateOnCase(clazz.getSimpleName());
			}
		}
		return displayName;
	}
	
	

	public static String getDisplayName(Class<?> clazz, String propertyName, Annotation[] annotations){
		//TODO NÃO FAZER CACHE PARA NAO CRIAR REFERENCIAS PARA CLASSES DINAMICAS (VERIRIFCAR FORMA DE FAZER)
//		boolean reloadCache = reloadCache();
//		if(reloadCache){ // nao é thread safe... o arquivo displayNames não deve ser modificado quando multiplas Threads estiverem sendo utilizadas
//			cachePropertyDisplayName = new HashMap<Class<?>, Map<String, String>>();
//		}
		
		String displayName = null;
		
//		Map<String, String> map = cachePropertyDisplayName.get(clazz);
//		if(map == null){
//			map = new HashMap<String, String>();
//			cachePropertyDisplayName.put(clazz, map);
//		} else {
//			displayName = map.get(propertyName);
//		}
		
		
		//String displayName = map.get(propertyName);
		
		if (displayName == null) {
			
			for (Annotation anno : annotations) {
				if (DisplayName.class.isAssignableFrom(anno.annotationType())) {
					displayName = ((DisplayName) anno).value();
				}
			}
//			if(displayName == null){ // tentar do displayNames.properties
//				//I18N
//				String[] names = {
//						clazz.getName()+"."+propertyName,
//						clazz.getSimpleName()+"."+propertyName,
//						strings.uncaptalize(clazz.getSimpleName())+"."+propertyName,
//						propertyName
//				};
//				for (int i = 0; i < names.length; i++) {
//					displayName = locale.getBundleKey(names[i]);
//					if (displayName != null)
//						break;
//				}
//			}
			if (displayName == null) {
				displayName = convertPropertyNameToDisplayName(propertyName);
			}
			
//			map.put(propertyName, displayName);
		}
		return displayName;
	}

	static String convertPropertyNameToDisplayName(String propertyName) {
		return separateOnCase(StringUtils.capitalize(propertyName)).replace('_', ' ');
	}

	/**
	 * Separa as palavras da string toda vez que trocar o case
	 * @param string
	 * @return
	 */
	private static String separateOnCase(String string) {
		if(string.length() <= 1){
			return string;
		}
		char[] toCharArray = string.substring(1).toCharArray();
		StringBuilder builder = new StringBuilder();
		builder.append(string.charAt(0));
		for (char c : toCharArray) {
			if(Character.isUpperCase(c)){
				builder.append(" ");
			}
			builder.append(c);
		}
		return builder.toString();
	}

}
