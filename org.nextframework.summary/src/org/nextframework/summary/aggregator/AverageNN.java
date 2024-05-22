package org.nextframework.summary.aggregator;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class AverageNN<E extends Number> implements Aggregator<E> {

	int count = 0;
	List<E> values = new ArrayList<E>();

	@Override
	public E aggreagte(E n1, E n2) {
		if (n2 != null) {
			values.add(n2);
			count++;
		}
		return calculateAverage();
	}

	@SuppressWarnings("all")
	private E calculateAverage() {
		Double sum = 0.0;
		Class type = null;
		for (E e : values) {
			if (e != null) {
				type = e.getClass();
				sum += e.doubleValue();
			}
		}
		double avg = count == 0 ? 0 : sum / count;
		if (type == null) {
			return null;
		}
		if (Byte.class.isAssignableFrom(type)) {
			return (E) new Byte((byte) (avg));
		}
		if (Short.class.isAssignableFrom(type)) {
			return (E) new Short((short) (avg));
		}
		if (Integer.class.isAssignableFrom(type)) {
			return (E) new Integer((int) (avg));
		}
		if (Long.class.isAssignableFrom(type)) {
			return (E) new Long((long) (avg));
		}
		if (Float.class.isAssignableFrom(type)) {
			return (E) new Float((float) (avg));
		}
		if (Double.class.isAssignableFrom(type)) {
			return (E) new Double((double) (avg));
		}
		try {
			return (E) type.getConstructor(Double.class).newInstance(new Double(avg));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Could not agrregate avg of " + type + ". ", e);
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Could not agrregate avg of " + type + ". ", e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Could not agrregate avg of " + type + ". ", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Could not agrregate avg of " + type + ". ", e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Could not agrregate avg of " + type + ". ", e);
		} catch (NoSuchMethodException e) {
			try {
				return (E) type.getConstructor(double.class).newInstance(new Double(avg));
			} catch (IllegalArgumentException e1) {
				throw new IllegalArgumentException("Could not agrregate avg of " + type + ". ", e);
			} catch (SecurityException e1) {
				throw new IllegalArgumentException("Could not agrregate avg of " + type + ". ", e);
			} catch (InstantiationException e1) {
				throw new IllegalArgumentException("Could not agrregate avg of " + type + ". ", e);
			} catch (IllegalAccessException e1) {
				throw new IllegalArgumentException("Could not agrregate avg of " + type + ". ", e);
			} catch (InvocationTargetException e1) {
				throw new IllegalArgumentException("Could not agrregate avg of " + type + ". ", e);
			} catch (NoSuchMethodException e1) {
				throw new IllegalArgumentException("Could not agrregate avg of " + type + ". There should be a constructor with argument Double or double in " + type, e);
			}
		}
	}

}
