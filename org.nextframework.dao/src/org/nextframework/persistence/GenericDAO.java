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
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import javax.persistence.Entity;
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
import org.nextframework.persistence.translator.AliasMap;
import org.nextframework.persistence.translator.QueryBuilderResultTranslator;
import org.nextframework.persistence.translator.QueryBuilderResultTranslatorImpl;
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
	protected TransactionTemplate transactionTemplate;
	protected FileDAO<File> fileDAO;
	protected JdbcTemplate jdbcTemplate;

	private String persistenceContext;

	/**
	 * Spring application context
	 */
	private ApplicationContext applicationContext;

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
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

	/**
	 * Cria um saveOrUpdateStrategy e salva o objeto (Não executa)
	 * @param entity
	 */
	protected SaveOrUpdateStrategy save(Object entity) {
		return new SaveOrUpdateStrategy(getPersistenceContext(), entity).saveEntity();
	}

	/**
	 * Cria um saveOrUpdateStrategy e salva o objeto (Não executa)
	 * @param entity
	 */
	protected SaveOrUpdateStrategy save(Object entity, boolean clearSession) {
		return new SaveOrUpdateStrategy(getPersistenceContext(), entity).saveEntity(clearSession);
	}

	protected final List<BEAN> empty() {
		return new ArrayList<BEAN>();
	}

	@Override
	protected HibernateTemplate createHibernateTemplate(SessionFactory sessionFactory) {
		if (getHibernateTemplate() != null) {
			return getHibernateTemplate();
		}
		return super.createHibernateTemplate(sessionFactory);
	}

	public TransactionTemplate getTransactionTemplate() {
		return transactionTemplate;
	}

	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}

	public String getPersistenceContext() {
		return persistenceContext;
	}

	public void setPersistenceContext(String persistenceContext) {
		this.persistenceContext = persistenceContext;
	}

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

	protected void init() {

	}

	protected List<PropertyDescriptor> fileProperties = new ArrayList<PropertyDescriptor>();

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

	@SuppressWarnings("unchecked")
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

	/**
	 * Indica se os atributos do tipo File devem ser detectados automaticamente para salvar e carregar ao carregar o bean
	 * @return
	 */
	protected boolean isDetectFileProperties() {
		return true;
	}

	protected QueryBuilder<BEAN> queryWithOrderBy() {
		QueryBuilder<BEAN> query = query();
		if (this.orderBy != null) {
			query.orderBy(orderBy);
		}
		return query;
	}

	/**
	 * Cria um QueryBuilder para esse DAO já com o from configurado
	 * (O From pode ser alterado)
	 * @return
	 */
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

	/* (non-Javadoc)
	 * @see org.nextframework.persistence.DAO#loadForEntrada(BEAN)
	 */
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

	protected boolean autoManageFileProperties() {
		return true;
	}

	// campos uteis para fazer cache dos findAlls dos combos
	protected long cacheTime = 0;
	protected QueryBuilderResultTranslator translatorQueryFindForCombo;
	private String queryFindForCombo;
	private long lastRead;
	private WeakReference<List<?>> findForComboCache = new WeakReference<List<?>>(null);

	/* (non-Javadoc)
	 * @see org.nextframework.persistence.DAO#findForCombo(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<BEAN> findForCombo(String... extraFields) {
		if (extraFields != null && extraFields.length > 0) {
			return newQueryBuilder(beanClass)
					.select(getSelectClauseForIdAndDescription(extraFields))
					.from(beanClass)
					.orderBy(orderBy)
					.list();
		} else {
			if (queryFindForCombo == null) {
				initQueryFindForCombo();
			}
			List<?> listCached = findForComboCache.get();
			if (listCached == null || (System.currentTimeMillis() - lastRead > cacheTime)) {
				listCached = getHibernateTemplate().find(queryFindForCombo);
				listCached = translatorQueryFindForCombo.translate(listCached);
				findForComboCache = new WeakReference<List<?>>(listCached);
				lastRead = System.currentTimeMillis();
			}
			return (List<BEAN>) listCached;
		}
	}

	@Deprecated
	public List<BEAN> findForCombo() {
		return findForCombo((String[]) null);
	}

	/* (non-Javadoc)
	 * @see org.nextframework.persistence.DAO#findBy(java.lang.Object)
	 */
	public List<BEAN> findBy(Object o) {
		return findBy(o, false, (String[]) null);
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

	//cache
	private Map<Class<?>, String> mapaQueryFindByForCombo = new WeakHashMap<Class<?>, String>();
	private Map<Class<?>, String> mapaQueryFindBy = new WeakHashMap<Class<?>, String>();

	/* (non-Javadoc)
	 * @see org.nextframework.persistence.DAO#findBy(java.lang.Object, boolean, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<BEAN> findBy(Object o, boolean forCombo, String... extraFields) {

		if (o == null) {
			return new ArrayList<BEAN>();
		}

		Class<?> propertyClass = Util.objects.getRealClass(o.getClass());

		String queryString = null;
		if ((extraFields != null && extraFields.length > 0) ||
				(forCombo && (queryString = mapaQueryFindByForCombo.get(propertyClass)) == null) ||
				(!forCombo && (queryString = mapaQueryFindBy.get(propertyClass)) == null)) {
			//inicializa a query para essa classe
			//System.out.println("\n\n\nLOADING CLASSE "+this.beanClass+"  PROPRIEDADE CLASSE: "+o.getClass());
			String[] propertiesForClass = findPropertiesForClass(propertyClass);
			if (propertiesForClass.length == 1) {// achou uma propriedade
				String alias = Util.strings.uncaptalize(this.beanClass.getSimpleName());
				String property = propertiesForClass[0];
				QueryBuilder qb = queryWithOrderBy();
				qb.where(alias + "." + property + " = ? ", o);
				updateFindByQuery(qb);
				if (forCombo) {
					if (extraFields != null && extraFields.length > 0) {
						//verifcar se precisa fazer joins extras
						int i = 0;
						for (int j = 0; j < extraFields.length; j++) {
							String extra = extraFields[j];
							BeanDescriptor beanDescriptor = BeanDescriptorFactory.forClass(this.beanClass);
							Type type = beanDescriptor.getPropertyDescriptor(extra).getType();
							if (type instanceof Class) {
								if (((Class) type).isAnnotationPresent(Entity.class)) {
									extra += "." + BeanDescriptorFactory.forClass((Class) type).getDescriptionPropertyName();
								}
							}
							if (extra.contains(".")) {
								int ultimoponto = extra.lastIndexOf(".");
								String path = extra.substring(0, ultimoponto);
								qb.join(alias + "." + path + " autojoin" + i);
								extraFields[j] = "autojoin" + i + extra.substring(ultimoponto);
							}
							i++;
						}
						//se for com extraFields não pode usar o cache (não existe cache do select quando tem extra properties)						
						qb.select(getSelectClauseForIdAndDescription(extraFields));
						//String hbquery = qb.getQuery();	
						//queryString = hbquery;
						return qb.list();
					} else {
						qb.select(getSelectClauseForIdAndDescription());
						String hbquery = qb.getQuery();
						queryString = hbquery.replaceAll(":param0", "?");
						mapaQueryFindByForCombo.put(propertyClass, queryString);
					}
				} else {
					String hbquery = qb.getQuery();
					queryString = hbquery.replaceAll(":param0", "?");
					mapaQueryFindByForCombo.put(propertyClass, queryString);
				}
			} else if (propertiesForClass.length > 1) {// mais de uma propriedade do mesmo tipo
				throw new RuntimeException("Não foi possível executar findBy(..). Existe mais de uma propriedade da classe " + propertyClass.getName() + " na classe " + this.beanClass.getName());
			} else {//nenhuma propriedade do tipo fornecido
				throw new RuntimeException("Não foi possível executar findBy(..). Não existe nenhuma propriedade da classe " + propertyClass.getName() + " na classe " + this.beanClass.getName());
			}
		}

		List list = getHibernateTemplate().find(queryString, o);
		if (forCombo) {
			initQueryFindForCombo();
			list = translatorQueryFindForCombo.translate(list);
		}

		return list;
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

	//cache
	private Map<String, String> comboSelectCache = new HashMap<String, String>();

	/**
	 * Retorna o select necessário para apenas o @Id e o @DescriptionProperty
	 * ex.: new QueryBuilder(...).select(getSelectClauseForIdAndDescription()).from(X.class);
	 * @param extraFields Outros campos além do @Id e o @DescriptionProperty que devem ser carregados
	 * @return Clausula select montada
	 */
	protected String getSelectClauseForIdAndDescription(String... extraFields) {
		String alias = Util.strings.uncaptalize(beanClass.getSimpleName());
		return getSelectClauseForIdAndDescription(alias, extraFields);
	}

	/**
	 * Retorna o select necessário para apenas o @Id e o @DescriptionProperty
	 * ex.: new QueryBuilder(...).select(getSelectClauseForIdAndDescription()).from(X.class);
	 * @param extraFields Outros campos além do @Id e o @DescriptionProperty que devem ser carregados
	 * @return Clausula select montada
	 */
	protected String getSelectClauseForIdAndDescription(String alias, String... extraFields) {
		String key = alias + Arrays.toString(extraFields);
		String comboSelect = comboSelectCache.get(key);
		if (comboSelect == null) {
			String[] selectedProperties = getComboSelectedProperties(alias);
			selectedProperties = organizeExtraFields(alias, selectedProperties, extraFields);
			comboSelect = Util.collections.join(Arrays.asList(selectedProperties), ", ");
			comboSelectCache.put(key, comboSelect);
		}
		return comboSelect;
	}

	protected String[] getComboSelectedProperties(String alias) {
		BeanDescriptor beanDescriptor = BeanDescriptorFactory.forClass(beanClass);
		String descriptionPropertyName = beanDescriptor.getDescriptionPropertyName();
		String idPropertyName = beanDescriptor.getIdPropertyName();
		String[] selectedProperties;
		if (descriptionPropertyName == null) {
			selectedProperties = new String[] { alias + "." + idPropertyName };
		} else {
			//verificar se o descriptionproperty utiliza outros campos
			String[] usingFields = getUsingFieldsForDescriptionProperty(beanDescriptor, descriptionPropertyName);
			if (usingFields != null && usingFields.length > 0) {
				selectedProperties = new String[usingFields.length + 1];
				selectedProperties[0] = alias + "." + idPropertyName;
				for (int i = 0; i < usingFields.length; i++) {
					selectedProperties[i + 1] = alias + "." + usingFields[i];
				}
			} else {
				selectedProperties = new String[] { alias + "." + idPropertyName, alias + "." + descriptionPropertyName };
				if (beanDescriptor.getPropertyDescriptor(descriptionPropertyName).getAnnotation(Transient.class) != null) {
					new RuntimeException("@DescriptionProperty of " + beanDescriptor.getTargetClass() + " must declare usingFields").printStackTrace();
				}
			}
		}
		return selectedProperties;
	}

	private String[] getUsingFieldsForDescriptionProperty(BeanDescriptor beanDescriptor, String descriptionPropertyName) {
		Annotation[] annotations = beanDescriptor.getPropertyDescriptor(descriptionPropertyName).getAnnotations();
		DescriptionProperty descriptionProperty = null;
		for (Annotation annotation : annotations) {
			if (DescriptionProperty.class.isAssignableFrom(annotation.annotationType())) {
				descriptionProperty = (DescriptionProperty) annotation;
				break;
			}
		}
		Transient transientAnn = beanDescriptor.getPropertyDescriptor(descriptionPropertyName).getAnnotation(Transient.class);
		String[] usingFields = null;
		if (descriptionProperty != null) {
			//TODO PROCURAR O @DescriptionProperty nas classes superiores
			usingFields = descriptionProperty.usingFields();
		}
		if (usingFields.length == 0 && transientAnn != null) {
			log.warn("Description property of " + beanDescriptor.getTargetClass() + " is transient and does not declare usingFields");
		}
		return usingFields;
	}

	private String[] organizeExtraFields(String alias, String[] selectedProperties, String... extraFields) {
		if (extraFields != null && extraFields.length > 0) {
			for (int i = 0; i < extraFields.length; i++) {
				if (!extraFields[i].contains(".")) {
					extraFields[i] = alias + "." + extraFields[i];
				}
			}
			String[] oldselectedProperties = selectedProperties;
			selectedProperties = new String[selectedProperties.length + extraFields.length];
			System.arraycopy(oldselectedProperties, 0, selectedProperties, 0, oldselectedProperties.length);
			System.arraycopy(extraFields, 0, selectedProperties, oldselectedProperties.length, extraFields.length);
		}
		return selectedProperties;
	}

	protected void initQueryFindForCombo() {
		if (translatorQueryFindForCombo == null) {
			String alias = Util.strings.uncaptalize(beanClass.getSimpleName());
			String[] selectedProperties = getComboSelectedProperties(alias);
			String hbQueryFindForCombo = Util.collections.join(Arrays.asList(selectedProperties), ", ");
			hbQueryFindForCombo = "select " + hbQueryFindForCombo + " from " + beanClass.getName() + " " + alias;
			hbQueryFindForCombo = getQueryFindForCombo(hbQueryFindForCombo);
			if (orderBy != null && !hbQueryFindForCombo.contains("order by")) {
				hbQueryFindForCombo += "  order by " + orderBy;
			}
			translatorQueryFindForCombo = new QueryBuilderResultTranslatorImpl();
			translatorQueryFindForCombo.init(getSessionFactory(), selectedProperties, new AliasMap[] { new AliasMap(alias, null, beanClass) });
			queryFindForCombo = hbQueryFindForCombo;
		}
	}

	//TODO RENOMEAR PARA UPDATEFINDFORCOMBOQUERY
	/**
	 * Permite alterar a query que será utilizada no findForCombo. O parametro é a query montada automaticamente.
	 * Será feito cache dessa query entao esse método só será chamado uma vez.
	 * @param hbQueryFindForCombo
	 * @return
	 */
	protected String getQueryFindForCombo(String hbQueryFindForCombo) {
		return hbQueryFindForCombo;
	}

	/* (non-Javadoc)
	 * @see org.nextframework.persistence.DAO#findAll()
	 */
	public List<BEAN> findAll() {
		return findAll(this.orderBy);
	}

	public boolean isEmpty() {
		return query()
				.setUseTranslator(false)
				.select("1")
				.setMaxResults(1)
				.unique() == null;
	}

	/* (non-Javadoc)
	 * @see org.nextframework.persistence.DAO#findAll(java.lang.String)
	 */
	public List<BEAN> findAll(String orderBy) {
		return query()
				.orderBy(orderBy)
				.list();
	}

	/* (non-Javadoc)
	 * @see org.nextframework.persistence.DAO#findForListagem(org.nextframework.controller.crud.ListingFilter)
	 */
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

	/**
	 * Override this method to update the strategy to save the bean
	 * @param save
	 */
	public void updateSaveOrUpdate(SaveOrUpdateStrategy save) {
	}

	/* (non-Javadoc)
	 * @see org.nextframework.persistence.DAO#delete(BEAN)
	 */
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

	@SuppressWarnings("all")
	public Collection loadCollection(Object owner, String role) {
		owner = query()
				.entity(owner)
				.fetchCollection(role)
				.unique();
		return (Collection<?>) Util.beans.getPropertyValue(owner, role);
	}

	public void loadDescriptionProperty(BEAN object, String... extraFields) {

		String properties = getSelectClauseForIdAndDescription(extraFields);

		BEAN newValue = query()
				.select(properties)
				.entity(object)
				.unique();

		if (newValue == null) {
			throw new RuntimeException("Bean não encontrado!");
		}

		BeanWrapper pafOld = PropertyAccessorFactory.forBeanPropertyAccess(object);
		BeanWrapper pafNew = PropertyAccessorFactory.forBeanPropertyAccess(newValue);

		String alias = Util.strings.uncaptalize(beanClass.getSimpleName()) + ".";
		String[] propertiesArray = properties.split("\\s*[,|;]\\s*");
		for (String propertyFull : propertiesArray) {
			try {
				String property = propertyFull.replace(alias, "");
				Object value = pafNew.getPropertyValue(property);
				pafOld.setPropertyValue(property, value);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
