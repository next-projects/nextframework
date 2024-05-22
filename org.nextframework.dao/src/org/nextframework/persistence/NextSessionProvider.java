package org.nextframework.persistence;

import java.util.Map;
import java.util.Set;

import org.nextframework.core.standard.Next;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.StringUtils;

public class NextSessionProvider extends HibernateTransactionTemplateSessionProvider {

	private String persistenceContext;

	private HibernateTemplate hibernateTemplate;

	private TransactionTemplate transactionTemplate;

	public void setPersistenceContext(String persistenceContext) {
		this.persistenceContext = persistenceContext;
	}

	public HibernateTemplate getHibernateTemplate() {
		if (hibernateTemplate == null) {
			hibernateTemplate = getBeanForType(HibernateTemplate.class);
		}
		return hibernateTemplate;
	}

	@Override
	public TransactionTemplate getTransactionTemplate() {
		if (transactionTemplate == null) {
			transactionTemplate = getBeanForType(TransactionTemplate.class);
		}
		return transactionTemplate;
	}

	<X> X getBeanForType(Class<X> type) {
		X bean = null;
		Map<String, X> beansOfType = Next.getBeanFactory().getBeansOfType(type);
		Set<String> beans = beansOfType.keySet();
		if (beans.size() == 1) {
			bean = beansOfType.values().iterator().next();
		} else {
			for (String beanName : beans) {
				if (beanName.toLowerCase().endsWith(persistenceContext.toLowerCase())) {
					bean = beansOfType.get(beanName);
					break;
				}
				if (persistenceContext.equals(PersistenceConfiguration.DEFAULT_CONFIG)) {
					if (beanName.toLowerCase().equals(type.getSimpleName().toLowerCase())) {
						bean = beansOfType.get(beanName);
						break;
					}
				}
			}
		}
		if (bean == null) {
			throw new NoSuchBeanDefinitionException(StringUtils.uncapitalize(type.getSimpleName()) + persistenceContext);
		}
		return bean;
	}

}
