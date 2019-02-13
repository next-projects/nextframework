package org.nextframework.report.generator;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.nextframework.report.generator.chart.ChartElement;
import org.nextframework.report.generator.chart.ChartsElement;
import org.nextframework.report.generator.data.CalculatedFieldElement;
import org.nextframework.report.generator.data.DataElement;
import org.nextframework.report.generator.data.GroupElement;
import org.nextframework.report.generator.layout.FieldDetailElement;
import org.nextframework.report.generator.layout.LayoutElement;
import org.nextframework.report.generator.layout.LayoutItem;
import org.springframework.util.StringUtils;

public class ReportElement {

	String name;
	
	String reportTitle;
	
	DataElement data;
	LayoutElement layout;
	
	ChartsElement charts;
	
	public ReportElement() {
	}
	
	public ReportElement(String name) {
		this.name = name;
		this.reportTitle = name;
	}
	
	public ChartsElement getCharts() {
		return charts;
	}
	
	public void setCharts(ChartsElement charts) {
		this.charts = charts;
	}
	
	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public String getName() {
		return name;
	}

	public DataElement getData() {
		return data;
	}

	public LayoutElement getLayout() {
		return layout;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setData(DataElement data) {
		this.data = data;
	}

	public void setLayout(LayoutElement layout) {
		this.layout = layout;
	}

	@Override
	public String toString() {
		return String.format("ReportElement [name=%s, reportTitle=%s, %n\tdata=%s, %n\tlayout=%s, %n\tcharts=%s]", name, reportTitle, data, layout, charts);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((charts == null) ? 0 : charts.hashCode());
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		result = prime * result + ((layout == null) ? 0 : layout.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((reportTitle == null) ? 0 : reportTitle.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportElement other = (ReportElement) obj;
		if (charts == null) {
			if (other.charts != null)
				return false;
		} else if (!charts.equals(other.charts))
			return false;
		if (data == null) {
			if (other.data != null)
				return false;
		} else if (!data.equals(other.data))
			return false;
		if (layout == null) {
			if (other.layout != null)
				return false;
		} else if (!layout.equals(other.layout))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (reportTitle == null) {
			if (other.reportTitle != null)
				return false;
		} else if (!reportTitle.equals(other.reportTitle))
			return false;
		return true;
	}
	
	public List<String> getProperties() {
		Set<String> properties = new LinkedHashSet<String>();
		for (LayoutItem layoutItem : getLayout().getItems()) {
			if(layoutItem instanceof FieldDetailElement){
				properties.add(((FieldDetailElement) layoutItem).getName());
			}
		}
		List<GroupElement> groups = getData().getGroups();
		for (GroupElement groupElement : groups) {
			properties.add(groupElement.getName());
		}
		List<ChartElement> charts = getCharts().getItems();
		for (ChartElement chartElement : charts) {
			String groupProperty = chartElement.getGroupProperty();
			String seriesProperty = chartElement.getSeriesProperty();
			String valueProperty = chartElement.getValueProperty();
			if(groupProperty != null){
				properties.add(groupProperty);
			}
			if(StringUtils.hasText(seriesProperty)){
				properties.add(seriesProperty);
			}
			if(valueProperty != null && ! valueProperty.equals("count")){
				properties.add(valueProperty);
			}
		}
		List<CalculatedFieldElement> calculatedFields = getData().getCalculatedFields();
		for (CalculatedFieldElement calculatedField : calculatedFields) {
			String[] parts = calculatedField.getExpression().split(" ");
			for (String part : parts) {
				if (Character.isLetter(part.charAt(0)) && !getData().isCalculated(part)) {
					properties.add(part);
				}
			}
		}
		List<String> propertiesLst = new ArrayList<String>(properties);
		return propertiesLst;
	}
	
}
