package org.nextframework.chart;

import java.io.Serializable;
import java.util.Arrays;

public class ChartRow implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Object group;
	private Number[] values;
	
	public ChartRow(Object group, Number... values) {
		this.group = group;
		this.values = values;
	}

	public Object getGroup() {
		return group;
	}

	public Number[] getValues() {
		return values;
	}

	public void setGroup(Object group) {
		this.group = group;
	}

	public void setValues(Number[] values) {
		this.values = values;
	}
	
	@Override
	public String toString() {
		return "Group: "+group+" Series: "+Arrays.deepToString(values);
	}
	
}
