package org.nextframework.report.definition.elements;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.chart.ChartRow;
import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportSection;

public abstract class TableInformationAdaptor implements TableInformation{

	@Override
	public ReportSection getSectionForHeader(ReportDefinition definition) {
		return definition.getSectionDetailHeader();
	}
	
	@Override
	public int getFirstColumnWidth() {
		return ReportConstants.AUTO_WIDTH;
	}
	
	@Override
	public String formatRowGroup(Object row) {
		if(row instanceof ChartRow) {
			row = ((ChartRow)row).getGroup();
		}
		BeanDescriptor beanDescriptor = BeanDescriptorFactory.forBean(row);
		if(beanDescriptor.getDescriptionPropertyName() != null){
			Object description = beanDescriptor.getDescription();
			if(description == null){
				return "";
			}
		}
		String string = row != null? row.toString() : "";
		if(string.length() > 90){
			string = string.substring(0, 90);
		}
		return string;
	}
	
	@Override
	public String formatHeader(Object header) {
		BeanDescriptor beanDescriptor = BeanDescriptorFactory.forBean(header);
		if(beanDescriptor.getDescriptionPropertyName() != null){
			Object description = beanDescriptor.getDescription();
			if(description == null){
				return "";
			}
		}
		return header != null? header.toString() : "";
	}
	
	@Override
	public void configureGroupField(ReportTextField groupField) {
	}
	
	
	@Override
	public void configureHeaderField(ReportLabel headerField, int columnIndex) {
		
	}

	@Override
	public String getFirstColumnHeader() {
		return null;
	}
	
	@Override
	public Object getValueForRowAndColumn(Object row, Object header, int i) {
		return new TableInformationAdaptor.ValueHolder(getValue(row, header, i));
	}
	
	public abstract Object getValue(Object row, Object header, int columnIndex);

	@Override
	public ReportItem getComponentFor(Object header, int i) {
		return new ReportTextField("value");
	}	
	
	public static class ValueHolder {
		
		Object value;

		public ValueHolder(Object value) {
			this.value = value;
		}

		public Object getValue() {
			return value;
		}

		public void setValue(Object value) {
			this.value = value;
		}
	}
}
