package org.nextframework.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nextframework.service.StaticServiceProvider;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.StringUtils;

/**
 * Loader used for testing purposes.. Allows explicitly setting the classes to load
 * @author rogelgarcia
 *
 */
public class StaticBeanDefinitionLoader implements BeanDefinitionLoader {

	private static StaticBeanDefinitionLoader beanDefinitionLoader;

	public static StaticBeanDefinitionLoader getInstance() {
		if (beanDefinitionLoader == null) {
			beanDefinitionLoader = new StaticBeanDefinitionLoader();
			StaticServiceProvider.registerService(BeanDefinitionLoader.class, beanDefinitionLoader);
		}
		return beanDefinitionLoader;
	}

	Map<String, BeanDefinition> beans = new HashMap<String, BeanDefinition>();

	public void addBeanForClass(Class<?> clazz) {
		AnnotatedGenericBeanDefinition annotatedGenericBeanDefinition = new AnnotatedGenericBeanDefinition(clazz);
		String beanName = StringUtils.uncapitalize(clazz.getSimpleName());
		beans.put(beanName, annotatedGenericBeanDefinition);
	}

	@Override
	public void loadBeanDefinitions(AbstractApplicationContext applicationContext, DefaultListableBeanFactory beanFactory) {
		Set<String> keySet = beans.keySet();
		for (String beanName : keySet) {
			beanFactory.registerBeanDefinition(beanName, beans.get(beanName));
		}
	}

	@Override
	public void setApplicationScanPaths(String[] applicationScanPaths) {
	}

}
