package org.nextframework.persistence;

import java.util.List;

/**
 * Used in conjunction with the fetchCollection of the QueryBuilder class.<BR>
 * There's an overloaded method fetchCollection(..) in QueryBuilder class that receives a CollectionFetcher (this interface) 
 * as parameter. The CollectionFetcher is like a listener that receives a query builder configured to load the collection itens of the parent item.<BR>
 * A implementor of this interface have a chance to change the behavior of the default query builder to fetch collections, for example to make joins.
 * 
 * @author rogelgarcia
 *
 */
public interface CollectionFetcher {

	/**
	 * Selects a collection property of a bean.
	 * @param qb A configured QueryBuilder that selects all the elements of the owner item. A simple return qb.list() will do the default behaviour.
	 * @param owner The owner of the collection
	 * @param collectionProperty The property of the owner class to retrieve the collection
	 * @param itemType The collection items type
	 * @return
	 */
	List<?> load(QueryBuilder<?> qb, Object owner, String collectionProperty, Class<?> itemType);

}