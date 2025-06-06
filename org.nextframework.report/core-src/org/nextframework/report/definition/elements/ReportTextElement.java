package org.nextframework.report.definition.elements;

import org.nextframework.report.definition.elements.style.ReportItemStyle;

public abstract class ReportTextElement extends ReportItem {

	protected ReportItemStyle itemStyle = new ReportItemStyle();
	protected int height = ReportConstants.AUTO_HEIGHT;

	public ReportItemStyle getStyle() {
		return itemStyle;
	}

	public boolean isHeightAuto() {
		return (height & ReportConstants.AUTO_HEIGHT) == ReportConstants.AUTO_HEIGHT;
	}

	public int getHeight() {
		return height;
	}

	public ReportTextElement setHeight(int height) {
		this.height = height;
		return this;
	}

	@Override
	public ReportTextElement setWidth(int width) {
		return (ReportTextElement) super.setWidth(width);
	}

	@Override
	public ReportTextElement setColspan(int colspan) {
		return (ReportTextElement) super.setColspan(colspan);
	}

	public ReportTextElement setStyle(ReportItemStyle style) {
		this.itemStyle = style;
		return this;
	}

}
