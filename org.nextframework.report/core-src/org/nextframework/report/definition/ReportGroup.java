package org.nextframework.report.definition;

import java.util.Map;
import java.util.Set;

public class ReportGroup {

	ReportDefinition definition;
	String expression;

	public ReportGroup(ReportDefinition definition, String expression) {
		this.definition = definition;
		this.expression = expression;
	}

	public ReportSection getSectionHeader() {
		Map<ReportGroup, ReportGroupSection> sectionsForGroups = definition.getSectionsForGroups();
		Set<ReportGroup> groups = sectionsForGroups.keySet();
		for (ReportGroup reportGroup : groups) {
			if (reportGroup.equals(this)) {
				return ((ReportGroupSection) sectionsForGroups.get(reportGroup)).getHeader();
			}
		}
		return null;
	}

	public ReportSection getSectionFooter() {
		Map<ReportGroup, ReportGroupSection> sectionsForGroups = definition.getSectionsForGroups();
		Set<ReportGroup> groups = sectionsForGroups.keySet();
		for (ReportGroup reportGroup : groups) {
			if (reportGroup.equals(this)) {
				return ((ReportGroupSection) sectionsForGroups.get(reportGroup)).getFooter();
			}
		}
		return null;
	}

	public ReportSection getSectionDetail() {
		Map<ReportGroup, ReportGroupSection> sectionsForGroups = definition.getSectionsForGroups();
		Set<ReportGroup> groups = sectionsForGroups.keySet();
		for (ReportGroup reportGroup : groups) {
			if (reportGroup.equals(this)) {
				return ((ReportGroupSection) sectionsForGroups.get(reportGroup)).getDetail();
			}
		}
		return null;
	}

	public String getExpression() {
		return expression;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ReportGroup [expression=").append(expression).append("]");
		return builder.toString();
	}

}
