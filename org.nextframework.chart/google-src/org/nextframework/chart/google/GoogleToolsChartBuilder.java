package org.nextframework.chart.google;

import java.awt.Color;
import java.beans.PropertyEditor;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nextframework.chart.ChartData;
import org.nextframework.chart.ChartRow;
import org.nextframework.chart.ChartStyle;
import org.nextframework.chart.ChartStyle.LegendPosition;
import org.nextframework.chart.ChartStyle.PieSlice;
import org.nextframework.chart.ChartStyle.TextStyle;
import org.nextframework.chart.ChartType;
import org.nextframework.chart.ChartUtils;
import org.nextframework.exception.NextException;
import org.nextframework.view.js.JavascriptReferenciable;
import org.nextframework.view.js.api.GoogleVisualization;
import org.nextframework.view.js.api.Html;
import org.nextframework.view.js.builder.JavascriptBuilder;

public class GoogleToolsChartBuilder extends JavascriptBuilder implements GoogleVisualization, Html {

	static java.util.Map<ChartType, Class<? extends Chart>> chartMapping;

	static {
		chartMapping = new HashMap<ChartType, Class<? extends Chart>>();
		chartMapping.put(ChartType.AREA, AreaChart.class);
		chartMapping.put(ChartType.SCATTER, ScatterChart.class);
		chartMapping.put(ChartType.LINE, LineChart.class);
		chartMapping.put(ChartType.CURVED_LINE, CurvedLineChart.class);
		chartMapping.put(ChartType.PIE, PieChart.class);
		chartMapping.put(ChartType.BAR, BarChart.class);
		chartMapping.put(ChartType.COLUMN, ColumnChart.class);
		chartMapping.put(ChartType.COMBO, ComboChart.class);
		//chartMapping.put(ChartType.TABLE, TableChart.class);
	}

	private org.nextframework.chart.Chart chart;

	public GoogleToolsChartBuilder(org.nextframework.chart.Chart chart) {
		this.chart = chart;
	}

	private DataTable transformChartDataToDataTable() {
		DataTable data = new DataTable();
		ChartData chartData = chart.getData();
		List<ChartRow> rows = chartData.getData();
		if (rows.size() > 0 && chart.getStyle().isContinuous()) {
			data.addColumn(getTypeFor(rows.get(0).getGroup()), getLabelString(chartData.getGroupTitle()));
		} else {
			data.addColumn("string", getLabelString(chartData.getGroupTitle()));
		}
		for (Comparable<?> serie : chartData.getSeries()) {
			data.addColumn("number", getSerieString(serie));
		}
		data.addRows(rows.size());
		for (int i = 0; i < rows.size(); i++) {
			ChartRow chartRow = rows.get(i);
			if (chart.getStyle().isContinuous()) {
				data.setValue(i, 0, chartRow.getGroup());
			} else {
				data.setValue(i, 0, getGroupString(chartRow.getGroup()));
			}
			//fix in 2 decimal numbers
			DecimalFormat format = new DecimalFormat("0.##", new DecimalFormatSymbols(Locale.US));
			for (int j = 0; j < chartRow.getValues().length; j++) {
				Number value = chartRow.getValues()[j];
				data.setValue(i, j + 1, new StringBuilder(value == null || Double.isNaN(value.doubleValue()) ? "" : format.format(value)));
				String formattedValue = getFormatedValue(value);
				if (formattedValue != null) {
					data.setFormattedValue(i, j + 1, formattedValue);
				}
			}
		}
		if (ChartUtils.hasText(this.chart.getValuePattern())) {
			NumberFormat formatter = new NumberFormat(this.chart.getValuePattern());
			for (int j = 0; j < chartData.getSeries().length; j++) {
				formatter.format(data, j + 1);
			}
		}
		return data;
	}

	private String getTypeFor(Object group) {
		if (group instanceof Date || group instanceof Calendar) {
			return "datetime";
		} else if (group instanceof Number) {
			return "number";
		}
		chart.getStyle().setContinuous(false);
		return "string";
	}

	protected String getLabelString(Object label) {
		PropertyEditor formatter = this.chart.getLabelsFormatter();
		if (formatter != null) {
			formatter.setValue(label);
			return formatter.getAsText();
		}
		return label.toString();
	}

	protected Comparable<String> getGroupString(Object group) {
		PropertyEditor formatter = this.chart.getGroupFormatter();
		if (formatter != null) {
			formatter.setValue(group);
			return formatter.getAsText();
		}
		return group.toString();
	}

	private String getSerieString(Comparable<?> serie) {
		PropertyEditor formatter = this.chart.getSeriesFormatter();
		if (formatter != null) {
			formatter.setValue(serie);
			return formatter.getAsText();
		}
		return serie.toString();
	}

	private String getFormatedValue(Number value) {
		PropertyEditor formatter = this.chart.getValuesFormatter();
		if (formatter != null) {
			formatter.setValue(value);
			return formatter.getAsText();
		}
		return value.toString();
	}

	private void createAndDrawChart(final String id, DataTable data) {

		Chart chart = instanciateChart(this.chart.getStyle().getChartType(), id);
		ChartStyle style = this.chart.getStyle();

		Map chartParams = map();
		if (style.getWidth() != null && style.getHeight() != null) {
			chartParams.putProperty("width", style.getWidth());
			chartParams.putProperty("height", style.getHeight());
		}

		if (this.chart.getTitle() != null) {
			chartParams.putProperty("title", getLabelString(GoogleToolsChartBuilder.this.chart.getTitle()));
			if (style.getTitleTextStyle() != null) {
				Map titleParams = map();
				putTextStyleInMap(style.getTitleTextStyle(), titleParams);
				chartParams.putProperty("titleTextStyle", titleParams);
			}
		}

		TextStyle pieSliceTextStyle = style.getPieSliceTextStyle();
		if (pieSliceTextStyle != null) {
			Map pieSliceTextStyleParams = map();
			putTextStyleInMap(pieSliceTextStyle, pieSliceTextStyleParams);
			chartParams.putProperty("pieSliceTextStyle", pieSliceTextStyleParams);
		}
		if (style.is3d()) {
			chartParams.putProperty("is3D", true);
		}

		if (style.getLegendPosition() != LegendPosition.DEFAULT || style.getLegendTextStyle() != null) {
			Map legendParams = map();
			if (style.getLegendPosition() != LegendPosition.DEFAULT) {
				legendParams.putProperty("position", style.getLegendPosition().toString().toLowerCase());
			}
			if (style.getLegendTextStyle() != null) {
				Map textStyleParams = map();
				putTextStyleInMap(style.getLegendTextStyle(), textStyleParams);
				legendParams.putProperty("textStyle", textStyleParams);
			}
			chartParams.putProperty("legend", legendParams);
		}

		Color[] customColors = this.chart.getStyle().getColors();
		if (customColors != null) {
			Array array = array();
			for (int i = 0; i < customColors.length; i++) {
				Color color = customColors[i];
				array.add(getHexColor(color));
			}
			chartParams.putProperty("colors", array);
		}

		List<PieSlice> slices = this.chart.getStyle().getSlices();
		if (slices != null && slices.size() > 0) {
			Array array = array();
			for (int i = 0; i < slices.size(); i++) {
				PieSlice pieSlice = slices.get(i);
				Map sliceObjParams = map();
				if (pieSlice != null) {
					if (pieSlice.getColor() != null) {
						sliceObjParams.putProperty("color", getHexColor(pieSlice.getColor()));
					}
					if (pieSlice.getTextStyle() != null) {
						Map textStyleObj = map();
						putTextStyleInMap(pieSlice.getTextStyle(), textStyleObj);
						sliceObjParams.putProperty("textStyle", textStyleObj);
					}
				}
				array.add(sliceObjParams);
			}
			chartParams.putProperty("slices", array);
		}

		Map hAxisParams = map();
		if (this.chart.getData().getGroupTitle() != null) {
			hAxisParams.putProperty("title", getLabelString(this.chart.getData().getGroupTitle()));
		}
		if (style.getTitleTextStyle() != null) {
			Map titleParams = map();
			putTextStyleInMap(style.getTitleTextStyle(), titleParams);
			hAxisParams.putProperty("titleTextStyle", titleParams);
		}
		if (this.chart.getStyle().isContinuous() && this.chart.getGroupPattern() != null) {
			//format:'dd/MM/yy'
			hAxisParams.putProperty("format", this.chart.getGroupPattern());
		}
		if (this.chart.getStyle().getGroupMinValue() != null
				&& this.chart.getStyle().getGroupMaxValue() != null) {
			hAxisParams.putProperty("viewWindowMode", "explicit");
			hAxisParams.putProperty("viewWindow", map(
					"max", this.chart.getStyle().getGroupMaxValue(),
					"min", this.chart.getStyle().getGroupMinValue()));
		}
		chartParams.putProperty("hAxis", hAxisParams);

		Map vAxisParams = map();
		if (this.chart.getData().getSeriesTitle() != null) {
			vAxisParams.putProperty("title", getLabelString(this.chart.getData().getSeriesTitle()));
		}
		if (style.getTitleTextStyle() != null) {
			Map titleParams = map();
			putTextStyleInMap(style.getTitleTextStyle(), titleParams);
			vAxisParams.putProperty("titleTextStyle", titleParams);
		}
		vAxisParams.putProperty("viewWindow", map("min", 0));
		chartParams.putProperty("vAxis", vAxisParams);

		if (style.getBar().getGroupWidth() != null) {
			Map barParams = map();
			barParams.putProperty("groupWidth", style.getBar().getGroupWidth());
			chartParams.putProperty("bar", barParams);
		}

		if (style.getTopPadding() != null || style.getBottomPadding() != null || style.getLeftPadding() != null || style.getRightPadding() != null || style.getChartAreaHeight() != null || style.getChartAreaWidth() != null) {
			Map chartArea = map();
			if (this.chart.getStyle().getTopPadding() != null) {
				chartArea.putProperty("top", this.chart.getStyle().getTopPadding());
			}
			if (this.chart.getStyle().getBottomPadding() != null) {
				chartArea.putProperty("bottom", this.chart.getStyle().getBottomPadding());
			}
			if (this.chart.getStyle().getLeftPadding() != null) {
				chartArea.putProperty("left", this.chart.getStyle().getLeftPadding());
			}
			if (this.chart.getStyle().getRightPadding() != null) {
				chartArea.putProperty("right", this.chart.getStyle().getRightPadding());
			}
			if (this.chart.getStyle().getChartAreaHeight() != null) {
				chartArea.putProperty("height", this.chart.getStyle().getChartAreaHeight());
			}
			if (this.chart.getStyle().getChartAreaWidth() != null) {
				chartArea.putProperty("width", this.chart.getStyle().getChartAreaWidth());
			}
			chartParams.putProperty("chartArea", chartArea);
		}

		if (this.chart.getStyle().getChartType() == ChartType.COMBO) {
			String styleType = getComboDescription(this.chart.getComboDefaultChartType());
			chartParams.putProperty("seriesType", styleType);
			Comparable<?>[] series = this.chart.getData().getSeries();
			Map seriesTypeParams = map();
			for (int i = 0; i < series.length; i++) {
				ChartType comboSerieType = this.chart.getComboSerieType(i);
				if (comboSerieType != null) {
					seriesTypeParams.putProperty(String.valueOf(i), map("type", getComboDescription(comboSerieType)));
				}
			}
			chartParams.putProperty("series", seriesTypeParams);
		}

		chartParams.putProperty("backgroundColor", "none");

		if (this.chart.getStyle().getPointSize() != null) {
			chartParams.putProperty("pointSize", this.chart.getStyle().getPointSize());
		}
		if (this.chart.getStyle().getLineWidth() != null) {
			chartParams.putProperty("lineWidth", this.chart.getStyle().getLineWidth());
		}

		putTextStyleInMap(style.getTextStyle(), chartParams);

		chart.draw(data, chartParams);
	}

	private void putTextStyleInMap(TextStyle textStyleObject, Map map) {
		if (textStyleObject == null || map == null) {
			return;
		}
		if (textStyleObject.getColor() != null) {
			map.putProperty("color", getHexColor(textStyleObject.getColor()));
		}
		if (textStyleObject.getFontName() != null) {
			map.putProperty("fontName", textStyleObject.getFontName());
		}
		if (textStyleObject.getFontSize() != null) {
			Matcher matcher = Pattern.compile("(\\d+)").matcher(textStyleObject.getFontSize());
			if (matcher.find()) {
				map.putProperty("fontSize", Integer.parseInt(matcher.group(1)));
			}
		}
	}

	private String getComboDescription(ChartType comboDefaultChartType) {
		switch (comboDefaultChartType) {
			case LINE:
				return "line";
			case COLUMN:
				return "bars";
			case AREA:
				return "area";
		}
		throw new NextException("ChartType " + comboDefaultChartType + " not allowed for combo charts. Only LINE, COLUMN and AREA are permited.");
	}

	private String getHexColor(Color color) {
		return "#" + toHexString(color.getRed()) + toHexString(color.getGreen()) + toHexString(color.getBlue());
	}

	private String toHexString(int color) {
		String hexString = Integer.toHexString(color);
		if (hexString.length() == 1) {
			hexString = "0" + hexString;
		}
		return hexString;
	}

	private Chart instanciateChart(ChartType chartType, String id) {
		Class<? extends Chart> class1 = chartMapping.get(chartType);
		if (class1 == null) {
			throw new NextException("Não é possível renderizar o gráfico do tipo especificado " + chartType);
		}
		try {
			return class1.getConstructor(JavascriptReferenciable.class).newInstance(document.getElementById(id));
		} catch (Exception e) {
			throw new NextException("Não foi possível instanciar o tipo de gráfico especificado. " + class1, e);
		}
	}

	@Override
	public void build() {
		final String id = this.chart.getId();
		if (id == null) {
			throw new RuntimeException("O id do gráfico é null, não foi possível gerar gráfico");
		}
		google.load("visualization", "1", map("packages", array("corechart"/*, "table"*/)));
		Function function = new Function(new JavascriptBuilder() {

			public void build() {
				DataTable data = transformChartDataToDataTable();
				createAndDrawChart(id, data);
			}

		});
		google.setOnLoadCallback(function);
	}

}
