package org.nextframework.report.generator.mvc.resource;

import org.stjs.javascript.Map;

public interface SelectView extends Selectable {

	public void select(String name, Map<String, Object> properties);

	public void unselect(String name);

}