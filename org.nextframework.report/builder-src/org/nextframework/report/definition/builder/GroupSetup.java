package org.nextframework.report.definition.builder;

import org.nextframework.report.definition.ReportGroup;
import org.nextframework.report.definition.elements.ReportTextField;

public class GroupSetup {

	private ReportGroup reportGroup;
	private String groupExpression;
	private String groupName;
	private ReportTextField labelTextField;

	public ReportGroup getReportGroup() {
		return reportGroup;
	}

	public String getGroupName() {
		return groupName;
	}

	public String getGroupExpression() {
		return groupExpression;
	}

	public void setReportGroup(ReportGroup reportGroup) {
		this.reportGroup = reportGroup;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public void setGroupExpression(String groupExpression) {
		this.groupExpression = groupExpression;
	}

	public void setLabelTextField(ReportTextField field) {
		this.labelTextField = field;
	}

	public ReportTextField getLabelTextField() {
		return labelTextField;
	}

}
