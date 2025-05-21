package org.nextframework.persistence;

public interface HibernateTransactionSessionProvider<E> extends HibernateSessionProvider {

	/**
	 * Executes the command inside a transaction
	 * @param command
	 * @return
	 */
	Object executeInTransaction(HibernateTransactionCommand<E> command);

}
