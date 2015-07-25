package org.nextframework.view.js;

import java.util.ArrayList;
import java.util.List;

public class JavascriptCode {
	
	List<Object> code = new ArrayList<Object>();
	
	String identation;
	
	public JavascriptCode() {
	}
	public JavascriptCode(String string) {
		append(string);
	}
	
	public void setIdentation(int level){
		identation = "";
		for (int i = 0; i < level; i++) {
			identation += "    ";
		}
	}

	@Override
	public String toString() {
		return JSBuilderUtils.join(code, "\n");
	}

	public void append(Object o) {
		if(identation != null){
			o = identation + o.toString().replace("\n", "\n"+identation);
		}
		code.add(o);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JavascriptCode jsCode = new JavascriptCode();
		JavascriptObjectReference google = new JavascriptObjectReference("google");
		JavascriptFunctionReference drawChart = new JavascriptFunctionReference("drawChart", new JavascriptCode());
		
		jsCode.append(google.call("load", "visualization", "1", new JavascriptMap("packages", new JavascriptArray("corechart"))));
		jsCode.append(google.call("setOnLoadCallback", drawChart));
		
		JavascriptObjectReference data = new JavascriptObjectReference("data", "google.visualization.DataTable");
		drawChart.append(data);
		drawChart.append(data.call("addColumn", "string", "Task"));
		drawChart.append(data.call("addColumn", "string", "Hours per Day"));
		drawChart.append(data.call("addRows", new JavascriptArray(
				new JavascriptArray("2004", 1000, 400),
				new JavascriptArray("2005", 1170, 460),
				new JavascriptArray("2006", 660, 1120),
				new JavascriptArray("2007", 1030, 540)
		)));
		
		//var chart = new google.visualization.AreaChart(document.getElementById('chart_div'));
		JavascriptObjectReference chart = new JavascriptObjectReference("chart", "google.visualization.AreaChart", new JavascriptObjectReference("document.getElementById('chart_div')"));
		drawChart.append(chart);
		drawChart.append(chart.call("data", new JavascriptMap("width", 400, "height", 200, "title", "Company Performance", 
											"hAxis", new JavascriptMap("title", "Year", 
														"titleTextStyle", new JavascriptMap("color", "#FF0000")))));
		
		jsCode.append(drawChart);
		
		System.out.println(jsCode);
	}
}
