package org.nextframework.report.generator.datasource.extension;

import java.lang.reflect.Type;
import java.util.Map;

import org.nextframework.persistence.QueryBuilder;
import org.nextframework.report.generator.ReportElement;
import org.nextframework.report.generator.mvc.ReportDesignModel;

public interface GeneratedReportListener {

	Class getFromClass();

	void checkFilters(ReportDesignModel model, ReportElement reportElement, String filter, Map<String, Map<String, Object>> filtersMetadataMap);

	boolean setWhereClause(QueryBuilder<?> query, String filter, String filterNoSuffix, Object parameterValue, Type type);

}