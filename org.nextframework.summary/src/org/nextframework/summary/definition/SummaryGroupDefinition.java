package org.nextframework.summary.definition;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.nextframework.summary.Summary;
import org.nextframework.summary.annotations.Group;
import org.nextframework.summary.annotations.Scope;

public class SummaryGroupDefinition implements SummaryItemDefinition {

	Group group;
	String name;
	Class<?> type;
	Method method;

	public SummaryGroupDefinition(final Group group, final String name, Class<?> type, Method method) {
		this.group = group;
		if (this.group.name().equals("METHOD-NAME")) {
			this.group = new Group() {

				@Override
				public Class<? extends Annotation> annotationType() {
					return Group.class;
				}

				@Override
				public String name() {
					return name;
				}

				public int value() {
					return group.value();
				}

			};
		}
		this.name = name;
		this.type = type;
		this.method = method;
	}

	public Method getMethod() {
		return method;
	}

	public Class<?> getType() {
		return type;
	}

	public Group getGroup() {
		return group;
	}

	public String getName() {
		return name;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("\n{").append(group.value()).append("}");
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
		return Scope.GROUP;
	}

	@Override
	public String getScopeGroup() {
		return name;
	}

}
