package org.nextframework.persistence.internal;

import static org.nextframework.context.support.ConnectionPropertiesBeanDefinitionLoader.DATA_SOURCE_BEAN_NAME;
import static org.nextframework.context.support.ConnectionPropertiesBeanDefinitionLoader.NEXT_DATASOURCE_DISCRIMINATOR;

import javax.sql.DataSource;

import org.nextframework.core.standard.Next;
import org.springframework.beans.factory.BeanFactory;

public class NextPersistenceUtils {

	public static DataSource getNextSpecificDataSource() {
		return getNextSpecificDataSource(Next.getBeanFactory());
	}

	public static DataSource getNextSpecificDataSource(BeanFactory beanFactory) {
		return beanFactory.getBean(DATA_SOURCE_BEAN_NAME + NEXT_DATASOURCE_DISCRIMINATOR, DataSource.class);
	}

}
