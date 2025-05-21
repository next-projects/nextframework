package org.nextframework.test.persistence;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.jdbc.Work;
import org.junit.Before;
import org.junit.Test;
import org.nextframework.persistence.HibernateCommand;
import org.nextframework.persistence.HibernateTransactionCommand;
import org.nextframework.persistence.HibernateTransactionSessionProvider;
import org.nextframework.persistence.SaveOrUpdateStrategy;

import junit.framework.Assert;

public class TestSaveOrUpdateStrategy extends TestHibernate {

	@SuppressWarnings("all")
	protected HibernateTransactionSessionProvider sessionProvider;

	@SuppressWarnings("rawtypes")
	@Override
	@Before
	public void setUp() throws ClassNotFoundException, SQLException {

		super.setUp();

		sessionProvider = new HibernateTransactionSessionProvider() {

			public Session newSession() {
				return TestSaveOrUpdateStrategy.super.session;
			}

			public SessionFactory getSessionFactory() {
				return TestSaveOrUpdateStrategy.super.sessionFactory;
			}

			public Object execute(HibernateCommand command) {
				try {
					return command.doInHibernate(newSession());
				} catch (HibernateException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public Object executeInTransaction(final HibernateTransactionCommand command) {
				Session newSession = newSession();
				System.out.println("begin");
				Transaction t = newSession.beginTransaction();
				try {
					Object value = execute(new HibernateCommand() {

						@SuppressWarnings("unchecked")
						@Override
						public Object doInHibernate(Session session) throws HibernateException {
							return command.doInHibernate(session, new Object());
						}

					});
					System.out.println("commit");
					t.commit();
					return value;
				} catch (Throwable e) {
					System.out.println("rollback");
					t.rollback();
					throw new RuntimeException(e);
				}
			}

		};

	}

	@Test
	public void testNew() {
		new SaveOrUpdateStrategy(sessionProvider, new TestEntityParent());
	}

	@Test
	public void testSaveEntity() {

		SaveOrUpdateStrategy ss = new SaveOrUpdateStrategy(sessionProvider, new TestEntityParent());
		ss.saveEntity();
		ss.execute();

		session.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				boolean saved = connection.prepareStatement("select * from testentityparent").executeQuery().next();
				Assert.assertEquals(true, saved);

				connection.prepareStatement("delete from testentityparent").executeUpdate();
				connection.commit();
			}

		});

	}

	@Test
	public void testSave2Entity() {

		SaveOrUpdateStrategy ss = new SaveOrUpdateStrategy(sessionProvider, new TestEntityParent("A"));
		ss.saveEntity();

		SaveOrUpdateStrategy ss2 = new SaveOrUpdateStrategy(sessionProvider, new TestEntityParent("B"));
		ss2.saveEntity();

		ss.attach(ss2);
		ss.execute();

		session.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				ResultSet rs = connection.prepareStatement("select id, name from testentityparent order by id").executeQuery();
				boolean saved = rs.next();
				Assert.assertEquals(true, saved);
				Assert.assertEquals("A", rs.getString("name"));
				saved = rs.next();
				Assert.assertEquals(true, saved);
				Assert.assertEquals("B", rs.getString("name"));

				connection.prepareStatement("delete from testentityparent").executeUpdate();
				connection.commit();
			}

		});

	}

	@Test
	public void testSave2EntityRollback() {

		SaveOrUpdateStrategy ss = new SaveOrUpdateStrategy(sessionProvider, new TestEntityParent("A"));
		ss.saveEntity();

		SaveOrUpdateStrategy ss2 = new SaveOrUpdateStrategy(sessionProvider, new TestEntityParent("B"));
		ss2.saveEntity();

		ss.attach(new HibernateCommand() {

			public Object doInHibernate(Session session) throws HibernateException {
				throw new IllegalAccessError("error");
			}

		});

		ss.attach(ss2);
		try {
			ss.execute();
		} catch (RuntimeException e) {
			if (!(e.getCause() instanceof IllegalAccessError)) {
				throw e;
			}
		} catch (IllegalAccessError e) {
		}

		session.doWork(new Work() {

			@Override
			public void execute(Connection connection) throws SQLException {
				ResultSet rs = connection.prepareStatement("select id, name from testentityparent order by id").executeQuery();
				boolean saved = rs.next();
				Assert.assertEquals(false, saved);

				connection.prepareStatement("delete from testentityparent").executeUpdate();
				connection.commit();
			}

		});

	}

}
