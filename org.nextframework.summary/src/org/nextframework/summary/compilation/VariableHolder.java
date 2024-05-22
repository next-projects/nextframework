package org.nextframework.summary.compilation;

import org.nextframework.summary.aggregator.Aggregator;
import org.nextframework.summary.aggregator.GetLast;

public class VariableHolder<E> {

	public VariableHolder(Aggregator<E> aggregator) {
		super();
		this.aggregator = aggregator;
	}

	Aggregator<E> aggregator = new GetLast<E>();

	E value;

	public VariableHolder() {

	}

	public void setAggregator(Aggregator<E> aggregator) {
		this.aggregator = aggregator;
	}

	public Aggregator<E> getAggregator() {
		return aggregator;
	}

	public VariableHolder(E value) {
		this.value = value;
	}

	public void setValue(E value) {
		this.value = getAggregator().aggreagte(this.getValue(), value);
	}

	public E getValue() {
		return value;
	}

	@Override
	public String toString() {
		return String.valueOf(getValue());
	}

}
