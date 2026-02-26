package org.nextframework.persistence;

import org.hibernate.SessionFactory;

public interface HibernateSessionProvider {

	SessionFactory getSessionFactory();

	<BEAN> BEAN execute(HibernateCommand<BEAN> command);

}
