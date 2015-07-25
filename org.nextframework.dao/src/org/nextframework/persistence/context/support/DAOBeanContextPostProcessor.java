package org.nextframework.persistence.context.support;

import java.lang.annotation.Annotation;

import org.nextframework.persistence.DAO;
import org.nextframework.persistence.PersistenceConfiguration;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class DAOBeanContextPostProcessor implements BeanPostProcessor, BeanFactoryAware {

	private ConfigurableListableBeanFactory beanFactory;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		if(bean instanceof DAO<?>){
			//it's a DAO, let's configure the context with the qualifiers
			String persistenceContext = PersistenceConfiguration.DEFAULT_CONFIG;
			Qualifier qualifier = getQualifierAnnotation(Qualifier.class, bean);
			if(qualifier != null){
				persistenceContext = qualifier.value();
			}
			((DAO<?>)bean).setPersistenceContext(persistenceContext);
		}
		return bean;
	}

	private static <X> X getQualifierAnnotation(Class<X> qualififerAnnotation, Object bean) {
		Class<? extends Object> beanClass = bean.getClass();
		return getQualifierAnnotation(qualififerAnnotation, beanClass);
	}

	@SuppressWarnings("unchecked")
	public static <X> X getQualifierAnnotation(Class<X> qualififerAnnotation, Class<? extends Object> beanClass) {
		Annotation[] annotations = beanClass.getAnnotations();
		for (Annotation annotation : annotations) {
			if(qualififerAnnotation.isAssignableFrom(annotation.annotationType())){
				return (X) annotation;
			}
			Annotation[] annotations2 = annotation.annotationType().getAnnotations();
			for (Annotation annotation2 : annotations2) {
				if(qualififerAnnotation.isAssignableFrom(annotation2.annotationType())){
					return (X) annotation2;
				}
			}
		}
		return null;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
