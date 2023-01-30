package org.nextframework.report.renderer.html.builder.config;

import org.nextframework.report.definition.ReportSection;
import org.nextframework.report.renderer.html.design.HtmlTag;

public interface HtmlReportViewConfigurator {

	public void configureTable(HtmlTag containerTag, HtmlTag table);

	public void configureTr(HtmlTag tr, String styleClass, ReportSection trSection, boolean isFirstRowOfSection, boolean isFirstRowOfBlock);

	public void configureTd(HtmlTag td);

	public void configureTextTag(HtmlTag tag);

	public void configureFrameTag(HtmlTag tag);

	public void configureImageTag(HtmlTag tag);

}