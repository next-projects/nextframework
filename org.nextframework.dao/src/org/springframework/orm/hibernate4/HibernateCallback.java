package org.springframework.orm.hibernate4;

import org.hibernate.HibernateException;
import org.hibernate.Session;

public interface HibernateCallback<T> {

	T doInHibernate(Session session) throws HibernateException;

}