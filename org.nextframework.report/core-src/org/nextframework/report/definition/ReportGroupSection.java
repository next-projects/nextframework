package org.nextframework.report.definition;

import java.util.List;

public class ReportGroupSection extends ReportSection {

	private ReportGroup group;

	private ReportSection detail;
	private ReportSection header;
	private ReportSection footer;

	private ReportGroupSection(ReportDefinition definition, ReportGroup group, ReportSectionType sectionType) {
		super(definition, sectionType);
		this.group = group;
	}

	public ReportGroupSection(ReportDefinition definition, ReportGroup group) {
		this(definition, group, ReportSectionType.GROUP);
		header = new ReportGroupSection(definition, group, ReportSectionType.GROUP_HEADER);
		footer = new ReportGroupSection(definition, group, ReportSectionType.GROUP_FOOTER);
		detail = new ReportGroupSection(definition, group, ReportSectionType.GROUP_DETAIL);
	}

	public ReportGroup getGroup() {
		return group;
	}

	public ReportSection getHeader() {
		return header;
	}

	public ReportSection getFooter() {
		return footer;
	}

	public ReportSection getDetail() {
		return detail;
	}

	@Override
	public List<ReportSectionRow> getRows() {
		if (getType() == ReportSectionType.GROUP) {
			return header.getRows();
		} else {
			return super.getRows();
		}
	}

}
