package org.nextframework.summary.dynamic;

import java.lang.reflect.Type;

import org.nextframework.summary.annotations.CalculationType;

public class DynamicVariableDecorator extends DynamicVariable {

	private String forVariable;

	public DynamicVariableDecorator(String name, String displayName, String forVariable, String expression, Type decoratorType) {
		super(name, displayName, CalculationType.NONE, expression, decoratorType);
		this.forVariable = forVariable;
	}

	public String getForVariable() {
		return forVariable;
	}

}
