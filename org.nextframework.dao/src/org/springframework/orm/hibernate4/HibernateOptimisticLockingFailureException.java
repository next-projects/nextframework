package org.springframework.orm.hibernate4;

import org.hibernate.StaleObjectStateException;
import org.hibernate.StaleStateException;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

/**
 * Hibernate-specific subclass of ObjectOptimisticLockingFailureException.
 * Converts Hibernate's StaleObjectStateException, StaleStateException
 * and OptimisticEntityLockException.
 *
 * @author Juergen Hoeller
 * @since 3.1
 * @see SessionFactoryUtils#convertHibernateAccessException
 */
@SuppressWarnings("serial")
public class HibernateOptimisticLockingFailureException extends ObjectOptimisticLockingFailureException {

	public HibernateOptimisticLockingFailureException(StaleObjectStateException ex) {
		super(ex.getEntityName(), ex.getIdentifier(), ex);
	}

	public HibernateOptimisticLockingFailureException(StaleStateException ex) {
		super(ex.getMessage(), ex);
	}

	public HibernateOptimisticLockingFailureException(OptimisticEntityLockException ex) {
		super(ex.getMessage(), ex);
	}

}