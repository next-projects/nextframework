package org.nextframework.report.definition.builder;

import java.util.List;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.report.definition.ReportGroup;
import org.nextframework.report.definition.ReportSection;
import org.nextframework.report.definition.elements.ReportGrid;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportTextElement;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.definition.elements.style.ReportAlignment;

public abstract class LayoutReportBuilder extends BaseReportBuilder {

	public static final String FILTER_PARAMETER = "REPORT_FILTER";

	protected int currentColumn = 0;
	protected Object filter;
	
	public void setFilter(Object filter) {
		this.filter = filter;
	}
	
	/**
	 * Override to configure the report columns
	 * @return
	 */
	public int[] getColumnConfig() {
		return null;
	}

	protected void breakDetailLine() {
		getDefinition().getSectionDetailHeader().breakLine();
		getDefinition().getSectionDetail().breakLine();
		gotoColumn(2);
	}
	
	private BeanDescriptor filterClassBeanDescriptor;

	BeanDescriptor getFilterClassBeanDescriptor() {
		if(filterClassBeanDescriptor == null){
			filterClassBeanDescriptor = BeanDescriptorFactory.forClass(getFilterClass());
		}
		return filterClassBeanDescriptor;
	}
	
	protected FieldConfig getConfigForFilterField(String fieldName) {
		String fieldPreffix = FILTER_PARAMETER + ".";
		return getConfigForBeanDescriptor(fieldName, fieldPreffix, getFilterClassBeanDescriptor(), filter);
	}
	
	
	@SuppressWarnings("all")
	private Class getFilterClass() {
		if(filter == null){
			throw new NullPointerException("Report filter not set");
		}
		return filter.getClass();
	}

	public Object getFilter() {
		return filter;
	}
	
	public int getColumnQuantity(){
		return getColumnConfig().length;
	}
	
	protected void gotoColumn(int column){
		currentColumn = column;
		if(getColumnConfig() != null){
			if(currentColumn > getColumnConfig().length -1){
				throw new IllegalArgumentException("invalid column "+column);
			}
		}
	}
	
	protected void incrementColumn(int colspan, ReportSection section){
		currentColumn += colspan;
		if(getColumnConfig() != null){
			if(currentColumn > getColumnConfig().length -1){
				currentColumn = 0;
				if(section != null){
					section.breakLine();
				}
			}
		}
	}
	
	public ColumnBuilder columnSpan(int colspan){
		return columnSpan(colspan, null);
	}
	public ColumnBuilder columnSpan(int colspan, ReportAlignment alignment){
		return column(currentColumn, colspan, alignment);
	}
	
	public ColumnBuilder column(){
		return column(currentColumn);
	}
	public ColumnBuilder column(int column){
		return column(column, 1);
	}
	
	public ColumnBuilder column(int column, int colspan){
		return column(column, colspan, null);
	}

	public ColumnBuilder column(ReportAlignment alignment){
		return column(currentColumn, 1, alignment);
	}
	
	public ColumnBuilder column(int column, int colspan, ReportAlignment alignment){
		ColumnBuilder columnBuilder = new ColumnBuilder(column, colspan, alignment, this, sumarizedData);
		gotoColumn(column);
		incrementColumn(colspan, null);
		return columnBuilder;
	}

	
	public ColumnBuilder fieldSummaryForGroups(String fieldName){
		return column().fieldSummaryForGroups(fieldName);
	}
	public ColumnBuilder fieldSummaryForGroups(String fieldName, String label){
		return fieldSummaryForGroups(fieldName, label, 1);
	}
	public ColumnBuilder fieldSummaryForGroups(String fieldName, String label, int colspan){
		return column(currentColumn, colspan, null).fieldSummaryForGroups(fieldName, label, colspan, null);
	}
	public ColumnBuilder fieldSummary(String fieldName){
		return column().fieldSummary(fieldName);
	}
	public ColumnBuilder fieldSummary(String fieldName, String label){
		return column().fieldSummary(fieldName, label);
	}
	public ColumnBuilder fieldSummary(String fieldName, ReportAlignment alignment){
		return column(alignment).fieldSummary(fieldName);
	}
	public ColumnBuilder fieldSummary(String fieldName, String label, ReportAlignment alignment){
		return column(alignment).fieldSummary(fieldName, label);
	}
	public ColumnBuilder fieldSummary(String fieldName, String label, String pattern){
		return column().fieldSummary(fieldName, label, pattern);
	}
	public ColumnBuilder fieldDetail(String fieldName, ReportAlignment alignment){
		return column(alignment).fieldDetail(fieldName, 1);
	}
	public ColumnBuilder fieldDetail(String fieldName, String label, ReportAlignment alignment){
		return column(alignment).fieldDetail(fieldName, label, 1);
	}
	public ColumnBuilder fieldDetail(String fieldName){
		return fieldDetail(fieldName, 1);
	}
	public ColumnBuilder fieldDetail(String fieldName, int colspan){
		return column(currentColumn, colspan, null).fieldDetail(fieldName, colspan);
	}
	public ColumnBuilder fieldDetail(String fieldName, String label){
		return fieldDetail(fieldName, label, (String)null);
	}

	public ColumnBuilder fieldDetail(String fieldName, String label, String pattern){
		return fieldDetail(fieldName, label, 1, pattern);
	}
	
	public ColumnBuilder fieldDetail(String fieldName, String label, String pattern, ReportAlignment alignment){
		return fieldDetail(fieldName, label, 1, pattern, alignment);
	}
	public ColumnBuilder fieldDetail(String fieldName, String label, int colspan){
		return fieldDetail(fieldName, label, colspan, null);
	}

	public ColumnBuilder fieldDetail(String fieldName, String label, int colspan, String pattern){
		return column(currentColumn, colspan, null).fieldDetail(fieldName, label, colspan, pattern);
	}
	
	public ColumnBuilder fieldDetail(String fieldName, String label, int colspan, String pattern, ReportAlignment alignment){
		return column(currentColumn, colspan, alignment).fieldDetail(fieldName, label, colspan, pattern, alignment);
	}
	
	protected ReportGrid reportFilterGrid;

	/**
	 * Will configure column widths? (will call getColumnConfig() method)
	 */
	protected boolean setupColumnWidths = true;
	
	public ReportGrid getReportFilterGrid() {
		return reportFilterGrid;
	}
	
	@Override
	protected void configureDefinition() {
		configureReport();
		super.configureDefinition();
		if(setupColumnWidths){
			getDefinition().setColumnWidths(getColumnConfig());
		}
		getDefinition().getParameters().put(FILTER_PARAMETER, filter);
		configureSections();
		this.reportFilterGrid = getConfigurator().getReportFilterGrid(this);
		configureFilterGrid(reportFilterGrid);
		adjustFilterGrid(reportFilterGrid);
		layoutReport();
		afterLayout();
	}
	
	protected void filter(String fieldName) {
		filter(fieldName, null);
	}
	
	protected void filter(String fieldName, String label) {
		FieldConfig configForFilterField = getConfigForFilterField(fieldName);
		
		BeanDescriptor beanWrapperImpl = BeanDescriptorFactory.forBean(filter);
		Object propertyValue = beanWrapperImpl.getPropertyDescriptor(configForFilterField.reportExpression.substring(FILTER_PARAMETER.length() + 1)).getValue();
		
		if(propertyValue == null && configForFilterField.entity){
			propertyValue = "[TODOS]";
		}
		
		getDefinition().getParameters().put(configForFilterField.reportExpression, propertyValue);
		
		ReportTextField fieldElement = new ReportTextField("param."+configForFilterField.reportExpression);
		fieldElement.setPattern(configForFilterField.pattern);
		fieldElement.getStyle().setAlignment(ReportAlignment.LEFT);
		fieldElement.getStyle().setPaddingRight(4);
		
		ReportLabel labelElement = new ReportLabel((label == null? configForFilterField.label : label) + ":");
		labelElement.getStyle().setPaddingRight(4);
		labelElement.getStyle().setAlignment(ReportAlignment.LEFT);
		
		addLabelAndFieldToFilterGrid(fieldElement, labelElement);
	}

	protected void addLabelAndFieldToFilterGrid(ReportTextElement fieldElement, ReportLabel labelElement) {
		reportFilterGrid.addItem(labelElement);
		reportFilterGrid.addItem(fieldElement);
	}
	
	protected void adjustFilterGrid(ReportGrid reportFilterGrid) {
		
	}

	protected void configureFilterGrid(ReportGrid grid) {
	}

	protected void configureReport() {
		getConfigurator().configureReport(this);
	}

	protected void afterLayout() {
		getConfigurator().afterLayout(this);
	}

	protected void configureSections() {
		getConfigurator().configureSections(this);
	}

	@Override
	protected void onSetupGroup(String groupName, GroupSetup groupSetup, int index) {
		super.onSetupGroup(groupName, groupSetup, index);
		addLabelForGroup(groupName, groupSetup, index);
		configureGroupStyle(groupName, groupSetup, index);
	}

	private void configureGroupStyle(String groupName, GroupSetup groupSetup, int index) {
		getConfigurator().configure(groupName, groupSetup, index);
	}

	protected void addLabelForGroup(String groupName, GroupSetup groupSetup, int index) {
		ReportTextField field = getGroupTextField(groupName, groupSetup);
		groupSetup.setLabelTextField(field);
		configureAndAddGroupLabel(groupName, groupSetup, index, field);
	}

	protected void configureAndAddGroupLabel(String groupName, GroupSetup groupSetup, int index, ReportTextField field) {
		getConfigurator().configureLabelForGroup(groupName, groupSetup, index, field);
		configureGroupLabel(groupName, groupSetup, index, field);
		getDefinition().addItem(field, groupSetup.reportGroup.getSectionHeader().getRow(0), 0);
	}

	/**
	 * Override to configure group label
	 * @param groupName
	 * @param groupSetup
	 * @param index
	 * @param field
	 */
	protected void configureGroupLabel(String groupName, GroupSetup groupSetup, int index, ReportTextField field) {
	}

	private ReportTextField getGroupTextField(String groupName, GroupSetup groupSetup) {
		return field(groupSetup.groupExpression);
	}
	
	protected abstract void layoutReport();

	protected void breakGroupsLines() {
		getDefinition().getSectionSummaryDataHeader().breakLine();
		getDefinition().getSectionSummaryDataDetail().breakLine();
		List<ReportGroup> groups = getDefinition().getGroups();
		for (ReportGroup reportGroup : groups) {
			reportGroup.getSectionHeader().breakLine();
		}
	}
}
