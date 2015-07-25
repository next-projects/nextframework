package org.nextframework.test.report.builder;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.elements.ReportComposite;
import org.nextframework.report.definition.elements.ReportGrid;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportOverlapComposite;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.renderer.html.HtmlReportRenderer;
import org.nextframework.report.renderer.jasper.JasperReportsRenderer;
import org.nextframework.report.renderer.jasper.builder.JasperRenderParameters;

public class TestComponents {

	public static void main(String[] args) throws Exception {
		ReportDefinition definition = new ReportDefinition("examples/exampleComponents");
		definition.setData(TestBean.createDataset(30));
		definition.setTitle("This is Sparta!");
		definition.addItem(new ReportTextField("name"), definition.getSectionDetail(), 0);
		definition.addItem(new ReportLabel("PAGE FOOTER"), definition.getSectionPageFooter(), 0);
		definition.addItem(new ReportLabel("LAST PAGE FOOTER"), definition.getSectionLastPageFooter(), 0);

		ReportComposite composite = new ReportOverlapComposite();
		ReportLabel c1 = new ReportLabel("Velde", 100);
		c1.getStyle().setBackgroundColor(Color.GREEN);
		c1.setRenderParameter(JasperRenderParameters.PRINT_WHEN_EXPRESSION, "((String)$F{name}).contains(\"1\")");
		composite.addItem(c1);
		
		ReportLabel c2 = new ReportLabel("Velmelho", 100);
		c2.setRenderParameter(JasperRenderParameters.PRINT_WHEN_EXPRESSION, "((String)$F{name}).contains(\"0\")");
		c2.getStyle().setBackgroundColor(Color.RED);
		c2.getStyle().setForegroundColor(Color.WHITE);
		composite.addItem(c2);
		
		ReportLabel c3 = new ReportLabel("Amarelo", 100);
		c3.setRenderParameter(JasperRenderParameters.PRINT_WHEN_EXPRESSION, "!((String)$F{name}).contains(\"0\") && !((String)$F{name}).contains(\"1\")");
		c3.getStyle().setBackgroundColor(Color.YELLOW);
		composite.addItem(c3);
		
		definition.addItem(composite, definition.getSectionDetail(), 1);

		definition.getSectionDetail().breakLine();
		
		ReportGrid grid = new ReportGrid(2);
		grid.addItem(new ReportLabel("G1"));
		grid.addItem(new ReportLabel("G2"));
		grid.addItem(new ReportLabel("G3"));
		grid.addItem(new ReportLabel("G4"));
		grid.addItem(new ReportLabel("G5"));
		grid.addItem(new ReportLabel("G6", 200));
		
		definition.addItem(grid, definition.getSectionDetail(), 1);
		
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
