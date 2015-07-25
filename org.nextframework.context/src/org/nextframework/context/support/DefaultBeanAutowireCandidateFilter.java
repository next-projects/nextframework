package org.nextframework.context.support;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.context.AutowireCandidateFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class DefaultBeanAutowireCandidateFilter implements AutowireCandidateFilter  {

	private static final String DEFAULT_QUALIFIER = "default";
	
	Log logger = LogFactory.getLog(DefaultBeanAutowireCandidateFilter.class);

	@Override
	public Map<String, Object> filterAutowireCandidates(AbstractBeanFactory beanFactory, String beanName, Map<String, Object> candidateBeans, DependencyDescriptor descriptor) {
		//this will only be called when the bean to set a property has not set a qualifier in the setter or the property.. 
		//will only chose a default when all beans have qualifiers
		String qualifierToSearch = DEFAULT_QUALIFIER;
		
		Set<String> beanNames = candidateBeans.keySet();
		boolean allHaveQualifiers = true;
		String defaultBean = null;
		if(beanFactory instanceof DefaultListableBeanFactory){
			for (String candidate : beanNames) {
				BeanDefinition candidateBeanDefinition = ((DefaultListableBeanFactory)beanFactory).getBeanDefinition(candidate);
				if(candidateBeanDefinition instanceof AbstractBeanDefinition){
					//change to AutowireCandidateResolver
					AutowireCandidateQualifier qualifier = ((AbstractBeanDefinition)candidateBeanDefinition).getQualifier(Qualifier.class.getName());
					if(qualifier != null){
						Object qualifierValue = qualifier.getAttribute(AutowireCandidateQualifier.VALUE_KEY);
						if(qualifierToSearch.equals(qualifierValue)){
							if(defaultBean != null){
								throw new RuntimeException("Two beans of the same type declared a default @Qualifier ["+defaultBean+", "+candidate+"]. Only one must be set to default.");
							}
							defaultBean = candidate;
						}
						continue;
					}
				}
				allHaveQualifiers = false;
			}
		}

		if(defaultBean != null){ // there's a suitable default bean to autowire
			if(allHaveQualifiers){
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(defaultBean, candidateBeans.get(defaultBean));
				return map;
			} else {
				logger.warn("A bean ["+defaultBean+"] with qualifier '"+qualifierToSearch+"' has been found for autowiring for bean '"+beanName+"'. But, in order to choose between one candidate "+candidateBeans.keySet()+", all of them must declare qualifiers. Declare qualifiers for all beans listed.");
			}
		}
		return candidateBeans;
	}

}
