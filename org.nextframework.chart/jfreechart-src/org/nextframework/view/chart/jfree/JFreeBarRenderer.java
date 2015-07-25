package org.nextframework.view.chart.jfree;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.nextframework.chart.Chart;
import org.nextframework.chart.ChartType;

public class JFreeBarRenderer extends AbstractJFreeTypeRenderer {

	public JFreeChart render(Chart chart) {
		JFreeChart chart1;
		if(chart.getChartType() == ChartType.LINE || chart.getChartType() == ChartType.SCATTER){
			chart1 = ChartFactory.createLineChart(
					chart.getTitle(), // chart title
					"",//chart.getData().getGroupTitle(),
					"",//chart.getData().getSeriesTitle(),
					createCategoryDataset(chart), // data
					PlotOrientation.VERTICAL, // orientation
							chart.getStyle().isIncludeLegend(), // include legend
							false, 
							false);
			configureLinePlot((CategoryPlot)chart1.getPlot(), chart);
		} else if(chart.getChartType() == ChartType.AREA){
			chart1 = ChartFactory.createAreaChart(
					chart.getTitle(), // chart title
					"",// chart.getData().getGroup(), // domain axis label
					"",// chart.getData().getSeriesTitle(), // range axis label
					createCategoryDataset(chart), // data
					PlotOrientation.VERTICAL, // orientation
							chart.getStyle().isIncludeLegend(), // include legend
							false, 
							false);
			configureAreaPlot((CategoryPlot)chart1.getPlot(), chart);
		} else {
			chart1 = ChartFactory.createBarChart(
					chart.getTitle(), // chart title
					"",// chart.getData().getGroup(), // domain axis label
					"",// chart.getData().getSeriesTitle(), // range axis label
					createCategoryDataset(chart), // data
					chart.getChartType() == ChartType.BAR ? PlotOrientation.HORIZONTAL : PlotOrientation.VERTICAL, // orientation
							chart.getStyle().isIncludeLegend(), // include legend
							false, 
							false);
			//chart1.getLegend().setItemFont(new Font("Verdana", 0, 40));
			configureCategoryPlot((CategoryPlot)chart1.getPlot(), chart);
		}
		
		configureSubtitle(chart1, chart.getStyle());
		configureTitle(chart1);
		
		return chart1;
	}

}
