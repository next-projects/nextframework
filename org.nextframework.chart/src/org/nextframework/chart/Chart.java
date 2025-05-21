package org.nextframework.chart;

import java.beans.PropertyEditor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Chart implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private Object title;

	private ChartData data = new ChartData();

	private PropertyEditor labelsFormatter = new ChartDefaultPropertyEditor();
	private PropertyEditor groupFormatter = new ChartDefaultPropertyEditor();
	private PropertyEditor seriesFormatter = new ChartDefaultPropertyEditor();
	private PropertyEditor valuesFormatter = new ChartDefaultPropertyEditor();
	private String groupPattern;
	private String valuePattern;

	private ChartStyle style;
	private ChartType comboDefaultChartType = ChartType.COLUMN;
	private List<ChartType> comboSeriesType = new ArrayList<ChartType>();
	private Map<Object, Object> properties = new LinkedHashMap<Object, Object>();

	public Chart(ChartType type, Object title, String width, String height) {
		this(type, title);
		this.style.setDimension(width, height);
	}

	public Chart(ChartType type, String width, String height) {
		this(type, null);
		this.style.setDimension(width, height);
	}

	public Chart(ChartType type) {
		this(type, null);
	}

	public Chart(ChartType type, Object title) {
		this.style = new ChartStyle(type);
		this.title = title;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getTitle() {
		return title;
	}

	public void setTitle(Object title) {
		this.title = title;
	}

	public ChartData getData() {
		return data;
	}

	public void setData(ChartData data) {
		this.data = data;
	}

	public void setFormatters(PropertyEditor labelsFormatter) {
		setLabelsFormatter(labelsFormatter);
		setGroupFormatter(labelsFormatter);
		setSeriesFormatter(labelsFormatter);
		setValuesFormatter(labelsFormatter);
	}

	public PropertyEditor getLabelsFormatter() {
		return labelsFormatter;
	}

	public void setLabelsFormatter(PropertyEditor labelsFormatter) {
		this.labelsFormatter = labelsFormatter;
	}

	public PropertyEditor getGroupFormatter() {
		return groupFormatter;
	}

	public void setGroupFormatter(PropertyEditor groupFormatter) {
		this.groupFormatter = groupFormatter;
	}

	public PropertyEditor getSeriesFormatter() {
		return seriesFormatter;
	}

	public void setSeriesFormatter(PropertyEditor seriesFormatter) {
		this.seriesFormatter = seriesFormatter;
	}

	public PropertyEditor getValuesFormatter() {
		return valuesFormatter;
	}

	public void setValuesFormatter(PropertyEditor valuesFormatter) {
		this.valuesFormatter = valuesFormatter;
	}

	public String getGroupPattern() {
		return groupPattern;
	}

	public void setGroupPattern(String groupPattern) {
		this.groupPattern = groupPattern;
	}

	public String getValuePattern() {
		return valuePattern;
	}

	public void setValuePattern(String valuePattern) {
		this.valuePattern = valuePattern;
	}

	public ChartStyle getStyle() {
		return style;
	}

	public ChartType getChartType() {
		return style.getChartType();
	}

	public void setDimension(String width, String height) {
		this.style.setDimension(width, height);
	}

	public ChartType getComboDefaultChartType() {
		return comboDefaultChartType;
	}

	public void setComboDefaultChartType(ChartType comboDefaultChartType) {
		this.comboDefaultChartType = comboDefaultChartType;
	}

	public ChartType getComboSerieType(int serieIndex) {
		if (comboSeriesType.size() > serieIndex) {
			return comboSeriesType.get(serieIndex);
		}
		return null;
	}

	public void setComboSerieType(int serieIndex, ChartType chartType) {
		while (comboSeriesType.size() < serieIndex) {
			comboSeriesType.add(null);
		}
		comboSeriesType.add(chartType);
	}

	public Object getProperty(Object key) {
		return properties.get(key);
	}

	public Object putProperty(Object key, Object value) {
		return properties.put(key, value);
	}

}
