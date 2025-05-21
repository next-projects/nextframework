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

import org.nextframework.view.ComboReloadGroupTag;

public class ComboFilter {

	private Class<?> type;
	private Object parentValue;
	private String loadFunction;
	private String label;
	private String parameterList;
	private String classesList;

	public Class<?>[] getClasses() {
		if (classesList.length() == 0) {
			return new Class[0];
		}
		String[] split = classesList.split(ComboReloadGroupTag.CLASS_SEPARATOR);
		Class<?>[] classes = new Class[split.length];
		for (int i = 0; i < classes.length; i++) {
			try {
				classes[i] = Class.forName(split[i]);
			} catch (ClassNotFoundException e) {
				throw new RuntimeException("Classe " + split[i] + " não encontrada");
			}
		}
		return classes;
	}

	public String getClassesList() {
		return classesList;
	}

	public void setClassesList(String classesList) {
		this.classesList = classesList;
	}

	public String[] getParameter() {
		return parameterList.split(ComboReloadGroupTag.PARAMETER_SEPARATOR);
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
