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
import java.util.ArrayList;
import java.util.List;

public class ValidationItem {

	protected List<Annotation> validations = new ArrayList<Annotation>();
	protected String fieldName;

	protected PropertyValidator typeValidator;

	//registro com o qual esse validation item foi gerado
//	protected ValidatorRegistry validatorRegistry;
//	
//	public ValidatorRegistry getValidatorRegistry() {
//		return validatorRegistry;
//	}
//
//	public void setValidatorRegistry(ValidatorRegistry validatorRegistry) {
//		this.validatorRegistry = validatorRegistry;
//	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldName() {
//		if(fieldName == null && method!=null){
//			// achar o nome do field pelo Método
//			if (method.getName().startsWith("get")) {
//				//get?
//				fieldName = method.getName().substring(3);
//			} else {
//				//is?
//				fieldName = method.getName().substring(2);
//			}
//			fieldName = StringUtils.uncapitalize(fieldName);
//		}
		return fieldName;
	}

	public ValidationItem(String fieldName, List<Annotation> validations) {
		super();
		this.fieldName = fieldName;
		this.validations = validations;
	}

	public ValidationItem() {
	}

	public List<Annotation> getValidations() {
		return validations;
	}

	public void setValidations(List<Annotation> validations) {
		this.validations = validations;
	}

	public boolean addValidation(Annotation o) {
		return validations.add(o);
	}

	public int validationSize() {
		return validations.size();
	}

	public PropertyValidator getTypeValidator() {
		return typeValidator;
	}

	public void setTypeValidator(PropertyValidator typeValidator) {
		this.typeValidator = typeValidator;
	}

}
