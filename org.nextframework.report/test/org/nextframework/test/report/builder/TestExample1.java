package org.nextframework.test.report.builder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.builder.BaseReportBuilder;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.renderer.ReportBuilderValueConverter;
import org.nextframework.report.renderer.jasper.JasperReportsRenderer;

public class TestExample1 {

	public static void main(String[] args) throws Exception {
		ReportDefinition definition = new ReportDefinition("examples/example1");
		definition.setParameter(BaseReportBuilder.CONVERTER, new ReportBuilderValueConverter());
		definition.setData(TestBean.createDataset(100));
		definition.setTitle("This is Sparta!");
		definition.addItem(new ReportTextField("name"), definition.getSectionDetail(), 0);
		definition.addItem(new ReportLabel("PAGE FOOTER"), definition.getSectionPageFooter(), 0);
		definition.addItem(new ReportLabel("LAST PAGE FOOTER"), definition.getSectionLastPageFooter(), 0);
		writePDF(definition);
	}

	private static void writePDF(ReportDefinition definition) throws IOException {
		write(definition, JasperReportsRenderer.renderAsPDF(definition), "pdf");
		write(definition, JasperReportsRenderer.renderAsJRXML(definition), "jrxml");
	}

	public static void write(ReportDefinition definition, byte[] bytes, String type) throws FileNotFoundException, IOException {
		FileOutputStream out = new FileOutputStream(definition.getReportName() + "." + type);
		out.write(bytes);
		out.flush();
		out.close();
	}

}
