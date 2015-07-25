package org.nextframework.chart.google;

import org.nextframework.chart.Chart;
import org.nextframework.chart.ChartRenderer;

public class ChartRendererGoogleTools implements ChartRenderer {
	
	public static String TYPE = "GOOGLE-TOOLS";

	public String getOutputType() {
		return TYPE;
	}

	public Object renderChart(Chart chart) {
		GoogleToolsChartBuilder builder = new GoogleToolsChartBuilder(chart);
		return builder.toString();
	}

}
