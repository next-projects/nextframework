package org.nextframework.report.definition.builder;

import java.util.Map;

import org.nextframework.report.definition.ReportSection;
import org.nextframework.report.definition.builder.BaseReportBuilder.FieldConfig;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.definition.elements.style.ReportAlignment;

public class ColumnBuilder {

	private int column;
	private BaseReportBuilder baseReportBuilder;
	private int colspan;
	private ReportAlignment alignment;

	public ColumnBuilder(int column, int colspan, ReportAlignment alignment, BaseReportBuilder baseReportBuilder, boolean sumarizedData) {
		this.baseReportBuilder = baseReportBuilder;
		this.column = column;
		this.colspan = colspan;
		this.alignment = alignment;
	}

	public ColumnBuilder summary(Object... elements) {
		configure(baseReportBuilder.getDefinition().getSectionSummary(), elements, "summary.");
		return this;
	}

	public ColumnBuilder lastPageFooter(Object... elements) {
		configure(baseReportBuilder.getDefinition().getSectionLastPageFooter(), elements, "summary.");
		return this;
	}

	public ColumnBuilder firstPage(Object... elements) {
		configure(baseReportBuilder.getDefinition().getSectionFirstPageHeader(), elements, "summary.");
		return this;
	}

	public ColumnBuilder summaryHeader(Object... elements) {
		configure(baseReportBuilder.getDefinition().getSectionSummaryDataHeader(), elements, "summary.");
		return this;
	}

	public ColumnBuilder summaryDetail(Object... elements) {
		configure(baseReportBuilder.getDefinition().getSectionSummaryDataDetail(), elements, "summary.");
		return this;
	}

	public ColumnBuilder pageHeader(Object... elements) {
		configure(baseReportBuilder.getDefinition().getSectionPageHeader(), elements, "summary.");
		return this;
	}

	public ColumnBuilder pageFooter(Object... elements) {
		configure(baseReportBuilder.getDefinition().getSectionPageFooter(), elements, "summary.");
		return this;
	}

	public ColumnBuilder detailHeader(Object... elements) {
		configure(baseReportBuilder.getDefinition().getSectionDetailHeader(), elements, "row.");
		return this;
	}

	public ColumnBuilder detail(Object... elements) {
		configure(baseReportBuilder.getDefinition().getSectionDetail(), elements, "row.");
		return this;
	}

	public ColumnBuilder groupHeader(String groupName, Object... elements) {
		configure(baseReportBuilder.getGroupSetups().get(groupName).getReportGroup().getSectionHeader(), elements, "summary.");
		return this;
	}

	public ColumnBuilder groupDetail(String groupName, Object... elements) {
		configure(baseReportBuilder.getGroupSetups().get(groupName).getReportGroup().getSectionDetail(), elements, "summary.");
		return this;
	}

	//syntax sugar methods
	public ColumnBuilder fieldSummary(String fieldName) {
		FieldConfig configForRowField = baseReportBuilder.getConfigForSummaryField(fieldName);
		fieldSummaryForGroups(fieldName, configForRowField.label, colspan, null);
		fieldDetailWithSummary(fieldName, configForRowField.label, colspan, null);
		return this;
	}

	public ColumnBuilder fieldSummary(String fieldName, Object label) {
		fieldSummaryForGroups(fieldName, label, colspan, null);
		fieldDetailWithSummary(fieldName, label, colspan, null);
		return this;
	}

	public ColumnBuilder fieldSummary(String fieldName, Object label, String pattern) {
		fieldSummaryForGroups(fieldName, label, colspan, pattern);
		fieldDetailWithSummary(fieldName, label, colspan, pattern);
		return this;
	}

	public ColumnBuilder fieldSummaryForGroups(String fieldName) {
		return fieldSummaryForGroups(fieldName, null, colspan, null);
	}

	public ColumnBuilder fieldSummaryForGroups(String fieldName, Object label) {
		return fieldSummaryForGroups(fieldName, label, colspan, null);
	}

	public ColumnBuilder fieldSummaryForGroups(String fieldName, Object label, String pattern) {
		return fieldSummaryForGroups(fieldName, label, colspan, pattern);
	}

	public ColumnBuilder fieldSummaryForGroups(String fieldName, Object label, int colspan) {
		return fieldSummaryForGroups(fieldName, label, colspan, null);
	}

	public ColumnBuilder fieldSummaryForGroups(String fieldName, Object label, int colspan, String pattern) {

		FieldConfig configForSummaryField = baseReportBuilder.getConfigForSummaryField(fieldName);

		ReportLabel labelElement = baseReportBuilder.label(label == null ? configForSummaryField.label : label);
		labelElement.getStyle().setPaddingRight(4);
		labelElement.getStyle().setAlignment(configForSummaryField.alignment);
		labelElement.setColspan(colspan);

		summaryHeader(labelElement);
		ReportTextField reportFieldElementForFieldConfig = getFieldElementForFieldConfig(configForSummaryField, "report");
		if (pattern != null) {
			reportFieldElementForFieldConfig.setPattern(pattern);
		}
		summaryDetail(reportFieldElementForFieldConfig.setColspan(colspan));

		Map<String, GroupSetup> groupSetups = baseReportBuilder.getGroupSetups();
		for (String group : groupSetups.keySet()) {
			ReportTextField fieldElementForFieldConfig = getFieldElementForFieldConfig(configForSummaryField, group);
			if (pattern != null) {
				fieldElementForFieldConfig.setPattern(pattern);
			}
			fieldElementForFieldConfig.setColspan(colspan);
			groupHeader(group, fieldElementForFieldConfig);
		}

		return this;
	}

	public ColumnBuilder fieldDetail(String fieldName) {
		return fieldDetail(fieldName, null, colspan, null, null);
	}

	public ColumnBuilder fieldDetail(String fieldName, int colspan) {
		return fieldDetail(fieldName, null, colspan, null, null);
	}

	public ColumnBuilder fieldDetail(String fieldName, Object label) {
		return fieldDetail(fieldName, label, colspan, null, null);
	}

	public ColumnBuilder fieldDetail(String fieldName, Object label, String pattern) {
		return fieldDetail(fieldName, label, colspan, pattern, null);
	}

	public ColumnBuilder fieldDetail(String fieldName, Object label, int colspan) {
		return fieldDetail(fieldName, label, colspan, null, null);
	}

	public ColumnBuilder fieldDetail(String fieldName, Object label, int colspan, String pattern) {
		return fieldDetail(fieldName, label, colspan, pattern, null);
	}

	public ColumnBuilder fieldDetail(String fieldName, Object label, int colspan, String pattern, ReportAlignment alignment) {
		FieldConfig configForRowField;
		if (fieldName.startsWith("summary.")) {
			configForRowField = baseReportBuilder.getConfigForSummaryField(fieldName.substring("summary.".length()));
		} else {
			configForRowField = baseReportBuilder.getConfigForRowField(fieldName);
		}
		if (alignment != null) {
			configForRowField.alignment = alignment;
		}
		fieldDetailWithConfig(label, configForRowField, colspan, pattern);
		return this;
	}

	public ColumnBuilder fieldDetailWithSummary(String fieldName, Object label) {
		return fieldDetailWithSummary(fieldName, label, colspan, null);
	}

	public ColumnBuilder fieldDetailWithSummary(String fieldName, Object label, int colspan) {
		return fieldDetailWithSummary(fieldName, label, colspan, null);
	}

	public ColumnBuilder fieldDetailWithSummary(String fieldName, Object label, int colspan, String pattern) {
		FieldConfig configForRowField = baseReportBuilder.getConfigForSummaryField(fieldName);
		fieldDetailWithConfig(label, configForRowField, colspan, pattern);
		return this;
	}

	private void fieldDetailWithConfig(Object label, FieldConfig configForRowField, int colspan, String pattern) {

		ReportLabel labelElement = baseReportBuilder.label(label == null ? configForRowField.label : label);
		labelElement.setColspan(colspan);

		ReportTextField fieldElement = getFieldElementForFieldConfig(configForRowField, null);
		fieldElement.setColspan(colspan);
		if (pattern != null) {
			fieldElement.setPattern(pattern);
		}

		detailHeader(labelElement);
		detail(fieldElement);

	}

	private ReportTextField getFieldElementForFieldConfig(FieldConfig configForRowField, String group) {
		ReportTextField fieldElement;
		String reportExpression = configForRowField.reportExpression;
		if (group != null) {
			reportExpression = reportExpression + capitalize(group);
		}
		fieldElement = baseReportBuilder.field(reportExpression, false);
		fieldElement.setPattern(configForRowField.pattern);
		fieldElement.getStyle().setAlignment(configForRowField.alignment);
		fieldElement.getStyle().setPaddingRight(4);
		return fieldElement;
	}

	private void configure(ReportSection section, Object[] elements, String defaultFieldPrefix) {
		baseReportBuilder.configure(this.column, this.colspan, this.alignment, section, elements, defaultFieldPrefix);
	}

	private String capitalize(String text) {
		return Character.toUpperCase(text.charAt(0)) + text.substring(1);
	}

}
