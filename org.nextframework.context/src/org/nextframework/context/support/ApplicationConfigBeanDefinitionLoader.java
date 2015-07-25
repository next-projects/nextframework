package org.nextframework.context.support;

import java.util.ArrayList;
import java.util.List;

import org.nextframework.context.BeanDefinitionLoader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.ObjectUtils;

public class ApplicationConfigBeanDefinitionLoader implements BeanDefinitionLoader {

	public static final String DEFAULT_NEXT_CONFIG_LOCATION = "/WEB-INF/applicationConfig.xml";
	public static final String DEFAULT_SPRING_CONFIG_LOCATION = "/WEB-INF/applicationContext.xml";
	
	protected String[] applicationScanPaths;

	@Override
	public void setApplicationScanPaths(String[] applicationScanPaths) {
		this.applicationScanPaths = applicationScanPaths;
	}
	
	@Override
	public void loadBeanDefinitions(AbstractApplicationContext applicationContext, DefaultListableBeanFactory beanFactory) {
		String[] configLocations = getConfigLocations(applicationContext);
		if(!ObjectUtils.isEmpty(configLocations)){
			XmlBeanDefinitionReader xmlReader = createXMLBeanDefinitionReader(applicationContext, beanFactory);
			for (String path : configLocations) {
				xmlReader.loadBeanDefinitions(path);
			}
		}
	}
	
	protected XmlBeanDefinitionReader createXMLBeanDefinitionReader(AbstractApplicationContext applicationContext, DefaultListableBeanFactory beanFactory) {
		// Create a new XmlBeanDefinitionReader for the given BeanFactory.
		XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

		// Configure the bean definition reader with this context's
		// resource loading environment.
		beanDefinitionReader.setEnvironment(applicationContext.getEnvironment());
		beanDefinitionReader.setResourceLoader(applicationContext);
		beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(applicationContext));
		return beanDefinitionReader;
	}
	
	protected String[] getConfigLocations(AbstractApplicationContext applicationContext) {
		List<String> defaultConfigLocations = new ArrayList<String>();
		if(applicationContext.getResource(DEFAULT_NEXT_CONFIG_LOCATION).exists()){
			defaultConfigLocations.add(DEFAULT_NEXT_CONFIG_LOCATION);
		}
		if(applicationContext.getResource(DEFAULT_SPRING_CONFIG_LOCATION).exists()){
			defaultConfigLocations.add(DEFAULT_SPRING_CONFIG_LOCATION);
		}
		return defaultConfigLocations.toArray(new String[defaultConfigLocations.size()]);
	}

}
