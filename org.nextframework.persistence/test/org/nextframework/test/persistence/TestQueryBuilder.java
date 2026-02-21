package org.nextframework.test.persistence;

import java.sql.SQLException;

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

import junit.framework.Assert;

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
	public void testFromChild() {
		QueryBuilder qb = new QueryBuilder(builderSessionProvider);
		qb.from(TestEntityChild.class).list();
	}

	@Test
	public void testInverseCollection() {
		InverseCollectionProperties inverseCollectionProperty = PersistenceUtils.getInverseCollectionProperty(sessionFactory, TestEntityExt.class, "children");
		Assert.assertEquals("parentSuper", inverseCollectionProperty.property);
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void test() {
		session.persist(new TestEntityParent());
		session.persist(new TestEntityParent());
		session.flush();
		QueryBuilder qb = new QueryBuilder(builderSessionProvider);
		qb.from(TestEntityParent.class).fetchCollection("children").list();
	}

	@Test
	public void testJoinFetch() {
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
	public void testJoinFetchTranslator() {
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testCollectionSetType() {
		session.persist(new TestEntityParent());
		session.persist(new TestEntityParent());
		session.flush();
		QueryBuilder qb = new QueryBuilder(builderSessionProvider);
		qb.from(TestEntityParent.class).fetchCollection("childrenSet").list();
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testResultSetTranslator() {
		session.persist(new TestEntityParent());
		session.persist(new TestEntityParent());
		session.flush();
		QueryBuilder qb = new QueryBuilder(builderSessionProvider);
		qb.select("testEntityParent.name, testEntityParent.id").from(TestEntityParent.class).fetchCollection("childrenSet").list();
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testResultSetTranslatorAutoId() {
		session.persist(new TestEntityParent());
		session.persist(new TestEntityParent());
		session.flush();
		QueryBuilder qb = new QueryBuilder(builderSessionProvider);
		qb.select("testEntityParent.name").from(TestEntityParent.class).fetchCollection("childrenSet").list();
	}

	@Test
	public void testInvertCollectionType() {
		InverseCollectionProperties inverseCollectionProperty = PersistenceUtils.getInverseCollectionProperty(sessionFactory, TestEntityParent.class, "children");
		Assert.assertEquals(TestEntityChild.class, inverseCollectionProperty.type);
		Assert.assertEquals("parent", inverseCollectionProperty.property);
	}

	@Test
	public void testInvertCollectionTypeInvalid() throws Exception {
		try {
			PersistenceUtils.getInverseCollectionProperty(sessionFactory, TestEntityParent.class, "name");
			Assert.fail("invalid property did not throw exception");
		} catch (PersistenceException e) {
		}
	}

	// ================= HQL Generation Tests =================

	@Test
	public void testGetQuerySimpleFrom() {
		QueryBuilder<TestEntityParent> qb = new QueryBuilder<TestEntityParent>(builderSessionProvider);
		qb.from(TestEntityParent.class);
		String hql = qb.getQuery();
		Assert.assertTrue("HQL should contain SELECT", hql.contains("SELECT"));
		Assert.assertTrue("HQL should contain FROM", hql.contains("FROM"));
		Assert.assertTrue("HQL should contain entity class name", hql.contains(TestEntityParent.class.getName()));
	}

	@Test
	public void testGetQueryWithAlias() {
		QueryBuilder<TestEntityParent> qb = new QueryBuilder<TestEntityParent>(builderSessionProvider);
		qb.from(TestEntityParent.class);
		String hql = qb.getQuery();
		Assert.assertTrue("HQL should contain alias 'testEntityParent'", hql.contains("testEntityParent"));
	}

	@Test
	public void testGetQueryWithCustomSelect() {
		QueryBuilder<TestEntityParent> qb = new QueryBuilder<TestEntityParent>(builderSessionProvider);
		qb.select("testEntityParent.name, testEntityParent.id").from(TestEntityParent.class);
		String hql = qb.getQuery();
		Assert.assertTrue("HQL should contain custom select fields", hql.contains("testEntityParent.name"));
		Assert.assertTrue("HQL should contain id in select", hql.contains("testEntityParent.id"));
	}

	@Test
	public void testGetQueryWithWhere() {
		QueryBuilder<TestEntityParent> qb = new QueryBuilder<TestEntityParent>(builderSessionProvider);
		qb.from(TestEntityParent.class).where("testEntityParent.name = 'test'");
		String hql = qb.getQuery();
		Assert.assertTrue("HQL should contain WHERE clause", hql.contains("WHERE"));
		Assert.assertTrue("HQL should contain where condition", hql.contains("testEntityParent.name = 'test'"));
	}

	@Test
	public void testGetQueryWithMultipleWheres() {
		QueryBuilder<TestEntityParent> qb = new QueryBuilder<TestEntityParent>(builderSessionProvider);
		qb.from(TestEntityParent.class)
				.where("testEntityParent.name = 'test'")
				.where("testEntityParent.id > 0");
		String hql = qb.getQuery();
		Assert.assertTrue("HQL should contain AND between where clauses", hql.contains("AND"));
	}

	@Test
	public void testGetQueryWithJoin() {
		QueryBuilder<TestEntityChild> qb = new QueryBuilder<TestEntityChild>(builderSessionProvider);
		qb.from(TestEntityChild.class).leftOuterJoin("testEntityChild.parent parent");
		String hql = qb.getQuery();
		Assert.assertTrue("HQL should contain LEFT OUTER JOIN", hql.contains("LEFT OUTER JOIN"));
		Assert.assertTrue("HQL should contain join path", hql.contains("testEntityChild.parent parent"));
	}

	@Test
	public void testGetQueryWithJoinFetch() {
		QueryBuilder<TestEntityChild> qb = new QueryBuilder<TestEntityChild>(builderSessionProvider);
		qb.from(TestEntityChild.class).leftOuterJoinFetch("testEntityChild.parent");
		String hql = qb.getQuery();
		Assert.assertTrue("HQL should contain LEFT OUTER JOIN FETCH", hql.contains("LEFT OUTER JOIN FETCH"));
	}

	@Test
	public void testGetQueryWithInnerJoin() {
		QueryBuilder<TestEntityChild> qb = new QueryBuilder<TestEntityChild>(builderSessionProvider);
		qb.from(TestEntityChild.class).join("testEntityChild.parent");
		String hql = qb.getQuery();
		Assert.assertTrue("HQL should contain INNER JOIN", hql.contains("INNER JOIN"));
	}

	@Test
	public void testGetQueryWithGroupBy() {
		QueryBuilder<TestEntityParent> qb = new QueryBuilder<TestEntityParent>(builderSessionProvider);
		qb.select("testEntityParent.name, count(testEntityParent.id)")
				.from(TestEntityParent.class)
				.groupBy("testEntityParent.name");
		String hql = qb.getQuery();
		Assert.assertTrue("HQL should contain GROUP BY", hql.contains("GROUP BY"));
		Assert.assertTrue("HQL should contain group by field", hql.contains("testEntityParent.name"));
	}

	@Test
	public void testGetQueryWithGroupByAndHaving() {
		QueryBuilder<TestEntityParent> qb = new QueryBuilder<TestEntityParent>(builderSessionProvider);
		qb.select("testEntityParent.name, count(testEntityParent.id)")
				.from(TestEntityParent.class)
				.groupBy("testEntityParent.name", "count(testEntityParent.id) > 1");
		String hql = qb.getQuery();
		Assert.assertTrue("HQL should contain GROUP BY", hql.contains("GROUP BY"));
		Assert.assertTrue("HQL should contain HAVING", hql.contains("HAVING"));
	}

	@Test
	public void testGetQueryWithOrderBy() {
		QueryBuilder<TestEntityParent> qb = new QueryBuilder<TestEntityParent>(builderSessionProvider);
		qb.from(TestEntityParent.class).orderBy("testEntityParent.name ASC");
		String hql = qb.getQuery();
		Assert.assertTrue("HQL should contain ORDER BY", hql.contains("ORDER BY"));
		Assert.assertTrue("HQL should contain order field", hql.contains("testEntityParent.name ASC"));
	}

	@Test
	public void testGetQueryWithParameterizedWhere() {
		QueryBuilder<TestEntityParent> qb = new QueryBuilder<TestEntityParent>(builderSessionProvider);
		qb.from(TestEntityParent.class).where("testEntityParent.name = ?", "test");
		String hql = qb.getQuery();
		Assert.assertTrue("HQL should contain WHERE clause", hql.contains("WHERE"));
		// Parameters should be converted to named parameters
		Assert.assertTrue("HQL should contain named parameter", hql.contains(":param"));
	}

	@Test
	public void testGetQueryNoWhereWhenParameterNull() {
		QueryBuilder<TestEntityParent> qb = new QueryBuilder<TestEntityParent>(builderSessionProvider);
		qb.from(TestEntityParent.class).where("testEntityParent.name = ?", (Object) null);
		String hql = qb.getQuery();
		Assert.assertFalse("HQL should not contain WHERE when parameter is null", hql.contains("WHERE"));
	}

	@Test
	public void testSelectInnerClassToString() {
		QueryBuilder.Select select = new QueryBuilder.Select("entity.name, entity.id");
		Assert.assertEquals("SELECT entity.name, entity.id", select.toString());
	}

	@Test
	public void testFromInnerClassToString() {
		QueryBuilder.From from = new QueryBuilder.From(TestEntityParent.class, "parent");
		String str = from.toString();
		Assert.assertTrue(str.startsWith("FROM "));
		Assert.assertTrue(str.contains(TestEntityParent.class.getName()));
		Assert.assertTrue(str.endsWith(" parent"));
	}

	@Test
	public void testJoinInnerClassToString() {
		QueryBuilder.Join join = new QueryBuilder.Join(QueryBuilder.JoinMode.LEFT_OUTER, true, "entity.prop");
		String str = join.toString();
		Assert.assertTrue("Should contain LEFT OUTER", str.contains("LEFT OUTER"));
		Assert.assertTrue("Should contain JOIN", str.contains("JOIN"));
		Assert.assertTrue("Should contain FETCH", str.contains("FETCH"));
		Assert.assertTrue("Should contain path", str.contains("entity.prop"));
	}

	@Test
	public void testJoinInnerClassDontFetchToString() {
		QueryBuilder.Join join = new QueryBuilder.Join(QueryBuilder.JoinMode.INNER, true, "entity.prop");
		String str = join.dontFetchToString();
		Assert.assertFalse("Should not contain FETCH", str.contains("FETCH"));
		Assert.assertTrue("Should contain INNER JOIN", str.contains("INNER JOIN"));
	}

	@Test
	public void testGroupByInnerClassToString() {
		QueryBuilder.GroupBy gb = new QueryBuilder.GroupBy("entity.name", null);
		Assert.assertEquals("GROUP BY entity.name", gb.toString());
	}

	@Test
	public void testGroupByWithHavingToString() {
		QueryBuilder.GroupBy gb = new QueryBuilder.GroupBy("entity.name", "count(*) > 1");
		String str = gb.toString();
		Assert.assertTrue(str.contains("GROUP BY entity.name"));
		Assert.assertTrue(str.contains("HAVING count(*) > 1"));
	}

	@Test
	public void testJoinModeToString() {
		Assert.assertEquals("LEFT OUTER", QueryBuilder.JoinMode.LEFT_OUTER.toString());
		Assert.assertEquals("INNER", QueryBuilder.JoinMode.INNER.toString());
		Assert.assertEquals("RIGHT OUTER", QueryBuilder.JoinMode.RIGHT_OUTER.toString());
	}

}
