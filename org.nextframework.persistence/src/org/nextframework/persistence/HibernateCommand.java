package org.nextframework.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public interface HibernateCommand<BEAN> {

	BEAN doInHibernate(Session session) throws HibernateException;

}