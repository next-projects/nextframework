package org.nextframework.test.summary;

import java.util.Map;

import org.nextframework.summary.Summary;
import org.nextframework.summary.annotations.Variable;

public class TestBeanSummaryMap extends Summary<TestBean> {

	@Variable(customAggregator = TestMapAggregator.class, scopeGroup = "report")
	public Map<String, Double> getMapReport() {
		return getCurrent().getMap();
	}

}
