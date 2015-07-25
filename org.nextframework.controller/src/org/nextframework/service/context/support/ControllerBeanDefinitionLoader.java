package org.nextframework.service.context.support;

import java.util.ArrayList;
import java.util.List;

import org.nextframework.context.support.CustomScannerBeanDefinitionLoader;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.web.servlet.mvc.Controller;

public class ControllerBeanDefinitionLoader extends CustomScannerBeanDefinitionLoader {
	
	/**
	 * List of controller classes that must be ignored by this loader.<BR>
	 * If another loader will register a special type of controller it must register in this list, the classes that must be ignored by this loader.<BR>
	 * Otherwise the controller can be registered twice
	 */
	public static List<Class<?>> IGNORE_CONTROLLER_CLASSES = new ArrayList<Class<?>>();
	
	@Override
	public void applyFilters(ClassPathBeanDefinitionScanner scanner) {
		setAutowireBeans(scanner, AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		scanner.addIncludeFilter(new AssignableTypeFilter(Controller.class));
		for (Class<?> cClass : IGNORE_CONTROLLER_CLASSES) {
			scanner.addExcludeFilter(new AssignableTypeFilter(cClass));
		}
	}

	@Override
	public void postProcessBeanDefinition(DefaultListableBeanFactory beanFactory, AbstractBeanDefinition beanDefinition, String beanName) {
		checkControllerDefinition(beanDefinition);
	}

	protected static void checkControllerDefinition(AbstractBeanDefinition beanDefinition) {
		try {
			Class<?> controllerClass = Class.forName(beanDefinition.getBeanClassName());
			if(!Controller.class.isAssignableFrom(controllerClass)){
				throw new RuntimeException("The "+controllerClass+" is not a Controller class");
			}
			if(controllerClass.getAnnotation(org.nextframework.controller.Controller.class) == null){
				throw new RuntimeException("The "+controllerClass+" is a controller but is not annotated with @Controller");
			}
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String toString() {
		return "Controller Loader";
	}
}