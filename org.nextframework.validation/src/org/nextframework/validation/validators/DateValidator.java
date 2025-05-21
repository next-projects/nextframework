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
package org.nextframework.validation.validators;

import java.lang.annotation.Annotation;

import org.apache.commons.validator.GenericValidator;
import org.nextframework.validation.JavascriptValidationItem;
import org.nextframework.validation.ObjectAnnotationValidator;
import org.nextframework.validation.PropertyValidator;
import org.springframework.validation.Errors;

public class DateValidator implements PropertyValidator {

	public void validate(Object bean, Object value, String fieldName, String fieldDisplayName, Annotation annotation, Errors errors, ObjectAnnotationValidator annotationValidator) {
		if (value != null && !value.toString().trim().equals("")) {
			if (!GenericValidator.isDate(value.toString(), "dd/MM/yyyy", true)) {
				errors.rejectValue(fieldName, "data", "O campo \\\"" + fieldDisplayName + "\\\" não é uma data válida");
			}
		}
	}

	public String getValidationName() {
		return "DateValidations";
	}

	public String getJavascriptFunctionPath() {
		return "org/nextframework/validation/validators/javascript/validateDate.js";
	}

	public String getJavascriptFunction(JavascriptValidationItem validationItem) {
		return "new Function (\"varName\", \"this.datePatternStrict='dd/MM/yyyy';  return this[varName];\")";
	}

	public String getMessage(JavascriptValidationItem validationItem) {
		return "O campo \\\"" + validationItem.getFieldDisplayName() + "\\\" não é uma data válida";
	}

	public String getValidationFunctionName() {
		return "Date";
	}

}
