package org.nextframework.summary.aggregator;

import java.lang.reflect.InvocationTargetException;


public class Sum<E extends Number> implements Aggregator<E> {

	@SuppressWarnings("unchecked")
	public E aggreagte(E n1, E n2) {
		if(n1 == null){
			return n2;
		}
		if(n2 == null){
			return n1;
		}
		if(n1 instanceof Byte){
			return (E) new Byte((byte) (n1.byteValue() + n2.byteValue()));
		}
		if(n1 instanceof Short){
			return (E) new Short((short) (n1.shortValue() + n2.shortValue()));
		}
		if(n1 instanceof Integer){
			return (E) new Integer((n1.intValue() + n2.intValue()));
		}
		if(n1 instanceof Long){
			return (E) new Long((n1.longValue() + n2.longValue()));	
		}
		if(n1 instanceof Float){
			return (E) new Float((float) (n1.floatValue() + n2.floatValue()));
		}
		if(n1 instanceof Double){
			return (E) new Double( (n1.doubleValue() + n2.doubleValue()));
		}
		try {
			return (E) n1.getClass().getConstructor(Double.class).newInstance(new Double( (n1.doubleValue() + n2.doubleValue())));
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Could not agrregate sum of "+n1+" and "+n2+". ", e);
		} catch (SecurityException e) {
			throw new IllegalArgumentException("Could not agrregate sum of "+n1+" and "+n2+". ", e);
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Could not agrregate sum of "+n1+" and "+n2+". ", e);
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Could not agrregate sum of "+n1+" and "+n2+". ", e);
		} catch (InvocationTargetException e) {
			throw new IllegalArgumentException("Could not agrregate sum of "+n1+" and "+n2+". ", e);
		} catch (NoSuchMethodException e) {
			try {
				return (E) n1.getClass().getConstructor(double.class).newInstance(new Double( (n1.doubleValue() + n2.doubleValue())));
			} catch (IllegalArgumentException e1) {
				throw new IllegalArgumentException("Could not agrregate sum of "+n1+" and "+n2+". ", e);
			} catch (SecurityException e1) {
				throw new IllegalArgumentException("Could not agrregate sum of "+n1+" and "+n2+". ", e);
			} catch (InstantiationException e1) {
				throw new IllegalArgumentException("Could not agrregate sum of "+n1+" and "+n2+". ", e);
			} catch (IllegalAccessException e1) {
				throw new IllegalArgumentException("Could not agrregate sum of "+n1+" and "+n2+". ", e);
			} catch (InvocationTargetException e1) {
				throw new IllegalArgumentException("Could not agrregate sum of "+n1+" and "+n2+". ", e);
			} catch (NoSuchMethodException e1) {
				throw new IllegalArgumentException("Could not agrregate sum of "+n1+" and "+n2+". There should be a constructor with argument Double or double in class "+n1.getClass().getName(), e);
			}
		}
	}
}
