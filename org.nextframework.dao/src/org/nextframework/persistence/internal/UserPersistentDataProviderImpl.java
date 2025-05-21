package org.nextframework.persistence.internal;

import static org.nextframework.context.support.ConnectionPropertiesBeanDefinitionLoader.DATA_SOURCE_BEAN_NAME;
import static org.nextframework.context.support.ConnectionPropertiesBeanDefinitionLoader.NEXT_DATASOURCE_DISCRIMINATOR;

import java.util.Map;

import org.nextframework.context.UserPersistentDataProvider;
import org.nextframework.core.standard.Next;
import org.nextframework.exception.NextException;

public class UserPersistentDataProviderImpl implements UserPersistentDataProvider {

	@Override
	public Map<String, String> getUserMap(String username) {
		if (username == null) {
			throw new NullPointerException("no username provided");
		}
		Map<String, NextPersistenceManager> beans = Next.getBeanFactory().getBeansOfType(NextPersistenceManager.class);
		if (beans.size() > 0) {
			NextPersistenceManager persistentManager = beans.values().iterator().next();
			return persistentManager.getPropertiesMapForUser(username);
		} else {
			throw new NextException(
					"No " + NextPersistenceManager.class.getSimpleName() + " bean found on bean factory. " +
							"To enable user persistent data, register a dataSource bean named " +
							DATA_SOURCE_BEAN_NAME + NEXT_DATASOURCE_DISCRIMINATOR +
							" in the bean factory.");
		}
	}

}
