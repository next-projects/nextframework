package org.nextframework.report.generator.chart;

public class ChartSerieElement {

	String property;
	String aggregateFunction;
	String label;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getAggregateFunction() {
		if (aggregateFunction == null) {
			return "SUM";
		}
		return aggregateFunction;
	}

	public void setAggregateFunction(String aggregateFunction) {
		this.aggregateFunction = aggregateFunction;
	}

	@Override
	public String toString() {
		return String.format("property=%s", property);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((property == null) ? 0 : property.hashCode());
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
		ChartSerieElement other = (ChartSerieElement) obj;
		if (property == null) {
			if (other.property != null)
				return false;
		} else if (!property.equals(other.property))
			return false;
		return true;
	}

}
