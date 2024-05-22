package org.nextframework.service.context.support;

import org.nextframework.context.support.CustomScannerBeanDefinitionLoader;
import org.nextframework.persistence.DAO;
import org.nextframework.service.GenericService;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.type.filter.AssignableTypeFilter;

public class ServiceBeanDefinitionLoader extends CustomScannerBeanDefinitionLoader {

	@Override
	public void applyFilters(ClassPathBeanDefinitionScanner scanner) {
		setAutowireBeans(scanner, AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		scanner.addIncludeFilter(new AssignableTypeFilter(GenericService.class));
	}

	@Override
	public void postProcessBeanDefinition(DefaultListableBeanFactory beanFactory, AbstractBeanDefinition beanDefinition, String beanName) {
		AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(DAO.EntityType.class);
		try {
			Class<?> serviceClass = Class.forName(beanDefinition.getBeanClassName());
			qualifier.setAttribute(AutowireCandidateQualifier.VALUE_KEY, GenericTypeResolver.resolveTypeArgument(serviceClass, GenericService.class));
			beanDefinition.addQualifier(qualifier);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "Service Loader";
	}

}