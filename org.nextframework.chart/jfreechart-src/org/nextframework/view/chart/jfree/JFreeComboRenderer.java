package org.nextframework.view.chart.jfree;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.LineBorder;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.AbstractCategoryItemRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.nextframework.chart.Chart;
import org.nextframework.chart.ChartData;
import org.nextframework.chart.ChartRow;

public class JFreeComboRenderer extends JFreeBarRenderer {

	@Override
	public JFreeChart render(Chart chartModel) {
		CategoryDataset fullDataset = createCategoryDataset(chartModel);
		
		List<Integer> barSeries = new ArrayList<Integer>();
		List<Integer> lineSeries = new ArrayList<Integer>();
		configureLineAndBarSeries(chartModel, barSeries, lineSeries);
		
		CategoryDataset barDataset = createCategoryDatasetFiltered(chartModel, barSeries);
		CategoryDataset lineDataset = createCategoryDatasetFiltered(chartModel, lineSeries);
		
		JFreeChart chart = ChartFactory.createBarChart(
				chartModel.getTitle(), // chart title
				"",// chart.getData().getGroup(), // domain axis label
				"",// chart.getData().getSeriesTitle(), // range axis label
				null, // data
				PlotOrientation.VERTICAL, // orientation
						false, // include legend
						false, 
						false);
		//chart1.getLegend().setItemFont(new Font("Verdana", 0, 40));
		CategoryPlot plot = (CategoryPlot)chart.getPlot();
		configureCategoryPlot(plot, chartModel);
		
		configureTitle(chart);
		
		NumberAxis plotAxis = new NumberAxis();
		BarRenderer barRenderer = new BarRenderer();
		barRenderer.setBaseOutlinePaint(Color.white);
		barRenderer.setDrawBarOutline(false);
		barRenderer.setBaseItemLabelsVisible(true);
		barRenderer.setMaximumBarWidth(0.08);
		
		
		
		LineAndShapeRenderer lineShapeRenderer = new LineAndShapeRenderer(true, false);
		lineShapeRenderer.setBaseOutlinePaint(Color.white);
		lineShapeRenderer.setBaseItemLabelsVisible(true);
		
		configureCategoryRenderer(barRenderer);
		configureCategoryRenderer(lineShapeRenderer);
		
		map(barDataset, plot, 2, barRenderer, plotAxis);
		map(lineDataset, plot, 1, lineShapeRenderer, plotAxis);
        
        NumberAxis tickAxis = new NumberAxis();
        
        tickAxis.setLabel(plot.getRangeAxis().getLabel());
        tickAxis.setLabelFont(plot.getRangeAxis().getLabelFont());
		map(fullDataset, plot, 0, new LineAndShapeRenderer(false, false), tickAxis);
		
		
		
		BarRenderer barRenderer2 = new BarRenderer();
		barRenderer2.setBaseOutlinePaint(Color.white);
		barRenderer2.setDrawBarOutline(false);
		barRenderer2.setBaseItemLabelsVisible(true);
		barRenderer2.setMaximumBarWidth(0.08);
		
		configureCategoryRenderer(barRenderer2);
		CategoryPlot legendPlot = new CategoryPlot(fullDataset, new CategoryAxis(), plotAxis, barRenderer2);
		configureCategoryPlot(legendPlot, chartModel);
		plotAxis.setAxisLineVisible(false);
		plotAxis.setVisible(false);
		
		reconfigurePlotColors(barSeries, lineSeries, plot);
		
        buildLegend(chartModel, chart, legendPlot);
		
		return chart;
	}

	private void buildLegend(Chart chartModel, JFreeChart chart, CategoryPlot legendPlot) {
		if(chartModel.getStyle().isIncludeLegend()){
			LegendTitle legend = new LegendTitle(legendPlot);
	        legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
	        legend.setFrame(new LineBorder());
	        legend.setBackgroundPaint(Color.white);
	        legend.setPosition(RectangleEdge.BOTTOM);
	        legend.addChangeListener(chart);
        	chart.addLegend(legend);
        	configureSubtitle(chart, chartModel.getStyle());
        }
	}

	private void reconfigurePlotColors(List<Integer> barSeries, List<Integer> lineSeries, CategoryPlot plot) {
		DrawingSupplier drawingSupplier = plot.getDrawingSupplier();
		List<Paint> oldColors = new ArrayList<Paint>();
		for (int i = 0; i < barSeries.size() + lineSeries.size(); i++) {
			oldColors.add(drawingSupplier.getNextPaint());
		}
		
		List<Paint> newColors = new ArrayList<Paint>();
		for (int i = 0; i < barSeries.size(); i++) {
			Integer index = barSeries.get(i);
			newColors.add(oldColors.get(index));
		}
		for (int i = 0; i < lineSeries.size(); i++) {
			Integer index = lineSeries.get(i);
			newColors.add(oldColors.get(index));
		}
		
		plot.setDrawingSupplier(new JFreeDrawingSupplier(newColors.toArray(new Color[newColors.size()])));
	}

	private void map(final CategoryDataset dataset1, final CategoryPlot plot, int index, AbstractCategoryItemRenderer renderer, NumberAxis axis) {
		plot.setDataset(index, dataset1);
        plot.mapDatasetToRangeAxis(index, index);
        plot.setRangeAxis(index, axis);
        plot.setRenderer(index, renderer);
	}
	
	private void configureLineAndBarSeries(Chart chart, List<Integer> barSeries, List<Integer> lineSeries) {
		Comparable<?>[] series = chart.getData().getSeries();
		for (int i = 0; i < series.length; i++) {
			if(chart.getComboSerieType(i) == null){
				barSeries.add(i);
			} else {
				lineSeries.add(i);
			}
		}
	}
	
	protected CategoryDataset createCategoryDatasetFiltered(Chart chart, List<Integer> series) {
		ChartData chartData = chart.getData();
		DefaultCategoryDataset ds = new DefaultCategoryDataset();
		
		List<ChartRow> rows = chartData.getData();
		for (ChartRow chartRow : rows) {
			Number[] values = chartRow.getValues();
			int i = 0;
			for (Number number : values) {
				if(series.contains(i)){
					ds.setValue(number, getSerieString(chart, chartData.getSeries()[i]), getGroupString(chart, chartRow.getGroup()));
				}
				i++;
			}
		}
		return ds;
	}
}
