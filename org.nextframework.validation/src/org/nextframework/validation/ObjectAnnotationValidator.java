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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.core.web.NextWeb;
import org.nextframework.util.Util;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.context.support.WebApplicationObjectSupport;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Valida um objeto baseado nas suas anotações
 * @author rogelgarcia | marcusabreu
 */
public class ObjectAnnotationValidator extends WebApplicationObjectSupport implements Validator {

	private ValidatorRegistry validatorRegistry;
	private HttpServletRequest servletRequest;

	//private static final Log log = LogFactory.getLog(ObjectAnnotationValidator.class);

	public ObjectAnnotationValidator(ValidatorRegistry validatorRegistry, HttpServletRequest servletRequest) {
		this.validatorRegistry = validatorRegistry;
		this.servletRequest = servletRequest;
	}

	public boolean supports(Class<?> clazz) {
		// suporta todas as classes
		return true;
	}

	@SuppressWarnings("rawtypes")
	public void validate(Object obj, Errors errors) {
		if (servletRequest instanceof MultipartHttpServletRequest) {
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) servletRequest;
			Iterator fileNames = multipartHttpServletRequest.getFileNames();
			while (fileNames.hasNext()) {
				String filename = (String) fileNames.next();
				MultipartFile file = multipartHttpServletRequest.getFile(filename);
				if (file.getOriginalFilename() != null && file.getOriginalFilename().length() > 0) {
					if (file.getSize() == 0) {
						errors.reject("", "O arquivo '" + filename + "' não pode ter tamanho 0 (zero)");
					}
				}
			}
		}
		Map<String, ?> parameterMap = servletRequest.getParameterMap();
		Set<String> keySet = parameterMap.keySet();
		BeanDescriptor beanDescriptor = BeanDescriptorFactory.forBean(obj);
		for (String parametro : keySet) {
			if (parametro.startsWith("_")) {
				parametro = parametro.substring(1);
			}
			validate(obj, errors, beanDescriptor, validatorRegistry, parametro);
		}
	}

	public void validate(Object obj, Errors errors, BeanDescriptor beanDescriptor, ValidatorRegistry validatorRegistry, String parametro) {
		PropertyDescriptor propertyDescriptor;
		try {
			propertyDescriptor = beanDescriptor.getPropertyDescriptor(parametro);
		} catch (NotReadablePropertyException e) {
			// se nao for uma propriedade do bean.. nao fazer nada
			return;
		} catch (InvalidPropertyException e) {
			// se nao for uma propriedade do bean.. nao fazer nada
			return;
		}
		Annotation[] annotations = propertyDescriptor.getAnnotations();
		for (Annotation annotation : annotations) {
			PropertyValidator propertyValidator = validatorRegistry.getPropertyValidator(annotation.annotationType());
			if (propertyValidator != null) {
				String displayName = Util.beans.getDisplayName(propertyDescriptor, NextWeb.getRequestContext().getLocale());
				propertyValidator.validate(obj, propertyDescriptor.getValue(), parametro, displayName, annotation, errors, this);
			}
		}
	}

}
