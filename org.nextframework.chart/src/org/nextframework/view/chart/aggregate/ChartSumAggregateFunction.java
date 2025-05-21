/**
 * 
 */
package org.nextframework.view.chart.aggregate;

import java.util.List;

public class ChartSumAggregateFunction implements ChartAggregateFunction {

	public Number aggregate(List<Number> values) {
		if (values == null || values.size() == 0) {
			return 0;
		}
		Number first = values.get(0);
		if (first == null) {
			first = 0;
		}
		double actual = first.doubleValue();
		for (int i = 1; i < values.size(); i++) {
			Number number = values.get(i);
			if (number == null) {
				number = 0;
			}
			actual += number.doubleValue();
		}
		return actual;
	}

}
