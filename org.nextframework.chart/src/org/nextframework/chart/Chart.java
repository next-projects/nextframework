package org.nextframework.chart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Chart implements Serializable {

	private static final long serialVersionUID = 1L;
	
	ChartStyle style;
	ChartData data;
	String id;
	
	String title;
	
	ChartType comboDefaultChartType = ChartType.COLUMN;
	
	List<ChartType> comboSeriesType = new ArrayList<ChartType>();
	
	Map<Object, Object> properties = new LinkedHashMap<Object, Object>();
	
	public void setComboSerieType(int serieIndex, ChartType chartType){
		while(comboSeriesType.size() < serieIndex){
			comboSeriesType.add(null);
		}
		comboSeriesType.add(chartType);
	}
	
	public ChartType getComboSerieType(int serieIndex){
		if(comboSeriesType.size() > serieIndex){
			return comboSeriesType.get(serieIndex);
		}
		return null;
	}
	
	public Chart(ChartType type, String title, int width, int height) {
		this(type, width, height);
		setTitle(title);
	}
	public Chart(ChartType type, int width, int height) {
		this(type, new ChartData());
		setDimension(width, height);
	}
	
	public Chart(ChartType type) {
		this(type, new ChartData());
	}

	public Chart(ChartType type, ChartData data) {
		this.style = new ChartStyle(type);
		this.data = data;
	}
	
	public Chart(ChartStyle style, ChartData data) {
		this.style = style;
		this.data = data;
	}
	
	public ChartStyle getStyle() {
		return style;
	}
	
	public void setData(ChartData data) {
		this.data = data;
	}
	
	public ChartType getComboDefaultChartType() {
		return comboDefaultChartType;
	}
	public void setComboDefaultChartType(ChartType comboDefaultChartType) {
		this.comboDefaultChartType = comboDefaultChartType;
	}
	public ChartData getData() {
		return data;
	}
	public void setDimension(int width, int height) {
		style.setDimension(width, height);
	}

	public Object getProperty(Object key) {
		return properties.get(key);
	}

	public Object putProperty(Object key, Object value) {
		return properties.put(key, value);
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

	public ChartType getChartType() {
		return style.getChartType();
	}
	
	
}
