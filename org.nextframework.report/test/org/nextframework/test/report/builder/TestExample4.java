package org.nextframework.test.report.builder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.elements.Subreport;
import org.nextframework.report.renderer.jasper.JasperReportsRenderer;

public class TestExample4 {

	public static void main(String[] args) throws IOException {
		TestExample4Builder builder = new TestExample4Builder();
		List<TestExample4MainBean> data = getData();
		Set<TestExample4PhaseBean> phases = getPhases(data);
		builder.setPhases(phases);
		builder.setData(data);
		ReportDefinition definition = builder.getDefinition();
		writePDF(definition);
	}


	public static Set<TestExample4PhaseBean> getPhases(List<TestExample4MainBean> data) {
		Set<TestExample4PhaseBean> phases = new LinkedHashSet<TestExample4PhaseBean>();
		for (TestExample4MainBean testExample4MainBean : data) {
			List<TestExample4ItemBean> items = testExample4MainBean.getItems();
			for (TestExample4ItemBean testExample4ItemBean : items) {
				phases.add(testExample4ItemBean.getPhase());
			}
		}
		return phases;
	}
	
	
	private static void writePDF(ReportDefinition definition) throws IOException {
		List<Subreport> subreports = definition.getSubreports();
		for (Subreport subreport : subreports) {
			writePDF(subreport.getReport());
		}
		write(definition, JasperReportsRenderer.renderAsJRXML(definition), "jrxml");
		write(definition, JasperReportsRenderer.renderAsPDF(definition), "pdf");
	}

	private static void write(ReportDefinition definition, byte[] bytes, String type) throws FileNotFoundException, IOException {
		FileOutputStream out = new FileOutputStream("examples/"+definition.getReportName()+"." + type);
		out.write(bytes);
		out.flush();
		out.close();
	}
	
	

	static TestExample4PhaseBean phase1 = new TestExample4PhaseBean(1, "Phase 1");
	static TestExample4PhaseBean phase2 = new TestExample4PhaseBean(2, "Phase 2");
	static TestExample4PhaseBean phase3 = new TestExample4PhaseBean(3, "Phase 3");
	static TestExample4PhaseBean phase4 = new TestExample4PhaseBean(4, "Phase 4");

	static List<TestExample4MainBean> getData(){
		return Arrays.asList(
				new TestExample4MainBean("A", getItemsForA()),
				new TestExample4MainBean("B", getItemsForB()),
				new TestExample4MainBean("C", getItemsForC())
		);
	}
	
	static List<TestExample4ItemBean> getItemsForA() {
		return Arrays.asList(
				new TestExample4ItemBean(phase1, "00:50"),
				new TestExample4ItemBean(phase2, "01:12")
			);
	}
	static List<TestExample4ItemBean> getItemsForB() {
		return Arrays.asList(
				new TestExample4ItemBean(phase2, "00:12"),
				new TestExample4ItemBean(phase3, "00:55")
				);
	}
	static List<TestExample4ItemBean> getItemsForC() {
		return Arrays.asList(
				new TestExample4ItemBean(phase2, "00:23"),
				new TestExample4ItemBean(phase4, "00:35")
				);
	}

}
