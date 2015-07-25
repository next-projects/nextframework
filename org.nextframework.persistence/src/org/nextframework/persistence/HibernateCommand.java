package org.nextframework.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public interface HibernateCommand {

	Object doInHibernate(Session session) throws HibernateException;
}
