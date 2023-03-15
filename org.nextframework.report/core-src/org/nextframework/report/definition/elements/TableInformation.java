package org.nextframework.report.definition.elements;

import java.util.Collection;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportSection;

public interface TableInformation {

	void configureDefinition(ReportDefinition definition);

	int getFirstColumnWidth();

	void configureGroupField(ReportTextField groupField);

	String getFirstColumnHeader();

	void configureHeaderField(ReportLabel headerField, int columnIndex);

	ReportSection getSectionForHeader(ReportDefinition definition);

	Collection<?> getColumnHeaderDataSet();

	ReportItem getComponentFor(Object header, int columnIndex);

	String formatHeader(Object header);

	Collection<?> getRowGroupDataSet();

	String formatRowGroup(Object object);

	Object getValueForRowAndColumn(Object row, Object header, int columnIndex);

}