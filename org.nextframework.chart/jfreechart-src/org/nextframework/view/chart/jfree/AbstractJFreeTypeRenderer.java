package org.nextframework.view.chart.jfree;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.beans.PropertyEditor;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.util.List;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.AxisSpace;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.block.LineBorder;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.category.AbstractCategoryItemRenderer;
import org.jfree.chart.renderer.category.AreaRenderer;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;
import org.nextframework.chart.Chart;
import org.nextframework.chart.ChartData;
import org.nextframework.chart.ChartRow;
import org.nextframework.chart.ChartStyle;
import org.nextframework.chart.ChartStyle.LegendPosition;

public abstract class AbstractJFreeTypeRenderer implements JFreeTypeRenderer {

	protected DefaultPieDataset createPieDataset(Chart chart) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		ChartData data = chart.getData();
		List<ChartRow> rows = data.getData();
		for (ChartRow chartRow : rows) {
			Number value = chartRow.getValues()[0];
			dataset.setValue(getGroupString(chart, chartRow.getGroup()), value);
		}
		return dataset;
	}
	
	
	protected XYDataset createXYDataset(Chart chart) {
		@SuppressWarnings("serial")
		class XYCategoryDataset extends AbstractXYDataset{
		   private CategoryDataset dataset;
		   public XYCategoryDataset(CategoryDataset dataset){
		      this.dataset = dataset;
		   }
		   public int getSeriesCount(){
		      return dataset.getRowCount();
		   }
		   public Comparable<?> getSeriesKey(int series){
		      return dataset.getRowKey(series);
		   }
		   public int getItemCount(int series){
		      return dataset.getColumnCount();
		   }
		   public Number getX(int series, int item){
		      return new Double(item);
		   }
		   public Number getY(int series, int item){
		      return dataset.getValue(series, item);
		   }
		}
	
		return new XYCategoryDataset(createCategoryDataset(chart));
	}
	
	protected CategoryDataset createCategoryDataset(Chart chart) {
		ChartData chartData = chart.getData();
		DefaultCategoryDataset ds = new DefaultCategoryDataset();
		
		List<ChartRow> rows = chartData.getData();
		for (ChartRow chartRow : rows) {
			Number[] values = chartRow.getValues();
			int i = 0;
			for (Number number : values) {
				Comparable<?> serie;
				serie = chartData.getSeries()[i];
				ds.setValue(number, getSerieString(chart, serie), getGroupString(chart, chartRow.getGroup()));
				i++;
			}
		}
		return ds;
	}
	
	protected String getSerieString(Chart chart, Comparable<?> serie) {
		PropertyEditor formatter = chart.getStyle().getSeriesFormatter();
		formatter.setValue(serie);
		return formatter.getAsText();
	}
	
	protected Comparable<String> getGroupString(Chart chart, Object group) {
		PropertyEditor formatter = chart.getStyle().getGroupFormatter();
		formatter.setValue(group);
		return formatter.getAsText();
	}

	protected void configureTitle(JFreeChart chart) {
		if(chart.getTitle() != null) {
			chart.getTitle().setFont(new Font("Arial", Font.BOLD, 12));
			chart.getTitle().setPaint(new Color(92, 92, 92));
		}
	}
	
	protected void configureSubtitle(JFreeChart chart, ChartStyle style) {
		if(chart.getLegend() != null){
        	LegendTitle title = (LegendTitle) chart.getLegend();
        	title.setBackgroundPaint(style.getBackgroundColor());
        	if(style.getLegendPosition() == LegendPosition.RIGHT){
        		title.setPosition(RectangleEdge.RIGHT);
        	}
        	if(style.getLegendPosition() == LegendPosition.TOP){
        		title.setPosition(RectangleEdge.TOP);
        	}
        	if(style.getLegendPosition() == LegendPosition.BOTTOM){
        		title.setPosition(RectangleEdge.BOTTOM);
        	}
        	title.setFrame(new LineBorder(style.getBackgroundColor(), new BasicStroke(1.0f), new RectangleInsets(1.0, 1.0, 1.0, 1.0)));
        }
	}

	protected void configurePlot(Plot plot, Chart chart) {
		plot.setBackgroundPaint(chart.getStyle().getBackgroundColor());
		plot.setOutlinePaint(chart.getStyle().getBackgroundColor());
		if(chart.getStyle().getColors() != null){
			plot.setDrawingSupplier(new JFreeDrawingSupplier(chart.getStyle().getColors()));
		} else {
			plot.setDrawingSupplier(new JFreeDrawingSupplier());
		}
	}
	
	protected void configureCategoryPlotDetails(Plot plot, Chart chart) {
		plot.setInsets(new RectangleInsets(0, 12, 0, 0));
		
		if(plot instanceof CategoryPlot){
			CategoryPlot categoryPlot = (CategoryPlot) plot;
			
			categoryPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
			
			//espaçamento na esquerda para não atrapalhar a legenda do gráfico 
			categoryPlot.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
			
			categoryPlot.getDomainAxis().setVisible(chart.getStyle().isGroupAxisVisible());
			categoryPlot.getRangeAxis().setVisible(chart.getStyle().isValueAxisVisible());
			
			categoryPlot.getDomainAxis().setLabel(chart.getData().getGroupTitle());
			categoryPlot.getRangeAxis().setLabel(chart.getData().getSeriesTitle());
			if(chart.getStyle().getValueAxisTickUnit() != null && categoryPlot.getRangeAxis() instanceof NumberAxis) {
				NumberAxis numberAxis = (NumberAxis) categoryPlot.getRangeAxis();
				numberAxis.setTickUnit(new NumberTickUnit(chart.getStyle().getValueAxisTickUnit()));
			}
		}
		//((NumberAxis)plot.getRangeAxis()).setTickUnit(new NumberTickUnit(2.0));
//		plot.getRenderer().setBaseItemLabelGenerator(generator);
		
	}

	protected void configureLinePlot(CategoryPlot plot, Chart chart) {
		// reset default settings
		configurePlot(plot, chart);
		configureCategoryPlotDetails(plot, chart);
		
		plot.getDomainAxis().setUpperMargin(0.01);
		plot.getDomainAxis().setLowerMargin(0.01);
		plot.getDomainAxis().setCategoryMargin(0.02);
		
		plot.getDomainAxis().setLabelInsets(new RectangleInsets(0, 0, 5, 5));
		
		configureRotation(plot, chart);
		
		LineAndShapeRenderer renderer = new LineAndShapeRenderer();
		renderer.setBaseStroke(new BasicStroke(4.0f));
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseShapesVisible(true);
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseShapesVisible(false);
		configureCategoryRenderer(renderer);
		plot.setRenderer(renderer);// força a utilizacao das configuracoes do framework
	}
	
	protected void configureCategoryPlot(CategoryPlot plot, Chart chart) {
		// reset default settings
		configurePlot(plot, chart);
		configureCategoryPlotDetails(plot, chart);
		
		plot.getRangeAxis().setUpperMargin(0.1);
		plot.getDomainAxis().setLowerMargin(0.01);
		plot.getDomainAxis().setCategoryMargin(0.2);
//		AxisSpace space = new AxisSpace();
//		space.setBottom(50.0);
//		space.setTop(10.0);
//		plot.setFixedDomainAxisSpace(space);
//		plot.getRangeAxis().setLowerMargin(0.5);
		plot.getDomainAxis().setLabelInsets(new RectangleInsets(0, 0, 5, 5));
//		plot.getRangeAxis().setTickLabelFont(new Font("Verdana", 0, 40));
//		plot.getDomainAxis().setTickLabelFont(new Font("Verdana", 0, 40));
//		plot.getDomainAxis().setTickLabelFont(new Font("Verdana", 0, 40));
		//auto rotate
		configureRotation(plot, chart);
		
		BarRenderer renderer = new BarRenderer();
		renderer.setBaseOutlinePaint(Color.white);
		renderer.setDrawBarOutline(false);
		renderer.setBaseItemLabelsVisible(true);
		renderer.setMaximumBarWidth(0.08);
		
		configureCategoryRenderer(renderer);
		plot.setRenderer(renderer);// força a utilizacao das configuracoes do framework
		
	}


	public void configureRotation(CategoryPlot plot, Chart chart) {
		int groupNumber = chart.getData().getData().size();
		if(groupNumber > 30){
			plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_90);
		} else if(groupNumber > 10){
			plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
		}
	}


	public void configureCategoryRenderer(AbstractCategoryItemRenderer renderer) {
		DecimalFormat decimalformat1 = getBaseItemDecimalFormat();
		renderer.setBaseItemLabelFont(new Font("Arial", Font.PLAIN, 7));
		renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", decimalformat1));
		renderer.setBaseItemLabelPaint(new Color(80,80,80, 200));
	}


	public DecimalFormat getBaseItemDecimalFormat() {
		DecimalFormat decimalformat1 = new DecimalFormat("0.#"){
			private static final long serialVersionUID = 1L;
			
			@Override
			public StringBuffer format(double number, StringBuffer result, FieldPosition fieldPosition) {
				if(number == 0){
					return new StringBuffer();
				}
				return super.format(number, result, fieldPosition);
			}
			@Override
			public StringBuffer format(long number, StringBuffer result, FieldPosition fieldPosition) {
				if(number == 0){
					return new StringBuffer();
				}
				return super.format(number, result, fieldPosition);
			}
		};
		return decimalformat1;
	}
	
	protected void configureAreaPlot(CategoryPlot plot, Chart chart) {
		// reset default settings
		configurePlot(plot, chart);
		configureCategoryPlotDetails(plot, chart);
		
		plot.setForegroundAlpha(0.5f);
		
		AreaRenderer renderer = new AreaRenderer();
		renderer.setBaseOutlinePaint(Color.white);
		renderer.setBaseItemLabelsVisible(true);
		plot.setRenderer(renderer);// força a utilizacao das configuracoes do framework
	}

}
