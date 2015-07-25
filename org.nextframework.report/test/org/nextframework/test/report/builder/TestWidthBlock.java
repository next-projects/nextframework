package org.nextframework.test.report.builder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportGroup;
import org.nextframework.report.definition.elements.ReportGrid;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.renderer.html.HtmlReportRenderer;
import org.nextframework.report.renderer.jasper.JasperReportsRenderer;

public class TestWidthBlock {

	public static void main(String[] args) throws Exception {
		ReportDefinition definition = new ReportDefinition("examples/exampleWidthBlock");
		definition.setData(TestBean.createDataset(30));
		definition.setTitle("This is Sparta!");
		definition.addItem(new ReportTextField("name"), definition.getSectionDetail(), 0);
		definition.addItem(new ReportLabel("PAGE FOOTER"), definition.getSectionPageFooter(), 0);
		definition.addItem(new ReportLabel("LAST PAGE FOOTER"), definition.getSectionLastPageFooter(), 0);

		ReportGroup g1 = definition.createGroup("mod");
		
		ReportGrid grid = new ReportGrid(3);
		grid.addItem(new ReportTextField("mod"));
		grid.addItem(new ReportLabel("Aaaaa"));
		grid.addItem(new ReportLabel("Bbbbb"));
		grid.addItem(new ReportLabel("Cccccc"));
		grid.addItem(new ReportLabel("Dddddd"));
		grid.addItem(new ReportLabel("Eeeeeee"));
		grid.setColspan(5);
		definition.addItem(grid, g1.getSectionDetail(), 0);
		
		
		writePDF(definition);
	}

	private static void writePDF(ReportDefinition definition) throws IOException {
		write(definition, JasperReportsRenderer.renderAsPDF(definition), "pdf");
		write(definition, JasperReportsRenderer.renderAsJRXML(definition), "jrxml");
		write(definition, HtmlReportRenderer.renderAsHtml(definition).getBytes(), "html");
	}

	public static void write(ReportDefinition definition, byte[] bytes, String type) throws FileNotFoundException, IOException {
		FileOutputStream out = new FileOutputStream(definition.getReportName()+"." + type);
		out.write(bytes);
		out.flush();
		out.close();
	}
}
