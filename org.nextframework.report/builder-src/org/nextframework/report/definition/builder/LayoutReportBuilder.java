package org.nextframework.report.definition.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.report.definition.ReportColumn;
import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportGroup;
import org.nextframework.report.definition.ReportSection;
import org.nextframework.report.definition.ReportSectionRow;
import org.nextframework.report.definition.builder.config.LayoutReportConfigurator;
import org.nextframework.report.definition.elements.ReportGrid;
import org.nextframework.report.definition.elements.ReportItem;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportTextElement;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.definition.elements.style.ReportAlignment;
import org.nextframework.service.ServiceFactory;

public abstract class LayoutReportBuilder extends BaseReportBuilder {

	public static final String FILTER_PARAMETER = "REPORT_FILTER";
	public static final String FIELD_NAME = "fieldName";

	protected int currentColumn = 0;
	protected Object filter;
	protected ReportGrid reportFilterGrid;
	private BeanDescriptor filterClassBeanDescriptorCache;

	@Override
	protected void configureDefinition() {

		super.configureDefinition();
		getConfigurator().configureReport(this);

		if (isSetupColumnWidths()) {
			getDefinition().setColumnWidths(getColumnConfig());
		}

		this.reportFilterGrid = getConfigurator().getReportFilterGrid(this);
		getDefinition().addTitleItem(this.reportFilterGrid);
		layoutReportFilter();

		layoutReport();

		afterLayout();

	}

	public LayoutReportConfigurator getConfigurator() {
		return ServiceFactory.getService(LayoutReportConfigurator.class);
	}

	@Override
	protected void onSetupGroup(String groupName, GroupSetup groupSetup, int index) {
		super.onSetupGroup(groupName, groupSetup, index);
		addLabelForGroup(groupName, groupSetup, index);
	}

	protected void addLabelForGroup(String groupName, GroupSetup groupSetup, int index) {
		ReportTextField field = getGroupTextField(groupName, groupSetup);
		groupSetup.setLabelTextField(field);
		configureLabelForGroup(groupName, groupSetup, index, field);
		getDefinition().addItem(field, groupSetup.getReportGroup().getSectionHeader().getRow(0), 0);
	}

	private ReportTextField getGroupTextField(String groupName, GroupSetup groupSetup) {
		return field(groupSetup.getGroupExpression());
	}

	protected void configureLabelForGroup(String groupName, GroupSetup groupSetup, int index, ReportTextField field) {
		getConfigurator().configureLabelForGroup(groupName, groupSetup, index, field);
	}

	/**
	 * Will configure column widths? (will call getColumnConfig() method)
	 */
	protected boolean isSetupColumnWidths() {
		return true;
	}

	protected void layoutReportFilter() {
		List<String> properties = getFilterProperties();
		if (properties != null) {
			for (String property : properties) {
				try {
					filter(property);
				} catch (Exception e) {
					addLabelAndFieldToFilterGrid(new ReportLabel(property), new ReportLabel("ERROR"));
				}
			}
		}
	}

	protected List<String> getFilterProperties() {
		if (getFilter() == null) {
			return null;
		}
		List<String> properties = new ArrayList<String>();
		PropertyDescriptor[] pds = BeanDescriptorFactory.forBean(getFilter()).getPropertyDescriptors();
		for (PropertyDescriptor pd : pds) {
			if (pd.getOwnerClass() == getFilter().getClass()) {
				properties.add(pd.getName());
			}
		}
		Collections.sort(properties);
		return properties;
	}

	protected abstract void layoutReport();

	protected void afterLayout() {
		setMaxColspanInGroups();
		getConfigurator().afterLayout(this);
	}

	private void setMaxColspanInGroups() {
		ReportDefinition definition = getDefinition();
		List<ReportGroup> groups = definition.getGroups();
		for (ReportGroup reportGroup : groups) {
			ReportItem element = definition.getElementFor(reportGroup.getSectionHeader().getRow(0), definition.getColumn(0));
			if (element.getColspan() == 1) {
				int colspan = getMaxColspan(element, reportGroup.getSectionHeader().getRow(0));
				element.setColspan(colspan);
			}
		}
	}

	protected int getMaxColspan(ReportItem element, ReportSection section) {
		ReportSectionRow elementRow = null;
		for (ReportSectionRow row : section.getRows()) {
			ReportItem element2 = getDefinition().getElementFor(row, element.getColumn());
			if (element2 == element) {
				elementRow = row;
				break;
			}
		}
		return getMaxColspan(element, elementRow);
	}

	protected int getMaxColspan(ReportItem element, ReportSectionRow elementRow) {
		int colspan = 1;
		ReportColumn nextColumn = element.getColumn().getNext();
		while (nextColumn != null) {
			ReportItem nextElement = getDefinition().getElementFor(elementRow, nextColumn);
			if (nextElement == null) {
				colspan++;
				nextColumn = nextColumn.getNext();
			} else {
				break;
			}
		}
		return colspan;
	}

	@Override
	protected FieldConfig createFieldConfig(BaseReportBuilder builder, BeanDescriptor beanDescriptor, PropertyDescriptor propertyDescriptor,
			Object label, String fieldName, String fieldPreffix, String reportExpression,
			String pattern, ReportAlignment alignment) {

		FieldConfig fieldConfig = super.createFieldConfig(builder, beanDescriptor, propertyDescriptor, label,
				fieldName, fieldPreffix, reportExpression,
				pattern, alignment);

		getConfigurator().updateFieldConfig((LayoutReportBuilder) builder, beanDescriptor, propertyDescriptor, fieldConfig);

		return fieldConfig;
	}

	protected void filter(String fieldName) {
		filter(fieldName, null);
	}

	protected void filter(String fieldName, Object label) {

		FieldConfig configForFilterField = getConfigForFilterField(fieldName);

		ReportLabel labelElement = new ReportLabel(label == null ? configForFilterField.label : label);
		labelElement.getStyle().setPaddingRight(4);
		labelElement.getStyle().setAlignment(ReportAlignment.LEFT);
		labelElement.setRenderParameter(FIELD_NAME, fieldName);

		BeanDescriptor beanWrapperImpl = BeanDescriptorFactory.forBean(filter);
		PropertyDescriptor propertyDescriptor = beanWrapperImpl.getPropertyDescriptor(configForFilterField.originalExpression);
		Object propertyValue = propertyDescriptor.getValue();
		propertyValue = checkFilterValue(propertyDescriptor, propertyValue);
		getDefinition().getParameters().put(configForFilterField.reportExpression, propertyValue);

		ReportTextField fieldElement = new ReportTextField("param." + configForFilterField.reportExpression);
		fieldElement.setPattern(configForFilterField.pattern);
		fieldElement.getStyle().setAlignment(ReportAlignment.LEFT);
		fieldElement.getStyle().setPaddingRight(4);
		fieldElement.setRenderParameter(FIELD_NAME, fieldName);

		addLabelAndFieldToFilterGrid(labelElement, fieldElement);

	}

	protected FieldConfig getConfigForFilterField(String fieldName) {
		String fieldPreffix = FILTER_PARAMETER + ".";
		return getConfigForBeanDescriptor(fieldName, fieldPreffix, getFilterClassBeanDescriptorCache());
	}

	protected BeanDescriptor getFilterClassBeanDescriptorCache() {
		if (filterClassBeanDescriptorCache == null) {
			if (filter == null) {
				throw new NullPointerException("Report filter not set");
			}
			filterClassBeanDescriptorCache = BeanDescriptorFactory.forClass(filter.getClass());
		}
		return filterClassBeanDescriptorCache;
	}

	protected Object checkFilterValue(PropertyDescriptor propertyDescriptor, Object propertyValue) {
		return propertyValue;
	}

	protected void addLabelAndFieldToFilterGrid(ReportLabel labelElement, ReportTextElement fieldElement) {
		reportFilterGrid.addItem(labelElement);
		reportFilterGrid.addItem(fieldElement);
	}

	public Object getFilter() {
		return filter;
	}

	public void setFilter(Object filter) {
		this.filter = filter;
	}

	public ReportGrid getReportFilterGrid() {
		return reportFilterGrid;
	}

	/**
	 * Override to configure the report columns
	 * @return
	 */
	public int[] getColumnConfig() {
		return null;
	}

	public int getColumnQuantity() {
		return getColumnConfig().length;
	}

	public ColumnBuilder columnSpan(int colspan) {
		return columnSpan(colspan, null);
	}

	public ColumnBuilder columnSpan(int colspan, ReportAlignment alignment) {
		return column(currentColumn, colspan, alignment);
	}

	public ColumnBuilder column() {
		return column(currentColumn);
	}

	public ColumnBuilder column(int column) {
		return column(column, 1);
	}

	public ColumnBuilder column(int column, int colspan) {
		return column(column, colspan, null);
	}

	public ColumnBuilder column(ReportAlignment alignment) {
		return column(currentColumn, 1, alignment);
	}

	public ColumnBuilder column(int column, int colspan, ReportAlignment alignment) {
		ColumnBuilder columnBuilder = new ColumnBuilder(column, colspan, alignment, this, sumarizedData);
		gotoColumn(column);
		incrementColumn(colspan, null);
		return columnBuilder;
	}

	protected void gotoColumn(int column) {
		currentColumn = column;
		if (getColumnConfig() != null) {
			if (currentColumn > getColumnConfig().length - 1) {
				throw new IllegalArgumentException("invalid column " + column);
			}
		}
	}

	protected void incrementColumn(int colspan, ReportSection section) {
		currentColumn += colspan;
		if (getColumnConfig() != null) {
			if (currentColumn > getColumnConfig().length - 1) {
				currentColumn = 0;
				if (section != null) {
					section.breakLine();
				}
			}
		}
	}

	protected void separator(Object text, int colspan, ReportSection section) {

		section.breakLine();
		ReportLabel line = separator(text, colspan);
		getDefinition().addItem(line, section, 0);
		section.breakLine();
		ReportLabel space = label("").setHeight(line.getHeight());
		getDefinition().addItem(space, section, 0);
		section.breakLine();

		getConfigurator().updateSeparator(this, line, space);

	}

	protected void breakGroupsLines() {
		getDefinition().getSectionSummaryDataHeader().breakLine();
		getDefinition().getSectionSummaryDataDetail().breakLine();
		List<ReportGroup> groups = getDefinition().getGroups();
		for (ReportGroup reportGroup : groups) {
			reportGroup.getSectionHeader().breakLine();
		}
	}

	protected void breakDetailLine() {
		getDefinition().getSectionDetailHeader().breakLine();
		getDefinition().getSectionDetail().breakLine();
		gotoColumn(2);
	}

	public ColumnBuilder fieldSummaryForGroups(String fieldName) {
		return column().fieldSummaryForGroups(fieldName);
	}

	public ColumnBuilder fieldSummaryForGroups(String fieldName, Object label) {
		return fieldSummaryForGroups(fieldName, label, 1);
	}

	public ColumnBuilder fieldSummaryForGroups(String fieldName, Object label, int colspan) {
		return column(currentColumn, colspan, null).fieldSummaryForGroups(fieldName, label, colspan, null);
	}

	public ColumnBuilder fieldSummary(String fieldName) {
		return column().fieldSummary(fieldName);
	}

	public ColumnBuilder fieldSummary(String fieldName, Object label) {
		return column().fieldSummary(fieldName, label);
	}

	public ColumnBuilder fieldSummary(String fieldName, ReportAlignment alignment) {
		return column(alignment).fieldSummary(fieldName);
	}

	public ColumnBuilder fieldSummary(String fieldName, Object label, ReportAlignment alignment) {
		return column(alignment).fieldSummary(fieldName, label);
	}

	public ColumnBuilder fieldSummary(String fieldName, Object label, String pattern) {
		return column().fieldSummary(fieldName, label, pattern);
	}

	public ColumnBuilder fieldDetail(String fieldName, ReportAlignment alignment) {
		return column(alignment).fieldDetail(fieldName, 1);
	}

	public ColumnBuilder fieldDetail(String fieldName, Object label, ReportAlignment alignment) {
		return column(alignment).fieldDetail(fieldName, label, 1);
	}

	public ColumnBuilder fieldDetail(String fieldName) {
		return fieldDetail(fieldName, 1);
	}

	public ColumnBuilder fieldDetail(String fieldName, int colspan) {
		return column(currentColumn, colspan, null).fieldDetail(fieldName, colspan);
	}

	public ColumnBuilder fieldDetail(String fieldName, Object label) {
		return fieldDetail(fieldName, label, (String) null);
	}

	public ColumnBuilder fieldDetail(String fieldName, Object label, String pattern) {
		return fieldDetail(fieldName, label, 1, pattern);
	}

	public ColumnBuilder fieldDetail(String fieldName, Object label, String pattern, ReportAlignment alignment) {
		return fieldDetail(fieldName, label, 1, pattern, alignment);
	}

	public ColumnBuilder fieldDetail(String fieldName, Object label, int colspan) {
		return fieldDetail(fieldName, label, colspan, null);
	}

	public ColumnBuilder fieldDetail(String fieldName, Object label, int colspan, String pattern) {
		return column(currentColumn, colspan, null).fieldDetail(fieldName, label, colspan, pattern);
	}

	public ColumnBuilder fieldDetail(String fieldName, Object label, int colspan, String pattern, ReportAlignment alignment) {
		return column(currentColumn, colspan, alignment).fieldDetail(fieldName, label, colspan, pattern, alignment);
	}

	public int getChartDefaultHeight() {
		return getConfigurator().getChartDefaultHeight();
	}

}
