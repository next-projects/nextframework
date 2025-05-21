package org.nextframework.context;

import java.util.Map;

import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AbstractBeanFactory;

public interface AutowireCandidateFilter {

	String LOG_NAME = AutowireCandidateFilter.class.getName();

	Map<String, Object> filterAutowireCandidates(AbstractBeanFactory beanFactory, String beanName, Map<String, Object> candidateBeans, DependencyDescriptor descriptor);

}
