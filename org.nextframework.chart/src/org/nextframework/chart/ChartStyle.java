package org.nextframework.chart;

import java.awt.Color;
import java.beans.PropertyEditor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Configura os estilos do gráfico.
 * Nota: Nem todos os estilos são aplicados a todos os tipos de gráfico ou todas as renderizações.
 * @author rogel
 *
 */
public class ChartStyle implements Serializable {

	private static final long serialVersionUID = 1L;

	private ChartType chartType;
	
	private Integer width = 900;
	private Integer height = 400;
	
	private Color[] colors = null;
	
	private LegendPosition legendPosition = LegendPosition.DEFAULT;
	
	private boolean is3d;
	
	private boolean includeLegend = true;
	
	private boolean groupAxisVisible = true;
	private boolean valueAxisVisible = true;
	
	private Double valueAxisTickUnit = null;
	
	private TextStyle textStyle = new TextStyle(null, null, null);
	
	private TextStyle pieSliceTextStyle;
	
	private Color backgroundColor = Color.white;
	
	private BarStyle bar = new BarStyle();
	
	private List<PieSlice> slices = new DynamicList<ChartStyle.PieSlice>();
	
	PropertyEditor groupFormatter = new ChartDefaultPropertyEditor();
	PropertyEditor seriesFormatter = new ChartDefaultPropertyEditor();
	PropertyEditor valuesFormatter;
	
	Integer topPadding;
	Integer leftPadding;
	
	Integer chartAreaHeight;
	Integer chartAreaWidth;
	
	Integer lineWidth;
	Integer pointSize;
	
	boolean continuous;

	private String groupPattern;
	private String valuePattern;
	
	Object groupMinValue;
	Object groupMaxValue;
	
	/**
	 * Returns the slices of a PIE chart. Only works for google chart.
	 * This list grows automatically, so it is possible to set an element in any position
	 * @return
	 */
	public List<PieSlice> getSlices() {
		return slices;
	}
	
	public void setSlices(List<PieSlice> slices) {
		this.slices = new DynamicList<ChartStyle.PieSlice>();
		this.slices.addAll(slices);
	}
	
	/**
	 * Configurations for bar chart type (google only)
	 * @return
	 */
	public BarStyle getBar() {
		return bar;
	}
	
	public ChartStyle(ChartType chartType){
		this.chartType = chartType;
	}
	
	/**
	 * (google only)
	 * @return
	 */	
	public TextStyle getTextStyle() {
		return textStyle;
	}
	
	/**
	 * (google only)
	 * @return
	 */	
	public void setTextStyle(TextStyle textStyle) {
		this.textStyle = textStyle;
	}
	
	public Object getGroupMinValue() {
		return groupMinValue;
	}

	public Object getGroupMaxValue() {
		return groupMaxValue;
	}

	public void setGroupMinValue(Object groupMinValue) {
		this.groupMinValue = groupMinValue;
	}

	public void setGroupMaxValue(Object groupMaxValue) {
		this.groupMaxValue = groupMaxValue;
	}

	public Integer getLineWidth() {
		return lineWidth;
	}
	public Integer getPointSize() {
		return pointSize;
	}

	public boolean isContinuous() {
		return continuous;
	}

	public void setContinuous(boolean continuous) {
		this.continuous = continuous;
	}

	public void setLineWidth(Integer lineWidth) {
		this.lineWidth = lineWidth;
	}


	public void setPointSize(Integer pointSize) {
		this.pointSize = pointSize;
	}


	/**
	 * Google chart tools
	 * @param leftPadding
	 */
	public Integer getLeftPadding() {
		return leftPadding;
	}
	
	/**
	 * Google chart tools
	 * @param leftPadding
	 */
	public Integer getTopPadding() {
		return topPadding;
	}
	
	/**
	 * Google chart tools
	 * @param leftPadding
	 */
	public void setLeftPadding(Integer leftPadding) {
		this.leftPadding = leftPadding;
	}
	
	/**
	 * Google chart tools
	 * @param leftPadding
	 */
	public void setTopPadding(Integer topPadding) {
		this.topPadding = topPadding;
	}
	
	
	/**
	 * Google chart tools
	 * @param leftPadding
	 */
	public Integer getChartAreaHeight() {
		return chartAreaHeight;
	}
	
	
	/**
	 * Google chart tools
	 * @param leftPadding
	 */
	public Integer getChartAreaWidth() {
		return chartAreaWidth;
	}
	
	
	/**
	 * Google chart tools
	 * @param leftPadding
	 */
	public void setChartAreaHeight(Integer chartAreaHeight) {
		this.chartAreaHeight = chartAreaHeight;
	}
	
	
	/**
	 * Google chart tools
	 * @param leftPadding
	 */
	public void setChartAreaWidth(Integer chartAreaWidth) {
		this.chartAreaWidth = chartAreaWidth;
	}
	
	public void set3d(boolean is3d) {
		this.is3d = is3d;
	}
	
	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}
	
	public boolean isGroupAxisVisible() {
		return groupAxisVisible;
	}


	public Color getBackgroundColor() {
		return backgroundColor;
	}


	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}


	public boolean isValueAxisVisible() {
		return valueAxisVisible;
	}


	public void setGroupAxisVisible(boolean groupAxisVisible) {
		this.groupAxisVisible = groupAxisVisible;
	}


	public void setValueAxisVisible(boolean valueAxisVisible) {
		this.valueAxisVisible = valueAxisVisible;
	}


	/**
	 * Apenas utilizado no JFreeChart quando o valueAxis é numérico
	 * @return
	 */
	public Double getValueAxisTickUnit() {
		return valueAxisTickUnit;
	}
	/**
	 * Apenas utilizado no JFreeChart quando o valueAxis é numérico
	 * @return
	 */
	public void setValueAxisTickUnit(Double valueAxisTickUnit) {
		this.valueAxisTickUnit = valueAxisTickUnit;
	}

	/**
	 * Apenas utilizado no PIE chart do google tools
	 * @return
	 */
	public TextStyle getPieSliceTextStyle() {
		return pieSliceTextStyle;
	}

	/**
	 * Apenas utilizado no PIE chart do google tools
	 * @param pieSliceTextStyle
	 */
	public void setPieSliceTextStyle(TextStyle pieSliceTextStyle) {
		this.pieSliceTextStyle = pieSliceTextStyle;
	}

	/**
	 * Apenas utilizado nos gráficos google tools
	 */
	public LegendPosition getLegendPosition() {
		return legendPosition;
	}

	/**
	 * Apenas utilizado nos gráficos google tools
	 * @param legendPosition
	 */
	public void setLegendPosition(LegendPosition legendPosition) {
		this.legendPosition = legendPosition;
		setIncludeLegend(this.legendPosition != LegendPosition.NONE);
	}

	public boolean isIncludeLegend() {
		return includeLegend;
	}

	public void setIncludeLegend(boolean includeLegend) {
		if(includeLegend == false){
			legendPosition = LegendPosition.NONE;
		} else {
			if (legendPosition == null) {
				legendPosition = LegendPosition.DEFAULT;
			}
		}
		this.includeLegend = includeLegend;
	}

	public Color[] getColors() {
		return colors;
	}

	public void setColors(Color[] colors) {
		this.colors = colors;
	}

	public boolean is3d() {
		return is3d;
	}

	public ChartType getChartType() {
		return chartType;
	}

	public Integer getWidth() {
		return width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public void setDimension(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void setGroupFormatterPattern(String pattern) {
		this.groupPattern = pattern;
		this.groupFormatter = new ChartDefaultPropertyEditor(pattern);
	}
	
	public void setValueFormatterPattern(String valuePattern) {
		this.valuePattern = valuePattern;
	}
	
	public String getGroupPattern() {
		return groupPattern;
	}
	
	public String getValuePattern() {
		return valuePattern;
	}
	
	public void setSeriesFormatterPattern(String pattern) {
		this.seriesFormatter = new ChartDefaultPropertyEditor(pattern);
	}
	public PropertyEditor getGroupFormatter() {
		return groupFormatter;
	}
	public PropertyEditor getValuesFormatter() {
		return valuesFormatter;
	}
	public void setGroupFormatter(PropertyEditor groupFormatter) {
		this.groupFormatter = groupFormatter;
	}
	public void setValuesFormatter(PropertyEditor valuesFormatter) {
		this.valuePattern = null;
		this.valuesFormatter = valuesFormatter;
	}
	
	public PropertyEditor getSeriesFormatter() {
		return seriesFormatter;
	}
	public void setSeriesFormatter(PropertyEditor seriesFormatter) {
		this.seriesFormatter = seriesFormatter;
	}
	
	public PropertyEditor getValuesFormatterForSerie(int serieIndex) {
		return valuesFormatter;
	}
	
	private static class DynamicList<E> extends ArrayList<E> {
		@Override
		public void add(int index, E element) {
			while(index > size()){
				add(null);
			}
			super.add(index, element);
		}
		@Override
		public E set(int index, E element) {
			while(index >= size()){
				add(null);
			}
			return super.set(index, element);
		}
	}
	
	public static class BarStyle implements Serializable {
		private static final long serialVersionUID = 1L;
		
		String groupWidth = null;

		/**
		 * Sets the distance (xx%) between bars.. 100% means no distance
		 * @param groupWidth
		 */
		public void setGroupWidth(String groupWidth) {
			this.groupWidth = groupWidth;
		}
		
		public String getGroupWidth() {
			return groupWidth;
		}
	}
	
	public static class PieSlice implements Serializable {

		private static final long serialVersionUID = 1L;
		
		Color color;
		TextStyle textStyle;
		
		public PieSlice(){
		}
		public PieSlice(Color color, TextStyle textStyle){
			this.color = color;
			this.textStyle = textStyle;
		}
		public PieSlice(Color color){
			this.color = color;
		}
		public PieSlice(TextStyle textStyle){
			this.textStyle = textStyle;
		}
		public Color getColor() {
			return color;
		}
		public TextStyle getTextStyle() {
			return textStyle;
		}
		public void setColor(Color color) {
			this.color = color;
		}
		public void setTextStyle(TextStyle textStyle) {
			this.textStyle = textStyle;
		}
	}

	public static class TextStyle implements Serializable{
		
		private static final long serialVersionUID = 1L;
		
		Color color;
		String fontName;
		String fontSize;
		public TextStyle(Color color) {
			this.color = color;
		}
		public TextStyle(Color color, String fontName, String fontSize) {
			this.color = color;
			this.fontName = fontName;
			this.fontSize = fontSize;
		}
		public Color getColor() {
			return color;
		}
		public String getFontName() {
			return fontName;
		}
		public String getFontSize() {
			return fontSize;
		}
		public void setColor(Color color) {
			this.color = color;
		}
		public void setFontName(String fontName) {
			this.fontName = fontName;
		}
		public void setFontSize(String fontSize) {
			this.fontSize = fontSize;
		}
	}
	
	public static enum LegendPosition {
		DEFAULT,
		NONE,
		IN,
		RIGHT,
		BOTTOM,
		TOP
	}

	
}
