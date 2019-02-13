package org.nextframework.controller.json;

import java.util.List;

public interface JsonTranslator {

	String toJson(Object o);

	<E> E fromJson(String json, Class<E> type);

	<E> List<E> fromJsonAsList(String json, Class<E> type);

}