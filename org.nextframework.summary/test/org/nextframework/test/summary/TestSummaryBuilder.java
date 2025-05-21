package org.nextframework.test.summary;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.nextframework.summary.Summary;
import org.nextframework.summary.SummaryRow;
import org.nextframework.summary.annotations.Group;
import org.nextframework.summary.annotations.Variable;
import org.nextframework.summary.compilation.SummaryBuilder;
import org.nextframework.summary.compilation.SummaryResult;

import junit.framework.Assert;

public class TestSummaryBuilder {

	@Test
	public void testCompileSummary() {
		SummaryBuilder.compileSummary(TestBeanSummary.class);
	}

	@Test
	public void testCompileSummaryInner() {
		SummaryBuilder.compileSummary(InnerSummary.class);
	}

	@Test
	public void testSummaryInnerResult() {

		List<TestBean> beans = new ArrayList<TestBean>();

		beans.add(new TestBean() {

			{
				setNome("g1");
				setValue(3);
			}

		});

		beans.add(new TestBean() {

			{
				setNome("g1");
				setValue(7);
			}

		});

		beans.add(new TestBean() {

			{
				setNome("g2");
				setValue(7);
			}

		});

		SummaryResult<TestBean, InnerSummary> summaryResult = SummaryBuilder.compileSummary(InnerSummary.class).createSummaryResult(beans);
		List<SummaryRow<TestBean, InnerSummary>> items = summaryResult.getItems();
		Assert.assertEquals(3, items.size());

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testCompileSummaryNoGeneric() {
		try {
			SummaryBuilder.compileSummary(TestBeanSummaryNoGeneric.class);
			Assert.fail();
		} catch (Exception e) {
		}
	}

	public static class InnerSummary extends Summary<TestBean> {

		@Group(1)
		public String getNome() {
			return getCurrent().getNome();
		}

		@Variable()
		public Integer getSoma() {
			return getCurrent().getValue();
		}

	}

}
