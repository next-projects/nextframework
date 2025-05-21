package org.nextframework.persistence.internal;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class UserPersistentMap implements Map<String, String> {

	protected UserPropertiesDAO userPropertiesDAO;

	protected Map<String, UserKeyValueMapEntity> userPropertiesMap = new HashMap<String, UserKeyValueMapEntity>();

	public UserPersistentMap(UserPropertiesDAO userPropertiesDAO) {
		this.userPropertiesDAO = userPropertiesDAO;
	}

	@Override
	public int size() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isEmpty() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsKey(Object key) {
		return get(key) != null;
	}

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String get(Object key) {
		UserKeyValueMapEntity r = loadUserProperty(key);
		if (r == null) {
			return null;
		}
		return r.getValue();
	}

	public UserKeyValueMapEntity loadUserProperty(Object key) {
		if (key == null) {
			throw new NullPointerException("invalid null key");
		}
		UserKeyValueMapEntity r = userPropertiesMap.get(key);
		if (r == null) {
			r = userPropertiesDAO.getUserKey(key.toString());
			if (r != null) {
				userPropertiesMap.put(key.toString(), r);
			}
		}
		return r;
	}

	@Override
	public String put(String key, String value) {
		if (key == null) {
			throw new NullPointerException("invalid null key");
		}
		UserKeyValueMapEntity r = loadUserProperty(key);
		String oldValue = null;
		if (r == null) {
			r = userPropertiesDAO.createUnsavedUserKey();
			r.setKey(key);
		} else {
			oldValue = r.getValue();
		}
		r.setValue(value);
		persist(r);
		return oldValue;
	}

	@Override
	public String remove(Object key) {
		UserKeyValueMapEntity r = loadUserProperty(key);
		if (r == null) {
			return null;
		}
		userPropertiesMap.remove(key);
		delete(r);
		return r.getValue();
	}

	@Override
	public void putAll(Map<? extends String, ? extends String> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<String> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<java.util.Map.Entry<String, String>> entrySet() {
		throw new UnsupportedOperationException();
	}

	private void delete(UserKeyValueMapEntity userProperty) {
		userPropertiesDAO.delete(userProperty);
	}

	private void persist(UserKeyValueMapEntity userProperty) {
		userPropertiesDAO.saveKey(userProperty);
	}

}
