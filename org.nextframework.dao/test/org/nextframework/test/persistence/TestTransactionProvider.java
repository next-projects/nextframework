package org.nextframework.test.persistence;

import java.sql.SQLException;

import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nextframework.persistence.HibernateTransactionTemplateSessionProvider;
import org.nextframework.persistence.PersistenceConfiguration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.jpa.hibernate.HibernateTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class TestTransactionProvider extends TestSaveOrUpdateStrategy {

	@Before
	@Override
	public void setUp() throws Exception {

		Configuration config = new Configuration();

		config.setProperty("hibernate.show_sql", "true");
		config.setProperty("hibernate.hbm2ddl.auto", "update");
		config.setProperty("hibernate.current_session_context_class", "org.springframework.orm.jpa.hibernate.SpringSessionContext");

		addAnnotatedClasses(config);

		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
		dataSource.setUrl("jdbc:hsqldb:mem:memdb");
		dataSource.setUsername("sa");
		dataSource.setPassword("update");

		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(config.getProperties())
				.applySetting(Environment.JAKARTA_NON_JTA_DATASOURCE, dataSource)
				.build();

		sessionFactory = config.buildSessionFactory(serviceRegistry);

		PersistenceConfiguration persistenceConfiguration = new PersistenceConfiguration();
		PersistenceConfiguration.configure(persistenceConfiguration);

		HibernateTemplate ht = new HibernateTemplate(sessionFactory);
		HibernateTransactionManager htm = new HibernateTransactionManager(sessionFactory);
		TransactionTemplate tt = new TransactionTemplate(htm);

		HibernateTransactionTemplateSessionProvider sp = new HibernateTransactionTemplateSessionProvider(ht, tt);
		sp.setConfigureQueryBuilder(true);

		sp.afterPropertiesSet();
		tt.afterPropertiesSet();
		ht.afterPropertiesSet();
		htm.afterPropertiesSet();

		sessionProvider = sp;

		// Open a session for verification queries (not part of the transaction)
		session = sessionFactory.openSession();

	}

	protected void configureDB(Configuration config) {

	}

	@Override
	@Test
	public void testNew() {
		super.testNew();
	}

	@Override
	@Test
	public void testSaveEntity() {
		super.testSaveEntity();
	}

	@Override
	@Test
	public void testSave2Entity() {
		super.testSave2Entity();
	}

	@Override
	@Test
	public void testSave2EntityRollback() {
		super.testSave2EntityRollback();
	}

	@After
	@Override
	public void tearDown() throws SQLException {
		if (session != null && session.isOpen()) {
			session.close();
		}
		sessionFactory.close();
	}

}
