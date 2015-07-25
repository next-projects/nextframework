package org.nextframework.test.report.builder;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Test;
import org.nextframework.report.definition.builder.BaseReportBuilder;
import org.nextframework.report.definition.builder.LayoutReportBuilder;
import org.nextframework.report.renderer.jasper.JasperReportsRenderer;
import org.nextframework.summary.Summary;
import org.nextframework.summary.compilation.SummaryBuilder;
import org.nextframework.summary.compilation.SummaryResult;
import org.nextframework.summary.dynamic.DynamicSummary;


public class TestBaseReportBuilder {

	@Test
	public void testGenericType(){
		TestBuilder testBuilder = new TestBuilder();
		SummaryResult<TestBean, Summary<TestBean>> summaryResult = DynamicSummary.getInstance(TestBean.class).getSummaryResult(new ArrayList<TestBean>());
		
		testBuilder.setData(summaryResult);
		Assert.assertEquals(TestBean.class, testBuilder.getRowClass());
	}
	
	@Test
	public void testGenericType2(){
		TestBuilder testBuilder = new TestBuilder();
		SummaryResult<TestBean, TestBeanSummary> summaryResult = SummaryBuilder.compileSummary(TestBeanSummary.class).createSummaryResult(new ArrayList<TestBean>());
		testBuilder.setData(summaryResult);
		Assert.assertEquals(TestBean.class, testBuilder.getRowClass());
	}
	
	@Test
	public void testPdf(){
		TestBuilder2 testBuilder = new TestBuilder2();
		SummaryResult<TestBean, TestBeanSummary> summaryResult = SummaryBuilder.compileSummary(TestBeanSummary.class).createSummaryResult(new ArrayList<TestBean>());
		testBuilder.setData(summaryResult);
		
		JasperReportsRenderer.renderAsPDF(testBuilder.getDefinition());
	}
	
	class TestBuilder extends BaseReportBuilder {
		@Override
		public Class<?> getRowClass() {
			return super.getRowClass();
		}
	}
	
	class TestBuilder2 extends LayoutReportBuilder {

		@Override
		protected void layoutReport() {
			
		}
		
	}
}
