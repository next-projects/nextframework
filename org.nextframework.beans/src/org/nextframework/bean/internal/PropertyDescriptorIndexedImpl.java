package org.nextframework.bean.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.nextframework.bean.PropertyDescriptor;

public class PropertyDescriptorIndexedImpl implements PropertyDescriptor {

	String name;

	Type type;

	Object value;

	public PropertyDescriptorIndexedImpl(String name, Type type, Object value) {
		this.name = name;
		this.type = type;
		this.value = value;
	}

	@Override
	public <E extends Annotation> E getAnnotation(Class<E> annotationClass) {
		return null;
	}

	@Override
	public Annotation[] getAnnotations() {
		return new Annotation[0];
	}

	@Override
	public String getDisplayName() {
		return name;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Class<?> getRawType() {
		return convertToRawType(type);
	}

	private Class<?> convertToRawType(Type type) {
		if (type instanceof Class<?>) {
			return (Class<?>) type;
		}
		if (type instanceof ParameterizedType) {
			return convertToRawType((ParameterizedType) type);
		}
		throw new RuntimeException("Cannot determine raw type of " + this.type);
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Class<?> getOwnerClass() {
		return null;
	}

	@Override
	public Object getValue() {
		return value;
	}

}
