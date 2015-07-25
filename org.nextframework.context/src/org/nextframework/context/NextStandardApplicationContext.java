package org.nextframework.context;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.nextframework.service.ServiceFactory;
import org.nextframework.service.StaticServiceProvider;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class NextStandardApplicationContext extends GenericApplicationContext implements ApplicationScanPathsProvider {
	
	private static final String JAVA_CLASS_PATH = "java.class.path";
	private String[] basePackages;
	
	{
		basePackages = findScanPaths();
		registerServices();
	}
	
	public NextStandardApplicationContext() {
		this(new org.nextframework.context.factory.support.DefaultListableBeanFactory(null));
	}

	public NextStandardApplicationContext(ApplicationContext parent) {
		this();
		setParent(parent);
	}

	public NextStandardApplicationContext(DefaultListableBeanFactory beanFactory, ApplicationContext parent) {
		this(beanFactory);
		setParent(parent);
	}

	public NextStandardApplicationContext(DefaultListableBeanFactory beanFactory) {
		super(beanFactory);
		beanFactory.setAutowireCandidateResolver(new QualifierAnnotationAutowireCandidateResolver());
	}
	
	private void registerServices() {
		// configure the spring application context as a service
		StaticServiceProvider.registerService(ApplicationContext.class, this);
		StaticServiceProvider.registerService(ConfigurableApplicationContext.class, this);
		StaticServiceProvider.registerService(ResourceLoader.class, this);
		
		StaticServiceProvider.registerService(ApplicationScanPathsProvider.class, this);
		
		StaticServiceProvider.registerService(ListableBeanFactory.class, getDefaultListableBeanFactory());
		StaticServiceProvider.registerService(DefaultListableBeanFactory.class, getDefaultListableBeanFactory());
	}

	public void setBasePackages(String[] basePackages) {
		this.basePackages = basePackages;
	}

	@Override
	protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
		AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(getDefaultListableBeanFactory());
		reader.setEnvironment(this.getEnvironment());
		
		ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(getDefaultListableBeanFactory());
		scanner.setEnvironment(this.getEnvironment());
		
		if (!ObjectUtils.isEmpty(this.basePackages)) {
			if (logger.isInfoEnabled()) {
				logger.info("Scanning base packages: [" +
						StringUtils.arrayToCommaDelimitedString(this.basePackages) + "]");
			}
			scanner.scan(this.basePackages);
		}
		
		ConfigurableListableBeanFactory beanFactory = super.obtainFreshBeanFactory();
		
		//apply AUTOWIRE_BY_TYPE for all application beans
		String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
		for (String beanDefinitionName : beanDefinitionNames) {
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
			if(isApplicationComponent(beanDefinition, basePackages)){
				((AbstractBeanDefinition)beanDefinition).setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
			}
		}
		
		for (BeanDefinitionLoader beanDefinitionLoader : ServiceFactory.loadServices(BeanDefinitionLoader.class)) {
			beanDefinitionLoader.setApplicationScanPaths(getApplicationScanPaths());
			beanDefinitionLoader.loadBeanDefinitions(this, getDefaultListableBeanFactory());
		}
		
		return beanFactory;
	}
	
	private boolean isApplicationComponent(BeanDefinition beanDefinition, String[] applicationScanPaths) {
		String beanClassName = beanDefinition.getBeanClassName();
		if(beanClassName.startsWith("org.springframework")){
			return false;
		}
		for (String appPath : applicationScanPaths) {
			if(beanClassName.startsWith(appPath)){
				return true;
			}
		}
		return false;
	}

	public String[] findScanPaths() {
		String classpathProperty = System.getProperty(JAVA_CLASS_PATH);
		String[] paths = classpathProperty.split("[;]{1}");
		Set<String> packages = new HashSet<String>();
		for (String pathname : paths) {
			File file = new File(pathname);
			if(file.isDirectory()){
				packages.addAll(searchPackages(null, file));
			}
		}
		return packages.toArray(new String[packages.size()]);
	}

	private Collection<? extends String> searchPackages(String basePackage, File file) {
		List<String> packages = new ArrayList<String>();
		File[] files = file.listFiles();
		for (File subdir : files) {
			if(subdir.isDirectory()){
				String packageName = subdir.getName();
				if(basePackage != null){
					packageName = basePackage + "." +packageName; 
				}
				if(packageName.startsWith(".")){
					continue;
				}
				if(packageName.startsWith("META-INF")){
					continue;
				}
				if(packageName.equals("org") || packageName.equals("com") || packageName.equals("net")){
					packages.addAll(searchPackages(packageName, subdir));
					continue;
				}
				if(packageName.equals("org.nextframework")){ // ignore org.nextframework
					continue;
				}
				if(packageName.equals("org.stjs")){ // ignore 
					continue;
				}
				if(packageName.equals("org.eclipse")){ // ignore 
					continue;
				}
				packages.add(packageName);
			}
		}
		return packages;
	}

	@Override
	public String[] getApplicationScanPaths() {
		return basePackages;
	}


}
