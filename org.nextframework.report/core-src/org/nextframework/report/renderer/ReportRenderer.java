package org.nextframework.report.renderer;

import org.nextframework.report.definition.ReportDefinition;

public interface ReportRenderer {

	public Object renderReport(ReportDefinition report);

	public String getOutputType();

}
