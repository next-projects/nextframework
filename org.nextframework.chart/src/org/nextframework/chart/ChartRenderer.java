package org.nextframework.chart;

public interface ChartRenderer {

	public Object renderChart(Chart chart);
	
	public String getOutputType();
}
