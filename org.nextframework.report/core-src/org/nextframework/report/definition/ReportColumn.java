package org.nextframework.report.definition;

import org.nextframework.report.definition.elements.ReportConstants;

public class ReportColumn {

	ReportDefinition definition;
	ReportColumn previous;
	ReportColumn next;
	
	int width = ReportConstants.AUTO_WIDTH;
	int index;

	public ReportColumn(ReportDefinition definition, int index) {
		this.definition = definition;
		this.index = index;
	}
	public int getIndex() {
		return index;
	}
	public boolean isWidthAuto(){
		return(width & ReportConstants.AUTO_WIDTH) == ReportConstants.AUTO_WIDTH;
	}
	public int getWidth() {
		return width;
	}

	public ReportColumn getPrevious() {
		return previous;
	}

	public ReportColumn getNext() {
		return next;
	}

	void setPrevious(ReportColumn previous) {
		this.previous = previous;
	}

	void setNext(ReportColumn next) {
		this.next = next;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("column(").append(index).append(", width=").append(isWidthAuto()?"AUTO":width).append(")");
		return builder.toString();
	}
	
}
