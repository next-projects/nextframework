package org.nextframework.test.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.Before;
import org.junit.Test;
import org.nextframework.persistence.FileDAO;
import org.nextframework.persistence.GenericDAO;
import org.nextframework.persistence.HibernateCommand;
import org.nextframework.persistence.HibernateTransactionCommand;
import org.nextframework.persistence.HibernateTransactionSessionProvider;
import org.nextframework.persistence.PersistenceConfiguration;
import org.nextframework.types.File;
import org.springframework.orm.hibernate4.HibernateTemplate;

public class TestFileDao extends TestHibernate {

	@Override
	@Before
	public void setUp() throws Exception {

		super.setUp();

		HibernateTransactionSessionProvider sessionProvider = new HibernateTransactionSessionProvider() {

			public Session newSession() {
				return TestFileDao.super.session;
			}

			public SessionFactory getSessionFactory() {
				return TestFileDao.super.sessionFactory;
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
				Transaction t = newSession.getTransaction();
				if (t == null || !t.isActive()) {
					t = newSession.beginTransaction();
				}
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

		PersistenceConfiguration config = new PersistenceConfiguration();
		config.setSessionProvider(sessionProvider);
		PersistenceConfiguration.configure(config);

	}

	@Override
	protected void addAnnotatedClasses(Configuration annotationConfiguration) {
		super.addAnnotatedClasses(annotationConfiguration);
		annotationConfiguration.addAnnotatedClass(TestEntitySuperDao.class);
		annotationConfiguration.addAnnotatedClass(TestEntityChildDAO.class);
		annotationConfiguration.addAnnotatedClass(TestEntityExtDao.class);
		annotationConfiguration.addAnnotatedClass(TestEntityFile.class);
	}

	@Test
	@SuppressWarnings("all")
	public void testSaveChildFile() throws Exception {

		final FileDAO<?> fd = new FileDAO(TestEntityFile.class, true);
		GenericDAO<TestEntityExtDao> dao = new GenericDAO<TestEntityExtDao>(TestEntityExtDao.class) {

			{
				fileDAO = (FileDAO<File>) fd;
				initDao();
			}

		};

		PersistenceConfiguration config = PersistenceConfiguration.getConfig();
		HibernateTemplate ht = new HibernateTemplate(config.getSessionProvider().getSessionFactory());
		fd.setPersistenceContext(config.getPersistenceContext());
		dao.setPersistenceContext(config.getPersistenceContext());
		fd.setHibernateTemplate(ht);
		dao.setHibernateTemplate(ht);

		TestEntityExtDao child1 = new TestEntityExtDao();
		dao.saveOrUpdate(child1);
		System.out.println(child1.getId());

		TestEntityFile entityFile = new TestEntityFile();
		entityFile.setName("f.txt");
		entityFile.setSize(10L);
		entityFile.setContent(new byte[10]);
		child1 = new TestEntityExtDao();
		child1.setId(1L);
		child1.setEntityFile(entityFile);
		dao.saveOrUpdate(child1);

	}

}
