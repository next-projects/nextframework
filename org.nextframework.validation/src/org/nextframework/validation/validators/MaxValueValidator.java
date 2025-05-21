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
import java.text.NumberFormat;
import java.util.List;

import org.nextframework.validation.JavascriptValidationItem;
import org.nextframework.validation.ObjectAnnotationValidator;
import org.nextframework.validation.PropertyValidator;
import org.nextframework.validation.annotation.MaxValue;
import org.springframework.validation.Errors;

public class MaxValueValidator implements PropertyValidator {

	public void validate(Object bean, Object value, String fieldName, String fieldDisplayName, Annotation annotation, Errors errors, ObjectAnnotationValidator annotationValidator) {
		if (value != null && !value.toString().trim().equals("")) {
			MaxValue maxValue = (MaxValue) annotation;
			double max = maxValue.value();
			double atual = Double.parseDouble(value.toString());
			if (atual > max) {
				errors.rejectValue(fieldName, "maxValue", "O campo " + fieldDisplayName + " deve ter um valor menor ou igual a " + max);
			}
		}
	}

	public String getValidationName() {
		return "floatMaxValue";
	}

	public String getValidationFunctionName() {
		return "FloatMaxValue";
	}

	public String getJavascriptFunctionPath() {
		return "org/nextframework/validation/validators/javascript/validateFloatMaxValue.js";
	}

	public String getJavascriptFunction(JavascriptValidationItem validationItem) {
		List<Annotation> validations = validationItem.getValidations();
		double max = 0;
		for (Annotation annotation : validations) {
			if (MaxValue.class.isAssignableFrom(annotation.getClass())) {
				max = ((MaxValue) annotation).value();
				break;
			}
		}
		return "new Function (\"varName\", \"this.max='" + max + "';  return this[varName];\")";
	}

	public String getMessage(JavascriptValidationItem validationItem) {
		List<Annotation> validations = validationItem.getValidations();
		double max = 0;
		for (Annotation annotation : validations) {
			if (MaxValue.class.isAssignableFrom(annotation.getClass())) {
				max = ((MaxValue) annotation).value();
				break;
			}
		}
		String maxValue = String.valueOf(max);
		if (new Integer((int) max).doubleValue() == max) {
			maxValue = NumberFormat.getIntegerInstance().format(max);
		}
		return "O campo " + validationItem.getFieldDisplayName() + " deve ter valor menor ou igual a " + maxValue;
	}

}
