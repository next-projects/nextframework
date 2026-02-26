package org.nextframework.persistence;

public interface HibernateTransactionSessionProvider<TS> extends HibernateSessionProvider {

	/**
	 * Executes the command inside a transaction
	 * @param command
	 * @return
	 */
	<BEAN> BEAN executeInTransaction(HibernateTransactionCommand<TS, BEAN> command);

}
