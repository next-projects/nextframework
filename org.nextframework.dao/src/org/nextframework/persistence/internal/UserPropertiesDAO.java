package org.nextframework.persistence.internal;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

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

	public void delete(UserKeyValueMapEntity userProperty) {
		Session session = sessionFactory.openSession();
		try {
			delete(userProperty, session);
			session.flush();
		} finally {
			session.close();
		}
	}

	private void delete(UserKeyValueMapEntity userProperty, Session session) {
		session.remove(userProperty);
	}

	public void saveKey(UserKeyValueMapEntity keyValueMapEntity) {
		Session session = sessionFactory.openSession();
		try {
			session.merge(keyValueMapEntity);
			session.flush();
		} finally {
			session.close();
		}
	}

	private UserKeyValueMapEntity getUserKey(String propertyName, Session session) {
		Query createQuery = session.createQuery(
				"from " + UserKeyValueMapEntity.class.getSimpleName() + " map " +
						"where map.username = :username and map.key = :key");
		createQuery.setParameter("username", username);
		createQuery.setParameter("key", propertyName);
		UserKeyValueMapEntity userKeyValueMapEntity = (UserKeyValueMapEntity) createQuery.uniqueResult();
		return userKeyValueMapEntity;
	}

	public UserKeyValueMapEntity createUnsavedUserKey() {
		UserKeyValueMapEntity k = new UserKeyValueMapEntity();
		k.setUsername(username);
		return k;
	}

}
