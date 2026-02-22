
package org.springframework.orm.hibernate4;

import java.sql.SQLException;

import org.hibernate.JDBCException;
import org.springframework.dao.UncategorizedDataAccessException;

/**
 * Hibernate-specific subclass of UncategorizedDataAccessException,
 * for JDBC exceptions that Hibernate wrapped.
 *
 * @author Juergen Hoeller
 * @since 3.1
 * @see SessionFactoryUtils#convertHibernateAccessException
 */
@SuppressWarnings("serial")
public class HibernateJdbcException extends UncategorizedDataAccessException {

	public HibernateJdbcException(JDBCException ex) {
		super("JDBC exception on Hibernate data access: SQLException for SQL [" + ex.getSQL() + "]; SQL state [" +
				ex.getSQLState() + "]; error code [" + ex.getErrorCode() + "]; " + ex.getMessage(), ex);
	}

	/**
	 * Return the underlying SQLException.
	 */
	public SQLException getSQLException() {
		return ((JDBCException) getCause()).getSQLException();
	}

	/**
	 * Return the SQL that led to the problem.
	 */
	public String getSql() {
		return ((JDBCException) getCause()).getSQL();
	}

}