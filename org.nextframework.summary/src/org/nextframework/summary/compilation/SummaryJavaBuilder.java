package org.nextframework.summary.compilation;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.nextframework.bean.BeanDescriptorUtils;
import org.nextframework.exception.NextException;
import org.nextframework.summary.Summary;
import org.nextframework.summary.SummaryRow;
import org.nextframework.summary.aggregator.Aggregator;
import org.nextframework.summary.annotations.CalculationType;
import org.nextframework.summary.annotations.Scope;
import org.nextframework.summary.definition.SummaryDefinition;
import org.nextframework.summary.definition.SummaryGroupDefinition;
import org.nextframework.summary.definition.SummaryItemDefinition;
import org.nextframework.summary.definition.SummaryVariableDefinition;
import org.springframework.core.GenericTypeResolver;
import org.springframework.util.StringUtils;

public class SummaryJavaBuilder {

	public <E extends Summary<?>> void writeSourceForSummary(Class<E> summaryClass) throws IOException {
		writeSourceForSummary(summaryClass, "CompiledSummary__", "src");
	}

	public <E extends Summary<?>> void writeSourceForSummary(Class<E> summaryClass, String generatedClassNameSuffix, String rootFolder) throws IOException {
		String packageFolder = (summaryClass.getName() + generatedClassNameSuffix).replace('.', '/');
		packageFolder = packageFolder.substring(0, packageFolder.lastIndexOf('/'));
		new File(rootFolder + "/" + packageFolder).mkdirs();
		BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(rootFolder + "/" + (summaryClass.getName() + generatedClassNameSuffix).replace('.', '/') + ".java"));
		writer.write(generateSourceForSummary(summaryClass, summaryClass.getName() + generatedClassNameSuffix));
		writer.flush();
		writer.close();
	}

	public <E extends Summary<?>> byte[] generateSourceForSummary(Class<E> summaryClass, String generatedClassName) {
		try {
			boolean valid = false;
			Constructor<?>[] constructors = summaryClass.getDeclaredConstructors();
			for (Constructor<?> constructor : constructors) {
				if (constructor.getParameterTypes().length == 0) {
					constructor.setAccessible(true);
					constructor.newInstance();
					valid = true;
				}
			}
			if (!valid) {
				throw new NextException("No default constructors found.");
			}
		} catch (Exception e) {
			String extraMessage = "";
			if (summaryClass.getEnclosingClass() != null) {
				extraMessage += " When using inner classes they must be public and static.";
			}
			throw new NextException("Cannot create source for summary class " + summaryClass + ". The class cannot be instantiated. " + extraMessage, e);
		}
		SummaryDefinition<E> definition = new SummaryDefinition<E>(summaryClass);
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		PrintWriter out = new PrintWriter(array);
		createClass(out, definition, summaryClass, generatedClassName);
		out.flush();
		return array.toByteArray();
	}

	private String getTypeAsStringForImport(Type type) {
		if (type instanceof Class<?>) {
			Class<?> clazz = (Class<?>) type;
			if (clazz.isArray()) {
				return getTypeAsStringForImport(clazz.getComponentType());
			}
			if (clazz.getName().startsWith("java.lang") || clazz.isPrimitive()) {
				return null;
			} else {
				return clazz.getName();
			}
		} else {
			return type.toString();
		}
	}

	@SuppressWarnings("all")
	private void createClass(PrintWriter out, SummaryDefinition<?> definition, Class<?> summaryClass, String generatedClassName) {
		List<SummaryItemDefinition> itens = definition.getItens();
		Class rowClass = GenericTypeResolver.resolveTypeArgument(summaryClass, Summary.class);
		if (rowClass == null) {
			throw new NextException("Cannot determine generic type for " + summaryClass);
		}
		out.println("package " + getPackageFromClassName(generatedClassName) + ";");
		out.println();
		out.println("import java.util.ArrayList;");
		out.println("import java.util.List;");
		out.println();
		out.println("import " + rowClass.getName() + ";");
		out.println("import " + getImportName(summaryClass) + ";");
		out.println();
		Set<String> imports = new HashSet<String>();
		for (SummaryItemDefinition summaryItemDefinition : itens) {
			String typeAsStringForImport = getTypeAsStringForImport(summaryItemDefinition.getType());
			if (typeAsStringForImport != null) {
				imports.add("import " + typeAsStringForImport + ";");
			}
		}
		for (String string : imports) {
			out.println(string);
		}
		out.println("import " + SummaryRow.class.getName() + ";");
		out.println("import " + CompiledSummary.class.getName() + ";");
		out.println("import " + VariableHolder.class.getName() + ";");
		out.println("import " + SummaryResult.class.getName() + ";");
		out.println();
		out.println("public class " + getClassName(generatedClassName) + " extends CompiledSummary<" + summaryClass.getSimpleName() + ", " + rowClass.getSimpleName() + "> {");
		createGroupsVariables(out, definition, summaryClass, generatedClassName);
		createVariableVariables(out, definition, summaryClass, generatedClassName);
		createCurrentVariable(out, definition, summaryClass, generatedClassName, rowClass);
		createConstructor(out, definition, summaryClass, generatedClassName);
		createResetScopeReport(out, definition, summaryClass, generatedClassName);
		createResetScopeRow(out, definition, summaryClass, generatedClassName);
		createResetScopeGroups(out, definition, summaryClass, generatedClassName);
		createResetVariables(out, definition, summaryClass, generatedClassName);
		createProxy(out, definition, summaryClass, generatedClassName, rowClass);
		createOnRowMethod(out, definition, summaryClass, generatedClassName, rowClass);
		createGetSummaryClassMethod(out, summaryClass);
		createGetGroupValuesForRow(out, definition, rowClass, summaryClass);
		out.println("}");
	}

	private void createGetGroupValuesForRow(PrintWriter out, SummaryDefinition<?> definition, Class<?> rowClass, Class<?> summaryClass) {
		out.println("    @Override");
		out.println("    protected Comparable<?>[] getGroupValuesForRow(" + rowClass.getSimpleName() + " row) {");
		out.println("        current = row;");
		out.println("        Comparable<?>[] comparables = new Comparable<?>[]{");
		Set<SummaryGroupDefinition> groups = definition.getGroups();
		for (SummaryGroupDefinition summaryGroupDefinition : groups) {
			String groupNameCap = StringUtils.capitalize(summaryGroupDefinition.getName());
			String comparableMethodName = "getComparable" + groupNameCap;
			out.println("            " + comparableMethodName + "(createProxy(\"" + summaryGroupDefinition.getName() + "\")),");
		}
		out.println("        };");
		out.println("        current = null;");
		out.println("        return comparables;");
		out.println("    }");
		for (SummaryGroupDefinition summaryGroupDefinition : groups) {
			String groupName = summaryGroupDefinition.getName();
			String groupNameCap = StringUtils.capitalize(summaryGroupDefinition.getName());
			String groupGetter = BeanDescriptorUtils.getGetterFromProperty(groupName);
			out.println("");
			String comparableMethodName = "getComparable" + groupNameCap;
			String summaryClassName = summaryClass.getCanonicalName();
			out.println("    protected Comparable<?> " + comparableMethodName + "(" + summaryClassName + " proxy) {");
			out.println("        " + summaryGroupDefinition.getType().getSimpleName() + " var = proxy." + groupGetter + "();");
			out.println("        return convertComparable(var);");
			out.println("    }");
		}
	}

	private String getImportName(Class<?> summaryClass) {
		//System.out.println(summaryClass.getEnclosingClass());
		//System.out.println(summaryClass.getDeclaringClass());
		if (summaryClass.getDeclaringClass() != null) {
			return summaryClass.getDeclaringClass().getName() + "." + summaryClass.getSimpleName();
		}
		return summaryClass.getName();
	}

	private void createGetSummaryClassMethod(PrintWriter out, Class<?> summaryClass) {
		out.println("");
		out.println("	@Override");
		out.println("	protected Class<" + summaryClass.getSimpleName() + "> getSummaryClass() {");
		out.println("		return " + summaryClass.getSimpleName() + ".class;");
		out.println("	}");
	}

	private void createResetScopeGroups(PrintWriter out, SummaryDefinition<?> definition, Class<?> summaryClass, String generatedClassName) {
		for (SummaryGroupDefinition summaryItemDefinition : definition.getGroups()) {
			out.println();
			out.println("    private void resetScopeGroup" + org.springframework.util.StringUtils.capitalize(summaryItemDefinition.getName()) + "() {");
			out.println("        " + summaryItemDefinition.getName() + " = " + getResetFunctionName(summaryItemDefinition) + "();");
			printResetItensForScope(out, definition, Scope.GROUP, summaryItemDefinition.getGroup().name());
			out.println("    }");
		}
	}

	private void createProxy(PrintWriter out, SummaryDefinition<?> definition, Class<?> summaryClass, String generatedClassName, Class<?> rowClass) {
		List<SummaryItemDefinition> itens = definition.getItens();
		out.println();
		out.println("    private " + summaryClass.getSimpleName() + " createProxy(final String field) {");
		out.println("        " + summaryClass.getSimpleName() + " proxy = new " + summaryClass.getSimpleName() + "(){");
		out.println();
		for (SummaryItemDefinition summaryItemDefinition : itens) {
			out.println("            VariableHolder<" + summaryItemDefinition.getType().getSimpleName() + "> _" + summaryItemDefinition.getName() + " = " + summaryItemDefinition.getName() + ";");
		}
		out.println();
		out.println("            " + rowClass.getSimpleName() + " _current = current; ");
		out.println();
		out.println("            public " + rowClass.getSimpleName() + " getCurrent(){");
		out.println("                return _current;");
		out.println("            }");
		for (SummaryItemDefinition summaryItemDefinition : itens) {
			generateProxyMethodFor(out, summaryItemDefinition);
		}
		out.println();

		/*
		 		return SummaryResult.createFrom(getSummaryResult()
				.filterByGroup("transportadora", getTransportadora())
		.getRowItens(), 
				DTTransportadoraSummary.class
		).getItems().get(0).getSummary();
		 */
		List<Method> summaryGetters = getSummaryGetters(summaryClass);
		for (Method method : summaryGetters) {
			generateProxyMethodForSummaryGetter(out, method);
		}

		out.println();
		out.println("            @Override");
		out.println("            public String toString() {");
		out.println("                StringBuilder builder = new StringBuilder();");
		for (SummaryItemDefinition summaryItemDefinition : itens) {
			out.println("                builder.append(\"" + summaryItemDefinition.getName() + "=\").append(_" + summaryItemDefinition.getName() + ").append(\", \");");
		}
		out.println("                return builder.toString();");
		out.println("            }");
		out.println("        };");
		out.println("        return proxy;");
		out.println("   }");
	}

	private List<Method> getSummaryGetters(Class<?> summaryClass) {
		List<Method> summaryGetters = new ArrayList<Method>();
		Method[] methods = summaryClass.getMethods();
		for (Method method : methods) {
			if (Summary.class.isAssignableFrom(method.getReturnType())
					&& method.getName().startsWith("get")) {
				summaryGetters.add(method);
			}
		}
		return summaryGetters;
	}

	@SuppressWarnings("all")
	private void generateProxyMethodForSummaryGetter(PrintWriter out, Method method) {
//		return SummaryResult.createFrom(getSummaryResult()
//				.filterByGroup("transportadora", getTransportadora())
//		.getRowItens(), 
//				DTTransportadoraSummary.class
//		).getItems().get(0).getSummary();
		Class<Summary<?>> subsummaryClass = (Class<Summary<?>>) method.getReturnType();
		SortedSet<SummaryGroupDefinition> groups = new SummaryDefinition(subsummaryClass).getGroups();
		out.println("");
		out.println("            @Override");
		out.println("            public " + getImportName(subsummaryClass) + " " + method.getName() + "() {");
		out.println("                return SummaryResult.createFrom(getSummaryResult()");
		for (SummaryGroupDefinition group : groups) {
			out.println("                            .filterByGroup(\"" + group.getName() + "\", " + group.getMethod().getName() + "())");
		}
		out.println("                        .getRowItens(), ");
		out.println("                            " + getImportName(method.getReturnType()) + ".class");
		out.println("                    ).getItems().get(0).getSummary();");
		out.println("            }");
	}

	private void generateProxyMethodFor(PrintWriter out, SummaryItemDefinition summaryItemDefinition) {
		out.println("");
		out.println("            @Override");
		out.println("            public " + summaryItemDefinition.getType().getSimpleName() + " " + BeanDescriptorUtils.getGetterFromProperty(summaryItemDefinition.getName()) + "() {");
		out.println("                if(field.equals(\"" + summaryItemDefinition.getName() + "\")){");
		out.println("                    return super." + BeanDescriptorUtils.getGetterFromProperty(summaryItemDefinition.getName()) + "();");
		out.println("                } else {");
		out.println("                    return _" + summaryItemDefinition.getName() + ".getValue();");
		out.println("                }");
		out.println("            }");
	}

	private void createResetVariables(PrintWriter out, SummaryDefinition<?> definition, Class<?> summaryClass, String generatedClassName) {
		out.println();
		out.println("    //reset groups");
		for (SummaryGroupDefinition summaryItemDefinition : definition.getGroups()) {
			createResetGroup(out, definition, summaryClass, generatedClassName, summaryItemDefinition);
		}
		out.println();
		out.println("    //reset variables");
		for (SummaryVariableDefinition summaryItemDefinition : definition.getVariables()) {
			createResetVariable(out, definition, summaryClass, generatedClassName, summaryItemDefinition);
		}
	}

	private void createResetGroup(PrintWriter out, SummaryDefinition<?> definition, Class<?> summaryClass, String generatedClassName, SummaryGroupDefinition summaryItemDefinition) {
		createResetItem(out, definition, summaryClass, generatedClassName, summaryItemDefinition);
	}

	private void createResetItem(PrintWriter out, SummaryDefinition<?> definition, Class<?> summaryClass, String generatedClassName, SummaryItemDefinition summaryItemDefinition) {
		out.println();
		printResetFunctionSignature(out, summaryItemDefinition);
		out.println("        return new VariableHolder<" + summaryItemDefinition.getType().getSimpleName() + ">() {");
		out.println("            " + summaryClass.getSimpleName() + " proxy = createProxy(\"" + summaryItemDefinition.getName() + "\");");
		out.println();
		out.println("            public " + summaryItemDefinition.getType().getSimpleName() + " getValue() {");
		out.println("            	return proxy." + BeanDescriptorUtils.getGetterFromProperty(summaryItemDefinition.getName()) + "();");
		out.println("            }");
		out.println("        };");
		out.println("    }");
	}
	/*
	private VariableHolder<String> resetGrupo() {
		return new Expression<String>() {
			ReportRegistroFactory registroFactory = createProxy("grupo");
			
			public String calculateValue() {
				return registroFactory.getGrupo();
			}
		};
	}*/

	private void printResetFunctionSignature(PrintWriter out, SummaryItemDefinition summaryItemDefinition) {
		String functionName = getResetFunctionName(summaryItemDefinition);
		out.println("    private VariableHolder<" + summaryItemDefinition.getType().getSimpleName() + "> " + functionName + "() {");
	}

	private String getResetFunctionName(SummaryItemDefinition summaryItemDefinition) {
		String functionName = "reset" + summaryItemDefinition.getClass().getSimpleName() + org.springframework.util.StringUtils.capitalize(summaryItemDefinition.getName());
		return functionName;
	}

	private void createResetVariable(PrintWriter out, SummaryDefinition<?> definition, Class<?> summaryClass, String generatedClassName, SummaryVariableDefinition summaryItemDefinition) {
		if (hasCalculation(summaryItemDefinition)) {
			createResetVariableCalculated(out, definition, summaryClass, generatedClassName, summaryItemDefinition);
		} else {
			createResetItem(out, definition, summaryClass, generatedClassName, summaryItemDefinition);
		}
	}

	@SuppressWarnings("unchecked")
	private void createResetVariableCalculated(PrintWriter out, SummaryDefinition<?> definition, Class<?> summaryClass, String generatedClassName, SummaryVariableDefinition summaryItemDefinition) {
		out.println();
		printResetFunctionSignature(out, summaryItemDefinition);
		Class<? extends Aggregator<?>> aggregatorClass;
		if (!Aggregator.class.equals(summaryItemDefinition.getVariable().customAggregator())) {//defined custom aggregator
			aggregatorClass = (Class<? extends Aggregator<?>>) summaryItemDefinition.getVariable().customAggregator();
		} else {
			aggregatorClass = summaryItemDefinition.getVariable().calculation().getAggregatorClass();
		}
		out.println("        return new VariableHolder<" + summaryItemDefinition.getType().getSimpleName() + ">(new " + aggregatorClass.getName() + "<" + summaryItemDefinition.getType().getSimpleName() + ">());");
		out.println("    }");
	}

	private void createResetScopeRow(PrintWriter out, SummaryDefinition<?> definition, Class<?> summaryClass, String generatedClassName) {
		out.println();
		out.println("    private void resetScopeRow() {");
		printResetItensForScope(out, definition, Scope.ROW, null);
		out.println("    }");
	}

	private void createResetScopeReport(PrintWriter out, SummaryDefinition<?> definition, Class<?> summaryClass, String generatedClassName) {
		out.println();
		out.println("    private void resetScopeReport() {");
		printResetItensForScope(out, definition, Scope.REPORT, null);
		out.println("    }");
	}

	private void printResetItensForScope(PrintWriter out, SummaryDefinition<?> definition, Scope scope, String groupName) {
		for (SummaryVariableDefinition variableDefinition : definition.getVariables()) {
			if (variableDefinition.getVariable().scope() == scope) {
				if (groupName != null) {
					if (groupName.equals(variableDefinition.getVariable().scopeGroup())) {
						out.println("        " + variableDefinition.getName() + " = " + getResetFunctionName(variableDefinition) + "();");
					}
				} else {
					out.println("        " + variableDefinition.getName() + " = " + getResetFunctionName(variableDefinition) + "();");
				}
			}
		}
	}

	private void createConstructor(PrintWriter out, SummaryDefinition<?> definition, Class<?> summaryClass, String generatedClassName) {
		out.println();
		out.println("    public " + getClassName(generatedClassName) + "() {");
		out.println("    }");
	}

	private void createCurrentVariable(PrintWriter out, SummaryDefinition<?> definition, Class<?> summaryClass, String generatedClassName, Class<?> rowClass) {
		out.println();
		out.println("    private " + rowClass.getSimpleName() + " current;");
	}

	private void createVariableVariables(PrintWriter out, SummaryDefinition<?> definition, Class<?> summaryClass, String generatedClassName) {
		out.println();
		out.println("    //variables");
		for (SummaryVariableDefinition summaryItemDefinition : definition.getVariables()) {
			createVariable(out, definition, summaryClass, generatedClassName, summaryItemDefinition);
		}
	}

	private void createGroupsVariables(PrintWriter out, SummaryDefinition<?> definition, Class<?> summaryClass, String generatedClassName) {
		out.println();
		out.println("    //groups");
		for (SummaryGroupDefinition summaryItemDefinition : definition.getGroups()) {
			createVariable(out, definition, summaryClass, generatedClassName, summaryItemDefinition);
		}
	}

	private void createVariable(PrintWriter out, SummaryDefinition<?> definition, Class<?> summaryClass, String generatedClassName, SummaryItemDefinition summaryItemDefinition) {
		out.println("    private VariableHolder<" + summaryItemDefinition.getType().getSimpleName() + "> " + summaryItemDefinition.getName() + ";");
	}

	private void createOnRowMethod(PrintWriter out, SummaryDefinition<?> definition, Class<?> summaryClass, String generatedClassName, Class<?> rowClass) {
		out.println();
		out.println(" 	@Override");
		out.println("    protected SummaryRow<" + rowClass.getSimpleName() + ", " + summaryClass.getSimpleName() + "> onNewRow(" + rowClass.getSimpleName() + " row, int rowIndex) {");
		out.println("        boolean first = current == null;");

		for (SummaryGroupDefinition groupDefinition : definition.getGroups()) {
			out.println("        boolean groupChange" + groupDefinition.getName() + " = false;");
			out.println("        " + groupDefinition.getType().getSimpleName() + " last" + groupDefinition.getName() + " = current != null? " +
					"createProxy(\"" + groupDefinition.getName() + "\")." + BeanDescriptorUtils.getGetterFromProperty(groupDefinition.getName()) + "() : null;");
		}
		out.println();
		out.println("        current = (" + rowClass.getSimpleName() + ") row;");
		out.println();
		out.println("        if(first){resetScopeReport();}");
		out.println();
		out.println("        boolean previousGroupChanged = false;");
		out.println("        List<String> changedGroups = new ArrayList<String>();");
		out.println();
		for (SummaryGroupDefinition groupDefinition : definition.getGroups()) {
			out.println("        " + groupDefinition.getType().getSimpleName() + " current" + groupDefinition.getName() + " = createProxy(\"" + groupDefinition.getName() + "\")." + BeanDescriptorUtils.getGetterFromProperty(groupDefinition.getName()) + "();");
			out.println("        if(previousGroupChanged || first || (last" + groupDefinition.getName() + " == null && current" + groupDefinition.getName() + " != null) || " +
					"(last" + groupDefinition.getName() + " != null && !last" + groupDefinition.getName() + ".equals(current" + groupDefinition.getName() + "))){");
			out.println("            resetScopeGroup" + org.springframework.util.StringUtils.capitalize(groupDefinition.getName()) + "();");
			out.println("            changedGroups.add(\"" + groupDefinition.getName() + "\");");
			out.println("            groupChange" + groupDefinition.getName() + " = true;");
			out.println("            previousGroupChanged = true;");
			out.println("        }");
		}
		out.println("        resetScopeRow();");
		out.println();
		for (SummaryVariableDefinition summaryVariableDefinition : definition.getVariables()) {
			if (hasCalculation(summaryVariableDefinition)) {
				if (org.springframework.util.StringUtils.hasText(summaryVariableDefinition.getVariable().incrementOnGroupChange())) {
					out.println("        if(groupChange" + summaryVariableDefinition.getVariable().incrementOnGroupChange() + ")");
				}
				out.println("        " + summaryVariableDefinition.getName() + ".setValue(createProxy(\"" + summaryVariableDefinition.getName() + "\")." + BeanDescriptorUtils.getGetterFromProperty(summaryVariableDefinition.getName()) + "());");
			}
		}
		out.println();
		out.println("        return new SummaryRow<" + rowClass.getSimpleName() + ", " + summaryClass.getSimpleName() + ">(rowIndex, row, createProxy(\".\"), first, changedGroups.toArray(new String[changedGroups.size()]));");
		out.println("    }");
	}

	public boolean hasCalculation(SummaryVariableDefinition summaryVariableDefinition) {
		return summaryVariableDefinition.getVariable().calculation() != CalculationType.NONE
				|| !Aggregator.class.equals(summaryVariableDefinition.getVariable().customAggregator());
	}

	private String getClassName(String generatedClassName) {
		return generatedClassName.substring(generatedClassName.lastIndexOf('.') + 1);
	}

	private String getPackageFromClassName(String generatedClassName) {
		return generatedClassName.substring(0, generatedClassName.lastIndexOf('.'));
	}

}
