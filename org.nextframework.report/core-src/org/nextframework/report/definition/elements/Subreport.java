package org.nextframework.report.definition.elements;

import org.nextframework.report.definition.ReportDefinition;

public class Subreport extends ReportItem {

	protected ReportDefinition report;
	protected String expression;
	protected int height = ReportConstants.AUTO_HEIGHT;

	public Subreport(ReportDefinition report) {
		super();
		this.report = report;
	}

	public Subreport(ReportDefinition report, String expression) {
		super();
		this.report = report;
		this.expression = expression;
	}

	protected Subreport() {

	}

	public int getHeight() {
		return height;
	}

	public Subreport setHeight(int height) {
		this.height = height;
		return this;
	}

	public ReportDefinition getReport() {
		return report;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String subreportExpression) {
		this.expression = subreportExpression;
	}

	@Override
	public String getDescriptionName() {
		return "Subreport";
	}

}
