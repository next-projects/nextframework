package org.nextframework.js;


public abstract class NextNumbers {
	
	public abstract String formatDecimal(Number number, Integer precision, String separatorDecimal, String separatorThousands);
	public abstract String formatDecimal(Number number, Integer precision);
	public abstract String formatDecimal(Number number);
	
}