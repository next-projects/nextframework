package org.nextframework.report.generator.datasource;

import java.util.List;
import java.util.Map;

import org.nextframework.report.generator.ReportElement;

public interface DataSourceProvider {

	public String getName();

	public Class<?> getMainType(String fromClass);

	public <ROW> List<ROW> getResult(Class<ROW> mainType, ReportElement reportElement, Map<String, Object> filterMap, Map<String, Object> fixedCriteriaMap, int limitResults);

}