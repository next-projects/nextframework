package org.nextframework.test.context;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.nextframework.context.factory.annotation.QualifyProperties;
import org.springframework.beans.factory.annotation.Qualifier;

@Qualifier("ts1")
@QualifyProperties
@Retention(RetentionPolicy.RUNTIME)
public @interface TS1 {

}
