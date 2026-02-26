package org.nextframework.context.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.context.BeanDefinitionLoader;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.support.AbstractApplicationContext;

public class MultipartBeanDefinitionLoader implements BeanDefinitionLoader {

	private static final String MULTIPART_CLASS = "org.nextframework.controller.NextCommonsMultipartResolver";

	protected final Log logger = LogFactory.getLog(LOG_NAME);

	public static int MAX_UPLOAD_SIZE = 20000000;//20Mb

	@Override
	public void loadBeanDefinitions(AbstractApplicationContext applicationContext, DefaultListableBeanFactory beanFactory) {

		try {
			Class.forName(MULTIPART_CLASS);
		} catch (ClassNotFoundException err) {
			//the multipart class is not avaiable
			return;
		}
		String beanName = "multipartResolver";
		if (beanFactory.containsBean(beanName)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Not registering " + beanName + ", already registered.");
			}
			return;
		}

		GenericBeanDefinition multipartResolverBD = new GenericBeanDefinition();
		multipartResolverBD.setBeanClassName(MULTIPART_CLASS);
		multipartResolverBD.setPropertyValues(new MutablePropertyValues());

		beanFactory.registerBeanDefinition(beanName, multipartResolverBD);

		logger.info("Multipart Loader: adding bean [" + beanName + "]");
	}

	@Override
	public void setApplicationScanPaths(String[] applicationScanPaths) {
	}

}
