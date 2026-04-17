package org.nextframework.bean.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.bean.annotation.DisplayName;
import org.springframework.util.StringUtils;

public class PropertyDescriptorImpl implements PropertyDescriptor {

	private static final ConcurrentMap<FieldCacheKey, Optional<Field>> FIELD_CACHE = new ConcurrentHashMap<>();

	private java.beans.PropertyDescriptor internalPropertyDescriptor;
	private Object bean;
	private transient volatile Annotation[] annotationsCache;
	private transient volatile boolean fieldResolved;
	private transient Field fieldCache;

	public PropertyDescriptorImpl(java.beans.PropertyDescriptor internalPropertyDescriptor, Object bean) {
		this.internalPropertyDescriptor = internalPropertyDescriptor;
		this.bean = bean;
	}

	@Override
	public Annotation[] getAnnotations() {

		Annotation[] cachedAnnotations = annotationsCache;
		if (cachedAnnotations != null) {
			return cachedAnnotations;
		}

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

		Annotation[] resolvedAnnotations = merged.values().toArray(new Annotation[0]);
		annotationsCache = resolvedAnnotations;

		return resolvedAnnotations;
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
	 * Gets the field for this property, searching through the class hierarchy.
	 * Handles private/protected fields by searching declared fields.
	 */
	private Field getField() {

		if (fieldResolved) {
			return fieldCache;
		}

		String fieldName = getName();
		Class<?> clazz = getOwnerClassSafe();
		if (clazz == null) {
			fieldResolved = true;
			return null;
		}

		FieldCacheKey cacheKey = new FieldCacheKey(clazz, fieldName);
		Field resolvedField = FIELD_CACHE.computeIfAbsent(cacheKey, PropertyDescriptorImpl::findField).orElse(null);
		fieldCache = resolvedField;
		fieldResolved = true;

		return resolvedField;
	}

	private static Optional<Field> findField(FieldCacheKey cacheKey) {
		Class<?> clazz = cacheKey.ownerClass;
		while (clazz != null && clazz != Object.class) {
			try {
				Field field = clazz.getDeclaredField(cacheKey.fieldName);
				try {
					field.setAccessible(true);
				} catch (SecurityException e) {
					// Best effort only; reading annotations does not require accessibility.
				}
				return Optional.of(field);
			} catch (NoSuchFieldException e) {
				clazz = clazz.getSuperclass();
			}
		}
		return Optional.empty();
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

	private static final class FieldCacheKey {

		private final Class<?> ownerClass;
		private final String fieldName;

		private FieldCacheKey(Class<?> ownerClass, String fieldName) {
			this.ownerClass = ownerClass;
			this.fieldName = fieldName;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof FieldCacheKey)) {
				return false;
			}
			FieldCacheKey other = (FieldCacheKey) obj;
			return ownerClass.equals(other.ownerClass) && fieldName.equals(other.fieldName);
		}

		@Override
		public int hashCode() {
			return 31 * ownerClass.hashCode() + fieldName.hashCode();
		}

	}

}
