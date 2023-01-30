package org.nextframework.report.renderer.html.builder.config;

import org.nextframework.report.definition.ReportSection;
import org.nextframework.report.renderer.html.design.HtmlTag;

public class DefaultHtmlReportViewConfigurator implements HtmlReportViewConfigurator {

	public void configureTable(HtmlTag containerTag, HtmlTag table) {
		table.getStyleClass().add("report-table");
	}

	public void configureTr(HtmlTag tr, String styleClass, ReportSection trSection, boolean isFirstRowOfSection, boolean isFirstRowOfBlock) {
		tr.getStyleClass().add(trSection.getType().toString());
		if (styleClass != null) {
			tr.getStyleClass().add(styleClass);
		}
		if (isFirstRowOfSection) {
			tr.getStyleClass().add("firstRowOfSection" + trSection.getType());
		}
		if (isFirstRowOfBlock) {
			tr.getStyleClass().add("firstRowOfBlock" + trSection.getType());
		}
	}

	public void configureTd(HtmlTag td) {

	}

	public void configureTextTag(HtmlTag tag) {

	}

	public void configureFrameTag(HtmlTag tag) {

	}

	public void configureImageTag(HtmlTag tag) {

	}

}