package org.nextframework.test.report.builder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.nextframework.chart.Chart;
import org.nextframework.chart.ChartData;
import org.nextframework.chart.ChartDataBuilder;
import org.nextframework.chart.ChartType;
import org.nextframework.view.chart.jfree.ChartRendererJFreeChart;

public class TestExample3Bean {

	String type;

	List<TestExample3BeanEvaluation> evaluations = new ArrayList<TestExample3BeanEvaluation>();

	public TestExample3Bean(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setEvaluations(List<TestExample3BeanEvaluation> evaluations) {
		this.evaluations = evaluations;
	}

	public List<TestExample3BeanEvaluation> getEvaluations() {
		return evaluations;
	}

	public Chart getDriversChart() {
		Chart chart = new Chart(ChartType.PIE);
		chart.setData(ChartDataBuilder.build(evaluations, "driver", null, "points"));
		byte[] image = ChartRendererJFreeChart.renderAsImage(chart);
		try {
			new FileOutputStream("examples/chart.png").write(image);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return chart;
	}

}
