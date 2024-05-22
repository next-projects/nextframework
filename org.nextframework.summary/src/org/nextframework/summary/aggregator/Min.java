package org.nextframework.summary.aggregator;

public class Min<E extends Number> implements Aggregator<E> {

	@SuppressWarnings("unchecked")
	public E aggreagte(E n1, E n2) {
		if (n1 == null) {
			return n2;
		}
		if (n2 == null) {
			return n1;
		}
		if (n1 instanceof Byte) {
			return (E) new Byte((byte) Math.min(n1.byteValue(), n2.byteValue()));
		}
		if (n1 instanceof Short) {
			return (E) new Short((short) Math.min(n1.shortValue(), n2.shortValue()));
		}
		if (n1 instanceof Integer) {
			return (E) new Integer(Math.min(n1.intValue(), n2.intValue()));
		}
		if (n1 instanceof Long) {
			return (E) new Long(Math.min(n1.longValue(), n2.longValue()));
		}
		if (n1 instanceof Float) {
			return (E) new Float((float) Math.min(n1.floatValue(), n2.floatValue()));
		}
		if (n1 instanceof Double) {
			return (E) new Double(Math.min(n1.doubleValue(), n2.doubleValue()));
		}
		throw new IllegalArgumentException("Could not agrregate min of " + n1 + " and " + n2 + ". Unknown class " + n1.getClass());
	}

}