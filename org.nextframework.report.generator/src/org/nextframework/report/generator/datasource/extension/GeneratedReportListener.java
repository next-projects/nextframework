package org.nextframework.report.generator.datasource.extension;

import java.util.Map;

import org.nextframework.report.generator.ReportElement;
import org.nextframework.report.generator.mvc.ReportDesignModel;

public interface GeneratedReportListener {

	boolean acceptMainClass(Class<?> mainType);

	void checkFilters(ReportDesignModel model, ReportElement reportElement, String filter, Map<String, Map<String, Object>> filtersMetadataMap);

}
