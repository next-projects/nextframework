
package org.springframework.orm.hibernate4;

import org.hibernate.QueryException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;

/**
 * Hibernate-specific subclass of InvalidDataAccessResourceUsageException,
 * thrown on invalid HQL query syntax.
 *
 * @author Juergen Hoeller
 * @since 3.1
 * @see SessionFactoryUtils#convertHibernateAccessException
 */
@SuppressWarnings("serial")
public class HibernateQueryException extends InvalidDataAccessResourceUsageException {

	public HibernateQueryException(QueryException ex) {
		super(ex.getMessage(), ex);
	}

	/**
	 * Return the HQL query string that was invalid.
	 */
	public String getQueryString() {
		return ((QueryException) getCause()).getQueryString();
	}

}