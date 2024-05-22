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
/*
 * Criado em 14/04/2005
 *
 */
package org.nextframework.bean.editors;

import java.beans.PropertyEditorSupport;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * @author rogelgarcia
 */
public class ValueBasedPropertyEditor extends PropertyEditorSupport {

	public ValueBasedPropertyEditor() {

	}

	public void setAsText(String text) throws IllegalArgumentException {
		if (text == null || text.equals("<null>")) {
			setValue(null);
			return;
		}
		if (!text.matches("\\w*((\\.\\w*)*)\\[((.)*)\\]")) {
			throw new IllegalArgumentException("ValueBasedPropertyEditor: Valor não suportado " + text);
		}
		int classSeparator = text.indexOf('[');
		String[] properties = text.substring(classSeparator + 1, text.length() - 1).split(",");
		Map<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < properties.length; i++) {
			if (properties[i].equals(""))
				continue;
			String[] nameValue = properties[i].split("=");
			String name = nameValue[0];
			String value = nameValue[1];
			map.put(name, value);
		}
		Class<?> clazz = null;
		if (clazz == null) {
			try {
				clazz = Class.forName(text.substring(0, classSeparator));
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException("Não existe uma classe para o parametro " + text, e);
			}
		}
		//changed from ExtendedBeanWrapper to BeanWrapperImpl
		BeanWrapper beanWrapper = new BeanWrapperImpl(clazz);
		beanWrapper.setPropertyValues(map);
		setValue(beanWrapper.getWrappedInstance());
	}

}
