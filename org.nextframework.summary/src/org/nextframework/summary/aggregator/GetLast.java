package org.nextframework.summary.aggregator;

public class GetLast<E> implements Aggregator<E> {

	@Override
	public E aggreagte(E n1, E n2) {
		return n2;
	}

}
