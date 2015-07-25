package org.nextframework.report.generator.generated;

import org.nextframework.report.definition.builder.IReportBuilder;
import org.nextframework.summary.dynamic.DynamicSummary;

public class ReportSpec {
	
	IReportBuilder reportBuilder;
	private DynamicSummary<?> summary;

	public IReportBuilder getReportBuilder() {
		return reportBuilder;
	}
	
	public DynamicSummary<?> getSummary() {
		return summary;
	}
	
	public void setReportBuilder(IReportBuilder reportBuilder) {
		this.reportBuilder = reportBuilder;
	}

	public void setSummary(DynamicSummary<?> summary) {
		this.summary = summary;
	}
}
