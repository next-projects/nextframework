package org.nextframework.report.renderer.html.builder.config;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportSection;
import org.nextframework.report.renderer.html.builder.PrintElement;
import org.nextframework.report.renderer.html.design.HtmlTag;

public interface HtmlReportViewConfigurator {

	public void configureTable(ReportDefinition definition, HtmlTag containerTag, HtmlTag table);

	public void configureTr(ReportSection trSection, HtmlTag tr, String styleClass, boolean isFirstRowOfSection, boolean isFirstRowOfBlock);

	public void configureTd(PrintElement element, HtmlTag td);

	public void configureTextTag(HtmlTag tag);

	public void configureFrameTag(HtmlTag tag);

	public void configureImageTag(HtmlTag tag);

}
