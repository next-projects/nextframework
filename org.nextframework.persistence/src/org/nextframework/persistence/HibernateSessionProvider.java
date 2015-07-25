package org.nextframework.persistence;

import org.hibernate.SessionFactory;

public interface HibernateSessionProvider {

	SessionFactory getSessionFactory();
	
	Object execute(HibernateCommand command);
}
