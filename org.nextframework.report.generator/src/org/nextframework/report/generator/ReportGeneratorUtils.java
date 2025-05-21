package org.nextframework.report.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.bean.annotation.DisplayName;
import org.nextframework.classmanager.ClassManagerFactory;
import org.nextframework.compilation.JavaSourceCompiler;
import org.nextframework.compilation.SourceCodeBuilder;
import org.nextframework.controller.ExtendedBeanWrapper;
import org.nextframework.exception.NextException;
import org.nextframework.report.definition.builder.AutoReportFilter;
import org.nextframework.report.generator.data.FieldProcessor;
import org.nextframework.report.generator.data.FilterElement;
import org.nextframework.util.Util;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;

public class ReportGeneratorUtils {

	public Object transformToObject(ClassLoader classLoader, ReportElement reportElement, String baseClassName, Map<String, Object> filterMap) {

		SourceCodeBuilder codeBuilder = new SourceCodeBuilder();
		String classQualifiedName = baseClassName + "Filter";

		codeBuilder.addImport(DisplayName.class);
		codeBuilder.setQualifiedClassName(classQualifiedName);

		BeanDescriptor bd = BeanDescriptorFactory.forClass(reportElement.getData().getMainType());
		List<FilterElement> filters = new ArrayList<FilterElement>(reportElement.getData().getFilters());

		for (FilterElement filterElement : filters) {

			PropertyDescriptor propertyDescriptor = bd.getPropertyDescriptor(filterElement.getName());
			String qualifiedName = filterElement.getName().replace('.', '_');
			if (isDate(propertyDescriptor)) {
				codeBuilder.addProperty(propertyDescriptor.getType(), qualifiedName + "_begin", filterElement.getFilterDisplayName());
				codeBuilder.addProperty(propertyDescriptor.getType(), qualifiedName + "_end", "até");
			} else if (filterElement.isFilterSelectMultiple()) {
				codeBuilder.addProperty(propertyDescriptor.getType(), qualifiedName, filterElement.getFilterDisplayName(), 1);
			} else {
				codeBuilder.addProperty(propertyDescriptor.getType(), qualifiedName, filterElement.getFilterDisplayName());
			}

		}

		codeBuilder.addImplements(AutoReportFilter.class);

		try {
			String sourceCode = codeBuilder.getSourceCode();
			Class<?> compiledClass = JavaSourceCompiler.compileClass(classLoader, classQualifiedName, sourceCode.getBytes());
			Object object = BeanUtils.instantiate(compiledClass);
			mapValues(filterMap, object);
			return object;
		} catch (Exception e) {
			throw new NextException("Could not create filter for report " + reportElement.getReportTitle(), e);
		}

	}

	private boolean isDate(PropertyDescriptor propertyDescriptor) {
		return propertyDescriptor.getType() instanceof Class<?>
				&& (Calendar.class.isAssignableFrom((Class<?>) propertyDescriptor.getType())
						|| Date.class.isAssignableFrom((Class<?>) propertyDescriptor.getType()));
	}

	private void mapValues(Map<String, Object> filterMap, Object object) {
		BeanWrapper bw = new ExtendedBeanWrapper(object);
		for (String key : filterMap.keySet()) {
			String qualifiedName = key.replace('.', '_');
			bw.setPropertyValue(qualifiedName, filterMap.get(key));
		}
	}

	public static String reorganizeExpression(ReportElement reportElement, Class<?> clazz, String expression, String processors) {

		BeanDescriptor bd = BeanDescriptorFactory.forClass(clazz);
		String[] expressionParts = parseExpression(expression);
		for (int i = 0; i < expressionParts.length; i++) {
			String part = expressionParts[i];
			if (Character.isLetter(part.charAt(0))) {

				if (reportElement.getData().isCalculated(part)) {
					expressionParts[i] = convertExpressionPartToGetter(part).substring("getCurrent().".length());
					continue;
				}

				Class<?> rawType = bd.getPropertyDescriptor(part).getRawType();
				//TODO CHECK TYPE AND APPLY SUFFIX IF NECESSARY
				//TODO CHECK THE RESULTING TYPE

				expressionParts[i] = convertExpressionPartToGetter(part);
				boolean castToDouble = rawType.isPrimitive();

				//TODO MOVE THIS CHECK
				if (Calendar.class.isAssignableFrom(rawType)) {
					expressionParts[i] += ".getTimeInMillis()";
					castToDouble = true;
				}
				if (Date.class.isAssignableFrom(rawType)) {
					expressionParts[i] += ".getTime()";
					castToDouble = true;
				}
				if (Number.class.isAssignableFrom(rawType)) {
					if (!rawType.getName().startsWith("java.lang")
							|| Integer.class.isAssignableFrom(rawType)
							|| Long.class.isAssignableFrom(rawType)) {
						expressionParts[i] += ".doubleValue()";
					}
				}
				if (castToDouble) {
					expressionParts[i] = "(double) " + expressionParts[i];
				}
			} else if (isSpecialField(part)) {
				expressionParts[i] = convertSpecialField(part);
			}
		}

		StringBuilder b = new StringBuilder();
		for (String p : expressionParts) {
			b.append(p).append(" ");
		}
		String exp = b.toString();
		exp = fillProcessors(exp, processors);

		return exp;
	}

	static Class<FieldProcessor>[] processorClasses;

	private static String fillProcessors(String exp, String processors) {
		if (processorClasses == null) {
			processorClasses = ClassManagerFactory.getClassManager().getAllClassesOfType(FieldProcessor.class);
		}
		if (processors == null) {
			return exp;
		}
		List<Class<FieldProcessor>> processorsSelected = new ArrayList<Class<FieldProcessor>>();
		List<String> ps = Arrays.asList(processors.split(","));
		for (Class<FieldProcessor> processorClass : processorClasses) {
			if (ps.contains(processorClass.getSimpleName())) {
				processorsSelected.add(processorClass);
			}
		}
		for (Class<FieldProcessor> processor : processorsSelected) {
			exp = "(new " + processor.getName() + "().process(" + exp + "))";
		}
		if (processorsSelected.size() > 0) {
			return exp + ".doubleValue()";
		} else {
			return exp;
		}
	}

	static final int ANY = 1;
	static final int IN_VAR = 2;
	static final int IN_SIGNAL = 3;

	static String[] parseExpression(String expression) {

		List<String> parts = new ArrayList<String>();

		String token = "";
		int status = ANY;
		for (int i = 0; i < expression.toCharArray().length; i++) {
			char c = expression.charAt(i);
			switch (status) {
				case ANY:
					if (Character.isLetter(c) || Character.isDigit(c) || c == '$') {
						token += c;
						status = IN_VAR;
					} else {
						if (Util.strings.isNotEmpty(token)) {
							parts.add(token);
						}
						token = "" + c;
						status = IN_SIGNAL;
					}
					break;
				case IN_VAR:
					if (Character.isLetter(c) || Character.isDigit(c) || c == '.' || c == '_') {
						token += c;
					} else {
						if (Util.strings.isNotEmpty(token)) {
							parts.add(token);
						}
						token = "" + c;
						status = IN_SIGNAL;
					}
					break;
				case IN_SIGNAL:
					if (Character.isLetter(c) || Character.isDigit(c)) {
						if (Util.strings.isNotEmpty(token)) {
							parts.add(token);
						}
						token = "" + c;
						status = IN_VAR;
					} else {
						if (Util.strings.isNotEmpty(token)) {
							parts.add(token);
						}
						token = "" + c;
					}
					break;
			}
		}

		if (Util.strings.isNotEmpty(token)) {
			parts.add(token);
		}

		return parts.toArray(new String[parts.size()]);
	}

	private static String convertExpressionPartToGetter(String part) {
		StringBuilder b = new StringBuilder();
		b.append("getCurrent()");
		String[] properties = part.split("\\.");
		for (int i = 0; i < properties.length; i++) {
			String p = properties[i];
			p = Util.strings.captalize(p);
			p = "get" + p + "()";
			b.append(".");
			b.append(p);
		}
		return b.toString();
	}

	public static boolean isSpecialField(String part) {
		return "$now".equals(part);
	}

	private static String convertSpecialField(String part) {
		if ("$now".equals(part)) {
			return "java.util.Calendar.getInstance().getTimeInMillis()";
		}
		throw new IllegalArgumentException("O campo especial " + part + " é inválido!");
	}

}
