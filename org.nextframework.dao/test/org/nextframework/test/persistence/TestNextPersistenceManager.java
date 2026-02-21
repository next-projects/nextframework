package org.nextframework.test.persistence;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.nextframework.persistence.internal.NextPersistenceManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class TestNextPersistenceManager {

	private NextPersistenceManager manager;
	private DataSource dataSource;

	@Before
	public void setUp() throws Exception {
		DriverManagerDataSource ds = new DriverManagerDataSource();
		ds.setDriverClassName("org.hsqldb.jdbcDriver");
		ds.setUrl("jdbc:hsqldb:mem:testPersistenceManager");
		ds.setUsername("sa");
		ds.setPassword("");
		dataSource = ds;

		manager = new NextPersistenceManager();
		manager.setDataSource(dataSource);
	}

	@After
	public void tearDown() throws Exception {
		if (manager != null) {
			try {
				manager.destroy();
			} catch (Exception e) {
				// ignore
			}
		}
	}

	@Test
	public void testDataSourceSet() {
		Assert.assertSame(dataSource, manager.getDataSource());
	}

	@Test
	public void testSessionFactoryCreated() throws Exception {
		manager.afterPropertiesSet();
		SessionFactory sf = manager.getSessionFactory();
		Assert.assertNotNull("SessionFactory should be created after initialization", sf);
		Assert.assertFalse("SessionFactory should be open", sf.isClosed());
	}

	@Test
	public void testSessionFactoryClosedOnDestroy() throws Exception {
		manager.afterPropertiesSet();
		SessionFactory sf = manager.getSessionFactory();
		Assert.assertFalse(sf.isClosed());
		manager.destroy();
		Assert.assertTrue("SessionFactory should be closed after destroy", sf.isClosed());
		manager = null; // prevent double-close in tearDown
	}

	@Test
	public void testSessionFactoryCanOpenSession() throws Exception {
		manager.afterPropertiesSet();
		SessionFactory sf = manager.getSessionFactory();
		org.hibernate.Session session = sf.openSession();
		Assert.assertNotNull("Should be able to open a session", session);
		Assert.assertTrue("Session should be open", session.isOpen());
		session.close();
	}

}
