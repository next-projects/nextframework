package org.nextframework.summary.definition;

import org.nextframework.summary.Summary;
import org.nextframework.summary.annotations.Scope;

public interface SummaryItemDefinition {

	String getName();

	Class<?> getType();

	Object getValue(Summary<?> summary);

	Scope getScope();

	String getScopeGroup();

}
