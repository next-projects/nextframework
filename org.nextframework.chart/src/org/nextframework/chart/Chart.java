package org.nextframework.chart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Chart implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String title;
	private ChartData data;
	private ChartStyle style;
	private ChartType comboDefaultChartType = ChartType.COLUMN;
	private List<ChartType> comboSeriesType = new ArrayList<ChartType>();
	private Map<Object, Object> properties = new LinkedHashMap<Object, Object>();

	public Chart(ChartType type, String title, String width, String height) {
		this(type, width, height);
		this.title = title;
	}

	public Chart(ChartType type, String width, String height) {
		this(type, new ChartData());
		this.style.setDimension(width, height);
	}

	public Chart(ChartType type, String title) {
		this(type, new ChartData());
		this.title = title;
	}

	public Chart(ChartType type) {
		this(type, new ChartData());
	}

	public Chart(ChartType type, String title, ChartData data) {
		this.style = new ChartStyle(type);
		this.title = title;
		this.data = data;
	}

	public Chart(ChartType type, ChartData data) {
		this.style = new ChartStyle(type);
		this.data = data;
	}

	public Chart(ChartStyle style, ChartData data) {
		this.style = style;
		this.data = data;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ChartData getData() {
		return data;
	}

	public void setData(ChartData data) {
		this.data = data;
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
