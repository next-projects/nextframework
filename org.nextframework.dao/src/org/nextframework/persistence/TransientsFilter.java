package org.nextframework.persistence;

import java.util.Map;

/**
 * Utility interface to be implemented by DAOs that can filter transients fields.
 * This interface is intended to be used by extensions of the framework. The framework itself does not use this interface.
 * 
 * @author rogel
 *
 */
public interface TransientsFilter {

	void filterQueryForTransients(QueryBuilder<?> query, Map<String, Object> filters);
}
