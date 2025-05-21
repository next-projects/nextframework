/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2009 the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * You may obtain a copy of the license at
 * 
 *     http://www.gnu.org/copyleft/lesser.html
 * 
 */
package org.nextframework.persistence;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.annotation.DescriptionProperty;
import org.nextframework.controller.crud.ListViewFilter;
import org.nextframework.exception.NextException;
import org.nextframework.persistence.QueryBuilder.JoinMode;
import org.nextframework.types.File;
import org.nextframework.util.ReflectionCache;
import org.nextframework.util.ReflectionCacheFactory;
import org.nextframework.util.Util;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.AutowireCandidateQualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.support.HibernateDaoSupport;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Generic implementation of a DataAccessObject. 
 * <p> Implements basic data operations. It is aware of CRUD views and has specialized methods.
 * 
 * @author rogelgarcia
 * @param <BEAN>
 */
public class GenericDAO<BEAN> extends HibernateDaoSupport implements DAO<BEAN>, ApplicationContextAware {

	protected Log log = LogFactory.getLog(this.getClass());

	protected Class<BEAN> beanClass;
	protected String orderBy;

	private String persistenceContext;
	private ApplicationContext applicationContext;
	protected JdbcTemplate jdbcTemplate;
	protected TransactionTemplate transactionTemplate;

	protected List<PropertyDescriptor> fileProperties = new ArrayList<PropertyDescriptor>();
	protected FileDAO<File> fileDAO;

	public GenericDAO(Class<BEAN> beanClass) {
		this.beanClass = beanClass;
		ReflectionCache reflectionCache = ReflectionCacheFactory.getReflectionCache();
		DefaultOrderBy orderBy = reflectionCache.getAnnotation(this.getClass(), DefaultOrderBy.class);
		if (orderBy != null) {
			this.orderBy = orderBy.value();
		}
		checkFileProperties();
	}

	@SuppressWarnings("unchecked")
	public GenericDAO() {
		beanClass = (Class<BEAN>) GenericTypeResolver.resolveTypeArgument(this.getClass(), DAO.class);
		ReflectionCache reflectionCache = ReflectionCacheFactory.getReflectionCache();
		DefaultOrderBy orderBy = reflectionCache.getAnnotation(this.getClass(), DefaultOrderBy.class);
		if (orderBy != null) {
			this.orderBy = orderBy.value();
		}
		checkFileProperties();
	}

	protected void checkFileProperties() {
		if (isDetectFileProperties()) {
			BeanWrapper beanWrapper;
			try {
				beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(beanClass.newInstance());
				PropertyDescriptor[] propertyDescriptors = beanWrapper.getPropertyDescriptors();
				for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
					if (File.class.isAssignableFrom(propertyDescriptor.getPropertyType())) {
						if (propertyDescriptor.getReadMethod().getAnnotation(Transient.class) == null) {
							fileProperties.add(propertyDescriptor);
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException("Cannot check file type for dao", e);
			}
		}
	}

	/**
	 * Indica se os atributos do tipo File devem ser detectados automaticamente para salvar e carregar ao carregar o bean
	 * @return
	 */
	protected boolean isDetectFileProperties() {
		return true;
	}

	protected void init() {

	}

	@SuppressWarnings("all")
	@Override
	protected final void initDao() throws Exception {
		if (fileProperties.size() > 0 && this.fileDAO == null) {
			//achar o DAO para os files...
			Map<String, FileDAO> beans = applicationContext.getBeansOfType(FileDAO.class);
			if (beans.size() == 1) {
				this.fileDAO = beans.values().iterator().next();
			} else if (beans.size() == 0) {
				//nao existe um DAO definido na aplicacao, definir um default
				this.fileDAO = new FileDAO((Class<File>) fileProperties.get(0).getPropertyType(), true);//todas as propriedades do tipo arquivo devem ser da mesma classe
				this.fileDAO.setHibernateTemplate(getHibernateTemplate());
				this.fileDAO.setJdbcTemplate(getJdbcTemplate());
				this.fileDAO.setSessionFactory(getSessionFactory());
				this.fileDAO.setTransactionTemplate(getTransactionTemplate());
			}
		}
		init();
	}

	public String getPersistenceContext() {
		return persistenceContext;
	}

	public void setPersistenceContext(String persistenceContext) {
		this.persistenceContext = persistenceContext;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	public JdbcTemplate getJdbcTemplate() {
		if (jdbcTemplate == null) {
			String[] beans = applicationContext.getBeanNamesForType(DataSource.class);
			if (beans.length == 1) {
				jdbcTemplate = new JdbcTemplate(applicationContext.getBean(beans[0], DataSource.class));
			} else if (applicationContext instanceof ConfigurableApplicationContext) {
				//TODO THIS LOGIC SHOULD NOT BE HERE
				//check the qualifiers to find the correct datasource
				ConfigurableListableBeanFactory beanFactory = ((ConfigurableApplicationContext) applicationContext).getBeanFactory();
				for (String beanName : beans) {
					AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) beanFactory.getBeanDefinition(beanName);
					Set<AutowireCandidateQualifier> qualifiers = beanDefinition.getQualifiers();
					for (AutowireCandidateQualifier autowireCandidateQualifier : qualifiers) {
						String[] attributeNames = autowireCandidateQualifier.attributeNames();
						for (String attribute : attributeNames) {
							Object value = autowireCandidateQualifier.getMetadataAttribute(attribute).getValue();
							if (getPersistenceContext().equals(value)) {
								jdbcTemplate = new JdbcTemplate(applicationContext.getBean(beanName, DataSource.class));
								return jdbcTemplate;
							}
						}
					}
				}
			}
		}
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	@Override
	protected HibernateTemplate createHibernateTemplate(SessionFactory sessionFactory) {
		if (getHibernateTemplate() != null) {
			return getHibernateTemplate();
		}
		return super.createHibernateTemplate(sessionFactory);
	}

	//cache
	private Map<String, QueryBuilder<BEAN>> queryWithSimpleFieldsCache = new HashMap<String, QueryBuilder<BEAN>>();

	/**
	 * Retorna a query contendo apenas o @Id e o @DescriptionProperty, além de campos extras.
	 */
	protected QueryBuilder<BEAN> queryWithSimpleFields(String... extraFields) {

		String key = extraFields != null && extraFields.length > 0 ? Arrays.toString(extraFields) : beanClass.getSimpleName();
		QueryBuilder<BEAN> queryModel = queryWithSimpleFieldsCache.get(key);
		if (queryModel == null) {
			queryModel = newQueryWithSimpleFields(extraFields);
			queryWithSimpleFieldsCache.put(key, queryModel);
		}

		QueryBuilder<BEAN> query = new QueryBuilder<BEAN>();
		query.select(queryModel.getSelect());
		query.from(queryModel.getFrom());
		query.joinAll(queryModel.getJoins());
		query.fetchAllCollections(queryModel.getFetches());
		query.setResultTranslatorClass(queryModel.getResultTranslatorClass());

		return query;
	}

	private QueryBuilder<BEAN> newQueryWithSimpleFields(String... extraFields) {

		String[] selectedProperties = getSimpleFields();

		if (extraFields != null && extraFields.length > 0) {
			BeanDescriptor beanDescriptor = BeanDescriptorFactory.forClass(beanClass);
			List<String> selectedPropertiesList = new ArrayList<String>(Arrays.asList(selectedProperties));
			for (String extraField : extraFields) {
				org.nextframework.bean.PropertyDescriptor pd = beanDescriptor.getPropertyDescriptor(extraField);
				if (pd.getAnnotation(Transient.class) != null) {
					continue;
				}
				selectedPropertiesList.add(extraField);
			}
			selectedProperties = selectedPropertiesList.toArray(new String[selectedPropertiesList.size()]);
		}

		return queryWithFields(selectedProperties);
	}

	public String[] getSimpleFields() {

		BeanDescriptor bd = BeanDescriptorFactory.forClass(beanClass);
		String descriptionPropertyName = bd.getDescriptionPropertyName();
		String idPropertyName = bd.getIdPropertyName();

		String[] selectedProperties;
		if (descriptionPropertyName == null) {
			selectedProperties = new String[] { idPropertyName };
		} else {

			org.nextframework.bean.PropertyDescriptor pd = bd.getPropertyDescriptor(descriptionPropertyName);
			DescriptionProperty descriptionProperty = pd.getAnnotation(DescriptionProperty.class);
			String[] usingFields = descriptionProperty != null ? descriptionProperty.usingFields() : null; //TODO PROCURAR O @DescriptionProperty nas classes superiores

			if (usingFields != null && usingFields.length > 0) {
				selectedProperties = new String[usingFields.length + 1];
				selectedProperties[0] = idPropertyName;
				for (int i = 0; i < usingFields.length; i++) {
					selectedProperties[i + 1] = usingFields[i];
				}
			} else {
				selectedProperties = new String[] { idPropertyName, descriptionPropertyName };
				if (bd.getPropertyDescriptor(descriptionPropertyName).getAnnotation(Transient.class) != null) {
					log.warn("@DescriptionProperty of " + bd.getTargetClass() + " is transient and must declare usingFields!");
				}
			}

		}

		return selectedProperties;
	}

	protected QueryBuilder<BEAN> queryWithFields(String... fields) {

		QueryBuilder<BEAN> query = new QueryBuilder<BEAN>();

		String fromAlias = Util.strings.uncaptalize(beanClass.getSimpleName());
		query.from(beanClass, fromAlias);

		if (fields != null && fields.length > 0) {

			String select = "";

			for (String field : fields) {

				String mainAttribute = field;
				if (mainAttribute.contains(".")) {
					mainAttribute = field.substring(0, field.indexOf("."));
				}

				Method getterMethod = Util.beans.getGetterMethod(beanClass, mainAttribute);
				if (getterMethod != null) {

					OneToMany otm = AnnotationUtils.getAnnotation(getterMethod, OneToMany.class);
					if (otm != null) {

						if (!mainAttribute.equals(field)) {
							throw new IllegalArgumentException("Atributos OneToMany não podem ter sub-atributos especificados com ponto!");
						}

						query.fetchCollection(mainAttribute);

					} else {

						ManyToOne mto = AnnotationUtils.getAnnotation(getterMethod, ManyToOne.class);
						OneToOne oto = AnnotationUtils.getAnnotation(getterMethod, OneToOne.class);

						if (mto != null || oto != null) {

							select += (select.length() == 0 ? "" : ", ") + field;
							query.join(JoinMode.LEFT_OUTER, false, fromAlias + "." + mainAttribute + " " + mainAttribute);

						} else {

							if (!mainAttribute.equals(field)) {
								throw new IllegalArgumentException("Atributos primitivos não podem ter sub-atributos especificados com ponto!");
							}

							select += (select.length() == 0 ? "" : ", ") + (field.contains(".") ? field : fromAlias + "." + field);

						}

					}

				} else {
					throw new IllegalArgumentException("O atributo " + mainAttribute + " é inváldo!");
				}

			}

			if (Util.strings.isNotEmpty(select)) {

				//Se apenas 1 atributo foi informado, é importante forçar um atributo simples para que o select tenha mais de 1 coluna.
				if (!select.contains(",")) {
					BeanDescriptor beanDescriptor = BeanDescriptorFactory.forClass(beanClass);
					String idPropertyName = beanDescriptor.getIdPropertyName();
					select += "," + fromAlias + "." + idPropertyName;
				}

				query.select(select);

			}

		}

		return query;
	}

	protected QueryBuilder<BEAN> queryWithOrderBy() {
		QueryBuilder<BEAN> query = query();
		if (this.orderBy != null) {
			query.orderBy(orderBy);
		}
		return query;
	}

	protected QueryBuilder<BEAN> query() {
		return query(beanClass);
	}

	protected <E> QueryBuilder<E> query(Class<E> clazz) {
		return newQueryBuilder(clazz).from(clazz);
	}

	/**
	 * Cria um query builder no devido 'PersistenceContext' para ser utilizado pelo DAO
	 */
	public <E> QueryBuilder<E> newQueryBuilder(Class<E> clazz) {
		return new QueryBuilder<E>(getPersistenceContext());
	}

	/* (non-Javadoc)
	 * @see org.nextframework.persistence.DAO#saveOrUpdate(BEAN)
	 */
	public void saveOrUpdate(final BEAN bean) {
		SaveOrUpdateStrategy save = getSaveOrUpdateCompleteStrategy(bean);
		save.execute();
		//getHibernateTemplate().flush();
	}

	protected SaveOrUpdateStrategy getSaveOrUpdateCompleteStrategy(final BEAN bean) {
		SaveOrUpdateStrategy save = save(bean);
		verifyFileToSave(bean, save);
		updateSaveOrUpdate(save);
		return save;
	}

	/**
	 * Cria um saveOrUpdateStrategy e salva o objeto (Não executa)
	 * @param entity
	 */
	protected SaveOrUpdateStrategy save(Object entity) {
		return new SaveOrUpdateStrategy(getPersistenceContext(), entity).saveEntity();
	}

	/** Verifica necessidade de persistir no banco algum atributo do tipo 'file'.**/
	protected void verifyFileToSave(final BEAN bean, SaveOrUpdateStrategy save) {
		if (autoManageFileProperties() && fileDAO != null) {
			for (final PropertyDescriptor pd : fileProperties) {
				save.attachBefore(new HibernateCommand() {

					@Override
					public Object doInHibernate(Session session) throws HibernateException {
						session.flush();
						session.clear(); //Importante limpar o cache dos beans na transação, pois objetos são carregados dentro do método saveFile e o cache atrapalha
						fileDAO.saveFile(bean, pd.getName());
						return null;
					}

				});
			}
		}
	}

	protected boolean autoManageFileProperties() {
		return true;
	}

	/**
	 * Override this method to update the strategy to save the bean
	 * @param save
	 */
	public void updateSaveOrUpdate(SaveOrUpdateStrategy save) {
	}

	/**
	 * Cria um saveOrUpdateStrategy e salva o objeto (Não executa)
	 * @param entity
	 */
	protected SaveOrUpdateStrategy save(Object entity, boolean clearSession) {
		return new SaveOrUpdateStrategy(getPersistenceContext(), entity).saveEntity(clearSession);
	}

	public void bulkSaveOrUpdate(Collection<BEAN> list) {
		if (list.isEmpty()) {
			return;
		}
		SaveOrUpdateStrategy saveFull = new SaveOrUpdateStrategy(getPersistenceContext(), null);
		saveFull.clear();
		for (BEAN bean : list) {
			SaveOrUpdateStrategy save = save(bean, false);
			verifyFileToSave(bean, save);
			updateSaveOrUpdate(save);
			saveFull.attach(save);
		}
		saveFull.execute();
		//getHibernateTemplate().flush();
	}

	/* (non-Javadoc)
	 * @see org.nextframework.persistence.DAO#load(BEAN)
	 */
	public BEAN load(BEAN bean) {
		if (bean == null) {
			return null;
		}
		return query()
				.entity(bean)
				.unique();
	}

	public BEAN loadById(Serializable id) {
		if (id == null) {
			return null;
		}
		return query()
				.idEq(id)
				.unique();
	}

	@SuppressWarnings("all")
	public Collection loadCollection(Object owner, String role) {
		owner = query()
				.entity(owner)
				.fetchCollection(role)
				.unique();
		return (Collection<?>) Util.beans.getPropertyValue(owner, role);
	}

	public BEAN loadWithIdAndDescription(BEAN bean) {
		if (bean == null) {
			return null;
		}
		return queryWithSimpleFields()
				.entity(bean)
				.unique();
	}

	public BEAN loadWithIdAndDescriptionById(Serializable id) {
		if (id == null) {
			return null;
		}
		return queryWithSimpleFields()
				.idEq(id)
				.unique();
	}

	public BEAN load(BEAN bean, String[] attributesToLoad) {

		if (bean == null || HibernateUtils.getId(getHibernateTemplate(), bean) == null) {
			throw new IllegalArgumentException("Para carregar o atributo de algum bean é necessário informar um bean já persistido e os atributos");
		}

		return queryWithFields(attributesToLoad)
				.entity(bean)
				.unique();
	}

	/** Carrega os atributos de um bean.
	 *  @param bean Entidade com a PK definida que será usada como referencia.
	 *  @param attributesToLoad Array de strings com os nomes dos atributos que devem ser carregados.
	 **/
	@SuppressWarnings("all")
	public void loadAttributes(BEAN bean, String[] attributesToLoad) {

		if (bean == null || attributesToLoad == null || attributesToLoad.length == 0 || HibernateUtils.getId(getHibernateTemplate(), bean) == null) {
			throw new IllegalArgumentException("Para carregar o atributo de algum bean é necessário informar um bean já persistido e os atributos!");
		}

		BEAN newBean = queryWithFields(attributesToLoad)
				.entity(bean)
				.unique();

		if (newBean == null) {
			throw new NullPointerException(bean.getClass().getSimpleName() + " id " + HibernateUtils.getId(getHibernateTemplate(), bean) + " não encontrado!");
		}

		Util.beans.copyAttributes(newBean, bean, attributesToLoad);

	}

	public void loadDescriptionProperty(BEAN bean, String... extraFields) {

		if (bean == null || HibernateUtils.getId(getHibernateTemplate(), bean) == null) {
			throw new IllegalArgumentException("Para carregar o atributo de algum bean é necessário informar um bean já persistido!");
		}

		QueryBuilder<BEAN> query = queryWithSimpleFields(extraFields);
		BEAN newBean = query
				.entity(bean)
				.unique();

		Objects.requireNonNull(newBean, "Bean não encontrado!");

		String[] simpleFields = getSimpleFields();
		Util.beans.copyAttributes(newBean, bean, simpleFields);

	}

	public BEAN loadFormModel(BEAN bean) {
		if (bean == null) {
			return null;
		}
		QueryBuilder<BEAN> query = query()
				.entity(bean);
		//é provavel que se existir uma propriedade do tipo File ela deva ser carregada
		if (autoManageFileProperties() && fileDAO != null) {
			for (PropertyDescriptor pd : fileProperties) {
				query.leftOuterJoinFetch(query.getAlias() + "." + pd.getName());
			}
		}
		updateFormQuery(query);
		return query.unique();
	}

	/**
	 * Override this method to update the form view query
	 * @param query
	 */
	public void updateFormQuery(QueryBuilder<BEAN> query) {
	}

	/**
	 * Override this method to update the collection fetch query
	 * @param query
	 */
	public void updateCollectionFetchQuery(QueryBuilder<BEAN> query, Object owner, String collectionProperty) {
		updateFormQuery(query);
	}

	public boolean isEmpty() {
		return query()
				.setUseTranslator(false)
				.select("1")
				.setMaxResults(1)
				.unique() == null;
	}

	/* (non-Javadoc)
	 * @see org.nextframework.persistence.DAO#findAll()
	 */
	public List<BEAN> findAll() {
		return findAll(this.orderBy);
	}

	/* (non-Javadoc)
	 * @see org.nextframework.persistence.DAO#findAll(java.lang.String)
	 */
	public List<BEAN> findAll(String orderBy) {
		return query()
				.orderBy(orderBy)
				.list();
	}

	public List<BEAN> findByProperty(String propertyName, Object o) {
		QueryBuilder<BEAN> query = queryWithOrderBy();
		return query
				.where(query.getAlias() + "." + propertyName + " = ?", o)
				.list();
	}

	public BEAN findByPropertyUnique(String propertyName, Object o) {
		QueryBuilder<BEAN> query = queryWithOrderBy();
		return query
				.where(query.getAlias() + "." + propertyName + " = ?", o)
				.unique();
	}

	public List<BEAN> findBy(Object o, String... extraFields) {

		if (o == null) {
			return new ArrayList<BEAN>();
		}

		Class<?> propertyClass = Util.objects.getRealClass(o.getClass());
		String[] propertiesForClass = findPropertiesForClass(propertyClass);
		if (propertiesForClass.length == 0 || propertiesForClass.length > 1) {
			throw new IllegalArgumentException("Não foi possível executar findBy(..). Deve haver apenas uma propriedade da classe " + propertyClass.getName() + " na classe " + this.beanClass.getName());
		}

		QueryBuilder<BEAN> query = queryWithSimpleFields(extraFields);
		query.where(query.getAlias() + "." + propertiesForClass[0] + " = ? ", o);
		query.orderBy(orderBy);

		updateFindByQuery(query);

		return query.list();
	}

	/**
	 * Atualiza as querys de findBy
	 * ex.: findBy(Object o, boolean forCombo, String... extraFields)
	 * @param query
	 */
	protected void updateFindByQuery(QueryBuilder<?> query) {

	}

	protected String[] findPropertiesForClass(Class<? extends Object> propertyClass) {
		List<String> properties = new ArrayList<String>();
		ReflectionCache reflectionCache = ReflectionCacheFactory.getReflectionCache();
		Method[] methods = reflectionCache.getMethods(this.beanClass);
		for (Method method : methods) {
			if (Util.beans.isGetter(method)) {
				if (method.getReturnType().equals(propertyClass)) {
					properties.add(Util.beans.getPropertyFromGetter(method.getName()));
				}
			}
		}
		return properties.toArray(new String[properties.size()]);
	}

	public List<BEAN> findForCombo(String... extraFields) {
		QueryBuilder<BEAN> query = queryWithSimpleFields(extraFields).orderBy(orderBy);
		updateFindForComboQuery(query);
		return query.list();
	}

	/**
	 * Permite alterar a query que será utilizada no findForCombo.
	 */
	protected void updateFindForComboQuery(QueryBuilder<BEAN> query) {
	}

	public ResultList<BEAN> loadListModel(ListViewFilter filter) {
		QueryBuilder<BEAN> query = queryWithOrderBy();
		if (orderBy == null) {//ordenação default para telas de listagem de dados
			query.orderBy(Util.strings.uncaptalize(beanClass.getSimpleName()) + ".id");
		}
		updateListQuery(query, filter);
		QueryBuilder<BEAN> queryBuilder = query;
		return new ResultListImpl<BEAN>(queryBuilder, filter);
	}

	/**
	 * Override this method to update the list view query 
	 * @param query
	 * @param _filter cast to the class configured in the crudcontroller
	 */
	public void updateListQuery(QueryBuilder<BEAN> query, ListViewFilter _filter) {
	}

	@SuppressWarnings("all")
	public void delete(final BEAN bean) {
		HibernateTransactionSessionProvider sessionProvider = (HibernateTransactionSessionProvider) PersistenceConfiguration.getConfig().getSessionProvider();
		sessionProvider.executeInTransaction(new HibernateTransactionCommand() {

			@Override
			public Object doInHibernate(Session session, Object transactionStatus) {

				BEAN bean2 = bean;

				if (autoManageFileProperties() && fileDAO != null) {
					BEAN load = load(bean2);
					if (load != null) {
						bean2 = load;
					}
				}

				session.delete(bean2);
				if (autoManageFileProperties() && fileDAO != null) {
					for (PropertyDescriptor pd : fileProperties) {
						File fileToDelete = null;
						try {
							fileToDelete = (File) pd.getReadMethod().invoke(bean2);
						} catch (Exception ex) {
							throw new NextException("Erro ao ler atributo", ex);
						}
						if (fileToDelete != null) {
							fileDAO.delete(fileToDelete);
						}
					}
				}

				session.flush();
				return null;
			}

		});
	}

}
