package org.nextframework.test.summary;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.nextframework.summary.Summary;
import org.nextframework.summary.SummaryRow;
import org.nextframework.summary.annotations.CalculationType;
import org.nextframework.summary.compilation.SummaryResult;
import org.nextframework.summary.dynamic.DynamicSummary;

public class TestSummary {

	@Test
	public void testEquality() {
		Class<Summary<TestBean>> a = DynamicSummary.getInstance(TestBean.class)
				.addGroup("nome")
				.addVariable("value", "", CalculationType.AVERAGE)
				.getSummaryClass();
		Class<Summary<TestBean>> b = DynamicSummary.getInstance(TestBean.class)
				.addGroup("nome")
				.addVariable("value", "", CalculationType.SUM)
				.getSummaryClass();
		Assert.assertNotSame(a, b);
	}

	@Test
	public void testNoOrder() {
		TestBean b1 = new TestBean("A", 1);
		TestBean b2 = new TestBean("A", 1);
		TestBean b3 = new TestBean("A", 1);
		List<TestBean> data = Arrays.asList(b1, b2, b3);
		SummaryResult<TestBean, TestBeanSummary> result = SummaryResult.createFrom(data, TestBeanSummary.class);
		List<SummaryRow<TestBean, TestBeanSummary>> items = result.getItems();
		Assert.assertEquals(2, items.get(0).getChangedGroups().length);
		Assert.assertEquals(0, items.get(1).getChangedGroups().length);
		Assert.assertEquals(0, items.get(2).getChangedGroups().length);
	}

	@Test
	public void testOrder() {
		TestBean b1 = new TestBean("A", 1);
		TestBean b2 = new TestBean("A", 1);
		TestBean b3 = new TestBean("A", 1);
		List<TestBean> data = Arrays.asList(b1, b2, b3);
		SummaryResult<TestBean, TestBeanSummary> result = SummaryResult.createFrom(data, TestBeanSummary.class).orderBy("summary.grupo1");
		List<SummaryRow<TestBean, TestBeanSummary>> items = result.getItems();
		Assert.assertEquals(2, items.get(0).getChangedGroups().length);
		Assert.assertEquals(0, items.get(1).getChangedGroups().length);
		Assert.assertEquals(0, items.get(2).getChangedGroups().length);
		Assert.assertSame(b1, items.get(0).getRow());
		Assert.assertSame(b2, items.get(1).getRow());
		Assert.assertSame(b3, items.get(2).getRow());
	}

	@Test
	public void testOrder2() {
		TestBean b1 = new TestBean("A", 1);
		TestBean b2 = new TestBean("A", 1);
		TestBean b3 = new TestBean("A", 1);
		TestBean b4 = new TestBean("B", 1);
		TestBean b5 = new TestBean("B", 2);
		TestBean b6 = new TestBean("B", 2);
		List<TestBean> data = Arrays.asList(b1, b2, b3, b4, b5, b6);
		SummaryResult<TestBean, TestBeanSummary> result = SummaryResult.createFrom(data, TestBeanSummary.class).orderBy("summary.grupo1");
		List<SummaryRow<TestBean, TestBeanSummary>> items = result.getItems();
		Assert.assertEquals(2, items.get(0).getChangedGroups().length);
		Assert.assertEquals(0, items.get(1).getChangedGroups().length);
		Assert.assertEquals(0, items.get(2).getChangedGroups().length);
		Assert.assertEquals(2, items.get(3).getChangedGroups().length);
		Assert.assertEquals(1, items.get(4).getChangedGroups().length);
		Assert.assertEquals(0, items.get(5).getChangedGroups().length);
		Assert.assertEquals(0, items.get(0).getRowIndex());
		Assert.assertEquals(1, items.get(1).getRowIndex());
		Assert.assertEquals(2, items.get(2).getRowIndex());
		Assert.assertEquals(3, items.get(3).getRowIndex());
		Assert.assertEquals(4, items.get(4).getRowIndex());
		Assert.assertEquals(5, items.get(5).getRowIndex());
		Assert.assertSame(b1, items.get(0).getRow());
		Assert.assertSame(b2, items.get(1).getRow());
		Assert.assertSame(b3, items.get(2).getRow());
		Assert.assertSame(b4, items.get(3).getRow());
		Assert.assertSame(b5, items.get(4).getRow());
		Assert.assertSame(b6, items.get(5).getRow());
	}

}
