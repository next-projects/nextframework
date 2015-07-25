package org.nextframework.persistence.internal;

import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * <p>Manages an internal connection to a data source used by nextframework to store persistent data.
 * 
 * <p>This manager will be automatically registered if there is a bean named <i>dataSourceNextFramework</i>.
 * This <i>dataSourceNextFramework</i> can be an alias to an existing dataSource.
 * 
 * @see org.nextframework.context.support.HibernateBeanDefinitionLoader
 *  
 * @author rogelgarcia
 *
 */
public class NextPersistenceManager implements InitializingBean, DisposableBean {
	
	private SessionFactory sessionFactory;
	
	private DataSource dataSource;
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public DataSource getDataSource() {
		return dataSource;
	}
	
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initializeHibernate();
	}
	
	private void initializeHibernate() {
		Configuration config = new Configuration()
				.setProperty("hibernate.hbm2ddl.auto", "update")
				.setProperty("hibernate.show_sql", "false")
				.addAnnotatedClass(UserKeyValueMapEntity.class);
		StandardServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(config.getProperties())
				.applySetting(Environment.DATASOURCE, dataSource)
				.build();
		this.sessionFactory = config
				.buildSessionFactory(serviceRegistry);
		
	}

	@Override
	public void destroy() throws Exception {
		sessionFactory.close();
	}
	
	protected UserPropertiesDAO getPropertiesDAOForUser(String username) {
		return new UserPropertiesDAO(this.sessionFactory, username);
	}

	public Map<String, String> getPropertiesMapForUser(String username){
		return new UserPersistentMap(getPropertiesDAOForUser(username));
	}
}
