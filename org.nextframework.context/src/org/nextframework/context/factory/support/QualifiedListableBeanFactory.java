package org.nextframework.context.factory.support;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.nextframework.context.AutowireCandidateFilter;
import org.nextframework.context.factory.annotation.QualifyProperties;
import org.nextframework.service.ServiceFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.util.ClassUtils;

public class QualifiedListableBeanFactory extends org.springframework.beans.factory.support.DefaultListableBeanFactory {

	protected AutowireCandidateFilter[] matchers = ServiceFactory.loadServices(AutowireCandidateFilter.class);

	protected QualifyPropertiesHelper qualifyPropertiesResolver = new QualifyPropertiesHelper();

	public QualifiedListableBeanFactory() {
		super();
	}

	public QualifiedListableBeanFactory(BeanFactory parentBeanFactory) {
		super(parentBeanFactory);
	}

	@Override
	protected Map<String, Object> findAutowireCandidates(String beanName, Class<?> requiredType, DependencyDescriptor descriptor) {
		//verify the @QualifyProperties

		descriptor = qualifyPropertiesResolver.checkDescriptorForQualifiedProperties(getType(beanName), beanName, descriptor);

		Map<String, Object> candidateBeans = super.findAutowireCandidates(beanName, requiredType, descriptor);
		for (AutowireCandidateFilter autowireCandidateMatcher : matchers) {
			if (candidateBeans.size() <= 1) {
				return candidateBeans;
			}
			candidateBeans = autowireCandidateMatcher.filterAutowireCandidates(this, beanName, candidateBeans, descriptor);
		}
		return candidateBeans;
	}

	protected static class QualifyPropertiesHelper {

		protected DependencyDescriptor checkDescriptorForQualifiedProperties(Class<?> beanType, String beanName, DependencyDescriptor descriptor) {
			if (beanType != null) {
				Annotation[] annotations = ClassUtils.getUserClass(beanType).getAnnotations();
				for (Annotation annotation : annotations) {
					if (isQualifyProperties(annotation)) {
						Annotation qualifier = getQualifierAnnotation(beanType, annotation);
						if (qualifier != null) {
							descriptor = getNewDescriptorWithQualifier(descriptor, qualifier);
						}
					}
				}
			}
			return descriptor;
		}

		protected Annotation getQualifierAnnotation(Class<?> beanType, Annotation annotation) {
			if (!annotation.annotationType().isAssignableFrom(QualifyProperties.class)) {
				beanType = annotation.annotationType();
				annotation = annotation.annotationType().getAnnotation(QualifyProperties.class);
			}
			return beanType.getAnnotation(((QualifyProperties) annotation).value());
		}

		protected DependencyDescriptor getNewDescriptorWithQualifier(final DependencyDescriptor _descriptor, final Annotation qualifier) {
			DependencyDescriptor descriptor;
			if (_descriptor.getField() != null) {
				descriptor = createDependencyDescriptorForField(_descriptor, qualifier);
			} else {
				descriptor = createDependencyDescriptorForMethodParameter(_descriptor, qualifier);
			}
			return descriptor;
		}

		protected DependencyDescriptor createDependencyDescriptorForMethodParameter(final DependencyDescriptor _descriptor, final Annotation qualifier) {
			DependencyDescriptor descriptor;
			descriptor = new DependencyDescriptor(_descriptor.getMethodParameter(), _descriptor.isRequired(), _descriptor.isEager()) {

				private static final long serialVersionUID = 1L;

				@Override
				public Annotation[] getAnnotations() {
					return addQualifier(qualifier, super.getAnnotations());
				}

			};
			return descriptor;
		}

		protected DependencyDescriptor createDependencyDescriptorForField(final DependencyDescriptor _descriptor, final Annotation qualifier) {
			DependencyDescriptor descriptor;
			descriptor = new DependencyDescriptor(_descriptor.getField(), _descriptor.isRequired(), _descriptor.isEager()) {

				private static final long serialVersionUID = 1L;

				@Override
				public Annotation[] getAnnotations() {
					return addQualifier(qualifier, super.getAnnotations());
				}

			};
			return descriptor;
		}

		protected Annotation[] addQualifier(Annotation qualifier, Annotation[] originalArray) {
			Annotation[] newArray = new Annotation[originalArray.length + 1];
			System.arraycopy(originalArray, 0, newArray, 0, originalArray.length);
			newArray[newArray.length - 1] = qualifier;
			return newArray;
		}

		public boolean isQualifyProperties(Annotation annotation) {
			return annotation.annotationType().isAssignableFrom(QualifyProperties.class)
					|| annotation.annotationType().isAnnotationPresent(QualifyProperties.class);
		}

	}

}
