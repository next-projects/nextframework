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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.nextframework.persistence.PersistenceUtils.InverseCollectionProperties;
import org.nextframework.service.ServiceFactory;

//TODO TERMINAR TRADUÇÃO DO JAVADOC
/**
 * Class to manage the persistence of entities.
 * The methods of this class returns a this object, allowing for a fluent interface.
 * 
 * Example:
 * 
 * <pre>
 *     new SaveOrUpdateStrategy(bean)
 *     		.saveEntity()
 *     		.saveOrUpdateManaged("detail")
 *     		.execute();
 * </pre>
 * 
 * Facilitador para salvar e atualizar entidades
 * 
 * @author rogelgarcia | marcusabreu
 */
@SuppressWarnings("unchecked")
public class SaveOrUpdateStrategy {

	private Object entity;

	private HibernateTransactionSessionProvider<?> hibernateTemplate;

	private List<HibernateCommand> firstCallbacks = new ArrayList<HibernateCommand>();
	private List<HibernateCommand> callbacks = new ArrayList<HibernateCommand>();
	private List<HibernateCommand> attachments = new ArrayList<HibernateCommand>();
	private List<HibernateCommand> attachmentsBefore = new ArrayList<HibernateCommand>();
	private List<HibernateCommand> attachmentsOnError = new ArrayList<HibernateCommand>();

	/**
	 * @param hibernateTemplate
	 * @param entity Entity that will be persisted
	 */
	public SaveOrUpdateStrategy(HibernateTransactionSessionProvider<?> hibernateTemplate, Object entity) {
		this.hibernateTemplate = hibernateTemplate;
		this.entity = entity;
	}

	/**
	 * Create a SaveOrUpdateStrategy, retrieving from Next context the HibernateTemplate and TransactionTemplate objects avaiable.
	 * The name of the beans must be hibernateTemplate and transactionTemplate, if they not exist an exception will be thrown.
	 * 
	 * Cria um Save or Update managed,
	 * buscando no contexto do Next os objetos HibernateTemplate e TransactionTemplate.
	 * Os nomes dos beans devem ser hibernateTemplate e transactionTemplate respectivamente, caso não existam será lançada uma exceção.
	 * 
	 * @param hibernateTemplate
	 * @param entity Entidade que será salva
	 */
	public SaveOrUpdateStrategy(Object entity) {
		this(PersistenceConfiguration.DEFAULT_CONFIG, entity);
	}

	/**
	 * Create a SaveOrUpdateStrategy, retrieving from Next context the HibernateTemplate and TransactionTemplate objects avaiable.
	 * The name of the beans must be hibernateTemplate and transactionTemplate, if they not exist an exception will be thrown.
	 * 
	 * Cria um Save or Update managed,
	 * buscando no contexto do Next os objetos HibernateTemplate e TransactionTemplate.
	 * Os nomes dos beans devem ser hibernateTemplate e transactionTemplate respectivamente, caso não existam será lançada uma exceção.
	 * 
	 * @param hibernateTemplate
	 * @param entity Entidade que será salva
	 */
	public SaveOrUpdateStrategy(String persitenceContext, Object entity) {
		this.hibernateTemplate = (HibernateTransactionSessionProvider<?>) PersistenceUtils.getSessionProvider(persitenceContext);
		this.entity = entity;
	}

	/**
	 * Returns the entity that is being saved.
	 * 
	 * Retorna a entidade que está sendo salva
	 * @return
	 */
	public Object getEntity() {
		return entity;
	}

	/**
	 * 
	 * @param entity Sets the entity that will be persisted
	 * @return
	 */
	public SaveOrUpdateStrategy setEntity(Object entity) {
		this.entity = entity;
		return this;
	}

	/**
	 * <p>
	 * Configures for each object in the collection referenced by the path parameter the owner (master) entity.
	 * </p>
	 * 
	 * Seta para cada objeto da coleção em path a entidade pai
	 * Path é o nome da propriedade, que deve ser um collection, onde encontram-se beans que tem referencia para
	 * a entidade que está sendo salva
	 * parentProperty é o nome da propriedade em cada bean da coleção que faz referencia a entidade sendo salva
	 * ex.:
	 * 
	 * A classe Pessoa tem uma referencia para municipio
	 * A classe Municipio tem um conjunto de referencias para Pessoa
	 * 
	 * Você irá salvar municipio mas precisa setar em todas as pessoas o municipio em que elas moram
	 * 
	 * Criando um saveOrUpdateStrategy voce diz que a entidade é municipio
	 * e fala que todas em todas as pessoas tem que ser executado pessoa.setMunicipio
	 * 
	 * setParent("pessoas","municipio")
	 * 
	 * pessoas é o conjunto que existe em municipio
	 * municipio é a propriedade em pessoa que faz referencia a municipio
	 * 
	 * @param path is the name of the bean property, that must be a collection, where there are beans that have reference to the entity being saved.
	 * @param parentProperty is the name of the property in each bean in the collection that refers to the entity being saved. The name of the property in the detail bean that refers to the master bean. 
	 * @return
	 */
	public SaveOrUpdateStrategy setParent(String path, String parentProperty) {
		try {
			Collection<?> collection = (Collection<?>) PersistenceUtils.getProperty(entity, path);
			if (collection != null) {
				for (Object object : collection) {
					PersistenceUtils.setProperty(object, parentProperty, entity);
				}
			}
			return this;
		} catch (Exception e) {
			throw new RuntimeException("Error in setParent call. ", e);
		}
	}

	/**
	 * Save the entity
	 * @return
	 */
	public SaveOrUpdateStrategy saveEntity() {
		return saveEntity(true);
	}

	/**
	 * Save the entity
	 * @return
	 */
	public SaveOrUpdateStrategy saveEntity(final boolean clearSession) {
		HibernateCommand callback = new HibernateCommand() {
			public Object doInHibernate(Session session) throws HibernateException {
				if (clearSession) {
					session.clear();
				}
				session.saveOrUpdate(entity);
				return null;
			}
		};
		callbacks.add(callback);
		return this;
	}

	/**
	 * Inserts the entity
	 * @return
	 */
	public SaveOrUpdateStrategy insertEntity() {
		HibernateCommand callback = new HibernateCommand() {
			public Object doInHibernate(Session session) throws HibernateException {
				session.clear();
				session.save(entity);
				return null;
			}
		};
		callbacks.add(callback);
		return this;
	}

	/**
	 * Force the executtion of the operations required until now.
	 * 
	 * Excecuta as operações pedidas até o momento
	 * @return
	 */
	public SaveOrUpdateStrategy flush() {
		return flush(false);
	}

	/**
	 * Excecuta as operações pedidas até o momento, mas dá preferencia aos comandos que devem ser executados primeiro
	 * Um exemplo de comando que é executado primeiro sao as deleçoes dos detalhes
	 * @return
	 */
	private SaveOrUpdateStrategy flush(boolean insertFirst) {
		HibernateCommand callback = new HibernateCommand() {
			public Object doInHibernate(Session session) throws HibernateException {
				session.flush();
				return null;
			}
		};
		if (insertFirst) {
			firstCallbacks.add(callback);
		} else {
			callbacks.add(callback);
		}
		return this;
	}

	public SaveOrUpdateStrategy clear() {
		HibernateCommand callback = new HibernateCommand() {
			public Object doInHibernate(Session session) throws HibernateException {
				session.clear();
				return null;
			}
		};
		callbacks.add(callback);
		return this;
	}

	/**
	 * <p>
	 * Save the objects in the collection.
	 * </p>
	 * 
	 * Salva todos os objetos da coleçao determinado por path
	 * path é o nome da propriedade na entidade que possui a coleçao a ser salva
	 * @param path is the property that has reference to the collection.
	 */
	public SaveOrUpdateStrategy saveCollection(String path) {
		return saveCollection(path, null);
	}

	/**
	 * Salva todos os objetos da coleçao determinado por path
	 * path é o nome da propriedade na entidade que possui a coleçao a ser salva
	 * @param path
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public SaveOrUpdateStrategy saveCollection(String path, final CollectionItemSaveOrUpdateListener collectionItemSaveOrUpdateListener) {
		try {
			final Collection<?> collection = (Collection) PersistenceUtils.getProperty(entity, path);
			callbacks.add(new HibernateCommand() {
				public Object doInHibernate(final Session session) throws HibernateException {
					if (collection == null) {
						//se a colecao é nula nao devemos salvá-la
						return null;
					}
					for (Iterator<?> it = collection.iterator(); it.hasNext();) {
						final Object next = it.next();
						SaveOrUpdateStrategyChain chain = new SaveOrUpdateStrategyChain() {
							public void execute() {
								session.saveOrUpdate(next);
							}
						};
						if (collectionItemSaveOrUpdateListener != null) {
							collectionItemSaveOrUpdateListener.onSaveOrUpdate(next, chain);
						} else {
							chain.execute();
						}
					}
					return null;
				}
			});
			return this;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Deleta do banco as propriedades que nao foram encontrados no objeto
	 * 
	 * @param path Nome da propriedade com a coleção
	 * @param parentProperty Nome da propriedade nas classes filhas que fazem referencia a entidade
	 * @param itemClass Classe dos itens da colecao indicada por path
	 * @return
	 */
	public SaveOrUpdateStrategy deleteNotInEntity(String path, final String parentProperty, final Class<?> itemClass) {
		return deleteNotInEntity(path, parentProperty, itemClass, false);
	}

	/**
	 * Deleta do banco as propriedades que nao foram encontrados no objeto
	 * 
	 * @param path Nome da propriedade com a coleção
	 * @param parentProperty Nome da propriedade nas classes filhas que fazem referencia a entidade
	 * @param itemClass Classe dos itens da colecao indicada por path
	 * @return
	 */
	public SaveOrUpdateStrategy deleteNotInEntity(String path, final String parentProperty, final Class<?> itemClass, CollectionItemDeleteListener<?> listener) {
		return deleteNotInEntity(path, parentProperty, itemClass, false, listener);
	}

	public SaveOrUpdateStrategy deleteNotInEntity(String path) {
		return deleteNotInEntity(path, null);
	}

	@SuppressWarnings("rawtypes")
	public SaveOrUpdateStrategy deleteNotInEntity(String path, CollectionItemDeleteListener<?> listener) {
		SessionFactory sessionFactory = hibernateTemplate.getSessionFactory();
		InverseCollectionProperties inverseCollectionProperty = PersistenceUtils.getInverseCollectionProperty(sessionFactory, entity.getClass(), path);
		Class itemClass = inverseCollectionProperty.type;
		String parentProperty = inverseCollectionProperty.property;
		return deleteNotInEntity(path, parentProperty, itemClass, listener);
	}

	/**
	 * Deleta do banco as propriedades que nao foram encontrados no objeto
	 * 
	 * @param path Nome da propriedade com a coleção
	 * @param parentProperty Nome da propriedade nas classes filhas que fazem referencia a entidade
	 * @param itemClass Classe dos itens da colecao indicada por path
	 * @param insertFirst Se for true dá preferencia a esse comando na hora de executar
	 * @return
	 */
	private SaveOrUpdateStrategy deleteNotInEntity(String path, final String parentProperty, final Class<?> itemClass, boolean insertFirst) {
		return deleteNotInEntity(path, parentProperty, itemClass, insertFirst, null);
	}

	/**
	 * Deleta do banco as propriedades que nao foram encontrados no objeto
	 * 
	 * @param path Nome da propriedade com a coleção
	 * @param parentProperty Nome da propriedade nas classes filhas que fazem referencia a entidade
	 * @param itemClass Classe dos itens da colecao indicada por path
	 * @param insertFirst Se for true dá preferencia a esse comando na hora de executar
	 * @param collectionItemDeleteListener Listener que será executado para cada objeto excluido (Não será feita exclusão em batch se listener != null)
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private SaveOrUpdateStrategy deleteNotInEntity(String path, final String parentProperty, final Class<?> itemClass, boolean insertFirst, final CollectionItemDeleteListener collectionItemDeleteListener) {
		try {
			Serializable entityid = PersistenceUtils.getId(entity, hibernateTemplate.getSessionFactory());
			if (entityid == null) {
				return this;
			}

			final Collection<?> collection = (Collection<?>) PersistenceUtils.getProperty(entity, path);
			HibernateCommand deleteCallback;
			if ((collection == null || collection.size() == 0) && collectionItemDeleteListener == null) {
				final String deleteQueryString = new StringBuilder()
						.append("delete ")
						.append(itemClass.getName())
						.append(" where ")
						.append(parentProperty)
						.append(" = :")
						.append(entity.getClass().getSimpleName())
						.toString();
				deleteCallback = new HibernateCommand() {
					public Object doInHibernate(Session session) throws HibernateException {
						Query queryObject = session.createQuery(deleteQueryString);
						queryObject.setEntity(entity.getClass().getSimpleName(), entity);
						queryObject.executeUpdate();
						return null;
					}
				};

			} else {
				final List<?> toDelete = findItensToDelete(parentProperty, itemClass, collection);
				if (toDelete.size() == 0) {
					return this;
				}
				deleteCallback = new HibernateCommand() {
					public Object doInHibernate(final Session session) throws HibernateException {
						for (final Object object : toDelete) {
							SaveOrUpdateStrategyChain chain = new SaveOrUpdateStrategyChain() {
								public void execute() {
									session.delete(object);
								}
							};
							if (collectionItemDeleteListener != null) {
								collectionItemDeleteListener.onDelete(object, chain);
							} else {
								chain.execute();
							}
						}
						return null;
					}
				};
			}
			if (insertFirst) {
				firstCallbacks.add(deleteCallback);
			} else {
				callbacks.add(deleteCallback);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return this;
	}

	private List<?> findItensToDelete(final String parentProperty, final Class<?> itemClass, final Collection<?> collection) {
		final List<?> toDelete = (List<?>) hibernateTemplate.execute(new HibernateCommand() {
			public Object doInHibernate(Session session) {

				// remover dessa lista os objetos transientes
				Collection<Object> itens = new ArrayList<Object>(collection == null ? new ArrayList<Object>() : collection);
				for (Iterator<Object> iter = itens.iterator(); iter.hasNext();) {
					Object entity = iter.next();
					Serializable id = PersistenceUtils.getId(entity, hibernateTemplate.getSessionFactory());
					if (id == null) {
						iter.remove();
					}
				}

				// se nao tiver itens remover os que estiverem no banco
				if (itens.size() == 0) {
					final String findQueryString = new StringBuilder()
							.append("from ")
							.append(itemClass.getName())
							.append(" ")
							.append(uncapitalize(itemClass.getSimpleName()))
							.append(" where ")
							.append(uncapitalize(itemClass.getSimpleName()))
							.append('.')
							.append(parentProperty)
							.append(" = :")
							.append(entity.getClass().getSimpleName())
							.toString();
					Query q = session.createQuery(findQueryString);
					q.setEntity(entity.getClass().getSimpleName(), entity);
					return q.list();
				} else {
					boolean compositeParentProperty = parentProperty.indexOf('.') > 0;
					if (compositeParentProperty) {
						// remover apenas os que nao estao na lista
						// when the property is composite.. 
						// must load all details and find manually the ones to delete 
						final String findQueryString = new StringBuilder()
								.append("from ")
								.append(itemClass.getName())
								.append(" ")
								.append(uncapitalize(itemClass.getSimpleName()))
								.append(" where ")
								.append(uncapitalize(itemClass.getSimpleName()))
								.append('.')
								.append(parentProperty)
								.append(" = :")
								.append(entity.getClass().getSimpleName())
								.toString();
						Query q = session.createQuery(findQueryString);
						q.setEntity(entity.getClass().getSimpleName(), entity);
						List<?> databaseItems = q.list();
						return PersistenceUtils.removeFromCollectionUsingId(session.getSessionFactory(), databaseItems, itens);
					} else {
						// remover apenas os que nao estao na lista
						final String findQueryString = new StringBuilder()
								.append("from ")
								.append(itemClass.getName())
								.append(" ")
								.append(uncapitalize(itemClass.getSimpleName()))
								.append(" where ")
								.append(uncapitalize(itemClass.getSimpleName()))
								.append(" not in (:collection)  and ")
								.append(uncapitalize(itemClass.getSimpleName()))
								.append('.')
								.append(parentProperty)
								.append(" = :")
								.append(entity.getClass().getSimpleName())
								.toString();
						Query q = session.createQuery(findQueryString);
						q.setParameterList("collection", itens);
						q.setEntity(entity.getClass().getSimpleName(), entity);
						return q.list();
					}
				}

			}
		});
		return toDelete;
	}

	/**
	 * Salva cada objeto novo na coleçao indicada por path no banco
	 * Seta a propriedade pai de cada item da colecao para a entidade sendo salva
	 * Deleta do banco os itens nao encontrados na coleçao
	 * @param path
	 * @param parentProperty
	 * @param itemClass
	 * @return
	 */
	public SaveOrUpdateStrategy saveOrUpdateManaged(String path, String parentProperty, Class<?> itemClass) {
		executeManagedSaving(path, itemClass, parentProperty);
		return this;
	}

	/**
	 * Deleta do banco os itens nao encontrados na coleçao e dá um flush na conexao
	 * 
	 * Salva cada objeto novo na coleçao indicada por path no banco
	 * Seta a propriedade pai de cada item da colecao para a entidade sendo salva
	 * 
	 * @param path
	 * @return
	 */
	public SaveOrUpdateStrategy saveOrUpdateManagedDeleteFirst(String path) {
		return saveOrUpdateManagedDeleteFirst(path, null);
	}

	/**
	 * Deleta do banco os itens nao encontrados na coleçao e dá um flush na conexao
	 * 
	 * Salva cada objeto novo na coleçao indicada por path no banco
	 * Seta a propriedade pai de cada item da colecao para a entidade sendo salva
	 * 
	 * @param path
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public SaveOrUpdateStrategy saveOrUpdateManagedDeleteFirst(String path, SaveOrUpdateStrategyListener<?> listener) {
		try {
			SessionFactory sessionFactory = hibernateTemplate.getSessionFactory();
			InverseCollectionProperties inverseCollectionProperty = PersistenceUtils.getInverseCollectionProperty(sessionFactory, entity.getClass(), path);
			Class itemClass = inverseCollectionProperty.type;
			String parentProperty = inverseCollectionProperty.property;
			executeManagedSavingDeleteFirst(path, itemClass, parentProperty, listener);
		} catch (Exception e) {
			if (entity == null) {
				throw new RuntimeException("Não foi possível usar o saveOrUpdateManaged(String) para entidade nula ", e);
			}
			throw new RuntimeException("Não foi possível usar o saveOrUpdateManaged(String) para " + entity.getClass().getName() + "! Possíveis causas: " +
					"Os itens do collection não possuem referencia para o pai, O path estava incorreto. O path leva a uma coleção que não tem classe persistente", e);
		}
		return this;
	}

	/**
	 * Deleta do banco os itens nao encontrados na coleçao e dá um flush na conexao
	 * 
	 * Salva cada objeto novo na coleçao indicada por path no banco
	 * Seta a propriedade pai de cada item da colecao para a entidade sendo salva
	 * 
	 * @param path
	 * @param parentProperty
	 * @param itemClass
	 * @param listener 
	 * @return
	 */
	private void executeManagedSavingDeleteFirst(String path, Class<?> itemClass, String parentProperty, SaveOrUpdateStrategyListener<?> listener) {
		setParent(path, parentProperty);
		deleteNotInEntity(path, parentProperty, itemClass, true, listener);
		flush(true);
		saveCollection(path, listener);
	}

	/**
	 * Deleta do banco os itens nao encontrados na coleçao e dá um flush na conexao
	 * 
	 * Salva cada objeto novo na coleçao indicada por path no banco
	 * Seta a propriedade pai de cada item da colecao para a entidade sendo salva
	 * 
	 * @param path
	 * @return
	 */
	public SaveOrUpdateStrategy saveOrUpdateManaged(String path) {
		flush();
		return saveOrUpdateManagedDeleteFirst(path);
	}

	/**
	 * Deleta do banco os itens nao encontrados na coleçao e dá um flush na conexao
	 * 
	 * Salva cada objeto novo na coleçao indicada por path no banco delegando para o DAO da entidade representada por path (caso delegateToEntityDAO for true)
	 * Seta a propriedade pai de cada item da colecao para a entidade sendo salva
	 * 
	 * @param path
	 * @param delegateToEntityDAO
	 * @return
	 */
	public SaveOrUpdateStrategy saveOrUpdateManaged(String path, boolean delegateToEntityDAO) {
		if (delegateToEntityDAO) {
			flush();
			return saveOrUpdateManaged(path, ServiceFactory.getService(DAODelegateListener.class));
		} else {
			return saveOrUpdateManaged(path);
		}
	}

	/**
	 * Deleta do banco os itens nao encontrados na coleçao e dá um flush na conexao
	 * 
	 * Salva cada objeto novo na coleçao indicada por path no banco
	 * Seta a propriedade pai de cada item da colecao para a entidade sendo salva
	 * 
	 * @param path
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public SaveOrUpdateStrategy saveOrUpdateManaged(String path, SaveOrUpdateStrategyListener listener) {
		flush();
		return saveOrUpdateManagedDeleteFirst(path, listener);
	}

	/**
	 * 
	 * Salva cada objeto novo na coleçao indicada por path no banco
	 * Seta a propriedade pai de cada item da colecao para a entidade sendo salva
	 * Deleta do banco os itens nao encontrados na coleçao
	 * 
	 * @param path
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public SaveOrUpdateStrategy saveOrUpdateManagedNormal(String path) {
		try {
			SessionFactory sessionFactory = hibernateTemplate.getSessionFactory();
			InverseCollectionProperties inverseCollectionProperty = PersistenceUtils.getInverseCollectionProperty(sessionFactory, entity.getClass(), path);
			Class itemClass = inverseCollectionProperty.type;
			String parentProperty = inverseCollectionProperty.property;
			executeManagedSaving(path, itemClass, parentProperty);
		} catch (Exception e) {
			throw new RuntimeException("Não foi possível usar o saveOrUpdateManaged(String) para " + entity.getClass().getName() + "! Possíveis causas: " +
					"Os itens do collection não possuem referencia para o pai, O path estava incorreto. O path leva a uma coleção que não tem classe persistente", e);
		}
		return this;
	}

	@SuppressWarnings("rawtypes")
	private void executeManagedSaving(String path, Class itemClass, String parentProperty) {
		setParent(path, parentProperty);
		deleteNotInEntity(path, parentProperty, itemClass);
		saveCollection(path);
	}

	/**
	 * Excecuta as os comandos desse saveOrUpdateStrategy<BR>
	 * E dos saveOrUpdateStrategy anexados
	 */
	@SuppressWarnings("rawtypes")
	public void execute() {
		flush();
		hibernateTemplate.executeInTransaction(new HibernateTransactionCommand() {
			@Override
			public Object doInHibernate(Session session, Object transactionStatus) {
				List<HibernateCommand> callbacks = getCallbacks();
				for (HibernateCommand callback : callbacks) {
					try {
						callback.doInHibernate(session);
					} catch (RuntimeException e) {
						try {
							executeOnError(session);
						} catch (Exception e2) {
							e2.printStackTrace();
						}
						throw e;
					}
				}
				return null;
			}

			private void executeOnError(Session session) {
				for (HibernateCommand hibernateCallback : attachmentsOnError) {
					hibernateCallback.doInHibernate(session);
				}
			}
		});
	}

	/**
	 * Anexa um outro save ou update a esse.<BR>
	 * Depois que esse saveOrUpdate tiver concluido suas tarefas ele executará
	 * as tarefas dos saveOrUpdates anexados.<BR>
	 * Tanto as tarefas desse strategy quanto do anexado serão executadas na mesma transação.<BR>
	 * As tarefas serão verificadas na chamada desse método, então, se forem adicionadas tarefas
	 * depois da chamada desse método, para o strategy fornecido, elas NÃO serão excecutadas 
	 * IMPORTANTE: NÃO CHAME O MÉTODO EXCECUTE NOS SAVEORUPDATESTRATEGYS ANEXADOS<BR>
	 *  
	 * @param strategy
	 */
	public SaveOrUpdateStrategy attach(SaveOrUpdateStrategy strategy) {
		if (strategy == null)
			throw new NullPointerException("SaveOrUpdateStrategy null");
		attachments.addAll(strategy.getCallbacks());
		return this;
	}

	/**
	 * Anexa um outro save ou update a esse.<BR>
	 * Antes que esse saveOrUpdate execute suas tarefas ele executará
	 * as tarefas dos saveOrUpdates anexados.<BR>
	 * Tanto as tarefas desse strategy quanto do anexado serão executadas na mesma transação.<BR>
	 * As tarefas serão verificadas na chamada desse método, então, se forem adicionadas tarefas
	 * depois da chamada desse método, para o strategy fornecido, elas NÃO serão excecutadas 
	 * IMPORTANTE: NÃO CHAME O MÉTODO EXCECUTE NOS SAVEORUPDATESTRATEGYS ANEXADOS<BR>
	 *  
	 * @param strategy
	 */
	public SaveOrUpdateStrategy attachBefore(SaveOrUpdateStrategy strategy) {
		if (strategy == null)
			throw new NullPointerException("SaveOrUpdateStrategy null");
		attachmentsBefore.addAll(strategy.getCallbacks());
		return this;
	}

	public SaveOrUpdateStrategy attachFlushBefore() {
		this.attachmentsBefore.add(new HibernateCommand() {
			public Object doInHibernate(Session session) throws HibernateException {
				session.flush();
				return null;
			}
		});
		return this;
	}

	/**
	 * Anexa uma tarefa a essa estratégia.<BR>
	 * Depois que esse saveOrUpdate tiver concluido suas tarefas ele executará
	 * as tarefas anexadas.<BR>
	 * Tanto as tarefas desse strategy quanto o anexo serão executadas na mesma transação. 
	 * Não é necessário criar um contexto transacional na tarefa anexada<BR>
	 * @param callback
	 * @return
	 */
	public SaveOrUpdateStrategy attach(HibernateCommand callback) {
		if (callback == null)
			throw new NullPointerException("HibernateCallback null");
		attachments.add(callback);
		return this;
	}

	/**
	 * Anexa uma tarefa a essa estratégia.<BR>
	 * Antes que esse saveOrUpdate execute suas tarefas ele executará
	 * as tarefas anexadas.<BR>
	 * Tanto as tarefas desse strategy quanto o anexo serão executadas na mesma transação. 
	 * Não é necessário criar um contexto transacional na tarefa anexada<BR>
	 * @param callback
	 * @return
	 */
	public SaveOrUpdateStrategy attachBefore(HibernateCommand callback) {
		if (callback == null)
			throw new NullPointerException("HibernateCallback null");
		attachmentsBefore.add(callback);
		return this;
	}

	/**
	 * Anexa uma tarefa a essa estratégia.<BR>
	 * Caso ocorra alguma exceção em alguma tarefa desse saveOrUpdate esse callback será executado.<BR>
	 * Não é necessário criar um contexto transacional na tarefa anexada<BR>
	 * @param callback
	 * @return
	 */
	public SaveOrUpdateStrategy attachOnError(HibernateCommand callback) {
		if (callback == null)
			throw new NullPointerException("HibernateCallback null");
		attachmentsOnError.add(callback);
		return this;
	}

	/**
	 * Arruma os callbacks na ordem que devem ser chamados
	 * Inclui os callbacks dos saveorupdatestrategys anexados
	 * @return
	 */
	private List<HibernateCommand> getCallbacks() {
		List<HibernateCommand> callbacks = new ArrayList<HibernateCommand>();
		callbacks.addAll(this.firstCallbacks);
		callbacks.addAll(this.attachmentsBefore);
		callbacks.addAll(this.callbacks);
		callbacks.addAll(this.attachments);
		return callbacks;
	}

	private static String uncapitalize(String name) {
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

}
