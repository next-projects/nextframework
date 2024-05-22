/**
 * 
 */
package org.nextframework.view.chart.aggregate;

import java.util.List;

public class ChartAverageNotNullAggregateFunction implements ChartAggregateFunction {

	public Number aggregate(List<Number> values) {
		if (values == null || values.size() == 0) {
			return 0;
		}
		double sum = 0;
		int count = 0;
		for (Number number : values) {
			if (number == null) {
				continue;
			}
			count++;
			sum += number.doubleValue();
		}
		return sum / count;
	}

}