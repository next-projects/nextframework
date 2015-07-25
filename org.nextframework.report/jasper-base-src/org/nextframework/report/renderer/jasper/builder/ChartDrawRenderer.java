package org.nextframework.report.renderer.jasper.builder;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import net.sf.jasperreports.engine.JRAbstractSvgRenderer;
import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.nextframework.chart.Chart;
import org.nextframework.chart.ChartRendererFactory;
import org.nextframework.view.chart.jfree.ChartRendererJFreeChart;

public class ChartDrawRenderer extends JRAbstractSvgRenderer {
	
	static Log logger = LogFactory.getLog(ChartDrawRenderer.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	Chart chart;


	private int width;
	private int height;
	
	public ChartDrawRenderer(Chart chart, int width, int height) {
		super();
		this.chart = chart;
		this.width = width;
		this.height = height;
	}
	
	public Chart getChart() {
		return chart;
	}

	@Override
	public void render(Graphics2D grx, Rectangle2D rectangle) throws JRException {
		if(chart != null){
			JFreeChart jFreeChart = (JFreeChart) ChartRendererFactory.getRendererForOutput(ChartRendererJFreeChart.TYPE).renderChart(chart);
			reduceChartFonts(jFreeChart);
				
			rectangle.setRect(0, 0, width, height);
			if(width == 0 || height == 0){
				logger.warn("Chart has no height or width ");
			}
			
			//grx.clearRect((int)rectangle.getX(), (int)rectangle.getY(), chart.getStyle().getWidth(), chart.getStyle().getHeight());
			//rectangle.setRect(0, 0, chart.getStyle().getWidth(), chart.getStyle().getHeight());
			jFreeChart.draw(grx, rectangle);
		}
	}

	private void reduceChartFonts(JFreeChart jFreeChart) {
		//configure font size (half-size)
		Plot plot = jFreeChart.getPlot();
		reduceLegends(plot);
		if(plot instanceof CategoryPlot){
			CategoryPlot categoryPlot = (CategoryPlot) plot;
			reduceRangeAxis(categoryPlot.getRangeAxis());
			reduceDomainAxis(categoryPlot.getDomainAxis());
		}
		if(plot instanceof PiePlot){
			PiePlot piePlot = (PiePlot) plot;
			piePlot.setLabelFont(reduceFont(piePlot.getLabelFont()));
		}
		
		reduceSubtitles(jFreeChart);
		
		TextTitle title = jFreeChart.getTitle();
		if(title != null){
			title.setFont(reduceFont(title.getFont()));
		}
	}

	@SuppressWarnings("unchecked")
	private void reduceSubtitles(JFreeChart jFreeChart) {
		List<LegendTitle> subtitles = jFreeChart.getSubtitles();
		for (int i = 0; i < subtitles.size(); i++) {
			LegendTitle legendTitle = subtitles.get(i);
			legendTitle.setItemFont(reduceFont(legendTitle.getItemFont()));
		}
	}

	private void reduceRangeAxis(ValueAxis rangeAxis) {
		rangeAxis.setLabelFont(reduceFont(rangeAxis.getLabelFont()));
		rangeAxis.setTickLabelFont(reduceFont(rangeAxis.getTickLabelFont()));
	}

	private void reduceDomainAxis(CategoryAxis domainAxis) {
		domainAxis.setLabelFont(reduceFont(domainAxis.getLabelFont()));
		domainAxis.setTickLabelFont(reduceFont(domainAxis.getTickLabelFont()));
	}

	private void reduceLegends(Plot piePlot) {
		LegendItemCollection legendItems = piePlot.getLegendItems();
		for (int i = 0; i < legendItems.getItemCount(); i++) {
			LegendItem legendItem = legendItems.get(i);
			legendItem.setLabelFont(reduceFont(legendItem.getLabelFont()));
		}
	}

	private Font reduceFont(Font labelFont) {
		if(labelFont == null){
			return null;
		}
		Font newFont = labelFont.deriveFont((float)(labelFont.getSize2D() * 0.6));
		return newFont;
	}

}
