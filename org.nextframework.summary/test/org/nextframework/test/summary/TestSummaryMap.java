package org.nextframework.test.summary;

import java.util.Arrays;
import java.util.HashMap;

import org.nextframework.summary.TextReportPrinter;
import org.nextframework.summary.compilation.SummaryResult;

public class TestSummaryMap {

	public static void main(String[] args) {
		TestBean tb1 = new TestBean();
		tb1.setMap(new HashMap<String, Double>(){{
			put("item1", 1.0);
			put("item2", 3.0);
			put("item3", 4.0);
		}});
		TestBean tb2 = new TestBean();
		tb2.setMap(new HashMap<String, Double>(){{
			put("item1", 1.0);
			put("item3", 3.0);
			put("item5", 2.0);
		}});
		
		SummaryResult<TestBean, TestBeanSummaryMap> result = SummaryResult.createFrom(Arrays.asList(new TestBean[]{tb1, tb2}), TestBeanSummaryMap.class);
		
		TextReportPrinter.print(result);
	}
}
