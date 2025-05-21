package org.nextframework.test.report.builder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.elements.ReportComposite;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.definition.elements.Subreport;
import org.nextframework.report.renderer.html.HtmlReportRenderer;

public class TestHtml {

	static class Bean {

		String propertyA;
		String propertyB;

		public Bean(String propertyA, String propertyB) {
			this.propertyA = propertyA;
			this.propertyB = propertyB;
		}

		public String getPropertyA() {
			return propertyA;
		}

		public String getPropertyB() {
			return propertyB;
		}

		public static BeanBuilder builder() {
			return new BeanBuilder();
		}

		static class BeanBuilder {

			String propertyA;
			String propertyB;

			public BeanBuilder a(String param) {
				propertyA = param;
				return this;
			}

			public BeanBuilder b(String param) {
				propertyB = param;
				return this;
			}

			public Bean build() {
				return new Bean(propertyA, propertyB);
			}

		}

	}

	public static void main(String[] args) throws Exception {
		ReportDefinition def = new ReportDefinition("BASE");
		List<Bean> beans = new ArrayList<TestHtml.Bean>();
		for (int i = 0; i < 2; i++) {
			beans.add(Bean.builder().a("a" + i).b("b" + i).build());
		}
		def.setData(beans);

		def.addItem(new ReportLabel("PAGE HEADER 0"), def.getSectionPageHeader(), 0);
		def.addItem(new ReportLabel("PAGE HEADER 1"), def.getSectionPageHeader(), 1);

		def.addItem(new ReportLabel("A"), def.getSectionDetailHeader(), 0);
		def.addItem(new ReportLabel("B"), def.getSectionDetailHeader(), 1);
		def.addItem(new ReportComposite(new ReportLabel("C1"), new ReportLabel("C2")), def.getSectionDetailHeader(), 2);

		def.addItem(new ReportTextField("propertyA"), def.getSectionDetail(), 0);
		def.addItem(new ReportTextField("propertyB"), def.getSectionDetail(), 1);

		def.getSectionDetail().breakLine();
		Subreport sub1 = new Subreport(getSub1());
		def.addItem(sub1.setColspan(2), def.getSectionDetail(), 0);

		def.getSectionDetail().breakLine();
		Subreport sub3 = new Subreport(getSub3());
		def.addItem(sub3.setColspan(3), def.getSectionDetail(), 0);

		String renderAsHtml = HtmlReportRenderer.renderAsHtml(def);
		File file = new File("examples/html-example.html");
		FileOutputStream out = new FileOutputStream(file);
		out.write(renderAsHtml.getBytes());
		out.flush();
		out.close();
	}

	public static ReportDefinition getSub1() {
		ReportDefinition sub1 = new ReportDefinition("SUB 1");

		sub1.addItem(new Subreport(getSub2()), sub1.getSectionPageHeader(), 0);
		sub1.getSectionPageHeader().breakLine();

		sub1.addItem(new ReportLabel("PAGE HEADER 0 SUB 1"), sub1.getSectionPageHeader(), 0);
		return sub1;
	}

	public static ReportDefinition getSub3() {
		ReportDefinition sub3 = new ReportDefinition("SUB 3");
		sub3.addItem(new ReportLabel("PAGE HEADER 0 SUB 3"), sub3.getSectionPageHeader(), 0);

		sub3.getSectionPageHeader().breakLine();
		sub3.addItem(new Subreport(getSub2()), sub3.getSectionPageHeader(), 0);
		return sub3;
	}

	public static ReportDefinition getSub2() {
		ReportDefinition sub2 = new ReportDefinition("_ SUB 2");
		sub2.addItem(new ReportLabel("PAGE HEADER 0 SUB 2"), sub2.getSectionPageHeader(), 0);
		return sub2;
	}

}
