package org.nextframework.report.generator.layout;

import org.nextframework.classmanager.ClassManagerFactory;
import org.nextframework.report.generator.data.FieldFormatter;

public class FieldDetailElement implements LayoutItem {

	String name;
	String label;
	String pattern;

	String aggregate;

	String aggregateType;

	public boolean isAggregateField() {
		return "true".equals(aggregate);
	}

	public FieldDetailElement(String name) {
		this.name = name;
	}

	public FieldDetailElement() {
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getName() {
		return name;
	}

	public String getAggregateType() {
		return aggregateType;
	}

	public void setAggregateType(String aggregateType) {
		this.aggregateType = aggregateType;
	}

	public String getAggregate() {
		return aggregate;
	}

	public void setAggregate(String aggregate) {
		this.aggregate = aggregate;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public boolean isCustomPattern() {
		if (pattern == null || pattern.equals("")) {
			return false;
		}
		return pattern.startsWith("c");
	}

	public boolean isDatePattern() {
		if (pattern == null || pattern.equals("")) {
			return false;
		}
		return !isDecimalPattern() && !isCustomPattern();
	}

	public boolean isDecimalPattern() {
		if (pattern == null || pattern.equals("")) {
			return false;
		}
		char[] charArray = pattern.toCharArray();
		for (char c : charArray) {
			if (!(c == '0' || c == '#' || c == ',' || c == '.')) {
				return false;
			}
		}
		return true;
	}

	public Class<?> getCustomPatternClass() {
		if (!isCustomPattern()) {
			return null;
		}
		String cName = pattern.substring(1);
		Class<FieldFormatter>[] fieldFormatters = ClassManagerFactory.getClassManager().getAllClassesOfType(FieldFormatter.class);
		for (Class<FieldFormatter> class1 : fieldFormatters) {
			if (class1.getSimpleName().equals(cName)) {
				return class1;
			}
		}
		return null;
	}

	public String getCustomPatternExpression() {
		if (!isCustomPattern()) {
			return null;
		}
		return "new " + getCustomPatternClass().getCanonicalName() + "().format";
	}

	@Override
	public String toString() {
		return String.format("\n\t\tFieldDetail[%s%s%s]", name, isAggregateField() ? "*" : "", pattern != null ? ", " + pattern : "");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aggregate == null) ? 0 : aggregate.hashCode());
		result = prime * result + ((label == null) ? 0 : label.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((pattern == null) ? 0 : pattern.hashCode());
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
		FieldDetailElement other = (FieldDetailElement) obj;
		if (aggregate == null) {
			if (other.aggregate != null)
				return false;
		} else if (!aggregate.equals(other.aggregate))
			return false;
		if (label == null) {
			if (other.label != null)
				return false;
		} else if (!label.equals(other.label))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (pattern == null) {
			if (other.pattern != null)
				return false;
		} else if (!pattern.equals(other.pattern))
			return false;
		return true;
	}

}
