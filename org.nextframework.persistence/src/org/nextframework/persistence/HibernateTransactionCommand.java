package org.nextframework.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public interface HibernateTransactionCommand<TS, BEAN> {

	BEAN doInHibernate(Session session, TS transactionStatus) throws HibernateException;

}
