package org.nextframework.context.support;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.context.BeanDefinitionLoader;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.StringUtils;

public abstract class CustomScannerBeanDefinitionLoader implements BeanDefinitionLoader {

	protected final Log logger = LogFactory.getLog(LOG_NAME);

	protected abstract void applyFilters(ClassPathBeanDefinitionScanner scanner);

	protected abstract void postProcessBeanDefinition(DefaultListableBeanFactory beanFactory, AbstractBeanDefinition beanDefinition, String beanName);

	protected String[] applicationScanPaths;

	@Override
	public void setApplicationScanPaths(String[] applicationScanPaths) {
		this.applicationScanPaths = applicationScanPaths;
	}

	@Override
	public void loadBeanDefinitions(AbstractApplicationContext applicationContext, DefaultListableBeanFactory beanFactory) {
		List<BeanDefinitionHolder> beansList = new ArrayList<BeanDefinitionHolder>();
		ClassPathBeanDefinitionScanner scanner = createScannerForCustomReader(applicationContext, beanFactory, beansList);
		applyFilters(scanner);
		scanner.scan(applicationScanPaths);
		if (logger.isInfoEnabled()) {
			StringBuilder sb = new StringBuilder();
			sb.append(this);
			sb.append(": adding beans [");
			sb.append(StringUtils.arrayToCommaDelimitedString(getBeanDefinitionNames(beansList)));
			sb.append("] ");
			logger.info(sb);
		}
	}

	private Object[] getBeanDefinitionNames(List<BeanDefinitionHolder> beansList) {
		int i = 0;
		String[] beanNames = new String[beansList.size()];
		for (BeanDefinitionHolder beanDefinitionHolder : beansList) {
			beanNames[i++] = beanDefinitionHolder.getBeanName();
		}
		return beanNames;
	}

	protected ClassPathBeanDefinitionScanner createScannerForCustomReader(AbstractApplicationContext applicationContext, final DefaultListableBeanFactory beanFactory, final List<BeanDefinitionHolder> beansDefined) {

		ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanFactory, false, applicationContext.getEnvironment()) {

			@Override
			protected void postProcessBeanDefinition(AbstractBeanDefinition beanDefinition, String beanName) {
				super.postProcessBeanDefinition(beanDefinition, beanName);
				CustomScannerBeanDefinitionLoader.this.postProcessBeanDefinition(beanFactory, beanDefinition, beanName);
			}

			@Override
			protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
				super.registerBeanDefinition(definitionHolder, registry);
				beansDefined.add(definitionHolder);
			}

		};

		//TODO REBUILD THIS (ONLY FOR WEB PROJECTS)
//		ScopeMetadataResolver scopeMetadataResolver = applicationContext.getScopeMetadataResolver();
//		if (scopeMetadataResolver != null) {
//			scanner.setScopeMetadataResolver(scopeMetadataResolver);
//		}

		return scanner;
	}

	//util

	protected void setAutowireBeans(ClassPathBeanDefinitionScanner scanner, int autowireType) {
		BeanDefinitionDefaults beanDefinitionDefaults = new BeanDefinitionDefaults();
		beanDefinitionDefaults.setAutowireMode(autowireType);
		scanner.setBeanDefinitionDefaults(beanDefinitionDefaults);
		scanner.setAutowireCandidatePatterns(new String[] { "*" });
	}

}
