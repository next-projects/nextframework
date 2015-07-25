package org.nextframework.context;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;

public interface BeanDefinitionLoader {
	
	String LOG_NAME = BeanDefinitionLoader.class.getName();

	void loadBeanDefinitions(AbstractApplicationContext applicationContext, DefaultListableBeanFactory beanFactory);

	void setApplicationScanPaths(String[] applicationScanPaths);
}
