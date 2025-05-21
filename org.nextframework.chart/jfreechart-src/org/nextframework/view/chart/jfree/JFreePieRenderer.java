package org.nextframework.view.chart.jfree;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.DefaultPieDataset;
import org.nextframework.chart.Chart;

public class JFreePieRenderer extends AbstractJFreeTypeRenderer {

	public JFreeChart render(Chart chart) {

		DefaultPieDataset dataset = createPieDataset(chart);

		JFreeChart pie;
		if (chart.getStyle().is3d()) {
			pie = ChartFactory.createPieChart3D(getLabelString(chart, chart.getTitle()), dataset, chart.getStyle().isIncludeLegend(), false, false);
		} else {
			pie = ChartFactory.createPieChart(getLabelString(chart, chart.getTitle()), dataset, chart.getStyle().isIncludeLegend(), false, false);
		}
		configureSubtitle(pie, chart.getStyle());
		configureTitle(pie);
		configurePlot(pie.getPlot(), chart);

		return pie;
	}

	protected void configurePlot(Plot plot, Chart chart) {

		//reset default settings
		super.configurePlot(plot, chart);
		//((PiePlot)plot).setExplodePercent("Next", 0.3);
		((PiePlot) plot).setShadowPaint(Color.WHITE);
		((PiePlot) plot).setLabelGenerator(null);

		if (plot instanceof PiePlot3D) {
			PiePlot3D plot3d = (PiePlot3D) plot;
			plot3d.setForegroundAlpha(0.6f);
			plot3d.setDepthFactor(0.1);
			plot3d.setCircular(false);
			plot3d.setLabelGenerator(null);
			plot3d.setBackgroundPaint(null);
			plot3d.setDarkerSides(true);
			plot3d.setOutlinePaint(null);
		} else {
			((PiePlot) plot).setBaseSectionOutlinePaint(Color.WHITE);
		}

	}

}
