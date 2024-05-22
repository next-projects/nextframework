package org.nextframework.context.factory.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.beans.factory.annotation.Qualifier;

/**
 * This annotation will qualify all the properties of the target class. 
 * Using it has the same effect of putting a @Qualifier in all properties (when the value is default).
 * <BR>
 * The type sets the qualifier annotation that should be set on properties.
 * <BR>
 * It is MANDATORY that the annotated target has one annotation of the same type as the value of this annotation.
 * 
 * Example:
 * <pre>
 * {@literal @}Qualifier("qX")
 * {@literal @}QualifyProperties
 * public class X {
 * }
 * 
 * {@literal @}MyQualifier
 * {@literal @}QualifyProperties(MyQualifier.class) // the properties will be qualified with @MyQualifier
 * public class X {
 * }
 * </pre>
 * 
 * @author rogelgarcia
 *
 */
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface QualifyProperties {

	Class<? extends Annotation> value() default Qualifier.class;

}
