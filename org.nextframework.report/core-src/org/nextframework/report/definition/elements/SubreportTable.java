package org.nextframework.report.definition.elements;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportSection;

public class SubreportTable extends Subreport {

	private TableInformation tableInformation;

	public SubreportTable(TableInformation tableInformation) {
		this.tableInformation = tableInformation;
	}

	@Override
	public ReportDefinition getReport() {
		if (report == null) {
			report = createReport();
		}
		return super.getReport();
	}

	private ReportDefinition createReport() {

		ReportDefinition definition = new ReportDefinition();
		definition.setReportName("subreporttable" + tableInformation.hashCode() + tableInformation.getClass().getSimpleName() + Math.random());

		tableInformation.configureDefinition(definition);
		definition.getColumn(0).setWidth(tableInformation.getFirstColumnWidth());

		ReportTextField groupField = new ReportTextField("GROUP");
		tableInformation.configureGroupField(groupField);
		definition.addItem(groupField, definition.getSectionDetail(), 0);

		Object firstColumnHeaderText = tableInformation.getFirstColumnHeader();
		firstColumnHeaderText = firstColumnHeaderText != null ? firstColumnHeaderText : "";
		ReportLabel firstColumnHeaderField = new ReportLabel(firstColumnHeaderText);
		tableInformation.configureHeaderField(firstColumnHeaderField, 0);
		ReportSection sectionForHeader = tableInformation.getSectionForHeader(definition);
		definition.addItem(firstColumnHeaderField, sectionForHeader, 0);

		int i = 1;
		Map<Object, Set<String>> expressionsForColumns = new HashMap<Object, Set<String>>();
		Collection<?> columnHeaderDataSet = tableInformation.getColumnHeaderDataSet();
		for (Object headerValue : columnHeaderDataSet) {

			ReportItem component = tableInformation.getComponentFor(headerValue, i);

			Set<String> expressions = getExpressionsForAndConfigurePrefix(component, i);
			expressionsForColumns.put(headerValue, expressions);

			ReportLabel headerField = new ReportLabel(tableInformation.formatHeader(headerValue));
			tableInformation.configureHeaderField(headerField, i);
			definition.addItem(headerField, sectionForHeader, i);
			definition.addItem(component, definition.getSectionDetail(), i);

			i++;

		}

		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Collection<?> rowGroupDataSet = tableInformation.getRowGroupDataSet();
		for (Object rowGroup : rowGroupDataSet) {

			Map<String, Object> rowMap = new HashMap<String, Object>();
			rowMap.put("GROUP", tableInformation.formatRowGroup(rowGroup));
			data.add(rowMap);

			i = 1;
			for (Object headerValue : columnHeaderDataSet) {
				Object o = tableInformation.getValueForRowAndColumn(rowGroup, headerValue, i);
				if (o == null) {
					throw new IllegalArgumentException("Value cannot be null. Null value returned from tableInformation.getValueForRowAndColumn(...)");
				}

				for (String exp : expressionsForColumns.get(headerValue)) {
					Object propertyValue = readValue(o, exp);

					rowMap.put("c" + i + "_" + exp, propertyValue);
				}
				i++;
			}

		}

		definition.setData(data);

		return definition;
	}

	private Object readValue(Object o, String property) {
		if (o == null) {
			return null;
		}
		String capitalized = Character.toUpperCase(property.charAt(0)) + property.substring(1);
		try {
			Method method = o.getClass().getMethod("get" + capitalized);
			method.setAccessible(true);
			return method.invoke(o);
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot read property '" + property + "' from object of class " + o.getClass().getName(), e);
		}
	}

	private Set<String> getExpressionsForAndConfigurePrefix(ReportItem component, int i) {
		Set<String> expressions = new HashSet<String>();
		ReportItemIterator iterator = new ReportItemIterator(component);
		while (iterator.hasNext()) {
			ReportItem next = iterator.next();
			if (next instanceof ReportTextField) {
				ReportTextField tf = (ReportTextField) next;
				expressions.add(tf.getExpression());
				tf.setExpression("c" + i + "_" + tf.getExpression());
			}
		}
		return expressions;
	}

}
