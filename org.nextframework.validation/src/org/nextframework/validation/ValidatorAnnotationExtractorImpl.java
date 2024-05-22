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

public class ValidatorAnnotationExtractorImpl implements ValidatorAnnotationExtractor {

	public static final String VALIDATOR_ANNOTATION_EXTRACTOR_BEAN_NAME = "validatorAnnotationExtractor";

	// private static final Log log = LogFactory.getLog(ObjectAnnotationValidator.class);

	// registro com as validações possíveis
	protected ValidatorRegistry validatorRegistry;

	public ValidatorAnnotationExtractorImpl(ValidatorRegistry registry) {
		validatorRegistry = registry;
	}

//	public List<ValidationItem> getValidationItens(Class clazz) {
//		
//		Method[] metodos = clazz.getMethods();
//		
//		//List<Method> methodsRequiringValidation = new ArrayList<Method>();
//		List<ValidationItem> validationItens = new ArrayList<ValidationItem>();
//		for (Method method : metodos) {
//			ValidationItem validationItem = getValidationItemFromMethod(method);
//			if(validationItem.validationSize()>0){
//				validationItens.add(validationItem);	
//			}
//		}
//		return validationItens;
//	}

	/* (non-Javadoc)
	 * @see org.nextframework.validation.ValidatorAnnotationExtractor#getValidationItem(java.lang.String, java.lang.Class, java.lang.annotation.Annotation[])
	 */
	public ValidationItem getValidationItem(String fieldName, Class<?> type, Annotation[] annotations) {
		//boolean validMethod = isGetterMethod(method);
		ValidationItem validationItem = new ValidationItem();
		//validationItem.setValidatorRegistry(getValidatorRegistry());
		validationItem.setFieldName(fieldName);
		//validationItem.setMethod(method);
		Annotation[] methodAnnotations = annotations;
		for (Annotation annotation : methodAnnotations) {
			if (validatorRegistry.getPropertyValidator(annotation.annotationType()) != null) {
				// se entrar aqui quer dizer que essa anotação foi registrada para validação
//				if (validMethod) {
//					//methodsRequiringValidation.add(method);
//				} else {
//					throw new RuntimeException("O método "+method.getName()+" possui uma anotação de validação "+annotation+". " +
//							"Somente métodos getters podem ter anotações de validação. " +
//							"O método não será incluido na validação");
//				}
				//validationItem.setMethod(method);
				validationItem.addValidation(annotation);
			}
		}
		Class<?> returnType = type;
		PropertyValidator typeValidator = validatorRegistry.getTypeValidator(returnType);
		if (typeValidator != null) {
			validationItem.setTypeValidator(typeValidator);
		}
		return validationItem;
	}

	public ValidationItem getValidationItem(String fieldName, String type, Annotation[] annotations) {
		//boolean validMethod = isGetterMethod(method);
		ValidationItem validationItem = new ValidationItem();
		//validationItem.setValidatorRegistry(getValidatorRegistry());
		validationItem.setFieldName(fieldName);
		//validationItem.setMethod(method);
		Annotation[] methodAnnotations = annotations;
		for (Annotation annotation : methodAnnotations) {
			if (validatorRegistry.getPropertyValidator(annotation.annotationType()) != null) {
				// se entrar aqui quer dizer que essa anotação foi registrada para validação
//				if (validMethod) {
//					//methodsRequiringValidation.add(method);
//				} else {
//					throw new RuntimeException("O método "+method.getName()+" possui uma anotação de validação "+annotation+". " +
//							"Somente métodos getters podem ter anotações de validação. " +
//							"O método não será incluido na validação");
//				}
				//validationItem.setMethod(method);
				validationItem.addValidation(annotation);
			}
		}
		String returnType = type;
		PropertyValidator typeValidator = validatorRegistry.getTypeValidator(returnType);
		if (typeValidator != null) {
			validationItem.setTypeValidator(typeValidator);
		}
		return validationItem;
	}

//	private boolean isGetterMethod(Method method) {
//		if(method.getName().startsWith("get")||method.getName().startsWith("is")){
//			if (method.getParameterTypes().length==0) {
//				if (!method.getReturnType().equals(Void.class)) {
//					return true;
//				}
//			}
//		}
//		return false;
//	}

//	public static ValidatorAnnotationExtractor getValidatorAnnotationExtractor(ServletContext servletContext){
//		ValidatorAnnotationExtractor extractor = (ValidatorAnnotationExtractor) WebApplicationContextUtils.getWebApplicationContext(servletContext).getBean(VALIDATOR_ANNOTATION_EXTRACTOR_BEAN_NAME);
//		return extractor;
//	}

}
