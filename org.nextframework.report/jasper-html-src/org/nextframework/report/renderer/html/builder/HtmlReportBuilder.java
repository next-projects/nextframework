package org.nextframework.report.renderer.html.builder;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.elements.ReportItem;
import org.nextframework.report.renderer.html.design.HtmlDesign;

public interface HtmlReportBuilder {

	/**
	 * Indicates which subreport must be enabled
	 */
	String ENALBE_SUBREPORT = "ENABLE_SUBREPORT";

	HtmlDesign getHtmlDesign(ReportDefinition definition);

	HtmlDesign getHtmlDesign(ReportItem item);

}
