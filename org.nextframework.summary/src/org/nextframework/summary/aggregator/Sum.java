package org.nextframework.summary.aggregator;

import java.lang.reflect.InvocationTargetException;

public class Sum<E extends Number> implements Aggregator<E> {

	@SuppressWarnings("unchecked")
	public E aggreagte(E n1, E n2) {
		if (n1 == null) {
			return n2;
		}
		if (n2 == null) {
			return n1;
		}
		if (n1 instanceof Byte) {
			return (E) Byte.valueOf((byte) (n1.byteValue() + n2.byteValue()));
		}
		if (n1 instanceof Short) {
			return (E) Short.valueOf((short) (n1.shortValue() + n2.shortValue()));
		}
		if (n1 instanceof Integer) {
			return (E) Integer.valueOf((n1.intValue() + n2.intValue()));
		}
		if (n1 instanceof Long) {
			return (E) Long.valueOf((n1.longValue() + n2.longValue()));
		}
		if (n1 instanceof Float) {
			return (E) Float.valueOf((float) (n1.floatValue() + n2.floatValue()));
		}
		if (n1 instanceof Double) {
			return (E) Double.valueOf((n1.doubleValue() + n2.doubleValue()));
		}
		try {
			return (E) n1.getClass().getConstructor(Double.class).newInstance(Double.valueOf((n1.doubleValue() + n2.doubleValue())));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Could not agrregate sum of " + n1 + " and " + n2 + ". ", e);
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Could not agrregate sum of " + n1 + " and " + n2 + ". ", e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Could not agrregate sum of " + n1 + " and " + n2 + ". ", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Could not agrregate sum of " + n1 + " and " + n2 + ". ", e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Could not agrregate sum of " + n1 + " and " + n2 + ". ", e);
		} catch (NoSuchMethodException e) {
			try {
				return (E) n1.getClass().getConstructor(double.class).newInstance(Double.valueOf((n1.doubleValue() + n2.doubleValue())));
			} catch (IllegalArgumentException e1) {
				throw new IllegalArgumentException("Could not agrregate sum of " + n1 + " and " + n2 + ". ", e);
			} catch (SecurityException e1) {
				throw new IllegalArgumentException("Could not agrregate sum of " + n1 + " and " + n2 + ". ", e);
			} catch (InstantiationException e1) {
				throw new IllegalArgumentException("Could not agrregate sum of " + n1 + " and " + n2 + ". ", e);
			} catch (IllegalAccessException e1) {
				throw new IllegalArgumentException("Could not agrregate sum of " + n1 + " and " + n2 + ". ", e);
			} catch (InvocationTargetException e1) {
				throw new IllegalArgumentException("Could not agrregate sum of " + n1 + " and " + n2 + ". ", e);
			} catch (NoSuchMethodException e1) {
				throw new IllegalArgumentException("Could not agrregate sum of " + n1 + " and " + n2 + ". There should be a constructor with argument Double or double in class " + n1.getClass().getName(), e);
			}
		}
	}

}
