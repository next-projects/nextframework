package org.nextframework.report.definition.elements;

import java.util.Collection;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportSection;

public interface TableInformation {

	Collection<?> getColumnHeaderDataSet();
	
	Collection<?> getRowGroupDataSet();
	
	String formatHeader(Object header);

	ReportSection getSectionForHeader(ReportDefinition definition);

	int getFirstColumnWidth();

	String formatRowGroup(Object object);

	ReportItem getComponentFor(Object header, int columnIndex);

	Object getValueForRowAndColumn(Object row, Object header, int columnIndex);

	void configureGroupField(ReportTextField groupField);

	void configureHeaderField(ReportLabel headerField, int columnIndex);

	String getFirstColumnHeader();
}
