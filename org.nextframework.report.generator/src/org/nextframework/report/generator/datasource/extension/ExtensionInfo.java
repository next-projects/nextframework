package org.nextframework.report.generator.datasource.extension;

import java.lang.reflect.Method;

public class ExtensionInfo {

	Class<?> classToExtend;
	Class<?> classExtended;

	Object service;

	Method method;

	public Class<?> getClassToExtend() {
		return classToExtend;
	}

	public Class<?> getClassExtended() {
		return classExtended;
	}

	public Object getService() {
		return service;
	}

	public Method getMethod() {
		return method;
	}

	public void setClassToExtend(Class<?> classToExtend) {
		this.classToExtend = classToExtend;
	}

	public void setClassExtended(Class<?> classExtended) {
		this.classExtended = classExtended;
	}

	public void setService(Object service) {
		this.service = service;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

}
