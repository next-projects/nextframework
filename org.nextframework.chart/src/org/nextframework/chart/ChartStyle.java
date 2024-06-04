package org.nextframework.chart;

import java.awt.Color;
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

	private String width;
	private String height;

	private TextStyle titleTextStyle;

	private Color[] colors = null;

	private LegendPosition legendPosition = LegendPosition.DEFAULT;
	private TextStyle legendTextStyle;

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

	private String topPadding = "20";
	private String bottomPadding = "20";
	private String leftPadding;
	private String rightPadding;
	private String chartAreaHeight;
	private String chartAreaWidth = "100%";

	private Integer lineWidth;
	private Integer pointSize;

	private boolean continuous;

	private Object groupMinValue;
	private Object groupMaxValue;

	public ChartStyle(ChartType chartType) {
		this.chartType = chartType;
	}

	public ChartType getChartType() {
		return chartType;
	}

	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public void setDimension(String width, String height) {
		this.width = width;
		this.height = height;
	}

	public TextStyle getTitleTextStyle() {
		return titleTextStyle;
	}

	public void setTitleTextStyle(TextStyle titleTextStyle) {
		this.titleTextStyle = titleTextStyle;
	}

	public Color[] getColors() {
		return colors;
	}

	public void setColors(Color[] colors) {
		this.colors = colors;
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

	public void setIncludeLegend(boolean includeLegend) {
		if (includeLegend == false) {
			legendPosition = LegendPosition.NONE;
		} else {
			if (legendPosition == null) {
				legendPosition = LegendPosition.DEFAULT;
			}
		}
		this.includeLegend = includeLegend;
	}

	public TextStyle getLegendTextStyle() {
		return legendTextStyle;
	}

	public void setLegendTextStyle(TextStyle legendTextStyle) {
		this.legendTextStyle = legendTextStyle;
	}

	public boolean is3d() {
		return is3d;
	}

	public void set3d(boolean is3d) {
		this.is3d = is3d;
	}

	public boolean isIncludeLegend() {
		return includeLegend;
	}

	public boolean isGroupAxisVisible() {
		return groupAxisVisible;
	}

	public void setGroupAxisVisible(boolean groupAxisVisible) {
		this.groupAxisVisible = groupAxisVisible;
	}

	public boolean isValueAxisVisible() {
		return valueAxisVisible;
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

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	/**
	 * Configurations for bar chart type (google only)
	 * @return
	 */
	public BarStyle getBar() {
		return bar;
	}

	public List<PieSlice> getSlices() {
		return slices;
	}

	public void setSlices(List<PieSlice> slices) {
		this.slices = new DynamicList<ChartStyle.PieSlice>();
		this.slices.addAll(slices);
	}

	/**
	 * Google chart tools
	 * @param leftPadding
	 */
	public String getTopPadding() {
		return topPadding;
	}

	/**
	 * Google chart tools
	 * @param leftPadding
	 */
	public void setTopPadding(String topPadding) {
		this.topPadding = topPadding;
	}

	public String getBottomPadding() {
		return bottomPadding;
	}

	public void setBottomPadding(String bottomPadding) {
		this.bottomPadding = bottomPadding;
	}

	/**
	 * Google chart tools
	 * @param leftPadding
	 */
	public String getLeftPadding() {
		return leftPadding;
	}

	/**
	* Google chart tools
	* @param leftPadding
	*/
	public void setLeftPadding(String leftPadding) {
		this.leftPadding = leftPadding;
	}

	public String getRightPadding() {
		return rightPadding;
	}

	public void setRightPadding(String rightPadding) {
		this.rightPadding = rightPadding;
	}

	/**
	 * Google chart tools
	 * @param leftPadding
	 */
	public String getChartAreaHeight() {
		return chartAreaHeight;
	}

	/**
	 * Google chart tools
	 * @param leftPadding
	 */
	public void setChartAreaHeight(String chartAreaHeight) {
		this.chartAreaHeight = chartAreaHeight;
	}

	/**
	 * Google chart tools
	 * @param leftPadding
	 */
	public String getChartAreaWidth() {
		return chartAreaWidth;
	}

	/**
	 * Google chart tools
	 * @param leftPadding
	 */
	public void setChartAreaWidth(String chartAreaWidth) {
		this.chartAreaWidth = chartAreaWidth;
	}

	public Integer getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(Integer lineWidth) {
		this.lineWidth = lineWidth;
	}

	public Integer getPointSize() {
		return pointSize;
	}

	public void setPointSize(Integer pointSize) {
		this.pointSize = pointSize;
	}

	public boolean isContinuous() {
		return continuous;
	}

	public void setContinuous(boolean continuous) {
		this.continuous = continuous;
	}

	public Object getGroupMinValue() {
		return groupMinValue;
	}

	public void setGroupMinValue(Object groupMinValue) {
		this.groupMinValue = groupMinValue;
	}

	public Object getGroupMaxValue() {
		return groupMaxValue;
	}

	public void setGroupMaxValue(Object groupMaxValue) {
		this.groupMaxValue = groupMaxValue;
	}

	private static class DynamicList<E> extends ArrayList<E> {

		private static final long serialVersionUID = 1L;

		@Override
		public void add(int index, E element) {
			while (index > size()) {
				add(null);
			}
			super.add(index, element);
		}

		@Override
		public E set(int index, E element) {
			while (index >= size()) {
				add(null);
			}
			return super.set(index, element);
		}

	}

	public static class BarStyle implements Serializable {

		private static final long serialVersionUID = 1L;

		String groupWidth = null;

		public String getGroupWidth() {
			return groupWidth;
		}

		/**
		 * Sets the distance (xx%) between bars.. 100% means no distance
		 * @param groupWidth
		 */
		public void setGroupWidth(String groupWidth) {
			this.groupWidth = groupWidth;
		}

	}

	public static class PieSlice implements Serializable {

		private static final long serialVersionUID = 1L;

		private Color color;
		private TextStyle textStyle;

		public PieSlice() {
		}

		public PieSlice(Color color, TextStyle textStyle) {
			this.color = color;
			this.textStyle = textStyle;
		}

		public PieSlice(Color color) {
			this.color = color;
		}

		public PieSlice(TextStyle textStyle) {
			this.textStyle = textStyle;
		}

		public Color getColor() {
			return color;
		}

		public void setColor(Color color) {
			this.color = color;
		}

		public TextStyle getTextStyle() {
			return textStyle;
		}

		public void setTextStyle(TextStyle textStyle) {
			this.textStyle = textStyle;
		}

	}

	public static class TextStyle implements Serializable {

		private static final long serialVersionUID = 1L;

		private Color color;
		private String fontName;
		private String fontSize;

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

		public void setColor(Color color) {
			this.color = color;
		}

		public String getFontName() {
			return fontName;
		}

		public void setFontName(String fontName) {
			this.fontName = fontName;
		}

		public String getFontSize() {
			return fontSize;
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
