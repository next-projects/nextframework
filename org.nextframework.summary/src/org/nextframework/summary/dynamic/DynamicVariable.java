package org.nextframework.summary.dynamic;

import java.lang.reflect.Type;

import org.nextframework.summary.annotations.CalculationType;

public class DynamicVariable {

	String name;
	String displayName;
	
	CalculationType calculationType;
	
	String javaExpression;
	Type returnType;
	
	public DynamicVariable(String name, CalculationType calculationType, String javaExpression, Type type) {
		super();
		this.name = name;
		this.calculationType = calculationType;
		this.javaExpression = javaExpression;
		this.returnType = type;
	}
	
	public DynamicVariable(String name, String displayName, CalculationType calculationType, String javaExpression, Type type) {
		super();
		this.name = name;
		this.displayName = displayName;
		this.calculationType = calculationType;
		this.javaExpression = javaExpression;
		this.returnType = type;
	}

	public DynamicVariable(String name, CalculationType calculationType) {
		super();
		this.name = name;
		this.calculationType = calculationType;
	}
	
	public DynamicVariable(String name, String displayName, CalculationType calculationType) {
		super();
		this.name = name;
		this.displayName = displayName;
		this.calculationType = calculationType;
	}

	public String getJavaExpression() {
		return javaExpression;
	}
	public Type getReturnType() {
		return returnType;
	}
	
	public String getName() {
		return name;
	}

	public CalculationType getCalculationType() {
		return calculationType;
	}

	public String getDisplayName() {
		return displayName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((calculationType == null) ? 0 : calculationType.hashCode());
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
		result = prime * result + ((javaExpression == null) ? 0 : javaExpression.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((returnType == null) ? 0 : returnType.hashCode());
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
		DynamicVariable other = (DynamicVariable) obj;
		if (calculationType == null) {
			if (other.calculationType != null)
				return false;
		} else if (!calculationType.equals(other.calculationType))
			return false;
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (javaExpression == null) {
			if (other.javaExpression != null)
				return false;
		} else if (!javaExpression.equals(other.javaExpression))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (returnType == null) {
			if (other.returnType != null)
				return false;
		} else if (!returnType.equals(other.returnType))
			return false;
		return true;
	}

	
}
