package org.nextframework.bean.internal;

import java.lang.annotation.Annotation;

import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.bean.annotation.DescriptionProperty;
import org.nextframework.exception.NextException;

public class BeanDescriptorImpl extends AbstractBeanDescriptor {

	public BeanDescriptorImpl(Class<?> targetClass) {
		super(targetClass);
	}

	public BeanDescriptorImpl(Object bean) {
		super(bean);
	}

	@Override
	public Object getId() {
		String idPropertyName = getIdPropertyName();
		if (idPropertyName == null) {
			throw new NullPointerException("No id found for " + getTargetClass() + getIdPropertyName());
		}
		return getPropertyDescriptor(idPropertyName).getValue();
	}

	@Override
	public Object getDescription() {
		String descriptionPropertyName = getDescriptionPropertyName();
		if (descriptionPropertyName != null) {
			return getPropertyDescriptor(descriptionPropertyName).getValue();
		}
		return null;
	}

	@Override
	public String getDescriptionPropertyName() {
		String name = null;
		PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors();
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			Annotation[] annotations = propertyDescriptor.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().equals(DescriptionProperty.class)) {
					if (name != null) {
						throw new NextException("Mais de um " + DescriptionProperty.class.getSimpleName() + " foram declarados na classe " + getTargetClass().getSimpleName());
					}
					name = propertyDescriptor.getName();
				}
			}
		}
		return name;
	}

	@Override
	public String getIdPropertyName() {
		PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors();
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			Annotation[] annotations = propertyDescriptor.getAnnotations();
			for (Annotation annotation : annotations) {
				if (annotation.annotationType().getSimpleName().equalsIgnoreCase("id")) {
					return propertyDescriptor.getName();
				}
			}
		}
		return null;
	}

}
