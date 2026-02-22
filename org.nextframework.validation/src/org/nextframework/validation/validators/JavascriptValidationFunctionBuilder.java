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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextframework.validation.JavascriptValidationItem;
import org.nextframework.validation.PropertyValidator;
import org.nextframework.validation.ValidatorRegistry;

import jakarta.servlet.ServletContext;

/**
 * @author rogelgarcia | marcusabreu
 * @since 31/01/2006
 * @version 1.1
 */
public class JavascriptValidationFunctionBuilder {

	protected List<JavascriptValidationItem> validations;
	protected String formName;
	protected ValidatorRegistry validatorRegistry;
	protected ServletContext context;
	private String functionName;

	public JavascriptValidationFunctionBuilder(List<JavascriptValidationItem> validations, String formName, String functionName, ValidatorRegistry validatorRegistry, ServletContext context) {
		this.validations = validations;
		this.formName = formName;
		this.context = context;
		this.validatorRegistry = validatorRegistry;
		this.functionName = functionName;
	}

	public String buildValidation() {
		if (validations.size() == 0) {
			return "    function " + functionName + "() {return true}";
		}
		ValidationHolder holder = new ValidationHolder(validations, validatorRegistry);
		StringBuilder builder = new StringBuilder();
		builder.append(" ")
				.append("\n");
		builder.append("    var bCancel = false; ")
				.append("\n");
		builder.append("    function " + functionName + "() {")
				.append("\n");
		builder.append("       var formObject = document.getElementsByName('" + formName + "')[0];")
				.append("\n");
		builder.append("       if (bCancel) ")
				.append("\n");
		builder.append("            return true; ")
				.append("\n");
		builder.append("        try{organizeProperties(formObject);}catch(e){} ")
				.append("\n");
		builder.append("        var formValidationResult;")
				.append("\n");
		builder.append("        try {")
				.append("\n");
		builder.append("        formValidationResult =");
		Set<PropertyValidator> propertyValidators = holder.getValidationMap().keySet();
		if (propertyValidators != null && propertyValidators.size() > 0) {
			for (Iterator<PropertyValidator> iter = propertyValidators.iterator(); iter.hasNext();) {
				PropertyValidator propertyValidator = iter.next();
				builder.append(" validate" + capitalize(propertyValidator.getValidationFunctionName()) + "(formObject)");
				if (iter.hasNext()) {
					builder.append(" &&");
				}
			}
		} else {
			builder.append("1");
		}
		builder.append(";")
				.append("\n");
		builder.append("        return (formValidationResult == 1);")
				.append("\n");
		builder.append("        } catch(e){ return true;}")
				.append("\n");
		builder.append("    }")
				.append("\n");
		if (propertyValidators != null && propertyValidators.size() > 0) {
			for (PropertyValidator validator : propertyValidators) {
				builder.append(createValidationFunction(validator, holder.getValidationMap().get(validator)));
			}
		}
		/*
		InputStream jsinputStream = this.getClass().getClassLoader().getResourceAsStream(ValidatorRegistry.validatorUtilities);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jsinputStream));
		String line = null;
		try {
			while((line = bufferedReader.readLine())!=null){
				builder.append(line);
				builder.append("\n");
			}
			builder.append("\n");
		} catch (IOException e) {
			throw new RuntimeException("Não foi possível ler o arquivo de validação do Javascript",e);
		}
		*/
		builder.append("  ")
				.append("\n");
		return builder.toString();
	}

	private String capitalize(String validationFunctionName) {
		return Character.toUpperCase(validationFunctionName.charAt(0)) + validationFunctionName.substring(1);
	}

	protected StringBuilder createValidationFunction(PropertyValidator validator, List<JavascriptValidationItem> javascriptValidationItens) {
		StringBuilder builder = new StringBuilder();
		builder.append("function " + formName + "_" + validator.getValidationName() + "() {\n");
		int index = 0;
		List<String> names = new ArrayList<String>();
		for (JavascriptValidationItem item : javascriptValidationItens) {
			if (isIndexed(item.getFieldName())) {
				String[] property = separateIndexedProperty(item.getFieldName());
				String function = validator.getJavascriptFunction(item);
				if (names.contains(property[0] + "[]" + property[1])) {
					continue;
				} else {
					names.add(property[0] + "[]" + property[1]);
				}
				builder.append("  formIndexedProperties(this, \"a" + index + "\", \"" + property[1] + "\", \"" + validator.getMessage(item) + "\", " + function + ", \"" + property[0] + "\", " + formName + ");\n");
			} else {
				String function = validator.getJavascriptFunction(item);
				builder.append("  this.a" + index + " = new Array(\"" + item.getFieldName() + "\", \"" + validator.getMessage(item) + "\", " + function + ");\n");
			}
			index++;
		}
		builder.append("}\n\n");
		/*
		String javascriptFunctionPath = validator.getJavascriptFunctionPath();
		InputStream jsinputStream = this.getClass().getClassLoader().getResourceAsStream(javascriptFunctionPath);
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(jsinputStream));
		String line = null;
		try {
			while((line = bufferedReader.readLine())!=null){
				builder.append(line);
				builder.append("\n");
			}
			builder.append("\n");
		} catch (IOException e) {
			throw new RuntimeException("Não foi possível ler o arquivo de validação do Javascript",e);
		}
		*/
		return builder;
	}

	private String[] separateIndexedProperty(String fieldName) {
		int oi = fieldName.lastIndexOf('[');
		int ci = fieldName.lastIndexOf(']');
		String indexedProperty = fieldName.substring(0, oi);
		String property = fieldName.substring(ci + 2);
		return new String[] { indexedProperty, property };
	}

	private boolean isIndexed(String fieldName) {
		if (fieldName != null) {
			return fieldName.indexOf('[') > 0;
		} else {
			return false;
		}
	}

}

class ValidationHolder {

	protected List<JavascriptValidationItem> validationItens;
	protected ValidatorRegistry validatorRegistry;

	public ValidationHolder(List<JavascriptValidationItem> validationItens, ValidatorRegistry validatorRegistry) {
		this.validationItens = validationItens;
		this.validatorRegistry = validatorRegistry;

	}

	public Map<PropertyValidator, List<JavascriptValidationItem>> getValidationMap() {
		Map<PropertyValidator, List<JavascriptValidationItem>> map = new HashMap<PropertyValidator, List<JavascriptValidationItem>>();
		for (JavascriptValidationItem validationItem : validationItens) {
			List<Annotation> list = validationItem.getValidations();
			PropertyValidator typeValidator = validationItem.getTypeValidator();
			if (typeValidator != null) {
				if (typeValidator.getJavascriptFunctionPath() != null) {
					// só adicionar funcoes para validação se tiver arquivo javascript
					List<JavascriptValidationItem> validationItensForValidator = map
							.get(typeValidator);
					if (validationItensForValidator == null) {
						validationItensForValidator = new ArrayList<JavascriptValidationItem>();
						map.put(typeValidator, validationItensForValidator);
					}
					validationItensForValidator.add(validationItem);
				}
			}
			for (Annotation annotation : list) {
				PropertyValidator validator = validatorRegistry.getPropertyValidator(annotation.annotationType());
				if (validator.getJavascriptFunctionPath() != null) {
					// só adicionar funcoes para validação se tiver arquivo javascript
					List<JavascriptValidationItem> validationItensForValidator = map.get(validator);
					if (validationItensForValidator == null) {
						validationItensForValidator = new ArrayList<JavascriptValidationItem>();
						map.put(validator, validationItensForValidator);
					}
					validationItensForValidator.add(validationItem);
				}
			}

		}
		return map;
	}

}
