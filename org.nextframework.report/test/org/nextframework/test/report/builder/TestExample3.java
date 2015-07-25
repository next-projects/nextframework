package org.nextframework.test.report.builder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.elements.ReportChart;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.definition.elements.Subreport;
import org.nextframework.report.definition.elements.style.ReportItemStyle;
import org.nextframework.report.renderer.jasper.JasperReportsRenderer;

public class TestExample3 {
	
	public static List<TestExample3Bean> getData(){
		return Arrays.asList(new TestExample3Bean[]{
				createAcceleration(),
				createBreak(),
				createTurnLeft(),
				createTurnRight(),
		});
	}

	public static void main(String[] args) throws Exception {
		ReportDefinition definition = new ReportDefinition("examples/example3");
		definition.setTitle("Test 3");
		definition.setData(getData());
		
		definition.addItem(new ReportTextField("type").setColspan(2).setStyle(new ReportItemStyle(true)), definition.getSectionDetail(), 0);
		definition.getSectionDetail().breakLine();
		definition.addItem(new ReportChart("driversChart"), definition.getSectionDetail(), 0);
		definition.addItem(new Subreport(getSubreport1(), "evaluations"), definition.getSectionDetail(), 1);
		writePDF(definition);
	}

	public static ReportDefinition getSubreport1() {
		ReportDefinition definition = new ReportDefinition("examples/example3_sub1");
		definition.getStyle().setNoMargin(true);
		definition.getStyle().setPageWidth(300);
		definition.addItem(new ReportLabel("Driver"), definition.getSectionColumnHeader(), 0);
		definition.addItem(new ReportLabel("Points"), definition.getSectionColumnHeader(), 1);
		definition.addItem(new ReportTextField("driver"), definition.getSectionDetail(), 0);
		definition.addItem(new ReportTextField("points"), definition.getSectionDetail(), 1);
		return definition;
	}

	private static void writePDF(ReportDefinition definition) throws IOException {
		List<Subreport> subreports = definition.getSubreports();
		for (Subreport subreport : subreports) {
			writePDF(subreport.getReport());
		}
		write(definition, JasperReportsRenderer.renderAsJRXML(definition), "jrxml");
		write(definition, JasperReportsRenderer.renderAsPDF(definition), "pdf");
	}

	public static void write(ReportDefinition definition, byte[] bytes, String type) throws FileNotFoundException, IOException {
		FileOutputStream out = new FileOutputStream(definition.getReportName()+"." + type);
		out.write(bytes);
		out.flush();
		out.close();
	}
	

	public static TestExample3Bean createTurnRight() {
		TestExample3Bean r = new TestExample3Bean("Turn Right");
		r.getEvaluations().add(new TestExample3BeanEvaluation("Joao", 4));
		r.getEvaluations().add(new TestExample3BeanEvaluation("Jose", 2));
		r.getEvaluations().add(new TestExample3BeanEvaluation("Mario", 7));
		r.getEvaluations().add(new TestExample3BeanEvaluation("Alberto", 4));
		return r;
	}

	public static TestExample3Bean createTurnLeft() {
		TestExample3Bean r = new TestExample3Bean("Turn Left");
		r.getEvaluations().add(new TestExample3BeanEvaluation("Joao", 4));
		r.getEvaluations().add(new TestExample3BeanEvaluation("Jose", 6));
		r.getEvaluations().add(new TestExample3BeanEvaluation("Mario", 7));
		r.getEvaluations().add(new TestExample3BeanEvaluation("Alberto", 2));
		return r;
	}

	public static TestExample3Bean createBreak() {
		TestExample3Bean r = new TestExample3Bean("Break");
		r.getEvaluations().add(new TestExample3BeanEvaluation("Joao", 1));
		r.getEvaluations().add(new TestExample3BeanEvaluation("Jose", 2));
		r.getEvaluations().add(new TestExample3BeanEvaluation("Mario", 3));
		r.getEvaluations().add(new TestExample3BeanEvaluation("Alberto", 4));
		return r;
	}

	public static TestExample3Bean createAcceleration() {
		TestExample3Bean r = new TestExample3Bean("Acceleration");
		r.getEvaluations().add(new TestExample3BeanEvaluation("Joao", 4));
		r.getEvaluations().add(new TestExample3BeanEvaluation("Jose", 1));
		r.getEvaluations().add(new TestExample3BeanEvaluation("Mario", 1));
		r.getEvaluations().add(new TestExample3BeanEvaluation("Alberto", 2));
		return r;
	}	
}
