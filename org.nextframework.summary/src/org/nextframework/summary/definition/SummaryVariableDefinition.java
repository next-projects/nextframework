package org.nextframework.summary.definition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.nextframework.summary.Summary;
import org.nextframework.summary.SummaryUtils;
import org.nextframework.summary.aggregator.Aggregator;
import org.nextframework.summary.annotations.CalculationType;
import org.nextframework.summary.annotations.Scope;
import org.nextframework.summary.annotations.Variable;

public class SummaryVariableDefinition implements SummaryItemDefinition {

	String name;
	Variable variable;
	Class<?> type;
	Method method;

	public SummaryVariableDefinition(final Variable variable, String name, Class<?> type, Method method) {
		if (!variable.scopeGroup().equals("") && variable.scope() != Scope.GROUP) {
			this.variable = new Variable() {

				@Override
				public Class<? extends Annotation> annotationType() {
					return Variable.class;
				}

				@Override
				public String scopeGroup() {
					return variable.scopeGroup().equals("report") ? "" : variable.scopeGroup();
				}

				@Override
				public Scope scope() {
					return variable.scopeGroup().equals("report") ? Scope.REPORT : Scope.GROUP;
				}

				@Override
				public CalculationType calculation() {
					return variable.calculation();
				}

				@Override
				public String incrementOnGroupChange() {
					return variable.incrementOnGroupChange();
				}

				@SuppressWarnings("rawtypes")
				@Override
				public Class<? extends Aggregator> customAggregator() {
					return variable.customAggregator();
				}

			};
		} else {
			this.variable = variable;
		}
		this.name = name;
		this.type = type;
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}

	@Override
	public Class<?> getType() {
		return type;
	}

	public Variable getVariable() {
		return variable;
	}

	public void setVariable(Variable variable) {
		this.variable = variable;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\n@").append(name).append(" -> ");
		if (variable.calculation() != CalculationType.NONE) {
			builder.append(variable.calculation()).append(", ");
		}
		builder.append(variable.scope());
		return builder.toString();
	}

	@Override
	public Object getValue(Summary<?> summary) {
		if (summary == null) {
			throw new NullPointerException("summary is null");
		}
		if (!method.getDeclaringClass().isAssignableFrom(summary.getClass())) {
			throw new IllegalArgumentException("This summary item is not of the summary class " + summary.getClass());
		}
		try {
			return method.invoke(summary);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Scope getScope() {
		return variable.scope();
	}

	String scopeGroup = null;

	@Override
	public String getScopeGroup() {
		// the scope group can be configured in the format foo.bar
		// however composite groups are compared with foo_Bar
		if (scopeGroup == null) {
			scopeGroup = SummaryUtils.convertCompositeGroupToMethodFormat(variable.scopeGroup());
		}
		return scopeGroup;
	}

}
