package org.nextframework.report.definition.builder.config;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.report.definition.builder.BaseReportBuilder.FieldConfig;
import org.nextframework.report.definition.builder.GroupSetup;
import org.nextframework.report.definition.builder.LayoutReportBuilder;
import org.nextframework.report.definition.elements.ReportGrid;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportTextField;

public interface LayoutReportConfigurator {

	public void configureReport(LayoutReportBuilder layoutReportBuilder);

	public void configureLabelForGroup(String groupName, GroupSetup groupSetup, int index, ReportTextField field);

	public ReportGrid getReportFilterGrid(LayoutReportBuilder layoutReportBuilder);

	public void afterLayout(LayoutReportBuilder layoutReportBuilder);

	public void updateFieldConfig(LayoutReportBuilder layoutReportBuilder, BeanDescriptor beanDescriptor, PropertyDescriptor propertyDescriptor, FieldConfig fieldConfig);

	public void updateSeparator(LayoutReportBuilder layoutReportBuilder, ReportLabel line, ReportLabel space);

	public int getChartDefaultHeight();

}
