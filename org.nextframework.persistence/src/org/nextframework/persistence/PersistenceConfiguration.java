package org.nextframework.persistence;

import java.util.HashMap;
import java.util.Map;

import org.nextframework.service.ServiceFactory;

public class PersistenceConfiguration {

	public static String DEFAULT_CONFIG = "default";

	static Map<String, PersistenceConfiguration> configMap = new HashMap<String, PersistenceConfiguration>();

	/**
	 * Sets the query builder configuration using 'default' persistence context.<BR>
	 * @param config
	 */
	public static void configure(PersistenceConfiguration config) {
		configure(DEFAULT_CONFIG, config);
	}

	public static void configure(String persistenceContext, PersistenceConfiguration config) {
		configMap.put(persistenceContext, config);
	}

	public static PersistenceConfiguration getConfig() {
		return getConfig(DEFAULT_CONFIG);
	}

	public static PersistenceConfiguration getConfig(String persistenceContext) {
		if (persistenceContext.equals(DEFAULT_CONFIG) && configMap.size() == 1) {
			//asked for a default definition.. let's return the unique datasource even if it is not the required one
			return configMap.entrySet().iterator().next().getValue();
		}
		return configMap.get(persistenceContext);
	}

	public PersistenceConfiguration() {
		this(DEFAULT_CONFIG);
	}

	public PersistenceConfiguration(String persistenceContext) {
		this.persistenceContext = persistenceContext;
		configure(persistenceContext, this);
	}

	private String persistenceContext;

	private String removeAccentFunction;

	private CollectionFetcher defaultCollectionFetcher;

	private HibernateSessionProvider sessionProvider;

	public String getPersistenceContext() {
		return persistenceContext;
	}

	public HibernateSessionProvider getSessionProvider() {
		if (sessionProvider == null) {
			sessionProvider = ServiceFactory
					.getService(HibernateSessionProviderFactory.class)
					.createForContext(persistenceContext);
		}
		return sessionProvider;
	}

	public CollectionFetcher getDefaultCollectionFetcher() {
		if (defaultCollectionFetcher == null) {
			defaultCollectionFetcher = ServiceFactory
					.getService(CollectionFetcherFactory.class)
					.createForContext(persistenceContext);
		}
		return defaultCollectionFetcher;
	}

	public void setSessionProvider(HibernateSessionProvider sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	public String getRemoveAccentFunction() {
		return removeAccentFunction;
	}

	public void setRemoveAccentFunction(String removeAccentFunction) {
		this.removeAccentFunction = removeAccentFunction;
	}

	public void setDefaultCollectionFetcher(CollectionFetcher defaultCollectionFetcher) {
		this.defaultCollectionFetcher = defaultCollectionFetcher;
	}

}
