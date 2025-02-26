package org.nextframework.persistence;

/**
 * Utility interface to be implemented by DAOs that can apply custom filters.
 * This interface is intended to be used by extensions of the framework. The framework itself does not use this interface.
 * @author rogel
 */
public interface GeneratedReportDAOFilter {

	public boolean isFilterableForPropertyForGeneratedReport(String filter);

	public void filterQueryForGeneratedReport(QueryBuilder<?> query, String filter, String filterNoSuffix, Object parameterValue);

}