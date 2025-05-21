package org.nextframework.summary.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.nextframework.summary.aggregator.Aggregator;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("rawtypes")
public @interface Variable {

	/**
	 * The type of the calculation that the variable must use.
	 * <BR><BR>
	 * (Note: When using one of the predefined calculations, the property customAggregator must not be used)
	 * @return
	 */
	CalculationType calculation() default CalculationType.NONE;

	/**
	 * Custom aggregator when calculation property is not used.
	 * @return
	 */
	Class<? extends Aggregator> customAggregator() default Aggregator.class;

	Scope scope() default Scope.ROW;

	String scopeGroup() default "";

	String incrementOnGroupChange() default "";

}
