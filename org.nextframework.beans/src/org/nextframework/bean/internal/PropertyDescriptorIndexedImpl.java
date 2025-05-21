package org.nextframework.bean.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.bean.annotation.DisplayName;
import org.springframework.util.StringUtils;

public class PropertyDescriptorIndexedImpl implements PropertyDescriptor {

	private java.beans.PropertyDescriptor internalPropertyDescriptor;
	private String name;
	private Type type;
	private Object value;

	public PropertyDescriptorIndexedImpl(java.beans.PropertyDescriptor internalPropertyDescriptor, String name, Type type, Object value) {
		this.internalPropertyDescriptor = internalPropertyDescriptor;
		this.name = name;
		this.type = type;
		this.value = value;
	}

	@Override
	public Annotation[] getAnnotations() {
		if (internalPropertyDescriptor.getReadMethod() == null) {
			return new Annotation[0];
		}
		return internalPropertyDescriptor.getReadMethod().getAnnotations();
	}

	@Override
	public <E extends Annotation> E getAnnotation(Class<E> annotationClass) {
		if (internalPropertyDescriptor.getReadMethod() == null) {
			return null;
		}
		return internalPropertyDescriptor.getReadMethod().getAnnotation(annotationClass);
	}

	@Override
	public String getDisplayName() {
		DisplayName annotation = getAnnotation(DisplayName.class);
		if (annotation != null) {
			return annotation.value();
		}
		String nameOk = StringUtils.capitalize(getName()).replace('_', ' ');
		return AbstractBeanDescriptor.separateOnCase(nameOk);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public Class<?> getRawType() {
		return convertToRawType(type);
	}

	private Class<?> convertToRawType(Type type) {
		if (type instanceof Class<?>) {
			return (Class<?>) type;
		}
		//if (type instanceof ParameterizedType) {
		//	return convertToRawType((ParameterizedType) type); Recursivo??
		//}
		throw new RuntimeException("Cannot determine raw type of " + this.type);
	}

	@Override
	public Class<?> getOwnerClass() {
		return internalPropertyDescriptor.getReadMethod().getDeclaringClass();
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "Indexed property " + getName() + " of type " + getType();
	}

}
