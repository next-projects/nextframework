package org.nextframework.summary.dynamic;

import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.BeanDescriptorUtils;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.bean.annotation.DisplayName;
import org.nextframework.compilation.JavaSourceCompiler;
import org.nextframework.compilation.SourceCodeBuilder;
import org.nextframework.exception.NextException;
import org.nextframework.summary.Summary;
import org.nextframework.summary.annotations.Group;
import org.springframework.util.StringUtils;

class DynamicSummaryImpl<E> extends DynamicSummary<E> {

	static Map<DynamicSummary<?>, WeakReference<Class<Summary<?>>>> cache = Collections.synchronizedMap(new HashMap<DynamicSummary<?>, WeakReference<Class<Summary<?>>>>());

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected Class<Summary<E>> getClassFor(DynamicSummary<E> dynamicSummary) {
		WeakReference<Class<Summary<?>>> ref = cache.get(dynamicSummary);
		Class class1;
		if (ref == null || ((class1 = ref.get()) == null)) {
			class1 = createSummaryClass();
			cache.put(dynamicSummary, new WeakReference<Class<Summary<?>>>(class1));
		}
		return class1;
	}

	protected Class<Summary<E>> createSummaryClass() {
		return createSummaryClass(new URLClassLoader(new URL[0], DynamicSummary.class.getClassLoader()));
		//return createSummaryClass(DynamicSummary.class.getClassLoader());
	}

	public String getSourceCode() {
		CodeGenerator codeGenerator = new CodeGenerator(this);
		return codeGenerator.getCode();
	}

	public String getSourceCode(String summaryClassName) {
		String pack = summaryClassName.substring(0, summaryClassName.lastIndexOf('.'));
		String className = summaryClassName.substring(summaryClassName.lastIndexOf('.') + 1);
		CodeGenerator codeGenerator = new CodeGenerator(this, className, pack);
		return codeGenerator.getCode();
	}

	@SuppressWarnings("unchecked")
	protected Class<Summary<E>> createSummaryClass(ClassLoader classLoader) {
		CodeGenerator codeGenerator = new CodeGenerator(this);
		String code = codeGenerator.getCode();
		try {
			return (Class<Summary<E>>) JavaSourceCompiler.compileClass(classLoader, codeGenerator.getGeneratedClassName(), code.getBytes());
		} catch (ClassNotFoundException e) {
			throw new NextException("cannot load DynamicSummary generated class", e);
		} catch (Exception e) {
			throw new NextException("cannot create DynamicSummary generated class", e);
		}
	}

	protected DynamicSummaryImpl(int serialId) {
		super(serialId);
	}

	@SuppressWarnings("rawtypes")
	protected Class getDataClass() {
		if (Summary.class.isAssignableFrom(getReferenceClass())) {
			throw new RuntimeException("not implemented");
		} else {
			return getReferenceClass();
		}
	}

	@SuppressWarnings("rawtypes")
	protected Type getSuperClass() {
		return Summary.class.isAssignableFrom(getReferenceClass()) ? getReferenceClass() : new ParameterizedType() {

			@Override
			public Type getRawType() {
				return Summary.class;
			}

			@Override
			public Type getOwnerType() {
				return null;
			}

			@Override
			public Type[] getActualTypeArguments() {
				return new Type[] { getReferenceClass() };
			}

			@Override
			public String toString() {
				return ((Class) getRawType()).getName() + "<" + ((Class) getActualTypeArguments()[0]).getName() + ">";
			}

		};
	}

	@SuppressWarnings("rawtypes")
	protected String getSuperclassString() {
		Type superClass = getSuperClass();
		if (superClass instanceof Class) {
			return ((Class) superClass).getName();
		} else if (superClass instanceof ParameterizedType) {
			if (((ParameterizedType) superClass).getRawType().equals(Summary.class)) {
				return "Summary<" + ((Class) ((ParameterizedType) superClass).getActualTypeArguments()[0]).getName() + ">";
			}
			return superClass.toString();
		} else {
			return superClass.toString();
		}
	}

}

class CodeGenerator {

	private DynamicSummaryImpl<?> dynamicSummary;
	private String generatedClassName;
	private String generatedClassSimpleName;
	private String generatedClassPackage;
	private BeanDescriptor beanDescriptor;

	public CodeGenerator(DynamicSummaryImpl<?> dynamicSummary) {
		this(dynamicSummary, "DynamicSummary_" + dynamicSummary.getSerialId());
	}

	public CodeGenerator(DynamicSummaryImpl<?> dynamicSummary, String className) {
		this(dynamicSummary, className, getPackage(dynamicSummary.getReferenceClass().getName()) + ".summary");
	}

	public CodeGenerator(DynamicSummaryImpl<?> dynamicSummary, String className, String packageName) {
		this.dynamicSummary = dynamicSummary;
		generatedClassSimpleName = className;
		generatedClassPackage = packageName;
		generatedClassName = generatedClassPackage + "." + generatedClassSimpleName;
	}

	private static String getPackage(String name) {
		if (name.lastIndexOf('.') > 0) {
			return name.substring(0, name.lastIndexOf('.'));
		}
		return name;
	}

	public String getGeneratedClassName() {
		return generatedClassName;
	}

	public String getCode() {
		StringBuilder builder = new StringBuilder();
		builder.append("package " + generatedClassPackage + ";\n\n");
		builder.append("import " + DisplayName.class.getName() + ";\n");
		builder.append("import " + getPackageName(Summary.class) + ".*;\n");
		builder.append("import " + getPackageName(Group.class) + ".*;\n");
		builder.append("import " + getPackageName(SourceCodeBuilder.class) + ".*;\n");
		builder.append("import java.text.*;\n");
		builder.append("import java.util.*;\n");
		builder.append("\n");
		builder.append("public class " + generatedClassSimpleName + " extends " + dynamicSummary.getSuperclassString() + " implements AutoGenerated {\n\n");
		createGroups(builder);
		createVariables(builder);
		createFormatFunction(builder);
		builder.append("}//close class");
		return builder.toString();
	}

	private String getPackageName(Class<?> class1) {
		return class1.getName().substring(0, class1.getName().lastIndexOf('.'));
	}

	private void createVariables(StringBuilder builder) {
		DynamicVariable[] variables = dynamicSummary.getVariables();
		int i = 0;
		for (DynamicVariable variable : variables) {
			createVariable(builder, variable, i++);
			builder.append("\n");
		}
	}

	private void createVariable(StringBuilder builder, DynamicVariable variable, int i) {

		BeanDescriptor beanDescriptor = getBeanDescriptor();

		String variableName = variable.getName();
		builder.append("	/*************************************************************************\n");
		builder.append("	  variable " + variableName + "\n");
		builder.append("	 ************************************************************************/\n\n");

		String type;
		if (variable.getJavaExpression() == null) {
			PropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(variableName);
			type = getTypeAsString(propertyDescriptor.getType());
		} else {
			type = getTypeAsString(variable.getReturnType());
		}

		if (!(variable instanceof DynamicVariableDecorator)) {
			String[] getters = getGetters(variableName);
			if (variable.getDisplayName() != null) {
				builder.append("	@DisplayName(\"" + variable.getDisplayName() + "\")\n");
			}
			builder.append("	public " + type + " " + getCompositeGetterMethodNameFromGroup(variableName) + "(){\n");
			if (variable.getJavaExpression() != null) {
				builder.append("		try {\n");
				builder.append("			return " + variable.getJavaExpression());
				builder.append(";\n");
				builder.append("		} catch(RuntimeException e){return null;}\n");
			} else {
				builder.append("		try {\n");
				builder.append("		    return getCurrent()");
				for (int j = 0; j < getters.length; j++) {
					builder.append("." + getters[j] + "()");
				}
				builder.append(";\n");
				builder.append("		} catch(NullPointerException e){return null;}\n");
			}
			builder.append("	}\n");
		} else {
			if (variable.getDisplayName() != null) {
				builder.append("	@DisplayName(\"" + variable.getDisplayName() + "\")\n");
			}
			DynamicVariableDecorator decorator = (DynamicVariableDecorator) variable;
			builder.append("	public " + type + " " + getCompositeGetterMethodNameFromGroup(variableName) + "(){\n");
			builder.append("		return " + decorator.getJavaExpression() + "(" + getCompositeGetterMethodNameFromGroup(decorator.getForVariable()) + "());\n");
			builder.append("	}\n");
		}

		if (variable.getDisplayName() != null) {
			builder.append("	@DisplayName(\"" + variable.getDisplayName() + "\")\n");
		}
		if (variable instanceof DynamicVariableDecorator) {
			DynamicVariableDecorator decorator = (DynamicVariableDecorator) variable;
			builder.append("	public " + type + " " + getCompositeGetterMethodNameFromGroup(variableName) + "Report(){\n");
			builder.append("		return " + decorator.getJavaExpression() + "(" + getCompositeGetterMethodNameFromGroup(decorator.getForVariable()) + "Report());\n");
			builder.append("	}\n");
		} else {
			builder.append("	@Variable(calculation=CalculationType." + variable.getCalculationType().toString() + ", scopeGroup=\"report\")\n");
			builder.append("	public " + type + " " + getCompositeGetterMethodNameFromGroup(variableName) + "Report(){\n");
			builder.append("		return " + getCompositeGetterMethodNameFromGroup(variableName) + "();\n");
			builder.append("	}\n");
		}

		DynamicGroup[] groups = dynamicSummary.getGroups();
		for (DynamicGroup dynamicGroup : groups) {
			if (variable.getDisplayName() != null) {
				builder.append("	@DisplayName(\"" + variable.getDisplayName() + "\")\n");
			}
			if (variable instanceof DynamicVariableDecorator) {
				DynamicVariableDecorator decorator = (DynamicVariableDecorator) variable;
				builder.append("	public " + type + " " + getCompositeGetterMethodNameFromGroup(variableName) + getCompositeGroupName(dynamicGroup.getName()) + "(){\n");
				builder.append("		return " + decorator.getJavaExpression() + "(" + getCompositeGetterMethodNameFromGroup(decorator.getForVariable()) + getCompositeGroupName(dynamicGroup.getName()) + "());\n");
				builder.append("	}\n");
			} else {
				builder.append("	@Variable(calculation=CalculationType." + variable.getCalculationType().toString() + ", scopeGroup=\"" + dynamicGroup.getName() + "\")\n");
				builder.append("	public " + type + " " + getCompositeGetterMethodNameFromGroup(variableName) + getCompositeGroupName(dynamicGroup.getName()) + "(){\n");
				builder.append("		return " + getCompositeGetterMethodNameFromGroup(variableName) + "();\n");
				builder.append("	}\n");
			}
		}

	}

	private void createGroups(StringBuilder builder) {
		DynamicGroup[] groups = dynamicSummary.getGroups();
		int i = 0;
		for (DynamicGroup dynamicGroup : groups) {
			createGroup(builder, dynamicGroup, i++);
			builder.append("\n");
		}
	}

	private void createGroup(StringBuilder builder, DynamicGroup dynamicGroup, int i) {
		BeanDescriptor beanDescriptor = getBeanDescriptor();
		String name = dynamicGroup.getName();
		builder.append("	//group " + name + "\n");
		PropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(name);
		String type = getTypeAsString(propertyDescriptor.getType());
		if (dynamicGroup.pattern != null) {
			type = "String";
		}
		String[] getters = getGetters(name);
		builder.append("	@Group(value=" + i + ", name=\"" + dynamicGroup.getName() + "\")\n");
		builder.append("	public " + type + " " + getCompositeGetterMethodNameFromGroup(name) + "(){\n");
		builder.append("		try {\n");
		if (dynamicGroup.pattern != null) {
			builder.append("		    return format(\"" + escape(dynamicGroup.pattern) + "\", getCurrent()");
			for (int j = 0; j < getters.length; j++) {
				builder.append("." + getters[j] + "()");
			}
			builder.append(")");
		} else {
			builder.append("		    return getCurrent()");
			for (int j = 0; j < getters.length; j++) {
				builder.append("." + getters[j] + "()");
			}
		}
		builder.append(";\n");
		builder.append("		} catch(NullPointerException e){return null;}\n");
		builder.append("	}\n");
	}

	private String getCompositeGetterMethodNameFromGroup(String name) {
		StringBuilder builder = getCompositeGroupName(name);
		return "get" + builder.toString();
	}

	public StringBuilder getCompositeGroupName(String name) {
		String[] names = getNames(name);
		StringBuilder builder = new StringBuilder();
		for (String var : names) {
			if (builder.length() > 0) {
				builder.append("_");
			}
			builder.append(StringUtils.capitalize(var));
		}
		return builder;
	}

	private void createFormatFunction(StringBuilder builder) {
		builder.append("	public String format(String format, Object value){\n");
		builder.append("		if(value instanceof Calendar)\n");
		builder.append("			value = ((Calendar)value).getTime();\n");
		builder.append("		if(value instanceof Date)\n"); //FIXME
		builder.append("			return new SimpleDateFormat(format).format(value);\n");
		builder.append("		if(value instanceof Number)\n");
		builder.append("			return new DecimalFormat(format).format(value);\n");
		builder.append("		if(value == null)\n");
		builder.append("			return \"\";\n");
		builder.append("		return value.toString();\n");
		builder.append("	}\n");
	}

	private String escape(String pattern) {
		return pattern.replace('"', ' ').replace('\\', ' ');
	}

	private BeanDescriptor getBeanDescriptor() {
		if (beanDescriptor == null) {
			beanDescriptor = BeanDescriptorFactory.forClass(dynamicSummary.getDataClass());
		}
		return beanDescriptor;
	}

	private String[] getGetters(String name) {
		String[] names = getNames(name);
		String[] getters = new String[names.length];
		for (int j = 0; j < names.length; j++) {
			getters[j] = getter(names[j]);
		}
		return getters;
	}

	private String[] getNames(String name) {
		return name.split("\\.");
	}

	private String getter(String name) {
		return BeanDescriptorUtils.getGetterFromProperty(name);
	}

	@SuppressWarnings("rawtypes")
	private String getTypeAsString(Type type) {
		if (type instanceof Class) {
			Class clazz = (Class) type;
			if (clazz.isArray()) {
				return getTypeAsString(clazz.getComponentType()) + "[]";
			}
			if (clazz.getName().startsWith("java.lang")) {
				return clazz.getSimpleName();
			} else {
				return clazz.getName();
			}
		} else {
			return type.toString();
		}
	}

}
