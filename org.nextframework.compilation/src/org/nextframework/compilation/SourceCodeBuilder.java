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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Utility class to build a Java source code.
 * 
 * @author rogelgarcia
 */
public class SourceCodeBuilder {

	String packageDeclaration;

	Set<String> imports = new TreeSet<String>();
	Set<String> implementsInterfaces = new TreeSet<String>();

	SourceCodeBlock classBlock = new SourceCodeBlock(this, "    ");

	Set<String> annotations = new LinkedHashSet<String>();

	Set<String> properties = new LinkedHashSet<String>();

	Set<String> requiredFields = new HashSet<String>();

	Map<String, String> displayNames = new HashMap<String, String>();

	private String className;
	private String extendsFrom;

	private Class<?> superClass;

	public SourceCodeBlock getClassBlock() {
		return classBlock;
	}

	/**
	 * Adds a property to the class. Including getter and setter.
	 * This method requires Next Framework classes (DisplayName and Required).
	 * 
	 * @param type The fully qualified name of the property class
	 * @param name The name of the property.
	 */
	public void addProperty(String type, String name) {
		if (isJavaLang(type)) {
			type = type.substring("java.lang.".length());
		} else if (isJavaUtil(type)) {
			addImport(type);
			type = type.substring("java.util.".length());
		}
		properties.add(type + " " + name);
	}

	/**
	 * Adds a property to the class. Including getter and setter.
	 * This method requires Next Framework classes (DisplayName and Required).
	 * 
	 * @param type The fully qualified name of the property class
	 * @param name The name of the property.
	 * @param displayName The displayName of the class (requires Next Framework)
	 */
	public void addProperty(Type type, String name, String displayName) {
		addProperty(type, name, displayName, false);
	}

	/**
	 * Adds a property to the class. Including getter and setter.
	 * This method requires Next Framework classes (DisplayName and Required).
	 * 
	 * @param type The fully qualified name of the property class
	 * @param name The name of the property.
	 * @param displayName The displayName of the class (requires Next Framework)
	 * @param required If true, a Required Annotation will be put in the property getter (requires Next Framework) 
	 */
	public void addProperty(Type type, String name, String displayName, boolean required) {
		addProperty(type, name, displayName, required, 0);
	}

	public void addProperty(Type type, String name, String displayName, int arrayLength) {
		addProperty(type, name, displayName, false, arrayLength);
	}

	/**
	 * Adds a property to the class. Including getter and setter.
	 * This method requires Next Framework classes (DisplayName and Required).
	 * 
	 * @param type The fully qualified name of the property class
	 * @param name The name of the property.
	 * @param displayName The displayName of the class (requires Next Framework)
	 * @param required If true, a Required Annotation will be put in the property getter (requires Next Framework) 
	 */
	public void addProperty(Type type, String name, String displayName, boolean required, int arrayLength) {

		if (!(type instanceof Class<?>)) {
			throw new IllegalArgumentException("type must be a class");
		}

		String typeName = ((Class<?>) type).getName();
		if (imports.contains(typeName)) {
			typeName = ((Class<?>) type).getSimpleName();
		}
		if (arrayLength == 0) {
			addProperty(typeName, name);
		} else {
			//TODO SUPPORT OTHER LENGTHS
			addProperty(typeName + "[]", name);
		}
		displayNames.put(name, displayName);

		if (displayName != null) {
			try {
				addImport(Class.forName("org.nextframework.bean.annotation.DisplayName"));
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException(e);
			}
		}

		if (required) {
			requiredFields.add(name);
			try {
				addImport(Class.forName("org.nextframework.validation.annotation.Required"));
			} catch (ClassNotFoundException e) {
				throw new IllegalArgumentException(e);
			}
		}

	}

	public SourceCodeBuilder addAnnotation(String ann) {
		annotations.add(ann);
		return this;
	}

	/**
	 * Adds an import to the source code of the class
	 * @param class1 The class to import
	 */
	public void addImport(String class1) {
		imports.add(class1);
	}

	/**
	 * Adds an import to the source code of the class
	 * @param class1 The class to import
	 */
	public void addImport(Class<?> class1) {
		if (class1.isPrimitive() || isJavaLang(class1)) {
			return;
		}
		if (class1.isArray()) {
			imports.add(class1.getCanonicalName().substring(0, class1.getCanonicalName().length() - 2));
		} else {
			imports.add(class1.getName());
		}
	}

	private boolean isJavaLang(Class<?> class1) {
		String fullyName = class1.getName();
		return isJavaLang(fullyName);
	}

	private boolean isJavaLang(String fullyName) {
		String[] parts = fullyName.split("\\.");
		return parts.length == 3 && parts[0].equals("java") && parts[1].equals("lang");
	}

	private boolean isJavaUtil(String fullyName) {
		String[] parts = fullyName.split("\\.");
		return parts.length == 3 && parts[0].equals("java") && parts[1].equals("util");
	}

	/**
	 * Adds an implements clause to the class. 
	 * @param class1 The class to import
	 */
	public void addImplements(String class1) {
		implementsInterfaces.add(class1);
	}

	/**
	 * Adds an implements clause to the class
	 * @param class1 The class to import
	 */
	public void addImplements(Class<?> class1) {
		addImport(class1);
		implementsInterfaces.add(class1.getSimpleName());
	}

	public void removeImplements(Class<?> class1) {
		implementsInterfaces.remove(class1.getSimpleName());
	}

	public void declareProperty(Class<?> class1, String field) {
		classBlock.declareProperty(class1, field);
		addImport(class1);
	}

	public void declareAttribute(Class<?> class1, String field) {
		classBlock.declareAttribute(class1, field);
		addImport(class1);
	}

	public SourceCodeBlock declareMethod(Method method) {
		return declareMethod(method.getReturnType(), method.getName(), method.getParameterTypes(), getDefaultParameterNames(method.getParameterTypes()));
	}

	private String[] getDefaultParameterNames(Class<?>[] parameterTypes) {
		String[] params = new String[parameterTypes.length];
		for (int i = 0; i < params.length; i++) {
			params[i] = "p" + i;
		}
		return params;
	}

	public SourceCodeBlock declareMethod(Class<?> returnType, String name, Class<?>[] parameterTypes, String[] parameterNames) {
		addImport(returnType);
		for (Class<?> class1 : parameterTypes) {
			addImport(class1);
		}
		StringBuilder declaration = new StringBuilder();
		declaration.append("public ");
		declaration.append(returnType.getSimpleName());
		declaration.append(" ");
		declaration.append(name);
		declaration.append("(");
		for (int i = 0; i < parameterTypes.length; i++) {
			declaration.append(parameterTypes[i].getSimpleName());
			declaration.append(" ");
			declaration.append(parameterNames[i]);
			if (i + 1 < parameterTypes.length) {
				declaration.append(",");
			}
		}
		declaration.append(")");
		return classBlock.declareMethod(declaration.toString());
	}

	public SourceCodeBlock declareMethod(String declaration, boolean override) {
		return classBlock.declareMethod(declaration, override);
	}

	public SourceCodeBlock declareMethod(String declaration) {
		return classBlock.declareMethod(declaration);
	}

	public SourceCodeBlock declareConstructor(String params) {
		String cn = getClassName();
		String scn = cn.substring(cn.lastIndexOf('.') + 1);
		return classBlock.declareMethod("public " + scn + "(" + params + ")");
	}

	private SourceCodeBlock createMethod(String declaration) {
		return classBlock.createMethod(declaration, false);
	}

	public void setPackage(String packageDeclaration) {
		this.packageDeclaration = packageDeclaration;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setExtends(String extendsFrom) {
		this.extendsFrom = extendsFrom;
	}

	public void setExtends(Class<?> class1) {
		setExtends(class1.getSimpleName());
		addImport(class1);
	}

	public void setSuperclass(Class<?> extendsFrom) {
		if (Modifier.isFinal(extendsFrom.getModifiers())) {
			throw new IllegalArgumentException("cannot extend final classes [" + extendsFrom + "]");
		}
		addImport(extendsFrom);
		if (this.packageDeclaration == null) {
			setPackage(extendsFrom);
		}
		this.extendsFrom = extendsFrom.getSimpleName();
		this.superClass = extendsFrom;
	}

	private String generateClassNameFromSuperclass(Class<?> clazz) {
		int result = properties.hashCode();
		result = 31 * result + classBlock.hashCode();
		return clazz.getSimpleName() + "$$EnhancedBySourceCodeBuilder_" + Math.abs(result);
	}

	public void setPackage(Class<?> type, String suffix) {
		setPackage(type.getName().substring(0, type.getName().lastIndexOf(type.getSimpleName())) + suffix);
	}

	public void setPackage(Class<?> type) {
		setPackage(type.getName().substring(0, type.getName().lastIndexOf(type.getSimpleName()) - 1));
	}

	public String getPackageDeclaration() {
		return packageDeclaration;
	}

	public void setQualifiedClassName(String qualifiedName) {
		int lastIndexOfDot = qualifiedName.lastIndexOf('.');
		setPackage(qualifiedName.substring(0, lastIndexOfDot));
		setClassName(qualifiedName.substring(lastIndexOfDot + 1));
	}

	public String getSourceCode() {
		return toString();
	}

	public String getClassName() {
		if (this.className == null) {
			this.className = generateClassNameFromSuperclass(superClass);
		}
		return className;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();

		builder.append("package ").append(packageDeclaration).append(";\n\n");

		for (String imp : imports) {
			builder.append("import ").append(imp).append(";\n");
		}
		builder.append("\n");

		for (String ann : annotations) {
			builder.append(ann).append("\n");
		}

		builder.append("public class ").append(getClassName());
		if (extendsFrom != null) {
			builder.append(" extends ").append(extendsFrom);
		}
		if (implementsInterfaces.size() > 0) {
			builder.append(" implements");
			int i = implementsInterfaces.size();
			for (String inter : implementsInterfaces) {
				builder.append(" ").append(inter);
				if (--i > 0) {
					builder.append(",");
				}
			}
		}
		builder.append(" {\n\n");
		for (String property : properties) {
			builder.append("    ");
			builder.append(property);
			builder.append(";\n");
		}
		builder.append("\n");
		for (String property : properties) {
			String[] split = property.split(" ");
			String displayName = displayNames.get(split[1]);
			SourceCodeBlock getter = createMethod("public " + split[0] + " get" + SourceCodeUtils.capitalize(split[1]) + "()");
			getter.statement("return this." + split[1]);
			if (displayName != null) {
				getter.addAnnotation("@DisplayName(\"" + displayName + "\")");
			}
			if (requiredFields.contains(split[1])) {
				getter.addAnnotation("@Required");
			}
			builder.append(getter.toString() + "\n");
		}
		for (String property : properties) {
			String[] split = property.split(" ");

			SourceCodeBlock setter = createMethod("public void set" + SourceCodeUtils.capitalize(split[1]) + "(" + split[0] + " " + split[1] + ")");
			setter
					.statement("this." + split[1] + " = " + split[1]);

			builder.append(setter);
			builder.append("\n");
		}

		builder.append(classBlock);
		builder.append("}");

		return builder.toString();
	}

	public String getFullyQualifiedName() {
		return getPackageDeclaration() + "." + getClassName();
	}

	@SuppressWarnings("unchecked")
	public <X> Class<X> generateClass(ClassLoader classLoader) throws InstantiationException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		try {
			Class<X> loadedClass;
			loadedClass = (Class<X>) classLoader.loadClass(getFullyQualifiedName());
			return loadedClass;
		} catch (ClassNotFoundException e) {
			return (Class<X>) JavaSourceCompiler.compileClass(classLoader, getFullyQualifiedName(), getSourceCode().getBytes());
		}
	}

	public <X> Class<X> generateClass() throws InstantiationException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		if (superClass != null) {
			return generateClass(new URLClassLoader(new URL[0], superClass.getClassLoader()));
		} else {
			ClassLoader classLoader = this.getClass().getClassLoader();
			return generateClass(new URLClassLoader(new URL[0], classLoader));
		}
	}

}
