package org.nextframework.controller.json;

public interface JsonTranslator {

	String toJson(Object o);
	
	<E> E fromJson(String json, Class<E> type);
}
