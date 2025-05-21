/**
 * 
 */
package org.nextframework.view.chart.aggregate;

import java.util.List;

public class ChartExceptionAggregateFunction implements ChartAggregateFunction {

	public Number aggregate(List<Number> values) {
		if (values == null) {
			return 0;
		}
		if (values.size() > 1) {
			throw new IllegalArgumentException("More then one value was set for group and serie.");
		}
		return values.get(0);
	}

}
