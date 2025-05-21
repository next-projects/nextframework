package org.nextframework.classmanager;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextframework.context.ApplicationScanPathsProvider;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;

@SuppressWarnings("rawtypes")
public class ClassPathScannerClassManager implements ClassManager {

	private Map<Class, Class[]> classesOfType = new HashMap<Class, Class[]>();
	private Map<Class, Class[]> classesWithAnnotation = new HashMap<Class, Class[]>();

	private ClassPathScanningCandidateComponentProvider componentProvider;
	private ApplicationScanPathsProvider applicationScanPathsProvider;

	public ClassPathScannerClassManager(ResourcePatternResolver resourcePatternResolver, ApplicationScanPathsProvider applicationScanPathsProvider) {
		componentProvider = new ClassPathScanningCandidateComponentProvider(false);
		componentProvider.setResourceLoader(resourcePatternResolver);
		this.applicationScanPathsProvider = applicationScanPathsProvider;
	}

	@Override
	public synchronized Class<?>[] getAllClasses() {
		return getAllClassesOfType(Object.class);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <E> Class<E>[] getAllClassesOfType(Class<E> type) {
		if (!classesOfType.containsKey(type)) {
			synchronized (componentProvider) {
				componentProvider.resetFilters(false);
				componentProvider.addIncludeFilter(new AssignableTypeFilter(type));
				classesOfType.put(type, getClasses());
			}
		}
		return classesOfType.get(type);
	}

	@Override
	public Class<?>[] getClassesWithAnnotation(Class<? extends Annotation> annotationType) {
		if (!classesWithAnnotation.containsKey(annotationType)) {
			synchronized (componentProvider) {
				componentProvider.resetFilters(false);
				componentProvider.addIncludeFilter(new AnnotationTypeFilter(annotationType));
				classesWithAnnotation.put(annotationType, getClasses());
			}
		}
		return classesWithAnnotation.get(annotationType);
	}

	private Class<?>[] getClasses() {
		String[] applicationScanPaths = applicationScanPathsProvider.getApplicationScanPaths();
		List<Class<?>> classes = new ArrayList<Class<?>>();
		for (String scan : applicationScanPaths) {
			Set<BeanDefinition> definitions = componentProvider.findCandidateComponents(scan);
			for (BeanDefinition beanDefinition : definitions) {
				Class<?> c;
				try {
					c = Class.forName(beanDefinition.getBeanClassName());
					classes.add(c);
				} catch (ClassNotFoundException e) {
				}
			}
		}
		return classes.toArray(new Class[classes.size()]);
	}

}
