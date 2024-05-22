package org.nextframework.report.generator.chart;

import java.util.ArrayList;
import java.util.List;

public class ChartsElement {

	List<ChartElement> items = new ArrayList<ChartElement>();

	public List<ChartElement> getItems() {
		return items;
	}

	@Override
	public String toString() {
		return String.format("%s", items);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		ChartsElement other = (ChartsElement) obj;
		if (items == null) {
			if (other.items != null)
				return false;
		} else if (!items.equals(other.items))
			return false;
		return true;
	}

}
