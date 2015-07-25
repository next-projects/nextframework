package org.nextframework.report.generator;

import org.nextframework.report.generator.chart.ChartsElement;
import org.nextframework.report.generator.data.DataElement;
import org.nextframework.report.generator.layout.LayoutElement;

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
	
}
