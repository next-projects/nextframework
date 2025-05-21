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
	public Object executeInTransaction(final HibernateTransactionCommand<TransactionStatus> command) {
		return getTransactionTemplate().execute(new TransactionCallback<Object>() {

			public Object doInTransaction(final TransactionStatus status) {
				return execute(new HibernateCommand() {

					public Object doInHibernate(Session session) throws HibernateException {
						return command.doInHibernate(session, status);
					}

				});
			}

		});
	}

}
