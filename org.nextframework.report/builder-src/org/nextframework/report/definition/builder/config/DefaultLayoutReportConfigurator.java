package org.nextframework.report.definition.builder.config;

import java.awt.Color;
import java.io.InputStream;
import java.util.Map;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.report.definition.ReportSection;
import org.nextframework.report.definition.ReportSectionRow;
import org.nextframework.report.definition.builder.BaseReportBuilder.FieldConfig;
import org.nextframework.report.definition.builder.GroupSetup;
import org.nextframework.report.definition.builder.LayoutReportBuilder;
import org.nextframework.report.definition.elements.ReportConstants;
import org.nextframework.report.definition.elements.ReportGrid;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.definition.elements.style.Border;

public class DefaultLayoutReportConfigurator implements LayoutReportConfigurator {

	public static final String LOGO = "LOGO";

	public void configureReport(LayoutReportBuilder layoutReportBuilder) {
	}

	public void configureLabelForGroup(String groupName, GroupSetup groupSetup, int index, ReportTextField field) {

		field.getStyle().setPaddingLeft(2 + index * 5);
		groupSetup.getReportGroup().getSectionHeader().getRenderParameters().put("index", index);
		for (ReportSectionRow row : groupSetup.getReportGroup().getSectionHeader().getRows()) {
			row.setStyleClass("groupLabelRow_" + index);
		}

	}

	public ReportGrid getReportFilterGrid(LayoutReportBuilder layoutReportBuilder) {
		return new ReportGrid(new int[] {
				15 | ReportConstants.PERCENT_WIDTH,
				35 | ReportConstants.PERCENT_WIDTH,
				15 | ReportConstants.PERCENT_WIDTH,
				35 | ReportConstants.PERCENT_WIDTH });
	}

	public void afterLayout(LayoutReportBuilder layoutReportBuilder) {
		configureLogo(layoutReportBuilder);
		configureSectionsStyle(layoutReportBuilder);
	}

	protected void configureLogo(LayoutReportBuilder layoutReportBuilder) {
		Map<String, Object> parameters = layoutReportBuilder.getDefinition().getParameters();
		Object logo = parameters.get(LOGO);
		if (logo == null) {
			logo = getLogo(layoutReportBuilder);
			parameters.put(LOGO, logo);
		}
	}

	protected InputStream getLogo(LayoutReportBuilder layoutReportBuilder) {
		return getClass().getClassLoader().getResourceAsStream("org/nextframework/report/renderer/jasper/logonextframework.png");
	}

	private void configureSectionsStyle(LayoutReportBuilder layoutReportBuilder) {
		configureSectionWithStyle(layoutReportBuilder.getDefinition().getSectionDetailHeader(), "detailHeader");
		configureSectionWithStyle(layoutReportBuilder.getDefinition().getSectionSummaryDataHeader(), "detailHeader");
		configureSectionWithStyle(layoutReportBuilder.getDefinition().getSectionSummaryDataDetail(), "summaryDetail");
	}

	private void configureSectionWithStyle(ReportSection section, String style) {
		for (ReportSectionRow reportSectionRow : section.getRows()) {
			if (reportSectionRow.getStyleClass() == null) {
				reportSectionRow.setStyleClass(style);
			}
		}
	}

	public void updateFieldConfig(LayoutReportBuilder layoutReportBuilder, BeanDescriptor beanDescriptor, PropertyDescriptor propertyDescriptor, FieldConfig fieldConfig) {

	}

	@Override
	public void updateSeparator(LayoutReportBuilder layoutReportBuilder, ReportLabel line, ReportLabel space) {
		line.getStyle().setBold(true);
		line.getStyle().setBorderBottom(new Border(1, Color.gray));
	}

	public int getChartDefaultHeight() {
		return 125;
	}

}
