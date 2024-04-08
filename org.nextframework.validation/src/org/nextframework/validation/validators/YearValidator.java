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

/**
 * Validador para campos email
 * 
 */
public class YearValidator implements PropertyValidator {

	public void validate(Object bean, Object value, String fieldName,
			String fieldDisplayName, Annotation annotation, Errors errors, ObjectAnnotationValidator annotationValidator) {
		if (value != null && value instanceof Number) {
			if (!GenericValidator.isInRange(((Number) value).intValue(), 1950, 2050)) {
				errors.rejectValue(fieldName, "year", "O campo " + fieldDisplayName + " deve ser um ano (1950 até 2050)");
			}
		}
	}

	public String getValidationName() {
		return "year";
	}

	public String getJavascriptFunctionPath() {
		return "org/nextframework/validation/validators/javascript/validateYear.js";
	}

	public String getMessage(JavascriptValidationItem validationItem) {
		return "O campo " + validationItem.getFieldDisplayName() + " deve ser um ano válido (de 1950 até 2050)";
	}

	public String getJavascriptFunction(JavascriptValidationItem validationItem) {
		return "new Function (\"varName\", \"this.min='1950'; this.max='2050';this.length='4';  return this[varName];\")";
	}

	public String getValidationFunctionName() {
		return "Year";
	}

}
