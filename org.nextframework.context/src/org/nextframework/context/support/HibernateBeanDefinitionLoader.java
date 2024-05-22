package org.nextframework.context.support;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.context.ApplicationScanPathsProvider;
import org.nextframework.context.BeanDefinitionLoader;
import org.nextframework.service.ServiceFactory;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.StringUtils;

public class HibernateBeanDefinitionLoader implements BeanDefinitionLoader {

	private static final String DATA_SOURCE_BEAN_NAME_PREFFIX = ConnectionPropertiesBeanDefinitionLoader.DATA_SOURCE_BEAN_NAME;

	private static final String NEXT_DATASOURCE_DISCRIMINATOR = ConnectionPropertiesBeanDefinitionLoader.NEXT_DATASOURCE_DISCRIMINATOR;

	protected final Log logger = LogFactory.getLog(LOG_NAME);

	@Override
	@SuppressWarnings("serial")
	public void loadBeanDefinitions(AbstractApplicationContext applicationContext, DefaultListableBeanFactory beanFactory) {

//		((AbstractRefreshableApplicationContext)applicationContext).get
//		private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(this.resourcePatternResolver);

		String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();

		for (String dataSourceBeanDefinitionName : beanDefinitionNames) {
			if (dataSourceBeanDefinitionName.startsWith(DATA_SOURCE_BEAN_NAME_PREFFIX)) {

//				BeanDefinition beanDefinition = beanFactory.getBeanDefinition(dataSourceBeanDefinitionName);
				String discriminator = dataSourceBeanDefinitionName.substring(DATA_SOURCE_BEAN_NAME_PREFFIX.length());
				String[] aliases = beanFactory.getAliases(dataSourceBeanDefinitionName);
				for (String alias : aliases) {
					if ((DATA_SOURCE_BEAN_NAME_PREFFIX + NEXT_DATASOURCE_DISCRIMINATOR).equals(alias)) {
						registerNextPersistenceManager(beanFactory); //register manager for the alias
						continue;
					}
				}
				if (NEXT_DATASOURCE_DISCRIMINATOR.equals(discriminator)) {
					//next specific datasource will not be automatically configured with hibernate
					registerNextPersistenceManager(beanFactory);
					continue;
				}

				List<String> registeredBeans = new ArrayList<String>() {

					public boolean add(String e) {
						if (e != null) {
							return super.add(e);
						} else {
							return false;
						}
					}

				};

				registeredBeans.add(registerSessionFactory(beanFactory, dataSourceBeanDefinitionName, discriminator));
				registeredBeans.add(registerHibernateTemplate(beanFactory, dataSourceBeanDefinitionName, discriminator));
				registeredBeans.add(registerTransactionManager(beanFactory, dataSourceBeanDefinitionName, discriminator));
				registeredBeans.add(registerTransactionTemplate(beanFactory, dataSourceBeanDefinitionName, discriminator));
				registeredBeans.add(registerPersistenceConfig(beanFactory, dataSourceBeanDefinitionName, discriminator));

				//copy the qualifiers from the datasource to the other beans
				AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) beanFactory.getBeanDefinition(dataSourceBeanDefinitionName);
				AutowireCandidateQualifier qualifier = beanDefinition.getQualifier(Qualifier.class.getName());
				if (qualifier != null) {
					for (String beanName : registeredBeans) {
						((AbstractBeanDefinition) beanFactory.getBeanDefinition(beanName)).addQualifier(qualifier);
					}
				}

				if (logger.isInfoEnabled()) {
					logger.info("Loading Hibernate bean definitions for data source [" + dataSourceBeanDefinitionName + "], adding beans " + registeredBeans);
				}

			}
		}
	}

	private void registerNextPersistenceManager(DefaultListableBeanFactory beanFactory) {

		GenericBeanDefinition nextPersistenceDB = new GenericBeanDefinition();
		nextPersistenceDB.setBeanClassName("org.nextframework.persistence.internal.NextPersistenceManager");

		nextPersistenceDB.setPropertyValues(new MutablePropertyValues());
		nextPersistenceDB.getPropertyValues().add("dataSource", new RuntimeBeanReference(DATA_SOURCE_BEAN_NAME_PREFFIX + NEXT_DATASOURCE_DISCRIMINATOR));

		beanFactory.registerBeanDefinition("_nextPersistenceManager", nextPersistenceDB);

	}

	private String registerPersistenceConfig(DefaultListableBeanFactory beanFactory, String dataSourceBeanDefinitionName, String discriminator) {

		String beanName = "persistenceConfig" + discriminator;
		if (beanFactory.containsBean(beanName)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Not registering " + beanName + ", already registered.");
			}
			return null;
		}

		GenericBeanDefinition persistenceConfigBD = new GenericBeanDefinition();
		persistenceConfigBD.setBeanClassName("org.nextframework.persistence.PersistenceConfiguration");
		ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
		String context = discriminator;
		if (!StringUtils.hasText(context)) {
			context = "default";
		}
		context = StringUtils.uncapitalize(context);
		constructorArgumentValues.addGenericArgumentValue(context, String.class.getName());
		persistenceConfigBD.setConstructorArgumentValues(constructorArgumentValues);

		beanFactory.registerBeanDefinition(beanName, persistenceConfigBD);

		return beanName;
	}

	private String registerTransactionTemplate(DefaultListableBeanFactory beanFactory, String dataSourceBeanDefinitionName, String discriminator) {

		String beanName = "transactionTemplate" + discriminator;
		if (beanFactory.containsBean(beanName)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Not registering " + beanName + ", already registered.");
			}
			return null;
		}

		GenericBeanDefinition sessionFactoryBD = new GenericBeanDefinition();
		sessionFactoryBD.setBeanClassName("org.springframework.transaction.support.TransactionTemplate");
		sessionFactoryBD.setPropertyValues(new MutablePropertyValues());
		sessionFactoryBD.getPropertyValues().add("transactionManager", new RuntimeBeanReference("transactionManager" + discriminator));

		beanFactory.registerBeanDefinition(beanName, sessionFactoryBD);

		return beanName;
	}

	private String registerTransactionManager(DefaultListableBeanFactory beanFactory, String dataSourceBeanDefinitionName, String discriminator) {

		String beanName = "transactionManager" + discriminator;
		if (beanFactory.containsBean(beanName)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Not registering " + beanName + ", already registered.");
			}
			return null;
		}

		GenericBeanDefinition sessionFactoryBD = new GenericBeanDefinition();
		sessionFactoryBD.setBeanClassName("org.springframework.orm.hibernate4.HibernateTransactionManager");
		sessionFactoryBD.setPropertyValues(new MutablePropertyValues());
		sessionFactoryBD.getPropertyValues().add("sessionFactory", new RuntimeBeanReference("sessionFactory" + discriminator));
		sessionFactoryBD.getPropertyValues().add("dataSource", new RuntimeBeanReference("dataSource" + discriminator));

		beanFactory.registerBeanDefinition(beanName, sessionFactoryBD);

		return beanName;
	}

	private String registerHibernateTemplate(DefaultListableBeanFactory beanFactory, String dataSourceBeanDefinitionName, String discriminator) {

		String beanName = "hibernateTemplate" + discriminator;
		if (beanFactory.containsBean(beanName)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Not registering " + beanName + ", already registered.");
			}
			return null;
		}

		GenericBeanDefinition sessionFactoryBD = new GenericBeanDefinition();
		sessionFactoryBD.setBeanClassName("org.springframework.orm.hibernate4.HibernateTemplate");
		sessionFactoryBD.setPropertyValues(new MutablePropertyValues());
		sessionFactoryBD.getPropertyValues().add("sessionFactory", new RuntimeBeanReference("sessionFactory" + discriminator));

		beanFactory.registerBeanDefinition(beanName, sessionFactoryBD);

		return beanName;
	}

	protected String registerSessionFactory(DefaultListableBeanFactory beanFactory, String dataSourceBeanDefinitionName, String discriminator) {

		String beanName = "sessionFactory" + discriminator;
		if (beanFactory.containsBean(beanName)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Not registering " + beanName + ", already registered.");
			}
			return null;
		}

		GenericBeanDefinition sessionFactoryBD = new GenericBeanDefinition();
		sessionFactoryBD.setBeanClassName("org.springframework.orm.hibernate4.LocalSessionFactoryBean");
		sessionFactoryBD.setPropertyValues(new MutablePropertyValues());
		sessionFactoryBD.getPropertyValues().add("dataSource", new RuntimeBeanReference("dataSource" + discriminator));
		sessionFactoryBD.getPropertyValues().add("packagesToScan", ServiceFactory.getService(ApplicationScanPathsProvider.class).getApplicationScanPaths());

		beanFactory.registerBeanDefinition(beanName, sessionFactoryBD);

		return beanName;
	}

	@Override
	public void setApplicationScanPaths(String[] applicationScanPaths) {

	}

}
