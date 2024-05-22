package org.nextframework.context.support;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.context.BeanDefinitionLoader;
import org.nextframework.core.standard.Next;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public class ConnectionPropertiesBeanDefinitionLoader implements BeanDefinitionLoader {

	private static final String HSQLDB_DRIVER_CLASS = "org.hsqldb.jdbc.JDBCDriver";
	private static final String HSQLDB_PACKAGE = "org.hsqldb.jdbc";

	public static final String DATA_SOURCE_BEAN_NAME = "dataSource";

	public static final String NEXT_DATASOURCE_DISCRIMINATOR = "NextFramework";

	private static String DEFAULT_CLASS_NAME = "org.springframework.jdbc.datasource.DriverManagerDataSource";

	private static String CLASS_NAME = "dataSourceClass";

	private static String NO_PROPERTIES_LOGGING = "logging.disabled";

	private static final String PROPERTY_DRIVER = "driver";
	private static final String PROPERTY_DRIVER_CLASS_NAME = "driverClassName";
	private static final String PROPERTY_PASSWORD = "password";
	private static final String PROPERTY_URL = "url";
	private static final String PROPERTY_USERNAME = "username";

	protected final Log logger = LogFactory.getLog(LOG_NAME);

	@Override
	public void loadBeanDefinitions(AbstractApplicationContext applicationContext, DefaultListableBeanFactory beanFactory) {
		Resource[] resources = null;
		try {
			//jboss7 error here - TODO check spring
			boolean loadedConnectionProperties = false;
			resources = applicationContext.getResources("classpath:connection*.properties");
			for (Resource resource : resources) {
				Properties connectionProperties = PropertiesLoaderUtils.loadProperties(resource);
				loadedConnectionProperties = configureDataSource(beanFactory, resource, connectionProperties)
						|| loadedConnectionProperties; //do not invert order
			}
			if (!loadedConnectionProperties) {
				//only check hsqldb if no connection.properties loaded
				registerHsqlDB(beanFactory);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);//TODO REFACTOR CHANGE TO BEANEXCEPTION
		}
	}

	/**
	 * If, there is no dataSource, and HSQLDB is present
	 * Create a dataSource for it
	 * @param beanFactory 
	 */
	private void registerHsqlDB(DefaultListableBeanFactory beanFactory) {
		//use scanner to avoid class loading
		ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(beanFactory);
		boolean hsqldbAvailable = isHsqldbAvailable(scanner);
		if (!hsqldbAvailable) {
			//if hsqldb is not present, return
			return;
		}
		boolean dataSourceAvailable = isDataSourceAvailable(beanFactory, scanner);
		if (dataSourceAvailable) {
			return;
		}
		//no dataSource available, hsqldb available 
		Properties properties = new Properties();
		String dbFile = Next.getApplicationContext().getApplicationDir()
				+ File.separator + "db" + File.separator + Next.getApplicationName();
		properties.put(PROPERTY_DRIVER, HSQLDB_DRIVER_CLASS);
		properties.put(PROPERTY_URL, "jdbc:hsqldb:file:" + dbFile);
		properties.put(PROPERTY_USERNAME, "SA");
		properties.put(PROPERTY_PASSWORD, "");
//		properties.put("", "");
//		properties.put("", "");
		logger.info("HSQLDB detected, executing automatic configuration");
		configureDataSource(beanFactory, null, properties);
	}

	public boolean isHsqldbAvailable(ClassPathBeanDefinitionScanner scanner) {
		scanner.addIncludeFilter(new TypeFilter() {

			public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
				String className = metadataReader.getClassMetadata().getClassName();
				return className.equals(HSQLDB_DRIVER_CLASS);
			}

		});
		boolean hsqldbPresent = scanner.findCandidateComponents(HSQLDB_PACKAGE).size() > 0;
		return hsqldbPresent;
	}

	public boolean isDataSourceAvailable(DefaultListableBeanFactory beanFactory, ClassPathBeanDefinitionScanner scanner) {
		String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
		boolean dataSourcePresent = false;
		AssignableTypeFilter assignableTypeFilter = new AssignableTypeFilter(DataSource.class);
		MetadataReaderFactory metadataReaderFactory = scanner.getMetadataReaderFactory();

		//first check default
		try {
			BeanDefinition dataSourceBeanDefinition = beanFactory.getBeanDefinition(DATA_SOURCE_BEAN_NAME);
			if (dataSourceBeanDefinition != null) {
				try {
					MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(dataSourceBeanDefinition.getBeanClassName());
					dataSourcePresent = assignableTypeFilter.match(metadataReader, metadataReaderFactory);
				} catch (IOException e) {
					logger.error(e);
				}
			}
			if (dataSourcePresent) {
				return true;
			}
		} catch (NoSuchBeanDefinitionException e) {
			//if no datasource bean defined no problem
		}

		//check other beans
		for (String beanDefinitionName : beanDefinitionNames) {
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
			String beanClassName = beanDefinition.getBeanClassName();
			try {
				MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(beanClassName);
				dataSourcePresent = assignableTypeFilter.match(metadataReader, metadataReaderFactory);
				if (dataSourcePresent) {
					break;
				}
			} catch (IOException e) {
				logger.error(e);
			}
		}
		return dataSourcePresent;
	}

	protected boolean configureDataSource(DefaultListableBeanFactory beanFactory, Resource resource, Properties connectionProperties) {

		if (connectionProperties.isEmpty()) {
			return false;
		}
		String discriminator = getDiscriminator(resource);
		String beanName = DATA_SOURCE_BEAN_NAME + discriminator;

		String dataSourceClassName = getProperty(connectionProperties, CLASS_NAME, DEFAULT_CLASS_NAME);

		Map<String, Object> propertiesMap = getPropertiesMap(connectionProperties);

		RootBeanDefinition beanDefinition = new RootBeanDefinition(dataSourceClassName);
		beanDefinition.setPropertyValues(new MutablePropertyValues(propertiesMap));

		beanFactory.registerBeanDefinition(beanName, beanDefinition);
		if (logger.isInfoEnabled()) {
			connectionProperties.put(PROPERTY_PASSWORD, "*****");
			if (connectionProperties.getProperty(NO_PROPERTIES_LOGGING) == null) {
				logger.info("Adding data source beans [" + beanName + ", jdbcTemplate" + discriminator + "] using properties " + connectionProperties + ". ");
			} else {
				logger.info("Adding data source beans [" + beanName + ", jdbcTemplate" + discriminator + "]. ");
			}
		}

		registerJdbcTemplateFactory(beanFactory, beanName, discriminator);

		return true;
	}

	public String getDiscriminator(Resource resource) {
		if (resource == null) {
			return "";
		}
		String filename = resource.getFilename();
		String discriminator = filename.substring("connection".length(), filename.length() - ".properties".length());
		return discriminator;
	}

	protected String registerJdbcTemplateFactory(DefaultListableBeanFactory beanFactory, String dataSourceBeanDefinitionName, String discriminator) {
		String beanName = "jdbcTemplate" + discriminator;
		if (beanFactory.containsBean(beanName)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Not registering " + beanName + ", already registered.");
			}
			return null;
		}

		GenericBeanDefinition jdbcTemplateBD = new GenericBeanDefinition();
		jdbcTemplateBD.setBeanClassName("org.springframework.jdbc.core.JdbcTemplate");
		jdbcTemplateBD.setPropertyValues(new MutablePropertyValues());
		jdbcTemplateBD.getPropertyValues().add("dataSource", new RuntimeBeanReference("dataSource" + discriminator));

		beanFactory.registerBeanDefinition(beanName, jdbcTemplateBD);

		return beanName;
	}

	protected Map<String, Object> getPropertiesMap(final Properties connectionProperties) {
		HashMap<String, Object> propertiesMap = new HashMap<String, Object>();
		CollectionUtils.mergePropertiesIntoMap(connectionProperties, propertiesMap);
		Object driverPropertyValue = propertiesMap.remove(PROPERTY_DRIVER);
		if (driverPropertyValue != null) {
			propertiesMap.put(PROPERTY_DRIVER_CLASS_NAME, driverPropertyValue);
		}
		propertiesMap.remove(CLASS_NAME);
		propertiesMap.remove(NO_PROPERTIES_LOGGING);
		return propertiesMap;
	}

	protected String getProperty(Properties properties, String param, String def) {
		String property = properties.getProperty(param);
		if (!StringUtils.hasText(property)) {
			return def;
		}
		return property;
	}

	@Override
	public void setApplicationScanPaths(String[] applicationScanPaths) {
	}

}
