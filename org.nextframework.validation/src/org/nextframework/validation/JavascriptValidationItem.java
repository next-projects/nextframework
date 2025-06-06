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
package org.nextframework.validation;

import java.lang.annotation.Annotation;
import java.util.List;

public class JavascriptValidationItem {

	private ValidationItem item;
	private String fieldDisplayName;
	private String fieldName;

	public JavascriptValidationItem(ValidationItem item) {
		this.item = item;
	}

	public String getFieldDisplayName() {
		return fieldDisplayName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public List<Annotation> getValidations() {
		return item.getValidations();
	}

	public PropertyValidator getTypeValidator() {
		return item.getTypeValidator();
	}

	public void setFieldDisplayName(String fieldDescription) {
		this.fieldDisplayName = fieldDescription;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

}
