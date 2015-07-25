/**
 * 
 */
package org.nextframework.view.chart.aggregate;

import java.util.List;

public class ChartAverageAggregateFunction implements ChartAggregateFunction{
	public Number aggregate(List<Number> values) {
		if(values == null || values.size() == 0){
			return 0;
		}
		double sum = 0;
		for (Number number : values) {
			if(number == null){
				number = 0;
			}
			sum += number.doubleValue();
		}
		return sum / values.size();
	}
}