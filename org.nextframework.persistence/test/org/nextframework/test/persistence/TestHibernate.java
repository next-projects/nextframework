package org.nextframework.test.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.context.internal.ManagedSessionContext;
import org.hibernate.jdbc.Work;
import org.junit.After;
import org.junit.Before;

public class TestHibernate {

	protected SessionFactory sessionFactory;
	protected Session session;
	protected Transaction transaction;

	@Before
	public void setUp() throws Exception {

		Configuration config = new Configuration();

		config.setProperty("hibernate.show_sql", "true");
		config.setProperty("hibernate.hbm2ddl.auto", "update");
		config.setProperty("hibernate.current_session_context_class", "managed");

		config.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver");
		config.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:memdb");
		config.setProperty("hibernate.connection.username", "sa");
		config.setProperty("hibernate.connection.password", "update");

		addAnnotatedClasses(config);

		sessionFactory = config.buildSessionFactory();
		session = sessionFactory.openSession();
		transaction = session.beginTransaction();

		ManagedSessionContext.bind(session);

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
		if (transaction != null && transaction.isActive()) {
			transaction.rollback();
		}
		ManagedSessionContext.unbind(sessionFactory);
		session.close();
		sessionFactory.close();
	}

}
