/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2012 the original author or authors.
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
package org.nextframework.compilation;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author rogelgarcia
 */
public class SourceCodeBlock {

	String prefix;
	String suffix;

	String identation = "";

	List<Object> blocks = new ArrayList<Object>();
	Set<String> annotations = new HashSet<String>();
	SourceCodeBuilder builder;

	public SourceCodeBlock(SourceCodeBuilder builder, String identation) {
		this.builder = builder;
		this.identation = identation;
	}

	public SourceCodeBlock(SourceCodeBuilder builder, String prefix, String suffix, String identation) {
		this.builder = builder;
		this.prefix = prefix;
		this.suffix = suffix;
		this.identation = identation;
	}

	public SourceCodeBlock addAnnotation(String ann) {
		annotations.add(ann);
		return this;
	}

	public void statement(String code) {
		append(code + ";");
	}

	public void call(String code, Object... parameters) {
		append(code + "(" + toStringParamters(parameters) + ")" + ";");
	}

	private String toStringParamters(Object... parameters) {
		String params = "";
		for (int i = 0; i < parameters.length; i++) {
			Object param = parameters[i];
			if (param == null) {
				params += "null";
			} else if (param instanceof String) {
				params += "\"" + param + "\"";
			} else if (param.getClass().isEnum()) {
				builder.addImport(param.getClass());
				params += param.getClass().getSimpleName() + "." + param;
			} else {
				params += param.toString();
			}
			if (i + 1 < parameters.length) {
				params += ", ";
			}
		}
		return params;
	}

	public void append(String code) {
		blocks.add(identation + code);
	}

	public String getPrefix() {
		return prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public String getIdentation() {
		return identation;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void setIdentation(String identation) {
		this.identation = identation;
	}

	public SourceCodeBlock declareMethod(String declaration) {
		return declareMethod(declaration, false);
	}

	public void declareAttribute(Class<?> class1, String field) {
		SourceCodeBlock block = new SourceCodeBlock(this.builder, identation);
		block.append(class1.getSimpleName() + " " + field + ";");
		blocks.add(0, block);
	}

	public void declareProperty(Class<?> class1, String field) {
		SourceCodeBlock block = new SourceCodeBlock(this.builder, identation);
		block.append(class1.getSimpleName() + " " + field + ";");
		blocks.add(block);

		SourceCodeBlock setter = declareMethod("public void set" + SourceCodeUtils.capitalize(field) + "(" + class1.getSimpleName() + " " + field + ")");
		setter.append("this." + field + " = " + field + ";");

		SourceCodeBlock getter = declareMethod("public " + class1.getSimpleName() + " get" + SourceCodeUtils.capitalize(field) + "()");
		getter.append("return this." + field + ";");
	}

	public SourceCodeBlock declareMethod(String declaration, boolean override) {
		SourceCodeBlock block = new SourceCodeBlock(this.builder, (override ? "\n" + identation + "@Override\n" : "\n") + identation + declaration + " {\n", identation + "}", identation + "    ");
		blocks.add(block);
		return block;
	}

	public void declareAnnotation(Annotation annotation) {
		declareAnnotation(annotation, false);
	}

	public void declareAnnotation(Annotation annotation, boolean alreadyImported) {
		Class<? extends Annotation> annotationType = annotation.annotationType();
		Method[] allMethods = annotationType.getMethods();
		List<Method> methods = new ArrayList<Method>();
		for (Method method : allMethods) {
			if (method.getDeclaringClass().equals(annotationType)) {
				methods.add(method);
			}
		}
		List<String> params = new ArrayList<String>();
		for (Method method : methods) {
			if (method.getDeclaringClass().equals(annotationType)) {
				String param = method.getName() + "=";
				if (method.getName().equals("value") && methods.size() == 1) {
					param = "";
				}
				try {
					Object annotationParamValue = method.invoke(annotation);
					if (!defaultAnnotationParamValue(method, annotation, annotationParamValue)) {
						param += convertAnnotationValueToString(annotationParamValue);
						params.add(param);
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		StringBuilder paramsString = new StringBuilder();
		for (Iterator<String> iterator = params.iterator(); iterator.hasNext();) {
			String param = iterator.next();
			paramsString.append(param);
			if (iterator.hasNext()) {
				paramsString.append(", ");
			}
		}
		String prefixString = "@" + (alreadyImported ? annotationType.getSimpleName() : annotationType.getName()) + "(" + paramsString + ")";
		prefix = prependToPreffix(prefixString);
	}

	private boolean defaultAnnotationParamValue(Method method, Annotation annotation, Object annotationParamValue) throws IllegalArgumentException, InvocationTargetException {
		return annotationParamValue.equals(method.getDefaultValue());
	}

	private String convertAnnotationValueToString(Object annotationParamValue) {
		if (annotationParamValue instanceof Class) {
			return ((Class<?>) annotationParamValue).getName() + ".class";
		} else if (annotationParamValue instanceof String) {
			return "\"" + annotationParamValue + "\"";
		} else if (annotationParamValue.getClass().isArray()) {
			Object[] array = (Object[]) annotationParamValue;
			if (array.length == 1) {
				return convertAnnotationValueToString(array[0]);
			} else {
				StringBuilder sb = new StringBuilder();
				sb.append("{");
				for (int i = 0; i < array.length; i++) {
					sb.append(convertAnnotationValueToString(array[i]));
					if (i + 1 < array.length) {
						sb.append(", ");
					}
				}
				sb.append("}");
				return sb.toString();
			}
		} else if (annotationParamValue.getClass().isEnum()) {
			return annotationParamValue.getClass().getName() + "." + annotationParamValue.toString();
		} else {
			return annotationParamValue.toString();
		}
	}

	public String prependToPreffix(String prefixString) {
		return "\n" + identation.substring(0, identation.length() - 4) + prefixString + prefix;
	}

	SourceCodeBlock createMethod(String declaration, boolean override) {
		SourceCodeBlock block = new SourceCodeBlock(this.builder, (override ? "\n" + identation + "@Override\n" : "\n") + identation + declaration + " {\n", identation + "}", identation + "    ");
		return block;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotations == null) ? 0 : annotations.hashCode());
		result = prime * result + ((blocks == null) ? 0 : blocks.hashCode());
		result = prime * result + ((identation == null) ? 0 : identation.hashCode());
		result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
		result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SourceCodeBlock other = (SourceCodeBlock) obj;
		if (annotations == null) {
			if (other.annotations != null)
				return false;
		} else if (!annotations.equals(other.annotations))
			return false;
		if (blocks == null) {
			if (other.blocks != null)
				return false;
		} else if (!blocks.equals(other.blocks))
			return false;
		if (identation == null) {
			if (other.identation != null)
				return false;
		} else if (!identation.equals(other.identation))
			return false;
		if (prefix == null) {
			if (other.prefix != null)
				return false;
		} else if (!prefix.equals(other.prefix))
			return false;
		if (suffix == null) {
			if (other.suffix != null)
				return false;
		} else if (!suffix.equals(other.suffix))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (String annotation : annotations) {
			builder.append("\n    " + annotation);
		}
		if (prefix != null) {
			builder.append(prefix);
		}
		for (Object o : blocks) {
			builder.append(o).append("\n");
		}
		if (suffix != null) {
			builder.append(suffix);
		}
		return builder.toString();
	}

}
