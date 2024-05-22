package org.nextframework.view.js.api;

import org.nextframework.view.js.JavascriptReferenciable;

public interface GoogleVisualization extends Google {

	class NumberFormat extends Obj {

		public NumberFormat(String pattern) {
			this(new Object[] { "pattern", pattern });
		}

		public NumberFormat(Object... options) {
			super(getScript().generateUniqueId("formatter"), "google.visualization.NumberFormat", new Map(options));
		}

		public NumberFormat(String variable, Object... options) {
			super(variable, "google.visualization.NumberFormat", new Map(options));
		}

		public void format(DataTable data, int i) {
			call("format", data, i);
		}

	}

	class DataTable extends Obj {

		public DataTable() {
			super(getScript().generateUniqueId("data"), "google.visualization.DataTable");
		}

		public DataTable(String variable) {
			super(variable, "google.vizualization.DataTable");
		}

		public void addColumn(String type, String name) {
			call("addColumn", type, name);
		}

		public void addRows(Array a) {
			call("addRows", a);
		}

		public void addRows(Object[][] objects) {
			Array a1 = new Array();
			for (Object[] objects2 : objects) {
				Array a2 = new Array();
				for (Object object : objects2) {
					a2.add(object);
				}
				a1.add(a2);
			}
			addRows(a1);
		}

		public void addRows(int size) {
			call("addRows", size);
		}

		public void setValue(int i, int j, Object o) {
			call("setValue", i, j, o);
		}

		public void setFormattedValue(int i, int j, Object o) {
			call("setFormattedValue", i, j, o);
		}

	}

	class Chart extends Obj {

		public Chart(String variable, String className, Object... parameters) {
			super(variable, className, parameters);
		}

		public void draw(DataTable data, Map map) {
			call("draw", data, map);
		}

	}

	class AreaChart extends Chart {

		public AreaChart(JavascriptReferenciable ref) {
			super(getScript().generateUniqueId("chart"), "google.visualization.AreaChart", ref);
		}

		public AreaChart(String variable, JavascriptReferenciable ref) {
			super(variable, "google.visualization.AreaChart", ref);
		}

	}

	class ScatterChart extends Chart {

		public ScatterChart(JavascriptReferenciable ref) {
			super(getScript().generateUniqueId("chart"), "google.visualization.ScatterChart", ref);
		}

		public ScatterChart(String variable, JavascriptReferenciable ref) {
			super(variable, "google.visualization.ScatterChart", ref);
		}

	}

	class LineChart extends Chart {

		public LineChart(JavascriptReferenciable ref) {
			super(getScript().generateUniqueId("chart"), "google.visualization.LineChart", ref);
		}

		public LineChart(String variable, JavascriptReferenciable ref) {
			super(variable, "google.visualization.LineChart", ref);
		}

	}

	class CurvedLineChart extends Chart {

		public CurvedLineChart(JavascriptReferenciable ref) {
			super(getScript().generateUniqueId("chart"), "google.visualization.LineChart", ref);
		}

		public CurvedLineChart(String variable, JavascriptReferenciable ref) {
			super(variable, "google.visualization.LineChart", ref);
		}

		@Override
		public void draw(DataTable data, Map map) {
			map.putProperty("curveType", "function");
			super.draw(data, map);
		}

	}

	class PieChart extends Chart {

		public PieChart(JavascriptReferenciable ref) {
			super(getScript().generateUniqueId("chart"), "google.visualization.PieChart", ref);
		}

		public PieChart(String variable, JavascriptReferenciable ref) {
			super(variable, "google.visualization.PieChart", ref);
		}

	}

	class BarChart extends Chart {

		public BarChart(JavascriptReferenciable ref) {
			super(getScript().generateUniqueId("chart"), "google.visualization.BarChart", ref);
		}

		public BarChart(String variable, JavascriptReferenciable ref) {
			super(variable, "google.visualization.BarChart", ref);
		}

	}

	class TableChart extends Chart {

		public TableChart(JavascriptReferenciable ref) {
			super(getScript().generateUniqueId("chart"), "google.visualization.Table", ref);
		}

		public TableChart(String variable, JavascriptReferenciable ref) {
			super(variable, "google.visualization.Table", ref);
		}

	}

	class ColumnChart extends Chart {

		public ColumnChart(JavascriptReferenciable ref) {
			super(getScript().generateUniqueId("chart"), "google.visualization.ColumnChart", ref);
		}

		public ColumnChart(String variable, JavascriptReferenciable ref) {
			super(variable, "google.visualization.ColumnChart", ref);
		}

	}

	class ComboChart extends Chart {

		public ComboChart(JavascriptReferenciable ref) {
			super(getScript().generateUniqueId("chart"), "google.visualization.ComboChart", ref);
		}

		public ComboChart(String variable, JavascriptReferenciable ref) {
			super(variable, "google.visualization.ComboChart", ref);
		}

	}

}
