package org.nextframework.persistence;

public interface HibernateSessionProviderFactory {

	public HibernateSessionProvider createForContext(String persistenceContext);
}
