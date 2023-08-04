package org.nextframework.view.chart.jfree;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.nextframework.chart.Chart;

public class JFreeCurvedLineRenderer extends AbstractJFreeTypeRenderer {

	public JFreeChart render(Chart chart) {

		JFreeChart chart1;

		chart1 = ChartFactory.createXYLineChart(
				getLabelString(chart, chart.getTitle()), // chart title
				"",//chart.getData().getGroupTitle(),
				"",//chart.getData().getSeriesTitle(),
				createXYDataset(chart), // data
				PlotOrientation.VERTICAL, // orientation
				chart.getStyle().isIncludeLegend(), // include legend
				false,
				false);
		configureCurvedLinePlot((XYPlot) chart1.getPlot(), chart);

		configureSubtitle(chart1, chart.getStyle());
		configureTitle(chart1);

		return chart1;
	}

	protected void configureCurvedLinePlot(XYPlot plot, Chart chart) {

		// reset default settings
		configurePlot(plot, chart);
		configureCategoryPlotDetails(plot, chart);

		plot.getDomainAxis().setLowerMargin(0);
		//plot.getDomainAxis().setCategoryMargin(0);
//		LineAndShapeRenderer renderer = new LineAndShapeRenderer();
//		renderer.setBaseStroke(new BasicStroke(2.0f));
//		renderer.setBaseItemLabelsVisible(true);
//		renderer.setBaseShapesVisible(true);
		XYSplineRenderer renderer = new XYSplineRenderer();
		renderer.setPrecision(16);
		plot.setRenderer(renderer);// força a utilizacao das configuracoes do framework

	}

}
