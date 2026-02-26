/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.orm.hibernate4;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Filter;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.nextframework.exception.ApplicationException;
import org.nextframework.persistence.PersistenceUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.orm.jpa.hibernate.HibernateTransactionManager;
import org.springframework.orm.jpa.hibernate.LocalSessionFactoryBean;
import org.springframework.orm.jpa.hibernate.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import jakarta.persistence.FlushModeType;

/**
 * Helper class that simplifies Hibernate data access code. Automatically
 * converts HibernateExceptions into DataAccessExceptions, following the
 * {@code org.springframework.dao} exception hierarchy.
 *
 * <p>The central method is {@code execute}, supporting Hibernate access code
 * implementing the {@link HibernateCallback} interface. It provides Hibernate Session
 * handling such that neither the HibernateCallback implementation nor the calling
 * code needs to explicitly care about retrieving/closing Hibernate Sessions,
 * or handling Session lifecycle exceptions. For typical single step actions,
 * there are various convenience methods (find, load, saveOrUpdate, delete).
 *
 * <p>Can be used within a service implementation via direct instantiation
 * with a SessionFactory reference, or get prepared in an application context
 * and given to services as bean reference. Note: The SessionFactory should
 * always be configured as bean in the application context, in the first case
 * given to the service directly, in the second case to the prepared template.
 *
 * <p><b>NOTE: Hibernate access code can also be coded in plain Hibernate style.
 * Hence, for newly started projects, consider adopting the standard Hibernate
 * style of coding data access objects instead, based on
 * {@link org.hibernate.SessionFactory#getCurrentSession()}.
 * This HibernateTemplate primarily exists as a migration helper for Hibernate 3
 * based data access code, to benefit from bug fixes in Hibernate 4.x.</b>
 *
 * @author Juergen Hoeller
 * @since 4.0.1
 * @see #setSessionFactory
 * @see HibernateCallback
 * @see org.hibernate.Session
 * @see LocalSessionFactoryBean
 * @see HibernateTransactionManager
 * @see org.springframework.orm.hibernate4.support.OpenSessionInViewFilter
 * @see org.springframework.orm.hibernate4.support.OpenSessionInViewInterceptor
 */
public class HibernateTemplate implements HibernateOperations, InitializingBean {

	protected final Log logger = LogFactory.getLog(getClass());

	private SessionFactory sessionFactory;

	private String[] filterNames;

	private boolean exposeNativeSession = false;

	private boolean checkWriteOperations = true;

	private boolean cacheQueries = false;

	private String queryCacheRegion;

	private int fetchSize = 0;

	private int maxResults = 0;

	/**
	 * Create a new HibernateTemplate instance.
	 */
	public HibernateTemplate() {
	}

	/**
	 * Create a new HibernateTemplate instance.
	 * @param sessionFactory the SessionFactory to create Sessions with
	 */
	public HibernateTemplate(SessionFactory sessionFactory) {
		setSessionFactory(sessionFactory);
		afterPropertiesSet();
	}

	/**
	 * Set the Hibernate SessionFactory that should be used to create
	 * Hibernate Sessions.
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * Return the Hibernate SessionFactory that should be used to create
	 * Hibernate Sessions.
	 */
	public SessionFactory getSessionFactory() {
		return this.sessionFactory;
	}

	/**
	 * Set one or more names of Hibernate filters to be activated for all
	 * Sessions that this accessor works with.
	 * <p>Each of those filters will be enabled at the beginning of each
	 * operation and correspondingly disabled at the end of the operation.
	 * This will work for newly opened Sessions as well as for existing
	 * Sessions (for example, within a transaction).
	 * @see #enableFilters(org.hibernate.Session)
	 * @see org.hibernate.Session#enableFilter(String)
	 */
	public void setFilterNames(String... filterNames) {
		this.filterNames = filterNames;
	}

	/**
	 * Return the names of Hibernate filters to be activated, if any.
	 */
	public String[] getFilterNames() {
		return this.filterNames;
	}

	/**
	 * Set whether to expose the native Hibernate Session to
	 * HibernateCallback code.
	 * <p>Default is "false": a Session proxy will be returned, suppressing
	 * {@code close} calls and automatically applying query cache
	 * settings and transaction timeouts.
	 * @see HibernateCallback
	 * @see org.hibernate.Session
	 * @see #setCacheQueries
	 * @see #setQueryCacheRegion
	 * @see #prepareQuery
	 * @see #prepareCriteria
	 */
	public void setExposeNativeSession(boolean exposeNativeSession) {
		this.exposeNativeSession = exposeNativeSession;
	}

	/**
	 * Return whether to expose the native Hibernate Session to
	 * HibernateCallback code, or rather a Session proxy.
	 */
	public boolean isExposeNativeSession() {
		return this.exposeNativeSession;
	}

	/**
	 * Set whether to check that the Hibernate Session is not in read-only mode
	 * in case of write operations (save/update/delete).
	 * <p>Default is "true", for fail-fast behavior when attempting write operations
	 * within a read-only transaction. Turn this off to allow save/update/delete
	 * on a Session with flush mode MANUAL.
	 * @see #checkWriteOperationAllowed
	 * @see org.springframework.transaction.TransactionDefinition#isReadOnly
	 */
	public void setCheckWriteOperations(boolean checkWriteOperations) {
		this.checkWriteOperations = checkWriteOperations;
	}

	/**
	 * Return whether to check that the Hibernate Session is not in read-only
	 * mode in case of write operations (save/update/delete).
	 */
	public boolean isCheckWriteOperations() {
		return this.checkWriteOperations;
	}

	/**
	 * Set whether to cache all queries executed by this template.
	 * <p>If this is "true", all Query and Criteria objects created by
	 * this template will be marked as cacheable (including all
	 * queries through find methods).
	 * <p>To specify the query region to be used for queries cached
	 * by this template, set the "queryCacheRegion" property.
	 * @see #setQueryCacheRegion
	 * @see org.hibernate.Query#setCacheable
	 * @see org.hibernate.Criteria#setCacheable
	 */
	public void setCacheQueries(boolean cacheQueries) {
		this.cacheQueries = cacheQueries;
	}

	/**
	 * Return whether to cache all queries executed by this template.
	 */
	public boolean isCacheQueries() {
		return this.cacheQueries;
	}

	/**
	 * Set the name of the cache region for queries executed by this template.
	 * <p>If this is specified, it will be applied to all Query and Criteria objects
	 * created by this template (including all queries through find methods).
	 * <p>The cache region will not take effect unless queries created by this
	 * template are configured to be cached via the "cacheQueries" property.
	 * @see #setCacheQueries
	 * @see org.hibernate.Query#setCacheRegion
	 * @see org.hibernate.Criteria#setCacheRegion
	 */
	public void setQueryCacheRegion(String queryCacheRegion) {
		this.queryCacheRegion = queryCacheRegion;
	}

	/**
	 * Return the name of the cache region for queries executed by this template.
	 */
	public String getQueryCacheRegion() {
		return this.queryCacheRegion;
	}

	/**
	 * Set the fetch size for this HibernateTemplate. This is important for processing
	 * large result sets: Setting this higher than the default value will increase
	 * processing speed at the cost of memory consumption; setting this lower can
	 * avoid transferring row data that will never be read by the application.
	 * <p>Default is 0, indicating to use the JDBC driver's default.
	 */
	public void setFetchSize(int fetchSize) {
		this.fetchSize = fetchSize;
	}

	/**
	 * Return the fetch size specified for this HibernateTemplate.
	 */
	public int getFetchSize() {
		return this.fetchSize;
	}

	/**
	 * Set the maximum number of rows for this HibernateTemplate. This is important
	 * for processing subsets of large result sets, avoiding to read and hold
	 * the entire result set in the database or in the JDBC driver if we're
	 * never interested in the entire result in the first place (for example,
	 * when performing searches that might return a large number of matches).
	 * <p>Default is 0, indicating to use the JDBC driver's default.
	 */
	public void setMaxResults(int maxResults) {
		this.maxResults = maxResults;
	}

	/**
	 * Return the maximum number of rows specified for this HibernateTemplate.
	 */
	public int getMaxResults() {
		return this.maxResults;
	}

	@Override
	public void afterPropertiesSet() {
		if (getSessionFactory() == null) {
			throw new IllegalArgumentException("Property 'sessionFactory' is required");
		}
	}

	@Override
	public <T> T execute(HibernateCallback<T> action) throws DataAccessException {
		return doExecute(action, false);
	}

	/**
	 * Execute the action specified by the given action object within a
	 * native {@link org.hibernate.Session}.
	 * <p>This execute variant overrides the template-wide
	 * {@link #isExposeNativeSession() "exposeNativeSession"} setting.
	 * @param action callback object that specifies the Hibernate action
	 * @return a result object returned by the action, or {@code null}
	 * @throws org.springframework.dao.DataAccessException in case of Hibernate errors
	 */
	public <T> T executeWithNativeSession(HibernateCallback<T> action) {
		return doExecute(action, true);
	}

	/**
	 * Execute the action specified by the given action object within a Session.
	 * @param action callback object that specifies the Hibernate action
	 * @param enforceNativeSession whether to enforce exposure of the native
	 * Hibernate Session to callback code
	 * @return a result object returned by the action, or {@code null}
	 * @throws org.springframework.dao.DataAccessException in case of Hibernate errors
	 */
	protected <T> T doExecute(HibernateCallback<T> action, boolean enforceNativeSession) throws DataAccessException {
		Assert.notNull(action, "Callback object must not be null");

		Session session = null;
		boolean isNew = false;
		try {
			session = getSessionFactory().getCurrentSession();
		} catch (HibernateException ex) {
			logger.debug("Could not retrieve pre-bound Hibernate session", ex);
		}
		if (session == null) {
			session = getSessionFactory().openSession();
			session.setFlushMode(FlushModeType.COMMIT);
			isNew = true;
		}

		try {
			enableFilters(session);
			Session sessionToExpose = (enforceNativeSession || isExposeNativeSession() ? session : createSessionProxy(session));
			return action.doInHibernate(sessionToExpose);
		} catch (HibernateException ex) {
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		} catch (RuntimeException ex) {
			// Callback code threw application exception...
			throw ex;
		} finally {
			if (isNew) {
				SessionFactoryUtils.closeSession(session);
			} else {
				disableFilters(session);
			}
		}
	}

	/**
	 * Create a close-suppressing proxy for the given Hibernate Session.
	 * The proxy also prepares returned Query and Criteria objects.
	 * @param session the Hibernate Session to create a proxy for
	 * @return the Session proxy
	 * @see org.hibernate.Session#close()
	 * @see #prepareQuery
	 * @see #prepareCriteria
	 */
	protected Session createSessionProxy(Session session) {
		return (Session) Proxy.newProxyInstance(
				session.getClass().getClassLoader(), new Class<?>[] { Session.class },
				new CloseSuppressingInvocationHandler(session));
	}

	/**
	 * Enable the specified filters on the given Session.
	 * @param session the current Hibernate Session
	 * @see #setFilterNames
	 * @see org.hibernate.Session#enableFilter(String)
	 */
	protected void enableFilters(Session session) {
		String[] filterNames = getFilterNames();
		if (filterNames != null) {
			for (String filterName : filterNames) {
				session.enableFilter(filterName);
			}
		}
	}

	/**
	 * Disable the specified filters on the given Session.
	 * @param session the current Hibernate Session
	 * @see #setFilterNames
	 * @see org.hibernate.Session#disableFilter(String)
	 */
	protected void disableFilters(Session session) {
		String[] filterNames = getFilterNames();
		if (filterNames != null) {
			for (String filterName : filterNames) {
				session.disableFilter(filterName);
			}
		}
	}

	//-------------------------------------------------------------------------
	// Convenience methods for loading individual objects
	//-------------------------------------------------------------------------

	@Override
	public <T> T get(Class<T> entityClass, Serializable id) throws DataAccessException {
		return get(entityClass, id, null);
	}

	@Override
	public <T> T get(final Class<T> entityClass, final Serializable id, final LockMode lockMode)
			throws DataAccessException {

		return executeWithNativeSession(new HibernateCallback<T>() {

			@Override
			@SuppressWarnings("all")
			public T doInHibernate(Session session) throws HibernateException {
				if (lockMode != null) {
					return (T) session.get(entityClass, id, new LockOptions(lockMode));
				} else {
					return (T) session.get(entityClass, id);
				}
			}

		});
	}

	@Override
	public Object get(String entityName, Serializable id) throws DataAccessException {
		return get(entityName, id, null);
	}

	@Override
	public Object get(final String entityName, final Serializable id, final LockMode lockMode)
			throws DataAccessException {

		return executeWithNativeSession(new HibernateCallback<Object>() {

			@Override
			@SuppressWarnings("all")
			public Object doInHibernate(Session session) throws HibernateException {
				if (lockMode != null) {
					return session.get(entityName, id, new LockOptions(lockMode));
				} else {
					return session.get(entityName, id);
				}
			}

		});
	}

	@Override
	public <T> T load(Class<T> entityClass, Serializable id) throws DataAccessException {
		return load(entityClass, id, null);
	}

	@Override
	public <T> T load(final Class<T> entityClass, final Serializable id, final LockMode lockMode)
			throws DataAccessException {

		return executeWithNativeSession(new HibernateCallback<T>() {

			@Override
			@SuppressWarnings("all")
			public T doInHibernate(Session session) throws HibernateException {
				if (lockMode != null) {
					return (T) session.get(entityClass, id, new LockOptions(lockMode));
				} else {
					return (T) session.get(entityClass, id);
				}
			}

		});
	}

	@Override
	public Object load(String entityName, Serializable id) throws DataAccessException {
		return load(entityName, id, null);
	}

	@Override
	public Object load(final String entityName, final Serializable id, final LockMode lockMode)
			throws DataAccessException {

		return executeWithNativeSession(new HibernateCallback<Object>() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				if (lockMode != null) {
					return session.get(entityName, id, new LockOptions(lockMode));
				} else {
					return session.get(entityName, id);
				}
			}

		});
	}

	@Override
	public <T> List<T> loadAll(final Class<T> entityClass) throws DataAccessException {
		throw new ApplicationException("NAO IMPLEMENTADO");
	}

	@Override
	public void load(final Object entity, final Serializable id) throws DataAccessException {
		executeWithNativeSession(new HibernateCallback<Object>() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				session.load(entity, id);
				return null;
			}

		});
	}

	@Override
	public void refresh(final Object entity) throws DataAccessException {
		refresh(entity, null);
	}

	@Override
	public void refresh(final Object entity, final LockMode lockMode) throws DataAccessException {
		executeWithNativeSession(new HibernateCallback<Object>() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				if (lockMode != null) {
					session.refresh(entity, new LockOptions(lockMode));
				} else {
					session.refresh(entity);
				}
				return null;
			}

		});
	}

	@Override
	public boolean contains(final Object entity) throws DataAccessException {
		return executeWithNativeSession(new HibernateCallback<Boolean>() {

			@Override
			public Boolean doInHibernate(Session session) {
				return session.contains(entity);
			}

		});
	}

	@Override
	public void evict(final Object entity) throws DataAccessException {
		executeWithNativeSession(new HibernateCallback<Object>() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				session.evict(entity);
				return null;
			}

		});
	}

	@Override
	public void initialize(Object proxy) throws DataAccessException {
		try {
			Hibernate.initialize(proxy);
		} catch (HibernateException ex) {
			throw SessionFactoryUtils.convertHibernateAccessException(ex);
		}
	}

	@Override
	public Filter enableFilter(String filterName) throws IllegalStateException {
		Session session = getSessionFactory().getCurrentSession();
		Filter filter = session.getEnabledFilter(filterName);
		if (filter == null) {
			filter = session.enableFilter(filterName);
		}
		return filter;
	}

	//-------------------------------------------------------------------------
	// Convenience methods for storing individual objects
	//-------------------------------------------------------------------------

	@Override
	public void lock(final Object entity, final LockMode lockMode) throws DataAccessException {
		throw new ApplicationException("NAO IMPLEMENTADO");
	}

	@Override
	public void lock(final String entityName, final Object entity, final LockMode lockMode) throws DataAccessException {
		throw new ApplicationException("NAO IMPLEMENTADO");
	}

	@Override
	public Serializable save(final Object entity) throws DataAccessException {
		return executeWithNativeSession(new HibernateCallback<Serializable>() {

			@Override
			public Serializable doInHibernate(Session session) throws HibernateException {
				checkWriteOperationAllowed(session);
				session.persist(entity);
				PersistenceUtils.refreshBeanId(session, entity, null);
				return (Serializable) session.getIdentifier(entity);
			}

		});
	}

	@Override
	public Serializable save(final String entityName, final Object entity) throws DataAccessException {
		return executeWithNativeSession(new HibernateCallback<Serializable>() {

			@Override
			public Serializable doInHibernate(Session session) throws HibernateException {
				checkWriteOperationAllowed(session);
				session.persist(entityName, entity);
				PersistenceUtils.refreshBeanId(session, entity, null);
				return (Serializable) session.getIdentifier(entity);
			}

		});
	}

	@Override
	public void update(Object entity) throws DataAccessException {
		update(entity, null);
	}

	@Override
	public void update(final Object entity, final LockMode lockMode) throws DataAccessException {
		executeWithNativeSession(new HibernateCallback<Object>() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				checkWriteOperationAllowed(session);
				Object merged = session.merge(entity);
				PersistenceUtils.refreshBeanId(session, entity, merged);
				return null;
			}

		});
	}

	@Override
	public void update(String entityName, Object entity) throws DataAccessException {
		update(entityName, entity, null);
	}

	@Override
	public void update(final String entityName, final Object entity, final LockMode lockMode)
			throws DataAccessException {

		executeWithNativeSession(new HibernateCallback<Object>() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				checkWriteOperationAllowed(session);
				Object merged = session.merge(entityName, entity);
				PersistenceUtils.refreshBeanId(session, entity, merged);
				return null;
			}

		});
	}

	@Override
	public void saveOrUpdate(final Object entity) throws DataAccessException {
		executeWithNativeSession(new HibernateCallback<Object>() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				checkWriteOperationAllowed(session);
				Object merged = session.merge(entity);
				PersistenceUtils.refreshBeanId(session, entity, merged);
				return null;
			}

		});
	}

	@Override
	public void saveOrUpdate(final String entityName, final Object entity) throws DataAccessException {
		executeWithNativeSession(new HibernateCallback<Object>() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				checkWriteOperationAllowed(session);
				Object merged = session.merge(entityName, entity);
				PersistenceUtils.refreshBeanId(session, entity, merged);
				return null;
			}

		});
	}

	@Override
	public void replicate(final Object entity, final ReplicationMode replicationMode)
			throws DataAccessException {

		executeWithNativeSession(new HibernateCallback<Object>() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				checkWriteOperationAllowed(session);
				session.replicate(entity, replicationMode);
				return null;
			}

		});
	}

	@Override
	public void replicate(final String entityName, final Object entity, final ReplicationMode replicationMode)
			throws DataAccessException {

		executeWithNativeSession(new HibernateCallback<Object>() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				checkWriteOperationAllowed(session);
				session.replicate(entityName, entity, replicationMode);
				return null;
			}

		});
	}

	@Override
	public void persist(final Object entity) throws DataAccessException {
		executeWithNativeSession(new HibernateCallback<Object>() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				checkWriteOperationAllowed(session);
				session.persist(entity);
				PersistenceUtils.refreshBeanId(session, entity, null);
				return null;
			}

		});
	}

	@Override
	public void persist(final String entityName, final Object entity) throws DataAccessException {
		executeWithNativeSession(new HibernateCallback<Object>() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				checkWriteOperationAllowed(session);
				session.persist(entityName, entity);
				PersistenceUtils.refreshBeanId(session, entity, null);
				return null;
			}

		});
	}

	@Override
	public <T> T merge(final T entity) throws DataAccessException {
		return executeWithNativeSession(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException {
				checkWriteOperationAllowed(session);
				Object merged = session.merge(entity);
				PersistenceUtils.refreshBeanId(session, entity, merged);
				return null;
			}

		});
	}

	@Override
	public <T> T merge(final String entityName, final T entity) throws DataAccessException {
		return executeWithNativeSession(new HibernateCallback<T>() {

			@Override
			public T doInHibernate(Session session) throws HibernateException {
				checkWriteOperationAllowed(session);
				Object merged = session.merge(entityName, entity);
				PersistenceUtils.refreshBeanId(session, entity, merged);
				return null;
			}

		});
	}

	@Override
	public void delete(Object entity) throws DataAccessException {
		delete(entity, null);
	}

	@Override
	public void delete(final Object entity, final LockMode lockMode) throws DataAccessException {
		executeWithNativeSession(new HibernateCallback<Object>() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				checkWriteOperationAllowed(session);
				session.remove(entity);
				return null;
			}

		});
	}

	@Override
	public void delete(String entityName, Object entity) throws DataAccessException {
		delete(entityName, entity, null);
	}

	@Override
	public void delete(final String entityName, final Object entity, final LockMode lockMode)
			throws DataAccessException {
		throw new ApplicationException("NAO IMPLEMENTADO");
	}

	@Override
	public void deleteAll(final Collection<?> entities) throws DataAccessException {
		executeWithNativeSession(new HibernateCallback<Object>() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				checkWriteOperationAllowed(session);
				for (Object entity : entities) {
					session.remove(entity);
				}
				return null;
			}

		});
	}

	@Override
	public void flush() throws DataAccessException {
		executeWithNativeSession(new HibernateCallback<Object>() {

			@Override
			public Object doInHibernate(Session session) throws HibernateException {
				session.flush();
				return null;
			}

		});
	}

	@Override
	public void clear() throws DataAccessException {
		executeWithNativeSession(new HibernateCallback<Object>() {

			@Override
			public Object doInHibernate(Session session) {
				session.clear();
				return null;
			}

		});
	}

	//-------------------------------------------------------------------------
	// Convenience finder methods for HQL strings
	//-------------------------------------------------------------------------

	@Override
	public List<?> find(final String queryString, final Object... values) throws DataAccessException {
		return executeWithNativeSession(new HibernateCallback<List<?>>() {

			@Override
			@SuppressWarnings("all")
			public List<?> doInHibernate(Session session) throws HibernateException {
				Query queryObject = session.createQuery(queryString);
				prepareQuery(queryObject);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						queryObject.setParameter(i, values[i]);
					}
				}
				return queryObject.list();
			}

		});
	}

	@Override
	public List<?> findByNamedParam(String queryString, String paramName, Object value)
			throws DataAccessException {

		return findByNamedParam(queryString, new String[] { paramName }, new Object[] { value });
	}

	@Override
	public List<?> findByNamedParam(final String queryString, final String[] paramNames, final Object[] values)
			throws DataAccessException {

		if (paramNames.length != values.length) {
			throw new IllegalArgumentException("Length of paramNames array must match length of values array");
		}
		return executeWithNativeSession(new HibernateCallback<List<?>>() {

			@Override
			@SuppressWarnings("all")
			public List<?> doInHibernate(Session session) throws HibernateException {
				Query queryObject = session.createQuery(queryString);
				prepareQuery(queryObject);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
					}
				}
				return queryObject.list();
			}

		});
	}

	@Override
	public List<?> findByValueBean(final String queryString, final Object valueBean)
			throws DataAccessException {

		return executeWithNativeSession(new HibernateCallback<List<?>>() {

			@Override
			@SuppressWarnings("all")
			public List<?> doInHibernate(Session session) throws HibernateException {
				Query queryObject = session.createQuery(queryString);
				prepareQuery(queryObject);
				queryObject.setProperties(valueBean);
				return queryObject.list();
			}

		});
	}

	//-------------------------------------------------------------------------
	// Convenience finder methods for named queries
	//-------------------------------------------------------------------------

	@Override
	public List<?> findByNamedQuery(final String queryName, final Object... values) throws DataAccessException {
		return executeWithNativeSession(new HibernateCallback<List<?>>() {

			@Override
			@SuppressWarnings("all")
			public List<?> doInHibernate(Session session) throws HibernateException {
				Query queryObject = session.getNamedQuery(queryName);
				prepareQuery(queryObject);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						queryObject.setParameter(i, values[i]);
					}
				}
				return queryObject.list();
			}

		});
	}

	@Override
	public List<?> findByNamedQueryAndNamedParam(String queryName, String paramName, Object value)
			throws DataAccessException {

		return findByNamedQueryAndNamedParam(queryName, new String[] { paramName }, new Object[] { value });
	}

	@Override
	public List<?> findByNamedQueryAndNamedParam(
			final String queryName, final String[] paramNames, final Object[] values)
			throws DataAccessException {

		if (values != null && (paramNames == null || paramNames.length != values.length)) {
			throw new IllegalArgumentException("Length of paramNames array must match length of values array");
		}
		return executeWithNativeSession(new HibernateCallback<List<?>>() {

			@Override
			@SuppressWarnings("all")
			public List<?> doInHibernate(Session session) throws HibernateException {
				Query queryObject = session.getNamedQuery(queryName);
				prepareQuery(queryObject);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						applyNamedParameterToQuery(queryObject, paramNames[i], values[i]);
					}
				}
				return queryObject.list();
			}

		});
	}

	@Override
	public List<?> findByNamedQueryAndValueBean(final String queryName, final Object valueBean)
			throws DataAccessException {

		return executeWithNativeSession(new HibernateCallback<List<?>>() {

			@Override
			@SuppressWarnings("all")
			public List<?> doInHibernate(Session session) throws HibernateException {
				Query queryObject = session.getNamedQuery(queryName);
				prepareQuery(queryObject);
				queryObject.setProperties(valueBean);
				return queryObject.list();
			}

		});
	}

	//-------------------------------------------------------------------------
	// Convenience finder methods for detached criteria
	//-------------------------------------------------------------------------

	@Override
	public <T> List<T> findByExample(T exampleEntity) throws DataAccessException {
		return findByExample(null, exampleEntity, -1, -1);
	}

	@Override
	public <T> List<T> findByExample(String entityName, T exampleEntity) throws DataAccessException {
		return findByExample(entityName, exampleEntity, -1, -1);
	}

	@Override
	public <T> List<T> findByExample(T exampleEntity, int firstResult, int maxResults) throws DataAccessException {
		return findByExample(null, exampleEntity, firstResult, maxResults);
	}

	@Override
	public <T> List<T> findByExample(
			final String entityName, final T exampleEntity, final int firstResult, final int maxResults)
			throws DataAccessException {
		throw new ApplicationException("NAO IMPLEMENTADO");
	}

	//-------------------------------------------------------------------------
	// Convenience query methods for iteration and bulk updates/deletes
	//-------------------------------------------------------------------------

	@Override
	public Iterator<?> iterate(final String queryString, final Object... values) throws DataAccessException {
		throw new ApplicationException("NAO IMPLEMENTADO");
	}

	@Override
	public void closeIterator(Iterator<?> it) throws DataAccessException {
		throw new ApplicationException("NAO IMPLEMENTADO");
	}

	@Override
	public int bulkUpdate(final String queryString, final Object... values) throws DataAccessException {
		return executeWithNativeSession(new HibernateCallback<Integer>() {

			@Override
			@SuppressWarnings("all")
			public Integer doInHibernate(Session session) throws HibernateException {
				Query queryObject = session.createQuery(queryString);
				prepareQuery(queryObject);
				if (values != null) {
					for (int i = 0; i < values.length; i++) {
						queryObject.setParameter(i, values[i]);
					}
				}
				return queryObject.executeUpdate();
			}

		});
	}

	//-------------------------------------------------------------------------
	// Helper methods used by the operations above
	//-------------------------------------------------------------------------

	/**
	 * Check whether write operations are allowed on the given Session.
	 * <p>Default implementation throws an InvalidDataAccessApiUsageException in
	 * case of {@code FlushMode.MANUAL}. Can be overridden in subclasses.
	 * @param session current Hibernate Session
	 * @throws InvalidDataAccessApiUsageException if write operations are not allowed
	 * @see #setCheckWriteOperations
	 * @see org.hibernate.Session#getFlushMode()
	 * @see org.hibernate.FlushMode#MANUAL
	 */
	protected void checkWriteOperationAllowed(Session session) throws InvalidDataAccessApiUsageException {
		if (isCheckWriteOperations() && session.getFlushMode().ordinal() < FlushModeType.COMMIT.ordinal()) {
			throw new InvalidDataAccessApiUsageException(
					"Write operations are not allowed in read-only mode (FlushMode.MANUAL): " +
							"Turn your Session into FlushMode.COMMIT/AUTO or remove 'readOnly' marker from transaction definition.");
		}
	}

	/**
	 * Prepare the given Query object, applying cache settings and/or
	 * a transaction timeout.
	 * @param queryObject the Query object to prepare
	 * @see #setCacheQueries
	 * @see #setQueryCacheRegion
	 */
	protected void prepareQuery(Query queryObject) {
		if (isCacheQueries()) {
			queryObject.setCacheable(true);
			if (getQueryCacheRegion() != null) {
				queryObject.setCacheRegion(getQueryCacheRegion());
			}
		}
		if (getFetchSize() > 0) {
			queryObject.setFetchSize(getFetchSize());
		}
		if (getMaxResults() > 0) {
			queryObject.setMaxResults(getMaxResults());
		}

		SessionHolder sessionHolder = (SessionHolder) TransactionSynchronizationManager.getResource(getSessionFactory());
		if (sessionHolder != null && sessionHolder.hasTimeout()) {
			queryObject.setTimeout(sessionHolder.getTimeToLiveInSeconds());
		}
	}

	/**
	 * Apply the given name parameter to the given Query object.
	 * @param queryObject the Query object
	 * @param paramName the name of the parameter
	 * @param value the value of the parameter
	 * @throws HibernateException if thrown by the Query object
	 */
	protected void applyNamedParameterToQuery(Query queryObject, String paramName, Object value)
			throws HibernateException {

		if (value instanceof Collection) {
			queryObject.setParameterList(paramName, (Collection<?>) value);
		} else if (value instanceof Object[]) {
			queryObject.setParameterList(paramName, (Object[]) value);
		} else {
			queryObject.setParameter(paramName, value);
		}
	}

	/**
	 * Invocation handler that suppresses close calls on Hibernate Sessions.
	 * Also prepares returned Query and Criteria objects.
	 * @see org.hibernate.Session#close
	 */
	private class CloseSuppressingInvocationHandler implements InvocationHandler {

		private final Session target;

		public CloseSuppressingInvocationHandler(Session target) {
			this.target = target;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// Invocation on Session interface coming in...

			if (method.getName().equals("equals")) {
				// Only consider equal when proxies are identical.
				return (proxy == args[0]);
			} else if (method.getName().equals("hashCode")) {
				// Use hashCode of Session proxy.
				return System.identityHashCode(proxy);
			} else if (method.getName().equals("close")) {
				// Handle close method: suppress, not valid.
				return null;
			}

			// Invoke method on target Session.
			try {
				Object retVal = method.invoke(this.target, args);

				// If return value is a Query or Criteria, apply transaction timeout.
				// Applies to createQuery, getNamedQuery, createCriteria.
				if (retVal instanceof Query) {
					prepareQuery(((Query) retVal));
				}

				return retVal;
			} catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}

		}

	}

}