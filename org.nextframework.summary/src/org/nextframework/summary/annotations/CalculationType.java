package org.nextframework.summary.annotations;

import org.nextframework.summary.aggregator.Aggregator;
import org.nextframework.summary.aggregator.Average;
import org.nextframework.summary.aggregator.AverageNN;
import org.nextframework.summary.aggregator.Increment;
import org.nextframework.summary.aggregator.Max;
import org.nextframework.summary.aggregator.Min;
import org.nextframework.summary.aggregator.Sum;

public enum CalculationType {

	NONE(null),
	SUM(Sum.class),
	MAX(Max.class),
	MIN(Min.class),
	AVERAGE(Average.class),
	AVERAGENN(AverageNN.class),
	INCREMENT(Increment.class);

	private Class<? extends Aggregator<?>> aggregatorClass;

	public Class<? extends Aggregator<?>> getAggregatorClass() {
		return aggregatorClass;
	}

	private <Z extends Aggregator<?>> CalculationType(Class<Z> aggregator) {
		this.aggregatorClass = aggregator;
	}

}
