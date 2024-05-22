package org.nextframework.view.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.nextframework.controller.ServletRequestDataBinderNext;
import org.nextframework.view.TagUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

public class CompositePKStringfyBuilder {

	private static final char OPEN_ATTRS = ServletRequestDataBinderNext.VALUE_OBJECT_OPEN_CLASS_ATTR;
	private static final char CLOSE_ATTRS = ServletRequestDataBinderNext.VALUE_OBJECT_CLOSE_CLASS_ATTR;
	private static final char PROPERTY_SEPARATOR = ServletRequestDataBinderNext.VALUE_OBJECT_ATTR_SEPARATOR;

	private Object target;

	Map<String, Object> properties = new HashMap<String, Object>();

	public CompositePKStringfyBuilder target(Object target) {
		this.target = target;
		return this;
	}

	public CompositePKStringfyBuilder property(String name, Object value) {
		properties.put(name, value);
		return this;
	}

	@Override
	public String toString() {
		return target.getClass().getName() + OPEN_ATTRS + stringfyProperties() + CLOSE_ATTRS;
	}

	private String stringfyProperties() {
		Set<String> keySet = properties.keySet();
		StringBuilder builder = new StringBuilder();
		for (Iterator<String> iterator = keySet.iterator(); iterator.hasNext();) {
			String property = iterator.next();
			builder.append(property).append("=");
			ConversionService conversionService = getConversionService();
			builder.append(conversionService.convert(properties.get(property), String.class));
			if (iterator.hasNext()) {
				builder.append(PROPERTY_SEPARATOR);
			}
		}
		return builder.toString();
	}

	protected ConversionService getConversionService() {
		return new ConversionService() {

			@Override
			public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
				return TagUtils.getObjectValueToString(source);
			}

			@Override
			@SuppressWarnings("unchecked")
			public <T> T convert(Object source, Class<T> targetType) {
				return (T) TagUtils.getObjectValueToString(source);
			}

			@Override
			public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
				return targetType.getType().equals(String.class);
			}

			@Override
			public boolean canConvert(Class<?> sourceType, Class<?> targetType) {
				return targetType.equals(String.class);
			}

		};
	}

}
