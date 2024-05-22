package org.nextframework.test.persistence;

import org.junit.Before;
import org.junit.Test;
import org.nextframework.persistence.HibernateTransactionTemplateSessionProvider;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class TestTransactionProvider extends TestSaveOrUpdateStrategy {

	@Before
	public void before() throws Exception {
		super.setUp();
		HibernateTemplate ht = new HibernateTemplate(sessionFactory);
		HibernateTransactionManager htm = new HibernateTransactionManager(sessionFactory);
		TransactionTemplate tt = new TransactionTemplate(htm);
		HibernateTransactionTemplateSessionProvider sp = new HibernateTransactionTemplateSessionProvider(ht, tt);
		sp.setConfigureQueryBuilder(true);
		ht.afterPropertiesSet();
		htm.afterPropertiesSet();
		tt.afterPropertiesSet();
		sp.afterPropertiesSet();
		sessionProvider = sp;
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

}
