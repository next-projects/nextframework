package org.nextframework.context.support;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.context.AutowireCandidateFilter;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.core.ResolvableType;

public class GenericTypeAutowireCandidateFilter implements AutowireCandidateFilter {

	protected final Log logger = LogFactory.getLog(this.getClass().getName());

	@Override
	@SuppressWarnings("all")
	public Map<String, Object> filterAutowireCandidates(AbstractBeanFactory beanFactory, String beanName, Map<String, Object> candidateBeans, DependencyDescriptor descriptor) {

		if (candidateBeans.size() <= 1) {
			return candidateBeans;
		}
		if (candidateBeans.size() > 1 && descriptor.getCollectionType() != null) {
			return candidateBeans;
		}
		Map<String, Object> matchingBeans = new HashMap<String, Object>();

		ResolvableType paramResolvableType = descriptor.getResolvableType();

		NEXT_BEAN:
		for (Map.Entry<String, Object> entry : candidateBeans.entrySet()) {
			String candidateBeanName = entry.getKey();
			Object candidate = entry.getValue();

//			System.out.println(candidate );
//			System.out.println(paramResolvableType);
			//matchingBeans.put(candidateBeanName, candidate);
		}

		if (matchingBeans.size() > 0) {
			if (logger.isDebugEnabled()) {
				logger.debug("Changing candidate beans for " + beanName + "." + descriptor.getMethodParameter().getParameterName() + " from " + candidateBeans + " to " + matchingBeans);
			}
			candidateBeans = matchingBeans;
		}

		return candidateBeans;
	}

}
