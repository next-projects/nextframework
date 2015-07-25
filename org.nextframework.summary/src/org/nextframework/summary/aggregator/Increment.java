package org.nextframework.summary.aggregator;

@SuppressWarnings("all")
public class Increment<E extends Incrementable> implements Aggregator<E> {

	@Override
	public E aggreagte(E n1, E n2) {
		if(n1 == null && n2 != null){
			return n2;
		}
		if(n1 != null && n2 == null){
			return n1;
		}
		if(n1 == null && n2 == null){
			return null;
		}
		return (E) n1.add(n2);
	}

}
