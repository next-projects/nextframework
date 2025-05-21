package org.nextframework.persistence;

public class NextSessionProviderFactory implements HibernateSessionProviderFactory {

	@Override
	public HibernateSessionProvider createForContext(String persistenceContext) {
		NextSessionProvider nextSessionProvider = new NextSessionProvider();
		nextSessionProvider.setPersistenceContext(persistenceContext);
		return nextSessionProvider;
	}

}
