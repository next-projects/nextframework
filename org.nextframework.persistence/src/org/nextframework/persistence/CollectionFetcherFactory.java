package org.nextframework.persistence;

public interface CollectionFetcherFactory {

	CollectionFetcher createForContext(String persistenceContext);

}
