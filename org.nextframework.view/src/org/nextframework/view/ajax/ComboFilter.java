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
package org.nextframework.view.ajax;

import java.util.List;

import org.nextframework.authorization.Authorization;
import org.nextframework.controller.ExtendedBeanWrapper;
import org.nextframework.controller.ServletRequestDataBinderNext;
import org.nextframework.view.ComboReloadGroupTag;

public class ComboFilter {

	Class<?> type;
	Object parentValue;
	String loadFunction;
	String label;
	String parameterList;
	String classesList;
	public Class<?>[] getClasses(){
		if(classesList.length() == 0){
			return new Class[0];
		}
		String[] split = classesList.split(ComboReloadGroupTag.CLASS_SEPARATOR);
		Class<?>[] classes = new Class[split.length];
		for (int i = 0; i < classes.length; i++) {
			try {
				classes[i] = Class.forName(split[i]);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Classe "+split[i] +" não encontrada");
			}
		}
		return classes;
	}
	
	public Object[] getValues(Class<?>[] classes){
		if(classes.length == 0){
			return new Object[0];
		}
		Object[] values = new Object[classes.length];
		String[] split = parameterList.split(ComboReloadGroupTag.PARAMETER_SEPARATOR);
		ExtendedBeanWrapper beanWrapper = new ExtendedBeanWrapper();
		for (int i = 0; i < split.length; i++) {
			Object value = split[i];
			if(ServletRequestDataBinderNext.isObjectValue(value)){
				//Quando chega uma string "com.app.Bean[id=1],com.app.Bean[id=2]", quebra em um array
				if (List.class.isAssignableFrom(classes[i]) && value instanceof String && ((String)value).contains(",") ) {
					value = ((String)value).split(",");
				}
				value = ServletRequestDataBinderNext.translateObjectValue("[?]", value, null);
			}
			if("user".equals(value)){
				value = Authorization.getUserLocator().getUser();
			}
			if(!classes[i].equals(Void.class)){
				values[i] = beanWrapper.convertIfNecessary(value, classes[i]);
			} else {
				values[i] = value;
			}
		}
		return values;
	}
	
	public String getClassesList() {
		return classesList;
	}
	public void setClassesList(String classesList) {
		this.classesList = classesList;
	}
	public String getParameterList() {
		return parameterList;
	}
	public void setParameterList(String parameterList) {
		this.parameterList = parameterList;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}

	public String getLoadFunction() {
		return loadFunction;
	}
	public void setLoadFunction(String loadFunction) {
		this.loadFunction = loadFunction;
	}

	public Object getParentValue() {
		return parentValue;
	}
	public void setParentValue(Object parentValue) {
		this.parentValue = parentValue;
	}
	public Class<?> getType() {
		return type;
	}
	public void setType(Class<?> type) {
		this.type = type;
	}
}
