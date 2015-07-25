package org.nextframework.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public interface HibernateTransactionCommand<E> {

	Object doInHibernate(Session session, E transactionStatus) throws HibernateException;
}
