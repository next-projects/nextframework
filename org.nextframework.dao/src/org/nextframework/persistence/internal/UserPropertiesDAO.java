package org.nextframework.persistence.internal;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nextframework.persistence.HibernateTransactionCommand;
import org.nextframework.persistence.HibernateTransactionSessionProvider;
import org.nextframework.persistence.PersistenceConfiguration;

public class UserPropertiesDAO {

	private SessionFactory sessionFactory;
	private String username;

	public UserPropertiesDAO(SessionFactory sessionFactory, String username) {
		this.sessionFactory = sessionFactory;
		this.username = username;
	}

	public UserKeyValueMapEntity getUserKey(String propertyName) {
		Session session = sessionFactory.openSession();
		try {
			return getUserKey(propertyName, session);
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("all")
	public void delete(UserKeyValueMapEntity userProperty) {
		HibernateTransactionSessionProvider sessionProvider = (HibernateTransactionSessionProvider) PersistenceConfiguration.getConfig().getSessionProvider();
		sessionProvider.executeInTransaction(new HibernateTransactionCommand() {

			@Override
			public Object doInHibernate(Session session, Object transactionStatus) {
				try {
					session.remove(userProperty);
					session.flush();
				} finally {
					session.close();
				}
				return null;
			}

		});
	}

	@SuppressWarnings("all")
	public void saveKey(UserKeyValueMapEntity keyValueMapEntity) {
		HibernateTransactionSessionProvider sessionProvider = (HibernateTransactionSessionProvider) PersistenceConfiguration.getConfig().getSessionProvider();
		sessionProvider.executeInTransaction(new HibernateTransactionCommand() {

			@Override
			public Object doInHibernate(Session session, Object transactionStatus) {
				try {
					session.merge(keyValueMapEntity);
					session.flush();
				} finally {
					session.close();
				}
				return null;
			}

		});
	}

	private UserKeyValueMapEntity getUserKey(String propertyName, Session session) {
		Query<UserKeyValueMapEntity> createQuery = session.createQuery(
				"from " + UserKeyValueMapEntity.class.getSimpleName() + " map " +
						"where map.username = :username and map.key = :key",
				UserKeyValueMapEntity.class);
		createQuery.setParameter("username", username);
		createQuery.setParameter("key", propertyName);
		UserKeyValueMapEntity userKeyValueMapEntity = createQuery.uniqueResult();
		return userKeyValueMapEntity;
	}

	public UserKeyValueMapEntity createUnsavedUserKey() {
		UserKeyValueMapEntity k = new UserKeyValueMapEntity();
		k.setUsername(username);
		return k;
	}

}
