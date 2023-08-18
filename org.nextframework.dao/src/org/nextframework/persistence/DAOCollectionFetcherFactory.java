package org.nextframework.persistence;

import java.util.List;

public class DAOCollectionFetcherFactory implements CollectionFetcherFactory {

	@Override
	public CollectionFetcher createForContext(String persistenceContext) {
		return new CollectionFetcher() {

			@SuppressWarnings("unchecked")
			public List<?> load(QueryBuilder<?> query, Object owner, String collectionProperty, Class<?> itemType) {
				@SuppressWarnings("rawtypes")
				GenericDAO daoForClass = DAOUtils.getDAOForClass(itemType);
				daoForClass.updateCollectionFetchQuery(query, owner, collectionProperty);
				return query.list();
			}

		};
	}

}