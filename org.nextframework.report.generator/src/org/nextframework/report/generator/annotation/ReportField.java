package org.nextframework.report.generator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ReportField {

	String[] usingFields() default {};

	boolean column() default true;

	boolean filter() default false;

	boolean requiredFilter() default false;

	int suggestedWidth() default 0;

}
