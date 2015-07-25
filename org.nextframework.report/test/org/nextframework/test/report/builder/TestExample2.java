package org.nextframework.test.report.builder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.nextframework.chart.Chart;
import org.nextframework.chart.ChartData;
import org.nextframework.chart.ChartType;
import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.elements.ReportChart;
import org.nextframework.report.definition.elements.ReportComposite;
import org.nextframework.report.definition.elements.ReportConstants;
import org.nextframework.report.definition.elements.ReportGrid;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.renderer.jasper.JasperReportsRenderer;

public class TestExample2 {

	public static void main(String[] args) throws Exception {
		Chart chart = new Chart(ChartType.PIE);
		ChartData chartData = new ChartData("Grupo", "Series");
		chartData.setSeries("Serie 1");
		chartData.addRow("X", 1);
		chartData.addRow("Y", 2);
		chart.setData(chartData);
		
		ReportDefinition definition = new ReportDefinition("examples/example2");
		definition.setData(TestBean.createDataset(100));
		definition.setTitle("This is Sparta 2!");
		
		definition.addItem(new ReportChart(chart, 100, 100), definition.getSectionPageHeader(), 0);
		definition.addItem(new ReportChart(chart, 100, 110), definition.getSectionPageHeader(), 1);
		definition.addItem(new ReportChart(chart), definition.getSectionPageHeader(), 2);
		definition.addItem(new ReportLabel("LABEL", 80), definition.getSectionPageHeader(), 3);
		
		definition.getSectionPageHeader().breakLine();
		
		ReportGrid grid = new ReportGrid(4);
		grid.setColspan(4);
		grid.addItem(new ReportChart(chart, 100, 100));
		grid.addItem(new ReportChart(chart, 100, 110));
		grid.addItem(new ReportChart(chart));
		grid.addItem(new ReportLabel("LABEL 1", 80));
		grid.addItem(new ReportChart(chart, 100, 100));
		grid.addItem(new ReportChart(chart, 100, 110));
		grid.addItem(new ReportChart(chart));
		grid.addItem(new ReportLabel("LABEL 2"));
		
		ReportGrid grid2 = new ReportGrid(4);
		grid2.setColspan(4);
		grid2.setColumnWidths(new int[]{20 | ReportConstants.PERCENT_WIDTH, 
				20 | ReportConstants.PERCENT_WIDTH,
				40 | ReportConstants.PERCENT_WIDTH,
				20 | ReportConstants.PERCENT_WIDTH,
		});
		grid2.addItem(new ReportChart(chart, 100, 100));
		grid2.addItem(new ReportChart(chart, 100, 110));
		grid2.addItem(new ReportChart(chart));
		grid2.addItem(new ReportLabel("LABEL 12", 80));
		grid2.addItem(new ReportChart(chart, 100, 100));
		grid2.addItem(new ReportChart(chart, 100, 110));
		grid2.addItem(new ReportChart(chart));
		grid2.addItem(new ReportLabel("LABEL 22"));
		
		ReportComposite composite = new ReportComposite();
		composite.setColspan(4);
		composite.addItem(new ReportLabel("LABEL SP"));
		composite.addItem(new ReportLabel("LABEL SP"));
		composite.addItem(new ReportLabel("LABEL A"));
		composite.addItem(new ReportLabel("LABEL B", 25 | ReportConstants.PERCENT_WIDTH));
		composite.addItem(new ReportLabel("LABEL C", 80));
		composite.addItem(new ReportLabel("LABEL D"));
		
		definition.addItem(grid, definition.getSectionPageHeader(), 0);
		
		definition.getSectionPageHeader().breakLine();
		definition.addItem(grid2, definition.getSectionPageHeader(), 0);
		definition.getSectionPageHeader().breakLine();
		definition.addItem(composite, definition.getSectionPageHeader(), 0);
		
		definition.addItem(new ReportTextField("name"), definition.getSectionDetail(), 0);
		definition.addItem(new ReportLabel("PAGE FOOTER"), definition.getSectionPageFooter(), 0);
		definition.addItem(new ReportLabel("LAST PAGE FOOTER"), definition.getSectionLastPageFooter(), 0);
		writePDF(definition);
	}

	private static void writePDF(ReportDefinition definition) throws IOException {
		write(definition, JasperReportsRenderer.renderAsJRXML(definition), "jrxml");
		write(definition, JasperReportsRenderer.renderAsPDF(definition), "pdf");
	}

	public static void write(ReportDefinition definition, byte[] bytes, String type) throws FileNotFoundException, IOException {
		FileOutputStream out = new FileOutputStream(definition.getReportName()+"." + type);
		out.write(bytes);
		out.flush();
		out.close();
	}
}
