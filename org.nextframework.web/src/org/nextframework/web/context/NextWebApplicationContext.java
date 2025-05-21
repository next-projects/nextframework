package org.nextframework.web.context;

import javax.servlet.ServletContext;

import org.nextframework.context.ApplicationScanPathsProvider;
import org.nextframework.context.BeanDefinitionLoader;
import org.nextframework.context.factory.support.QualifiedListableBeanFactory;
import org.nextframework.service.ServiceFactory;
import org.nextframework.web.service.ServletContextServiceProvider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

/**
 * Enhances Spring AnnotationConfigWebApplicationContext to also read XML files. <BR>
 * The XML reading functionality is copied from XmlWebApplicationContext.<BR>
 * 
 * @see AnnotationConfigWebApplicationContext
 * @see XmlWebApplicationContext
 * @author rogelgarcia
 */
public class NextWebApplicationContext extends AnnotationConfigWebApplicationContext implements ApplicationScanPathsProvider {

	@Override
	public void setServletContext(ServletContext servletContext) {
		super.setServletContext(servletContext);
		ServletContextServiceProvider.registerService(servletContext, ApplicationContext.class, this);
		ServletContextServiceProvider.registerService(servletContext, ConfigurableApplicationContext.class, this);
		ServletContextServiceProvider.registerService(servletContext, WebApplicationContext.class, this);
		ServletContextServiceProvider.registerService(servletContext, ResourceLoader.class, this);
		ServletContextServiceProvider.registerService(servletContext, ListableBeanFactory.class, this);
		ServletContextServiceProvider.registerService(servletContext, BeanFactory.class, this);
		ServletContextServiceProvider.registerService(servletContext, ApplicationScanPathsProvider.class, this);
		ServletContextServiceProvider.registerService(servletContext, MessageSource.class, this);
	}

	@Override
	protected DefaultListableBeanFactory createBeanFactory() {
		return new QualifiedListableBeanFactory(getInternalParentBeanFactory());
	}

	@Override
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException {

		setConfigLocations(new String[0]);//config locations will not be scan paths or registered classes in super class
		String[] applicationScanPaths = getApplicationScanPaths();
		scan(applicationScanPaths);//will automatically scan the application paths

		super.loadBeanDefinitions(beanFactory);

		// next specific functionality

		//apply AUTOWIRE_BY_TYPE for all application beans
		String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
		for (String beanDefinitionName : beanDefinitionNames) {
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
			if (isApplicationComponent(beanDefinition, applicationScanPaths)) {
				((AbstractBeanDefinition) beanDefinition).setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
			}
		}

		for (BeanDefinitionLoader beanDefinitionLoader : ServiceFactory.loadServices(BeanDefinitionLoader.class)) {
			beanDefinitionLoader.setApplicationScanPaths(applicationScanPaths);
			beanDefinitionLoader.loadBeanDefinitions(this, beanFactory);
		}

	}

	private boolean isApplicationComponent(BeanDefinition beanDefinition, String[] applicationScanPaths) {
		String beanClassName = beanDefinition.getBeanClassName();
		if (beanClassName.startsWith("org.springframework")) {
			return false;
		}
		for (String appPath : applicationScanPaths) {
			if (beanClassName.startsWith(appPath)) {
				return true;
			}
		}
		return false;
	}

	String[] paths = null;

	public String[] getApplicationScanPaths() {
		if (paths == null) {
			paths = WebInitUtils.findScanPaths(getServletContext());
			if (paths.length == 0) {
				logger.warn("No packages found in application");
				paths = new String[] { "no.packages.found" };//avoid exeption
			}
		}
		return paths;
	}

}
