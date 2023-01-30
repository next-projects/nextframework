package org.nextframework.report.generator.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nextframework.report.generator.ReportElement;
import org.nextframework.report.generator.datasource.DataSourceProvider;

public class DataElement {

	private DataSourceProvider dataSourceProvider;
	private Class<?> mainType;
	private List<FilterElement> filters = new ArrayList<FilterElement>();
	private List<GroupElement> groups = new ArrayList<GroupElement>();
	private List<CalculatedFieldElement> calculatedFields = new ArrayList<CalculatedFieldElement>();

	public DataSourceProvider getDataSourceProvider() {
		return dataSourceProvider;
	}

	public void setDataSourceProvider(DataSourceProvider dataSourceProvider) {
		this.dataSourceProvider = dataSourceProvider;
	}

	public Class<?> getMainType() {
		return mainType;
	}

	public void setMainType(Class<?> mainType) {
		this.mainType = mainType;
	}

	public List<?> getResult(ReportElement reportElement, Map<String, Object> filterMap, Map<String, Object> fixedCriteriaMap, int limitResults) {
		return dataSourceProvider.getResult(mainType, reportElement, filterMap, fixedCriteriaMap, limitResults);
	}

	public List<CalculatedFieldElement> getCalculatedFields() {
		return calculatedFields;
	}

	public void setCalculatedFields(List<CalculatedFieldElement> calculatedFields) {
		this.calculatedFields = calculatedFields;
	}

	public List<FilterElement> getFilters() {
		return filters;
	}

	public void setFilters(List<FilterElement> filters) {
		this.filters = filters;
	}

	public List<GroupElement> getGroups() {
		return groups;
	}

	public CalculatedFieldElement getCalculatedFieldWithName(String fieldName) {
		for (CalculatedFieldElement calculatedFieldElement : calculatedFields) {
			if (calculatedFieldElement.getName().equals(fieldName)) {
				return calculatedFieldElement;
			}
		}
		return null;
	}

	public FilterElement getFilterByName(String property) {
		for (FilterElement filter : filters) {
			if (filter.getName().equals(property)) {
				return filter;
			}
		}
		return null;
	}

	public boolean isCalculated(String fieldName) {
		return getCalculatedFieldWithName(fieldName) != null;
	}

	@Override
	public String toString() {
		return String.format("%s, %n\t\tfilters=%s, %n\t\tgroups=%s", dataSourceProvider, filters, groups);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dataSourceProvider == null) ? 0 : dataSourceProvider.hashCode());
		result = prime * result + ((filters == null) ? 0 : filters.hashCode());
		result = prime * result + ((groups == null) ? 0 : groups.hashCode());
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
		DataElement other = (DataElement) obj;
		if (dataSourceProvider == null) {
			if (other.dataSourceProvider != null)
				return false;
		} else if (!dataSourceProvider.equals(other.dataSourceProvider))
			return false;
		if (filters == null) {
			if (other.filters != null)
				return false;
		} else if (!filters.equals(other.filters))
			return false;
		if (groups == null) {
			if (other.groups != null)
				return false;
		} else if (!groups.equals(other.groups))
			return false;
		return true;
	}

}
