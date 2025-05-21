package org.nextframework.report.definition.elements;

import java.util.HashMap;
import java.util.Map;

import org.nextframework.report.definition.ReportColumn;
import org.nextframework.report.definition.ReportParent;
import org.nextframework.report.definition.ReportSectionRow;
import org.nextframework.report.definition.elements.style.ReportBasicStyle;

public abstract class ReportItem {

	protected ReportParent parent;
	protected ReportSectionRow row;
	protected ReportColumn column;
	protected int width = ReportConstants.AUTO_WIDTH;
	protected int colspan = 1;
	protected ReportBasicStyle style = new ReportBasicStyle();
	protected Map<String, Object> renderParameters = new HashMap<String, Object>();

	public ReportBasicStyle getStyle() {
		return style;
	}

	public Map<String, Object> getRenderParameters() {
		return renderParameters;
	}

	public Object setRenderParameter(String key, Object value) {
		return renderParameters.put(key, value);
	}

	public ReportParent getParent() {
		return parent;
	}

	public void setParent(ReportParent parent) {
		this.parent = parent;
	}

	public ReportSectionRow getRow() {
		return row;
	}

	public ReportColumn getColumn() {
		return column;
	}

	public void setRow(ReportSectionRow row) {
		this.row = row;
	}

	public void setColumn(ReportColumn column) {
		this.column = column;
	}

	protected boolean isFieldWidthAuto() {
		return (width & ReportConstants.AUTO_WIDTH) == ReportConstants.AUTO_WIDTH;
	}

	public boolean isWidthAuto() {
		return (getWidth() & ReportConstants.AUTO_WIDTH) == ReportConstants.AUTO_WIDTH;
	}

	public boolean isWidthPercent() {
		return (getWidth() & ReportConstants.PERCENT_WIDTH) == ReportConstants.PERCENT_WIDTH;
	}

	public int getWidth() {
		return width;
	}

	public ReportItem setWidth(int width) {
		this.width = width;
		return this;
	}

	public int getColspan() {
		return colspan;
	}

	public ReportItem setColspan(int colspan) {
		this.colspan = colspan;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getDescriptionName());
		return builder.toString();
	}

	public abstract String getDescriptionName();

}
