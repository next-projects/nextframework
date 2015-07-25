package org.nextframework.chart;


class ChartRendererWithListener implements ChartRenderer {
	
	ChartRendererListener chartRendererListener;
	ChartRenderer original;

	public ChartRendererWithListener(ChartRendererListener listener, ChartRenderer renderer) {
		this.chartRendererListener = listener;
		this.original = renderer;
	}

	@Override
	public Object renderChart(Chart chart) {
		chartRendererListener.onChartRender(chart);
		return original.renderChart(chart);
	}

	@Override
	public String getOutputType() {
		return original.getOutputType();
	}

}
