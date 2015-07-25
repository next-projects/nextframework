package org.nextframework.report.definition;

public class ReportSectionRow {

	ReportSection section;
	String styleClass;
	int rowNumber;
	
	public ReportSectionRow(ReportSection section, int rowNumber){
		this.section = section;
		this.rowNumber = rowNumber;
	}
	
	public int getRowNumber() {
		return rowNumber;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("row "+section.getType()+"(").append(rowNumber).append(")");
		return builder.toString();
	}
	
	public ReportSection getSection() {
		return section;
	}
	
	public boolean hasElements(){
		return section.getDefinition().hasRowElements(this);
	}
}
