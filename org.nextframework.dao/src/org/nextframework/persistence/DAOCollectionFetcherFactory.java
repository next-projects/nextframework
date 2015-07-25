package org.nextframework.persistence;

public class DAOCollectionFetcherFactory implements CollectionFetcherFactory {

	@Override
	public CollectionFetcher createForContext(String persistenceContext) {
		return GenericDAO.getDAODelegateCollectionFetcher();
	}

}
