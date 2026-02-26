package org.nextframework.persistence;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public class HibernateTransactionTemplateSessionProvider extends HibernateTemplateSessionProvider implements HibernateTransactionSessionProvider<TransactionStatus> {

	public HibernateTransactionTemplateSessionProvider() {
	}

	public HibernateTransactionTemplateSessionProvider(HibernateTemplate hibernateTemplate, TransactionTemplate transactionTemplate) {
		setHibernateTemplate(hibernateTemplate);
		setTransactionTemplate(transactionTemplate);
	}

	TransactionTemplate transactionTemplate;

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	@Override
	public <BEAN> BEAN executeInTransaction(HibernateTransactionCommand<TransactionStatus, BEAN> command) {
		return getTransactionTemplate().execute(new TransactionCallback<BEAN>() {

			public BEAN doInTransaction(TransactionStatus status) {
				return execute(new HibernateCommand<BEAN>() {

					public BEAN doInHibernate(Session session) throws HibernateException {
						return command.doInHibernate(session, status);
					}

				});
			}

		});
	}

}
