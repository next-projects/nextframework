package org.nextframework.report.generator.layout;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.summary.Summary;
import org.nextframework.summary.compilation.SummaryResult;

public class DynamicBaseReportDefinition extends ReportDefinition {

	private SummaryResult<?, ? extends Summary<?>> summarizedData;

	public void setSummarizedData(SummaryResult<?, ? extends Summary<?>> summaryResult) {
		this.summarizedData = summaryResult;
	}

	public SummaryResult<?, ? extends Summary<?>> getSummarizedData() {
		return summarizedData;
	}

}
