package org.nextframework.bean.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

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
		// Merge annotations from field and getter (field annotations first, getter can override)
		Map<Class<?>, Annotation> merged = new LinkedHashMap<>();

		Field field = getField();
		if (field != null) {
			for (Annotation annotation : field.getAnnotations()) {
				merged.put(annotation.annotationType(), annotation);
			}
		}

		Method readMethod = internalPropertyDescriptor.getReadMethod();
		if (readMethod != null) {
			for (Annotation annotation : readMethod.getAnnotations()) {
				merged.put(annotation.annotationType(), annotation);
			}
		}

		return merged.values().toArray(new Annotation[0]);
	}

	@Override
	public <E extends Annotation> E getAnnotation(Class<E> annotationClass) {
		// Check getter first, then field
		Method readMethod = internalPropertyDescriptor.getReadMethod();
		if (readMethod != null) {
			E annotation = readMethod.getAnnotation(annotationClass);
			if (annotation != null) {
				return annotation;
			}
		}

		Field field = getField();
		if (field != null) {
			return field.getAnnotation(annotationClass);
		}

		return null;
	}

	/**
	 * Gets the field for the base property name, searching through the class hierarchy.
	 */
	private Field getField() {
		// Use base property name from descriptor (not the indexed name)
		String fieldName = internalPropertyDescriptor.getName();
		Class<?> clazz = getOwnerClassSafe();
		if (clazz == null) {
			return null;
		}

		// Search through class hierarchy for the field
		while (clazz != null && clazz != Object.class) {
			try {
				Field field = clazz.getDeclaredField(fieldName);
				field.setAccessible(true);
				return field;
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}

		return null;
	}

	/**
	 * Gets the owner class without throwing if read method is null.
	 */
	private Class<?> getOwnerClassSafe() {
		Method readMethod = internalPropertyDescriptor.getReadMethod();
		if (readMethod != null) {
			return readMethod.getDeclaringClass();
		}
		Method writeMethod = internalPropertyDescriptor.getWriteMethod();
		if (writeMethod != null) {
			return writeMethod.getDeclaringClass();
		}
		return null;
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
