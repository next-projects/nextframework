package org.nextframework.view;

import org.nextframework.chart.Chart;
import org.nextframework.chart.ChartRenderer;
import org.nextframework.chart.ChartRendererFactory;
import org.nextframework.chart.google.ChartRendererGoogleTools;

public class ChartTag extends BaseTag {

	private Chart chart;
	private Object chartRendered;

	@Override
	protected void doComponent() throws Exception {
		if (chart == null) {
			return;
		}
		if (chart.getId() == null) {
			if (id == null) {
				id = "chart_" + generateUniqueId();
			}
			chart.setId(id);
		}
		ChartRenderer renderer = ChartRendererFactory.getRendererForOutput(ChartRendererGoogleTools.TYPE);
		if (renderer == null) {
			throw new RuntimeException("Renderer para o tipo " + ChartRendererGoogleTools.TYPE + " não encontrado.");
		}
		this.chartRendered = renderer.renderChart(chart);

		includeJspTemplate();
	}

	public Chart getChart() {
		return chart;
	}

	public void setChart(Chart chart) {
		this.chart = chart;
	}

	public Object getChartRendered() {
		return chartRendered;
	}

}
