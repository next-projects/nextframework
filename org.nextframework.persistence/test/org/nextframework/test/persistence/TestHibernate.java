package org.nextframework.test.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.Work;
import org.junit.After;
import org.junit.Before;

public class TestHibernate {

	protected SessionFactory sessionFactory;
	protected Session session;
	protected Transaction transaction;

	@Before
	public void setUp() throws ClassNotFoundException, SQLException {

		Configuration annotationConfiguration = new Configuration();

		addAnnotatedClasses(annotationConfiguration);

		annotationConfiguration.setProperty("hibernate.show_sql", "true");
		annotationConfiguration.setProperty("hibernate.hbm2ddl.auto", "update");
		annotationConfiguration.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
		annotationConfiguration.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:memdb");
		annotationConfiguration.setProperty("hibernate.connection.username", "sa");
		annotationConfiguration.setProperty("hibernate.connection.password", "update");

		sessionFactory = annotationConfiguration.buildSessionFactory();

		session = sessionFactory.openSession();
		transaction = session.beginTransaction();

		//validateHibernateSession();
	}

	protected void addAnnotatedClasses(Configuration annotationConfiguration) {
		annotationConfiguration.addAnnotatedClass(TestEntityChild.class);
		annotationConfiguration.addAnnotatedClass(TestEntityParent.class);
		annotationConfiguration.addAnnotatedClass(TestEntityExt.class);
		annotationConfiguration.addAnnotatedClass(TestEntitySuper.class);
	}

	public void validateHibernateSession() {
		session.doWork(new Work() {

			public void execute(Connection connection) throws SQLException {
				ResultSet rs = connection.prepareStatement("select testentity0_.id as id1_, testentity0_.name as name1_ from TestEntityParent testentity0_").executeQuery();
				if (rs == null) {
					throw new RuntimeException("Erro!");
				}
			}

		});
	}

	@After
	public void tearDown() throws SQLException {
		if (transaction.isActive()) {
			transaction.rollback();
		}
		session.close();
		sessionFactory.close();
	}

}
