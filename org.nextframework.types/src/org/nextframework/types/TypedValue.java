package org.nextframework.types;

import java.util.Collection;

/**
 * An ordinary list with a getType method that returns the type of the items
 * @author rogelgarcia
 *
 */
public interface TypedValue<E> extends Collection<E> {

	Class<E> getCollectionItemType();

	Collection<E> getOriginalCollection();

}
