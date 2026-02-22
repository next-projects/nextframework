package org.nextframework.context.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.context.AutowireCandidateFilter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.ResolvableType;

public class PackageSimilarityAutowireCandidateFilter implements AutowireCandidateFilter {

	protected final Log logger = LogFactory.getLog(LOG_NAME);

	@Override
	public Map<String, Object> filterAutowireCandidates(AbstractBeanFactory abstractBeanFactory, String beanName, Map<String, Object> candidateBeans, DependencyDescriptor descriptor) {
		if (candidateBeans.size() > 1 && descriptor.getResolvableType().asCollection() != ResolvableType.NONE) {
			return candidateBeans;
		}
		if (abstractBeanFactory instanceof DefaultListableBeanFactory) {
			DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) abstractBeanFactory;
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);

			String fullyQualifiedName = beanDefinition.getBeanClassName();
			String[] nameParts = getNameParts(fullyQualifiedName);
			int maximumMatch = 0;
			List<Map.Entry<String, Object>> bestMatch = new ArrayList<Map.Entry<String, Object>>();
			for (Map.Entry<String, Object> candidate : candidateBeans.entrySet()) {
				String[] candidateParts = getNameParts(candidate.getValue().getClass().getName());
				int matchDepth = getMatchDepth(nameParts, candidateParts);
				if (matchDepth > maximumMatch) {
					maximumMatch = matchDepth;
					bestMatch.clear();
					bestMatch.add(candidate);
				} else if (matchDepth == maximumMatch) {
					bestMatch.add(candidate);
				}
			}
			if (bestMatch.size() == 1 && maximumMatch > 1) {
				candidateBeans = new HashMap<String, Object>();
				candidateBeans.put(bestMatch.get(0).getKey(), bestMatch.get(0).getValue());
			} else {
				if (logger.isDebugEnabled()) {
					logger.debug("Could not filter candidates by package. Remaining candidates: " + candidateBeans);
				}
			}
		}
		return candidateBeans;
	}

	private int getMatchDepth(String[] nameParts, String[] candidateParts) {
		int i = 0;
		for (; i < nameParts.length && i < candidateParts.length; i++) {
			if (!nameParts[i].equals(candidateParts[i])) {
				break;
			}
		}
		return i;
	}

	private String[] getNameParts(String fullyQualifiedName) {
		return fullyQualifiedName.split("\\.");
	}

}
