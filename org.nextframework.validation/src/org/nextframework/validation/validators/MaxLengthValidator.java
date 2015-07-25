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
import java.util.List;

import org.apache.commons.validator.GenericValidator;
import org.nextframework.validation.JavascriptValidationItem;
import org.nextframework.validation.ObjectAnnotationValidator;
import org.nextframework.validation.PropertyValidator;
import org.nextframework.validation.annotation.MaxLength;
import org.springframework.validation.Errors;


public class MaxLengthValidator implements PropertyValidator{

	public void validate(Object bean, Object property, String fieldName, String fieldDisplayName, Annotation annotation, Errors errors, ObjectAnnotationValidator annotationValidator) {
		if (property!=null && !property.toString().trim().equals("")) {
			MaxLength maxLength = (MaxLength) annotation;
			int max = maxLength.value();
			if (!GenericValidator.maxLength(property.toString(), max)) {
				errors.rejectValue(fieldName, "maxLenght", "O campo "+fieldDisplayName+" deve ter um tamanho menor ou igual � "+max);
			}
		}
		
	}

	public String getValidationName() {
		return "maxlength";
	}

	public String getValidationFunctionName() {
		return "MaxLength";
	}

	public String getJavascriptFunctionPath() {
		return "org/nextframework/validation/validators/javascript/validateMaxLength.js";
	}

	public String getJavascriptFunction(JavascriptValidationItem validationItem) {
		List<Annotation> validations = validationItem.getValidations();
		int max = 0;
		for (Annotation annotation : validations) {
			if(MaxLength.class.isAssignableFrom(annotation.getClass())){
				max = ((MaxLength)annotation).value();
				break;
			}
		}
		return "new Function (\"varName\", \"this.maxlength='"+max+"';  return this[varName];\")";
	}

	public String getMessage(JavascriptValidationItem validationItem) {
		List<Annotation> validations = validationItem.getValidations();
		int max = 0;
		for (Annotation annotation : validations) {
			if(MaxLength.class.isAssignableFrom(annotation.getClass())){
				max = ((MaxLength)annotation).value();
				break;
			}
		}
		return "O campo "+validationItem.getFieldDisplayName()+" deve ter um tamanho menor que "+max;
	}

}
