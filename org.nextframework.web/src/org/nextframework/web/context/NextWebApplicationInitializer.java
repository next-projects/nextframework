package org.nextframework.web.context;

import static org.springframework.core.Ordered.LOWEST_PRECEDENCE;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.nextframework.service.ServiceException;
import org.nextframework.service.ServiceFactory;
import org.nextframework.web.WebContext;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;

/**
 * Automatically detected by {@link org.springframework.web.SpringServletContainerInitializer}.
 * 
 * @author rogelgarcia
 */
@Order(LOWEST_PRECEDENCE - 1000)
public class NextWebApplicationInitializer implements WebApplicationInitializer {
	
	private ServletContext servletContext;

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		this.servletContext = servletContext;
		registerContextLoaderListener();
	}
	
	protected void registerContextLoaderListener() {
//		List<ApplicationListener> applicationListeners = Arrays.asList(ServiceFactory.loadServices(ApplicationListener.class));
		WebContext.setServletContext(servletContext);
		try {
			org.springframework.web.context.ContextLoaderListener springLoader = ServiceFactory.getService(org.springframework.web.context.ContextLoaderListener.class);
			servletContext.log("Using custom ContextLoaderListener: "+springLoader);
			servletContext.addListener(springLoader);
		} catch (ServiceException e) {
			servletContext.addListener(new ContextLoaderListener());
		}
	}
	
	public ServletContext getServletContext() {
		return servletContext;
	}

//	protected WebApplicationContext createRootApplicationContext() {
//		String[] scanPaths = getScanPaths();
//		
//		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext(){
//			@Override
//			protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) {
//				System.out.println("LOADING BEAN DEFINITIONS");
//				super.loadBeanDefinitions(beanFactory);
//				XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
//				xmlBeanDefinitionReader.loadBeanDefinitions(getResource("/WEB-INF/applicationConfig.xml"));
//			}
//		};
////		context.setServletContext(servletContext);
//
//		{
//			long start = System.currentTimeMillis();
//			ClassPathBeanDefinitionScanner classPathBeanDefinitionScanner = new ClassPathBeanDefinitionScanner(new SimpleBeanDefinitionRegistry(), false);
//			classPathBeanDefinitionScanner.setEnvironment(context.getEnvironment());
//			classPathBeanDefinitionScanner.setResourceLoader(context);
//			try {
//				classPathBeanDefinitionScanner.addIncludeFilter(new AssignableTypeFilter(Class.forName("org.nextframework.persistence.DAO")));
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			}
//			System.out.println("# "+classPathBeanDefinitionScanner.scan(scanPaths));
//			System.out.println(System.currentTimeMillis() - start);
//		}
//		{
//			long start = System.currentTimeMillis();
//			ClassPathBeanDefinitionScanner classPathBeanDefinitionScanner = new ClassPathBeanDefinitionScanner(new SimpleBeanDefinitionRegistry(), false);
//			classPathBeanDefinitionScanner.setEnvironment(context.getEnvironment());
//			classPathBeanDefinitionScanner.setResourceLoader(context);
//			try {
//				classPathBeanDefinitionScanner.addIncludeFilter(new AssignableTypeFilter(Class.forName("org.nextframework.persistence.DAO")));
//			} catch (ClassNotFoundException e) {
//				e.printStackTrace();
//			}
//			System.out.println("# "+classPathBeanDefinitionScanner.scan(scanPaths));
//			System.out.println(System.currentTimeMillis() - start);
//		}
//		context.scan(scanPaths);
//		return context;
//	}
//
//	protected String[] getScanPaths() {
//		return WebInitUtils.findScanPaths(servletContext);
//	}

}
