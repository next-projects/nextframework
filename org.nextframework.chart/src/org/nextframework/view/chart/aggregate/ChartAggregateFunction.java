/**
 * 
 */
package org.nextframework.view.chart.aggregate;

import java.util.List;

public interface ChartAggregateFunction {

	Number aggregate(List<Number> values);

}
