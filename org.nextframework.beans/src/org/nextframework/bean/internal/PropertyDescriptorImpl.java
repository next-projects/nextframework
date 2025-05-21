package org.nextframework.bean.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.bean.annotation.DisplayName;
import org.springframework.util.StringUtils;

public class PropertyDescriptorImpl implements PropertyDescriptor {

	private java.beans.PropertyDescriptor internalPropertyDescriptor;
	private Object bean;

	public PropertyDescriptorImpl(java.beans.PropertyDescriptor internalPropertyDescriptor, Object bean) {
		this.internalPropertyDescriptor = internalPropertyDescriptor;
		this.bean = bean;
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
		return internalPropertyDescriptor.getName();
	}

	@Override
	public Type getType() {
		Method readMethod = internalPropertyDescriptor.getReadMethod();
		if (readMethod != null) {
			return readMethod.getGenericReturnType();
		}
		Method writeMethod = internalPropertyDescriptor.getWriteMethod();
		if (writeMethod != null) {
			return writeMethod.getGenericParameterTypes()[0];
		}
		return internalPropertyDescriptor.getPropertyType();
	}

	@Override
	public Class<?> getRawType() {
		return internalPropertyDescriptor.getPropertyType();
	}

	@Override
	public Class<?> getOwnerClass() {
		return internalPropertyDescriptor.getReadMethod().getDeclaringClass();
	}

	@Override
	public Object getValue() {
		if (bean == null) {
			return null;
		}
		try {
			Method readMethod = internalPropertyDescriptor.getReadMethod();
			if (readMethod != null) {
				readMethod.setAccessible(true);
				return readMethod.invoke(bean);
			} else {
				throw new Exception("No reader method for property " + internalPropertyDescriptor.getName() + " on class " + bean.getClass());
			}
		} catch (Exception e) {
			throw new RuntimeException("Cannot read bean property " + getName(), e);
		}
	}

	@Override
	public String toString() {
		return "Property " + getName() + " of type " + getType();
	}

}
