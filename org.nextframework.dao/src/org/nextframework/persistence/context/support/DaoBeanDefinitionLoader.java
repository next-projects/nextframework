package org.nextframework.persistence.context.support;

import java.util.ArrayList;
import java.util.List;

import org.nextframework.context.support.CustomScannerBeanDefinitionLoader;
import org.nextframework.persistence.DAO;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.type.filter.AssignableTypeFilter;

public class DaoBeanDefinitionLoader extends CustomScannerBeanDefinitionLoader {
	//TODO CHECK QUALIFIERS FOR HibernateTemplate, sessionFactory, jdbcTemplate.. DAOs get wrong beans when there are multiple connections

	@Override
	public void loadBeanDefinitions(AbstractApplicationContext applicationContext, DefaultListableBeanFactory beanFactory) {
		super.loadBeanDefinitions(applicationContext, beanFactory);

		GenericBeanDefinition persistenceConfigBD = new GenericBeanDefinition();
		persistenceConfigBD.setBeanClassName(DAOBeanContextPostProcessor.class.getName());
		beanFactory.registerBeanDefinition(DAOBeanContextPostProcessor.class.getSimpleName(), persistenceConfigBD);
	}
	
	@Override
	public void applyFilters(ClassPathBeanDefinitionScanner scanner) {
		setAutowireBeans(scanner, AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		scanner.addIncludeFilter(new AssignableTypeFilter(DAO.class));
	}

	@Override
	public void postProcessBeanDefinition(DefaultListableBeanFactory beanFactory, AbstractBeanDefinition beanDefinition, String beanName) {
		AutowireCandidateQualifier qualifier = new AutowireCandidateQualifier(DAO.EntityType.class);
		try {
			Class<?> daoClass = Class.forName(beanDefinition.getBeanClassName());
			if(!daoClass.isAnnotationPresent(DAO.NoGenericServiceInjection.class)){
				qualifier.setAttribute(AutowireCandidateQualifier.VALUE_KEY, GenericTypeResolver.resolveTypeArgument(daoClass, DAO.class));
				beanDefinition.addQualifier(qualifier);
			}
			//DAOs must be configured after the persistence configurations
			beanDefinition.setDependsOn(getPersistenceConfigBeans(beanFactory));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private String[] persistenceConfigBeansCache;
	public String[] getPersistenceConfigBeans(DefaultListableBeanFactory beanFactory) {
		if(persistenceConfigBeansCache != null){
			return persistenceConfigBeansCache;
		}
		String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
		List<String> persistenceConfigs = new ArrayList<String>();
		for (String bdn : beanDefinitionNames) {
			if(bdn.startsWith("persistenceConfig")){
				persistenceConfigs.add(bdn);
			}
		}
		this.persistenceConfigBeansCache = persistenceConfigs.toArray(new String[persistenceConfigs.size()]);
		return persistenceConfigBeansCache;
	}

	@Override
	public String toString() {
		return "DAO Loader";
	}
}
