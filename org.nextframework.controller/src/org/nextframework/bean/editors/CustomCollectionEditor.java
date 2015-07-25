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
package org.nextframework.bean.editors;

import java.beans.PropertyEditor;
import java.util.Collection;

public class CustomCollectionEditor extends
		org.springframework.beans.propertyeditors.CustomCollectionEditor {

	@SuppressWarnings("rawtypes")
	public CustomCollectionEditor(Class<? extends Collection> collectionType) {
		super(collectionType);
	}

	@Override
	protected boolean alwaysCreateNewCollection() {
		return true;
	}

	@Override
	protected Object convertElement(Object element) {
		if(element == null) return null;
        String stringValue = element.toString();
		// TODO verificar se já existe um property editor para o required type e path
        if(stringValue.equals("<null>")||stringValue.matches("\\w*((\\.\\w*)*)\\[((.)*)\\]")){
            PropertyEditor editor = new ValueBasedPropertyEditor();
            editor.setAsText(stringValue);
            return editor.getValue();
        } else {
        	return super.convertElement(element);	
        }
		
	}

}
