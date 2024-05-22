package org.nextframework.report.renderer.html;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.elements.ReportItem;
import org.nextframework.report.renderer.ReportRenderer;
import org.nextframework.report.renderer.ReportRendererFactory;
import org.nextframework.report.renderer.html.builder.HtmlReportBuilder;
import org.nextframework.report.renderer.html.builder.HtmlReportBuilderImpl;
import org.nextframework.report.renderer.html.design.HtmlDesign;

public class HtmlReportRenderer implements ReportRenderer {

	public static final String HTML = "HTML";

	static HtmlReportRenderer instance = new HtmlReportRenderer();

	static {
		ReportRendererFactory.registerRenderer(instance);
	}

	public static HtmlReportRenderer getInstance() {
		return instance;
	}

	private HtmlReportBuilder createHtmlReportBuilder() {
		return new HtmlReportBuilderImpl();
	}

	@Override
	public String getOutputType() {
		return HTML;
	}

	@Override
	public Object renderReport(ReportDefinition report) {
		HtmlDesign htmlDesign = getHtmlDesign(report);
		return htmlDesign.toString();
	}

	public String renderItem(ReportItem item) {
		return createHtmlReportBuilder().getHtmlDesign(item).toString();
	}

	public HtmlDesign getHtmlDesign(ReportDefinition report) {
		return createHtmlReportBuilder().getHtmlDesign(report);
	}

	public static String renderAsHtml(ReportDefinition report) {
		return (String) ReportRendererFactory.getRendererForOutput(HTML).renderReport(report);
	}

}
