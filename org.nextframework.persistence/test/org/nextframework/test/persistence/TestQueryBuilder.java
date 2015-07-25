package org.nextframework.test.persistence;

import java.sql.SQLException;

import junit.framework.Assert;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.nextframework.persistence.HibernateCommand;
import org.nextframework.persistence.HibernateSessionProvider;
import org.nextframework.persistence.PersistenceException;
import org.nextframework.persistence.PersistenceUtils;
import org.nextframework.persistence.PersistenceUtils.InverseCollectionProperties;
import org.nextframework.persistence.QueryBuilder;

public class TestQueryBuilder extends TestHibernate {

	private HibernateSessionProvider builderSessionProvider;

	@Override
	@Before
	public void setUp() throws ClassNotFoundException, SQLException {
		super.setUp();
		builderSessionProvider = new HibernateSessionProvider() {
			public Session newSession() {
				return TestQueryBuilder.super.session;
			}
			public SessionFactory getSessionFactory() {
				return TestQueryBuilder.super.sessionFactory;
			}
			public Object execute(HibernateCommand command) {
				try {
					return command.doInHibernate(newSession());
				} catch (Exception e) {
					return null;
				}
			}
		};
	}

	@Test
	@SuppressWarnings("all")
	public void testFromChild(){
		QueryBuilder qb = new QueryBuilder(builderSessionProvider);
		qb.from(TestEntityChild.class).list();
	}
	
	@Test
	public void testInverseCollection(){
		InverseCollectionProperties inverseCollectionProperty = PersistenceUtils.getInverseCollectionProperty(sessionFactory, TestEntityExt.class, "children");
		Assert.assertEquals("parentSuper", inverseCollectionProperty.property);
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void test(){
		session.persist(new TestEntityParent());
		session.persist(new TestEntityParent());
		session.flush();
		QueryBuilder qb = new QueryBuilder(builderSessionProvider);
		qb.from(TestEntityParent.class).fetchCollection("children").list();
	}
	
	@Test
	public void testJoinFetch(){
		TestEntityParent a = new TestEntityParent();
		TestEntityChild child = new TestEntityChild();
		child.setParent(a);
		
		session.persist(a);
		session.flush();
		session.persist(child);
		session.flush();
		QueryBuilder<TestEntityChild> qb = new QueryBuilder<TestEntityChild>(builderSessionProvider);
		TestEntityChild testEntityChild = qb.from(TestEntityChild.class).leftOuterJoinFetch("testEntityChild.parent").list().get(0);
		Assert.assertNotNull(testEntityChild.getParent());
	}
	
	@Test
	public void testJoinFetchTranslator(){
		TestEntityParent a = new TestEntityParent();
		a.setName("name");
		TestEntityChild child = new TestEntityChild();
		child.setParent(a);
		
		session.persist(a);
		session.flush();
		session.persist(child);
		session.flush();
		QueryBuilder<TestEntityChild> qb = new QueryBuilder<TestEntityChild>(builderSessionProvider);
		TestEntityChild testEntityChild = qb
											.select("testEntityChild.id, parent.id, parent.name")
											.from(TestEntityChild.class)
											.leftOuterJoin("testEntityChild.parent parent")
											.list().get(0);
		Assert.assertNotNull(testEntityChild.getParent());
		Assert.assertEquals("name", testEntityChild.getParent().getName());
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testCollectionSetType(){
		session.persist(new TestEntityParent());
		session.persist(new TestEntityParent());
		session.flush();
		QueryBuilder qb = new QueryBuilder(builderSessionProvider);
		qb.from(TestEntityParent.class).fetchCollection("childrenSet").list();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testResultSetTranslator(){
		session.persist(new TestEntityParent());
		session.persist(new TestEntityParent());
		session.flush();
		QueryBuilder qb = new QueryBuilder(builderSessionProvider);
		qb.select("testEntityParent.name, testEntityParent.id").from(TestEntityParent.class).fetchCollection("childrenSet").list();
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void testResultSetTranslatorAutoId(){
		session.persist(new TestEntityParent());
		session.persist(new TestEntityParent());
		session.flush();
		QueryBuilder qb = new QueryBuilder(builderSessionProvider);
		qb.select("testEntityParent.name").from(TestEntityParent.class).fetchCollection("childrenSet").list();
	}
	
	@Test
	public void testInvertCollectionType(){
		InverseCollectionProperties inverseCollectionProperty = PersistenceUtils.getInverseCollectionProperty(sessionFactory, TestEntityParent.class, "children");
		Assert.assertEquals(TestEntityChild.class, inverseCollectionProperty.type);
		Assert.assertEquals("parent", inverseCollectionProperty.property);
	}
	
	@Test
	public void testInvertCollectionTypeInvalid() throws Exception{
		try {
			PersistenceUtils.getInverseCollectionProperty(sessionFactory, TestEntityParent.class, "name");
			Assert.fail("invalid property did not throw exception");
		} catch (PersistenceException e) {
		}
	}
	
}
