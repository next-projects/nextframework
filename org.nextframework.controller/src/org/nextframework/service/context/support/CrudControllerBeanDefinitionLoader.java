package org.nextframework.service.context.support;

import java.util.Arrays;

import org.nextframework.context.support.CustomScannerBeanDefinitionLoader;
import org.nextframework.controller.crud.CrudController;
import org.nextframework.persistence.DAO;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.type.filter.AssignableTypeFilter;

public class CrudControllerBeanDefinitionLoader extends CustomScannerBeanDefinitionLoader {

	static {
		ControllerBeanDefinitionLoader.IGNORE_CONTROLLER_CLASSES.add(CrudController.class);
	}

	@Override
	public void applyFilters(ClassPathBeanDefinitionScanner scanner) {
		setAutowireBeans(scanner, AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		scanner.addIncludeFilter(new AssignableTypeFilter(CrudController.class));
	}

	@Override
	public void postProcessBeanDefinition(DefaultListableBeanFactory beanFactory, AbstractBeanDefinition beanDefinition, String beanName) {
		AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(DAO.EntityType.class);
		try {
			Class<?> crudControllerClass = Class.forName(beanDefinition.getBeanClassName());
			Class<?>[] typeArguments = GenericTypeResolver.resolveTypeArguments(crudControllerClass, CrudController.class);
			if (typeArguments == null || typeArguments.length < 3) {
				throw new IllegalArgumentException("Cannot resolve type argument for " + crudControllerClass + " with " + CrudController.class + " using index " + 2 + ". Found " + Arrays.deepToString(typeArguments));
			}
			qualifier.setAttribute(AutowireCandidateQualifier.VALUE_KEY, typeArguments[2]);
			beanDefinition.addQualifier(qualifier);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}

		ControllerBeanDefinitionLoader.checkControllerDefinition(beanDefinition);
	}

	@Override
	public String toString() {
		return "CrudController Loader";
	}

}