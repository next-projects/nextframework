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
import org.nextframework.validation.annotation.MinValue;
import org.springframework.validation.Errors;


public class MinValueValidator implements PropertyValidator {

	public void validate(Object bean, Object property, String fieldName, String fieldDisplayName, Annotation annotation, Errors errors, ObjectAnnotationValidator annotationValidator) {
		if (property!=null && !property.toString().trim().equals("")) {
			MinValue minValue = (MinValue) annotation;
			double min = minValue.value();
			double atual = Double.parseDouble(property.toString());
			if (atual < min) {
				errors.rejectValue(fieldName, "minValue", "O campo "+fieldDisplayName+" deve ter um valor maior ou igual a "+min);
			}
		}

	}

	public String getValidationName() {
		return "floatMinValue";
	}

	public String getValidationFunctionName() {
		return "FloatMinValue";
	}

	public String getJavascriptFunctionPath() {
		return "org/nextframework/validation/validators/javascript/validateFloatMinValue.js";
	}

	public String getJavascriptFunction(JavascriptValidationItem validationItem) {
		List<Annotation> validations = validationItem.getValidations();
		double min = 0;
		for (Annotation annotation : validations) {
			if(MinValue.class.isAssignableFrom(annotation.getClass())){
				min = ((MinValue)annotation).value();
				break;
			}
		}
		return "new Function (\"varName\", \"this.min='"+min+"';  return this[varName];\")";
	}

	public String getMessage(JavascriptValidationItem validationItem) {
		List<Annotation> validations = validationItem.getValidations();
		double min = 0;
		for (Annotation annotation : validations) {
			if(MinValue.class.isAssignableFrom(annotation.getClass())){
				min = ((MinValue)annotation).value();
				break;
			}
		}
		String minValue = String.valueOf(min);
		if(new Integer((int)min).doubleValue() == min){
			minValue = NumberFormat.getIntegerInstance().format(min);
		}
		return "O campo "+validationItem.getFieldDisplayName()+" deve ter valor maior ou igual a "+minValue;
	}

}
