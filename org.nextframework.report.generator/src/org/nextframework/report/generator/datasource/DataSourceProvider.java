package org.nextframework.report.generator.datasource;

import java.util.List;
import java.util.Map;

import org.nextframework.report.generator.ReportElement;

public interface DataSourceProvider<ROW> {

	public List<ROW> getResult(ReportElement reportElement, Map<String, Object> filterMap, Map<String, Object> fixedCriteriaMap, int limitResults);
	
	public Class<ROW> getMainType();

}
