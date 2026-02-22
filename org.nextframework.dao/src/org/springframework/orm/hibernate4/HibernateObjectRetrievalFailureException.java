
package org.springframework.orm.hibernate4;

import org.hibernate.UnresolvableObjectException;
import org.hibernate.WrongClassException;
import org.springframework.orm.ObjectRetrievalFailureException;

/**
 * Hibernate-specific subclass of ObjectRetrievalFailureException.
 * Converts Hibernate's UnresolvableObjectException and WrongClassException.
 *
 * @author Juergen Hoeller
 * @since 3.1
 * @see SessionFactoryUtils#convertHibernateAccessException
 */
@SuppressWarnings("serial")
public class HibernateObjectRetrievalFailureException extends ObjectRetrievalFailureException {

	public HibernateObjectRetrievalFailureException(UnresolvableObjectException ex) {
		super(ex.getEntityName(), ex.getIdentifier(), ex.getMessage(), ex);
	}

	public HibernateObjectRetrievalFailureException(WrongClassException ex) {
		super(ex.getEntityName(), ex.getIdentifier(), ex.getMessage(), ex);
	}

}