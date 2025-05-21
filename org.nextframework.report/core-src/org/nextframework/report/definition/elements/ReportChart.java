package org.nextframework.report.definition.elements;

import org.nextframework.chart.Chart;

public class ReportChart extends ReportItem {

	private Chart chart;
	private String reference;
	private int height;

	public ReportChart(String reference) {
		this.reference = reference;
	}

	public ReportChart(Chart chart, int width, int height) {
		this.chart = chart;
		this.width = width;
		this.height = height;
	}

	public ReportChart(String fieldReference, int width, int height) {
		this.reference = fieldReference;
		this.width = width;
		this.height = height;
	}

	public boolean isHeightAuto() {
		return (height & ReportConstants.AUTO_HEIGHT) == ReportConstants.AUTO_HEIGHT;
	}

	public ReportChart(Chart chart) {
		this.chart = chart;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Chart getChart() {
		return chart;
	}

	public void setChart(Chart chart) {
		this.chart = chart;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	/**
	 * Returns if the reference is a field reference. Otherwise it is a parameter reference.
	 * The reference is a parameter reference when the reference starts with "param."
	 * @return
	 */
	public boolean isFieldReference() {
		return reference != null && !reference.startsWith("param.");
	}

	public boolean isRendered() {
		return chart != null;
	}

	@Override
	public String getDescriptionName() {
		return "Chart";
	}

}
