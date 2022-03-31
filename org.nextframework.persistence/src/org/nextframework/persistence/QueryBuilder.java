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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.nextframework.persistence.PersistenceUtils.InverseCollectionProperties;
import org.nextframework.persistence.translator.QueryBuilderResultTranslator;
import org.nextframework.persistence.translator.QueryBuilderResultTranslatorImpl;

/**
 * Helps constructing and executing hibernate queries.
 * 
 * @author rogelgarcia
 *
 * @param <E>
 */
public class QueryBuilder<E> {
	
	private static final Log log = LogFactory.getLog(QueryBuilder.class);

	protected PersistenceConfiguration config;

	protected Select select;
	protected From from;
	protected List<Join> joins = new ArrayList<Join>();
	protected Map<String, CollectionFetcher> fetchs = new HashMap<String, CollectionFetcher>();
	protected Where where = new Where();
	protected GroupBy groupBy;
	protected String orderby;
	
	protected HibernateSessionProvider hibernateSessionProvider;
	
	protected int maxResults = Integer.MIN_VALUE;
	protected int firstResult = Integer.MIN_VALUE;
	
	protected boolean hasSelect = false;
	
	/**
	 * Utilizará o tradutor quando o resultado for do tipo array
	 */
	protected boolean useTranslator = true;
	protected Class<? extends QueryBuilderResultTranslator> resultTranslatorClass = QueryBuilderResultTranslatorImpl.class;
	protected String translatorAlias;
	private Set<String> ignoreJoinPaths = new HashSet<String>();
	
	private String persistenceContext;
	
	
	/**
	 */
	public QueryBuilder(){
		this(PersistenceUtils.getSessionProvider());
	}
	
	public QueryBuilder(String persistenceContext){
		this(PersistenceUtils.getSessionProvider(persistenceContext), persistenceContext);
	}

	public QueryBuilder(HibernateSessionProvider hibernateSessionProvider){
		this(hibernateSessionProvider, PersistenceConfiguration.DEFAULT_CONFIG);
	}
	
	public QueryBuilder(HibernateSessionProvider hibernateSessionProvider, String persistenceContext){
		if(hibernateSessionProvider == null){
			throw new IllegalArgumentException("Session provider must not be null. Try using the default constructor. i.e. new QueryBuilder()");
		}
		this.hibernateSessionProvider = hibernateSessionProvider;
		this.config = PersistenceConfiguration.getConfig(persistenceContext);
		this.persistenceContext = persistenceContext;
	}
	
	public boolean isUseTranslator() {
		return useTranslator;
	}

	public QueryBuilder<E> setUseTranslator(boolean useTranslator) {
		this.useTranslator = useTranslator;
		return this;
	}
	
	public QueryBuilder<E> setResultTranslatorClass(Class<? extends QueryBuilderResultTranslator> resultTranslatorClass) {
		this.resultTranslatorClass = resultTranslatorClass;
		return this;
	}
	
	public String getTranslatorAlias() {
		return translatorAlias;
	}
	
	public QueryBuilder<E> setTranslatorAlias(String alias) {
		this.translatorAlias = alias;
		return this;
	}
	
	public Set<String> getIgnoreJoinPaths() {
		return ignoreJoinPaths;
	}
	
	/**
	 * Faz com que o resultTranslator ignore joins com determinados alias
	 */
	public QueryBuilder<E> ignoreJoin(String alias){
		ignoreJoinPaths.add(alias);
		return this;
	}


//	public ResultList<E> pageAndOrder(PageAndOrder pageAndOrder) {
//		return new ResultList<E>(this, pageAndOrder);
//	}
	
	
	/**
	 * Sets the page number and the size of the pages
	 * @param pageNumber number of the page to be queried (zero based index)
	 * @param pageSize size of each page (maxResults)
	 */
	public QueryBuilder<E> setPageNumberAndSize(int pageNumber, int pageSize){
		maxResults = pageSize;
		firstResult = pageSize * pageNumber;
		return this;
	}
	
	
	
	/**
	 * Seta a cláusula from desse queryBuilder
	 * Seta o alias para o nome da clase minusculo
	 * @param clazz
	 * @return
	 */
	public QueryBuilder<E> from(Class<?> clazz){
		return from(clazz, uncapitalize(clazz.getSimpleName()));
	}



	/**
	 * Seta a cláusula from desse queryBuilder e o alias
	 * O alias pode ser utilizado em clausulas where por exemplo
	 * 
	 * @param _clazz
	 * @param _alias
	 * @return
	 */
	public QueryBuilder<E> from(Class<?> _clazz, String _alias){
		if (!hasSelect) {
			select(_alias);
		}
		this.from = new From(_clazz, _alias);
		return this;
	}
	
	/**
	 * Cria uma cláusula from a partir de outra cláusula from
	 * @param from
	 * @return
	 */
	public QueryBuilder<E> from(From from){
		from(from.getFromClass(), from.getAlias());
		return this;
	}

	/**
	 * Cria uma clausula select (opicional)
	 * @param select
	 * @return
	 */
	public QueryBuilder<E> select(String select){
		this.select = new Select(select);
		hasSelect = true;
		return this;
	}
	/**
	 * Cria uma clausula select (opicional)
	 * @param select
	 * @return
	 */
	public QueryBuilder<E> select(Select select){
		this.select = select;
		hasSelect = true;
		return this;
	}

	/**
	 * Fetchs the collection of the path property using a default collection fetcher (when useDefaultCollectionFetcher is true).<BR>
	 * The fetched collection property must be of the returned item type.<BR> 
	 * For example:
	 * <pre>
	 * 		query.from(Foo.class)
	 *           .fetchCollection("barList");
	 * </pre>
	 * The above query will return results of Foo type. The Foo type must have a collection property named barList.<BR>
	 * The collection property must not use aliases. Use "barList" intead of "foo.barList".<BR>
	 * The actual behavior of the default collection fetcher depends on the implementation configured in the QueryBuilder config object.
	 * 
	 * @see QueryBuilder.configure(...)
	 * @see CollectionFetcher
	 * 
	 * @param path Collection property of the returned items to fetch.
	 * @param useDefaultCollectionFetcher if true uses the default collection fetcher used in the query builder config.
	 */
	public QueryBuilder<E> fetchCollection(String path, boolean useDefaultCollectionFetcher){
		if(useDefaultCollectionFetcher){
			if(config.getDefaultCollectionFetcher() == null){
				throw new IllegalArgumentException("No default collection fetcher configured in QueryBuilder");
			}
			return fetch(path, config.getDefaultCollectionFetcher());
		} else {
			return fetch(path, null);
		}
	}
	
	public QueryBuilder<E> fetch(String path, CollectionFetcher collectionFetcher) {
		fetchs.put(path, collectionFetcher);
		return this;
	}

	/**
	 * Inicializa determinada coleção dos beans que essa query retornar
	 * IMPORTANTE: Não utilizar o alias, apenas o nome da propriedade que deve ser um Collection
	 * e deve estar mapeado no hibernate
	 * @param path
	 * @return
	 */
	public QueryBuilder<E> fetchCollection(String path){
		return fetchCollection(path, false);
	}
	
	/**
	 * Cria uma clausula join
	 * @param joinMode Tipo do join: INNER, LEFT, RIGHT
	 * @param fetch Indica se a entidade relacionada com o join é para ser inicializada
	 * @param path PAth da propriedade, utilizar o alias + ponto + propriedade. ex.: pessoa.municipio
	 * @return
	 */
	public QueryBuilder<E> join(JoinMode joinMode, boolean fetch, String path){
		Join join = new Join(joinMode, fetch, path);
		this.joins.add(join);
		return this;
	}
	/**
	 * Efetua um inner join sem fetch 
	 * Equivalente à: join(JoinMode.INNER, false, path);
	 * @param path
	 * @return
	 */
    public QueryBuilder<E> join(String path){
        return join(JoinMode.INNER, false, path);
    }

    /**
     * Efetua um inner join com fetch
     * Equivalente à: join(JoinMode.INNER, true, path);
     * @param path
     * @return
     */
    public QueryBuilder<E> joinFetch(String path){
        return join(JoinMode.INNER, true, path);
    }

    /**
     * Efetua um left outer join sem fetch
     * Equivalente à: join(JoinMode.LEFT_OUTER, false, path);
     * @param path
     * @return
     */
    public QueryBuilder<E> leftOuterJoin(String path){
        return join(JoinMode.LEFT_OUTER, false, path);
    }
	
    /**
     * Efetua um left outer join com fetch
     * Equivalente à: join(JoinMode.LEFT_OUTER, false, path);
     * @param path
     * @return
     */
	public QueryBuilder<E> leftOuterJoinFetch(String path){
		return join(JoinMode.LEFT_OUTER, true, path);
	}

	private boolean inOr = false;
	private boolean bypassedlastWhere = false;
	
	/**
	 * Cria uma cláusulua where ... like ...
	 * Só é necessário informar a expressao que deve ser usado o like, não utilizar '?'
	 * Ex.:
	 * whereLike("associado.nome", associado.getNome())
	 * Isso será transformado em: associado.nome like '%'||nome||'%'
	 * Se o parametro for null ou string vazia essa condição não será criada
	 * @param whereClause
	 * @param parameter
	 * @return
	 */
	public QueryBuilder<E> whereLike(String whereClause, String parameter) {
		if (parameter != null && !parameter.equals("")) {
			if (parameter.indexOf('?') > 0) {
				throw new IllegalArgumentException("A cláusula where do QueryBuilder não pode ter o caracter '?'. Deve ser passada apenas a expressão que se dejesa fazer o like. Veja javadoc!");
			}
			where(whereClause + " like '%'||?||'%'", parameter);
		}
		return this;
	}
	

	public QueryBuilder<E> whereIntervalMatches(String beginfield, String endfield, Object begin, Object end) {
		this.openParentheses()
			.openParentheses()
				.where(beginfield+" >= ?", begin)
				.where(endfield+" <= ?", end)
			.closeParentheses()
			.or()
			.openParentheses()
				.where(beginfield+" >= ?", begin)
				.where(beginfield+" <= ?", end)
			.closeParentheses()
			.or()
			.openParentheses()
				.where(endfield+" >= ?", begin)
				.where(endfield+" <= ?", end)
			.closeParentheses()
			.or()
			.openParentheses()
				.where(beginfield+" <= ?", begin)
				.where(endfield+" >= ?", end)
			.closeParentheses()
		.closeParentheses();
		return this;
	}

    /**
     * Cria uma cláusulua where ... like ... ignorando caixa e acentos.
     * Só é necessário informar a expressao que deve ser usado o like, não utilizar '?'
     * Ex.:
     * whereLikeIgnoreAll("associado.nome", associado.getNome())
     * Isso será transformado em: UPPER(TIRAACENTO(associado.nome)) LIKE '%'||UPPER(TIRAACENTO(nome))||'%'
     * Se o parametro for null ou string vazia essa condição não será criada
     * @param whereClause
     * @param parameter
     * @return
     */
    public QueryBuilder<E> whereLikeIgnoreAll(String whereClause, String parameter) {
		if (parameter != null && !parameter.equals("")) {
			if (parameter.indexOf('?') > 0) {
				throw new IllegalArgumentException("A cláusula where do QueryBuilder não pode ter o caracter '?'. Deve ser passada apenas a expressão que se deseja fazer o like. Veja javadoc!");
			}
//			String funcaoTiraacento = Next.getApplicationContext().getConfig().getProperties().getProperty(Config.FUNCAO_TIRA_ACENTO);
			String funcaoTiraacento = config.getRemoveAccentFunction();
			if (funcaoTiraacento != null) {
				where("UPPER(" + funcaoTiraacento + "(" + whereClause + ")) LIKE '%'||?||'%'", PersistenceUtils.removeAccents(parameter).toUpperCase());
			}
			else {
				where("UPPER(" + whereClause + ") LIKE '%'||?||'%'", PersistenceUtils.removeAccents(parameter).toUpperCase());
			}
		}
		return this;
	}

	/**
	 * Cria uma clausula where ... in ... Só é necessário informar a expressao que deve ser usado o in, não utilizar '?' Ex.: whereIn("associado.inscricoes", inscricoes) Isso será transformado em: associado.inscricoes in ? [onde no lugar de '?' será colocado a colecao] Se o parametro for null ou
	 * string vazia e emptyCollectionReturnFalse for false essa condição não será criada
	 * 
	 * @param whereClause
	 * @param collection
	 * @param emptyCollectionReturnTrue
	 *            Se a colecao for vazia é para retornar verdadeiro?
	 * @return
	 */
	public QueryBuilder<E> whereIn(String whereClause, Collection<?> collection, boolean emptyCollectionReturnTrue) {
		if (collection != null) {
			if (collection.isEmpty()) {
				if (!emptyCollectionReturnTrue) {
					where("1 = 0");
				}
			}
			else {
				where(whereClause + " in (?)", collection);
//				String cod = "p" + where.getNamedParameters().size();
//				where(whereClause + " in (:" + cod + ")");
//				where.putNamedParameters(cod, collection);
			}
		}
		else if (!emptyCollectionReturnTrue) {
			where("1 = 0");
		}
		return this;
	}
	
	
	/**
	 * Cria uma clausula where ... in ...
	 * Só é necessário informar a expressao que deve ser usado o in, não utilizar '?'
	 * Ex.:
	 * whereIn("associado.inscricoes", "1,2,4,5")
	 * Isso será transformado em: associado.inscricoes in ? [onde no lugar de '?' será colocado o values]
	 * Se o parametro for null ou string vazia e emptyCollectionReturnTrue for true essa condição não será criada
	 * @param whereClause 
	 * @param values valores separados por virgula
	 * @param emptyCollectionReturnTrue Se a colecao for vazia é para retornar verdadeiro?
	 * @return
	 */
	public QueryBuilder<E> whereIn(String whereClause, String values, boolean emptyCollectionReturnTrue) {
		if (values != null && values.trim().length() > 0) {
			where(whereClause + " in (" + values + ")");
		} else if (!emptyCollectionReturnTrue) {
			where("1 = 0");
		}
		return this;
	}
	
	/**
	 * Cria uma clausula where ... in ...
	 * Só é necessário informar a expressao que deve ser usado o in, não utilizar '?'
	 * Ex.:
	 * whereIn("associado.inscricoes", "1,2,4,5")
	 * Isso será transformado em: associado.inscricoes in ? [onde no lugar de '?' será colocado o values]
	 * Se o parametro for null ou string vazia essa condição não será criada
	 * @param whereClause 
	 * @param values valores separados por virgula
	 * @param emptyCollectionReturnTrue Se a colecao for vazia é para retornar verdadeiro?
	 * @return
	 */
	public QueryBuilder<E> whereIn(String whereClause, String values) {
		return whereIn(whereClause, values, true);
	}
	
	/**
	 * Cria uma clausula where ... in ...
	 * Só é necessário informar a expressao que deve ser usado o in, não utilizar '?'
	 * Ex.:
	 * whereIn("associado.inscricoes", inscricoes)
	 * Isso será transformado em: associado.inscricoes in ? [onde no lugar de '?' será colocado a colecao]
	 * Se a colecao for vazia a query retornará verdadeiro
	 * @param whereClause 
	 * @param collection
	 * @return
	 */
	public QueryBuilder<E> whereIn(String whereClause, Collection<?> collection) {
		return whereIn(whereClause, collection, true);
	}
	
	
	public QueryBuilder<E> where(String whereClause, Object parameter, boolean addClause) {
		if(addClause){
			where(whereClause, parameter);
		} else {
			bypassedlastWhere = true;
		}
		if(inOr){
			inOr = false;
		}
		return this;
	}
	/**
	 * Cria uma clausula where.
	 * Escrever a clausula completa . Ex:
	 * where("pessoa.municipio.nome like '?'", nome)
	 * Onde a ? será substituida por parameter
	 * Se parameter for null essa clausula nao será criada
	 * @param whereClause
	 * @param parameter
	 * @return
	 */
	public QueryBuilder<E> where(String whereClause, Object parameter) {
		if (parameter != null && (!(parameter instanceof String) || !parameter.equals(""))) {
			if (inOr) {
				where.or();
				inOr = false;
			}
			else {
				where.and();
			}
			where.append(whereClause, parameter);
			bypassedlastWhere = false;
		} else {
			bypassedlastWhere = true;
		}
		if(inOr){
			inOr = false;
		}
		return this;
	}
	
	/**
	 * Cria uma clausula where com vários parâmetros.
	 * Se parameter for null essa clausula nao será criada
	 * @param whereClause
	 * @param parameter
	 * @return
	 */
	public QueryBuilder<E> where(String whereClause, Object... parameters) {
		if(parameters == null){
			return this;
		}
		boolean allBlank = true;
		for (int i = 0; i < parameters.length; i++) {
			if (parameters[i] != null && (!(parameters[i] instanceof String) || !parameters[i].equals(""))) {
				allBlank = false;
				break;
			}
		}

		if (!allBlank) {
			if (inOr) {
				where.or();
				inOr = false;
			}
			else {
				where.and();
			}
			where.append(whereClause, parameters);
			bypassedlastWhere = false;
		} else {
			bypassedlastWhere = true;
		}
		return this;
	}
	
	/**
	 * Cria uma clausula where.
	 * Escrever a clausula completa . Ex:
	 * where("pessoa.municipio.nome like 'BH'")
	 * @param whereClause
	 * @param parameter
	 * @return
	 */
	public QueryBuilder<E> where(String whereClause) {
		if (inOr) {
			where.or();
			inOr = false;
		}
		else {
			where.and();
		}
		where.append(whereClause);
		bypassedlastWhere = false;
		return this;
	}
	
	public QueryBuilder<E> where(String whereClause, boolean addClause){
		if(addClause){
			where(whereClause);
		}
		return this;
	}
	
    public QueryBuilder<E> whereWhen(String whereClause, Boolean include) {
		if (include != null && include == true) {
			if (inOr) {
				where.or();
				inOr = false;
			}
			else {
				where.and();
			}
			where.append(whereClause);
			bypassedlastWhere = false;
		} else {
			bypassedlastWhere = true;
		}
		return this;
	}

    public QueryBuilder<E> where(Where where) {
		this.where = where;
		return this;
	}
	
	public QueryBuilder<E> orderBy(String order) {
		if (order != null && !order.trim().equals("")) {
			this.orderby = order;
		}
		return this;
	}
	
	private int subConditionStack = 0;
	
	/**
	 * Cria um "abre parenteses" na query
	 * @return
	 */
	public QueryBuilder<E> openParentheses(){
		if(inOr){
			where.or();
			inOr = false;
		} else {
			where.and();	
		}
		where.append("(");
		subConditionStack++;
		return this;
	}
	
	/**
	 * "Fecha parenteses" na query
	 * @return
	 */
	public QueryBuilder<E> closeParentheses(){
		if(inOr){
			inOr = false;
		}
		bypassedlastWhere = false;
		where.append(")");
		subConditionStack--;
		if(subConditionStack<0){
			throw new RuntimeException("Não existem subcondicoes a serem fechadas");
		}
		return this;
	}
		
	/**
	 * Cria uma condição or. A próxima instrução será concatenada a query com um or e nao com um and
	 * @see openParentheses
	 * @return
	 */
	public QueryBuilder<E> or(){
		if(!bypassedlastWhere){
			inOr = true;	
		}
		return this;
	}
	
	/**
	 * Adiciona ao where uma cláusula onde o id deve ser igual ao 
	 * Serializable fornecido
	 * @param serializable
	 * @return
	 */
	public QueryBuilder<E> idEq(Serializable serializable){
		String idPropertyName = PersistenceUtils.getIdPropertyName(from.getFromClass(), getSessionFactory());
		where(from.getAlias()+"."+idPropertyName+" = ?",serializable);
		return this;
	}

	
	/**
	 * Descobre o id do objeto e adiciona uma cláusula where
	 * que retornará o objeto com o mesmo id do objeto fornecido
	 * @param object
	 * @return
	 */
	public QueryBuilder<E> entity (Object object){
		if(object == null) throw new NullPointerException("entity null");
		idEq(PersistenceUtils.getId(object, getSessionFactory()));
		return this;
	}
	
	public QueryBuilder<E> groupBy (String groupBy, String having){
		this.groupBy = new GroupBy(groupBy, having);
		return this;
	}
	
	public QueryBuilder<E> groupBy (String groupBy) {
		this.groupBy = new GroupBy(groupBy, null);
		return this;
	}
	
	/**
	 * Executa a query e retorna a lista
	 */
	@SuppressWarnings("all")	 
	public List<E> list(){
		if(from == null){
			throw new NullPointerException("Query criada sem cláusula from");
		}
		QueryBuilderResultTranslator qbt = getQueryBuilderResultTranslator();
		List list = (List) hibernateSessionProvider.execute(new HibernateCommand() {
			public Object doInHibernate(Session session) {
				Query query = createQuery(session);
				if(maxResults != Integer.MIN_VALUE){
					query.setMaxResults(maxResults);
				}
				if(firstResult != Integer.MIN_VALUE){
					query.setFirstResult(firstResult);
				}
				return query.list();
			}
		});
		if (qbt != null) {
			list = organizeListWithResultTranslator(qbt, list);
		}
		try {
			for (Object object : list) {
				initializeProxys(object);
			}
		} catch (RuntimeException e) {
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			throw new RuntimeException("Erro ao inicializar Proxys (Coleções). "+stackTrace[7], e);
		}
		return (List<E>) list;
	}
	
//	/**
//	 * Excecuta a query e retorna um iterator
//	 */
// 	@SuppressWarnings("unchecked")
//	public Iterator<E> iterate(){
//		if(from == null){
//			throw new NullPointerException("Query criada sem cláusula from");
//		}
//		final Session session = sessionProvider.newSession();
//		
//		Query query = createQuery(session);
//		if(maxResults != Integer.MIN_VALUE){
//			query.setMaxResults(maxResults);
//		}
//		if(firstResult != Integer.MIN_VALUE){
//			query.setFirstResult(firstResult);
//		}
//		final Iterator<E> iterator = query.iterate();
//		Iterator<E> closeSessionIterator = new Iterator<E>(){
//
//			public boolean hasNext() {
//				boolean hasNext = iterator.hasNext();
//				if(!hasNext){
//					if (session.isConnected()) {
//						session.close();
//						//System.out.println("closing connection");
//					}
//				}
//				return hasNext;
//			}
//
//			public E next() {
//				E next = iterator.next();
//				initializeProxys(next);
//				return next;
//			}
//
//			public void remove() {
//				iterator.remove();
//			}
//
//			@Override
//			protected void finalize() throws Throwable {
//				if (session.isConnected()) {
//					session.close();
//					//System.out.println("closing finalize");
//				}
//			}
//			
//		};
//		return closeSessionIterator;
//	}	
	
	@SuppressWarnings("unchecked")
	/**
	 * Exceuta a query e retorna um único resultado
	 */
	public E unique(){
		if(from == null){
			throw new NullPointerException("query with null from clause");
		}
		QueryBuilderResultTranslator qbt = getQueryBuilderResultTranslator();
		final boolean useUnique = qbt == null;
		Object execute = hibernateSessionProvider.execute(new HibernateCommand() {
			public Object doInHibernate(Session session) {
				Query query = createQuery(session);
				if(maxResults != Integer.MIN_VALUE){
					if(maxResults != 1){
						throw new IllegalArgumentException("Method unique of "+QueryBuilder.class.getSimpleName()+" must be used with maxResults set to 1");
					}
					query.setMaxResults(maxResults);
				}
				if(firstResult != Integer.MIN_VALUE){
					query.setFirstResult(firstResult);
				}
				Object uniqueResult;
				if(useUnique){
					uniqueResult = query.uniqueResult();
				} else {
					uniqueResult = query.list();
				}
				
				try {
					initializeProxys(uniqueResult);
				} catch (RuntimeException e) {
					StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
					throw new RuntimeException("Erro ao inicializar Proxys (Coleções). "+stackTrace[7],e);
				}
				
				if(	uniqueResult != null && !uniqueResult.getClass().getName().startsWith("java") && 
					uniqueResult.getClass().getName().indexOf("framework") == -1 && !uniqueResult.getClass().isArray()){
					
					//only evict entities
					//TODO check with hibernate if the result is an entity
					session.evict(uniqueResult);
				}
				return uniqueResult;
			}
		});
		if (qbt != null) {//ORGANIZAR.. TEM 2 LUGARES COM CÓDIGO IGUAL
			if(execute instanceof List){
				execute = organizeListWithResultTranslator(qbt, (List)execute);
				if(((List)execute).size() == 0){
					execute = null;
				} else {
					execute = ((List)execute).get(0);	
				}
			} else {
				execute = organizeUniqueResultWithTranslator(qbt, (Object[])execute);				
			}
		}
		return (E) execute;
	}


	protected Object organizeUniqueResultWithTranslator(QueryBuilderResultTranslator qbt, Object[] execute) {
		return qbt.translate(execute);
	}


	protected List<?> organizeListWithResultTranslator(QueryBuilderResultTranslator qbt, List<?> execute) {
		List<Object> novoResultado = new ArrayList<Object>(execute.size());
		for (int j = 0; j < execute.size(); j++) {
			Object translate = qbt.translate((Object[]) execute.get(j));
			if(translate != null){
				novoResultado.add(translate);
			}
		}
		return novoResultado;
	}

	protected QueryBuilderResultTranslator getQueryBuilderResultTranslator() {
		
		QueryBuilderResultTranslator qbt = null;
		
		if(useTranslator && resultTranslatorClass != null && select.select.contains(".")){
			
			try {
				qbt = resultTranslatorClass.newInstance();
				qbt.init(getSessionFactory(), this);
			} catch (Exception e) {
				throw new QueryBuilderException("Não foi possível inicializar o " + resultTranslatorClass.getSimpleName(), e);
			}
			
			//os extra fields são campos que o QueryBuilderResultTranslator necessita
			String extraFieldsSelect = "";
			for (String extra : qbt.getExtraFields()) {
				extraFieldsSelect += ", " + extra;
			}
			
			select.select += extraFieldsSelect;
			
		}
		
		return qbt;
	}

	protected void initializeProxys(Object object) {
		if(object == null){
			return;//se o objeto for nulo, nao devemos inicializar os proxys pois não houve resultados
		}
		Set<Entry<String, CollectionFetcher>> entrySet = fetchs.entrySet();
		for (Entry<String, CollectionFetcher> entry : entrySet) {
			String collectionProperty = entry.getKey();
			CollectionFetcher fetcher = entry.getValue();
			if(fetcher == null){
				fetcher = new CollectionFetcher() {
					public List<?> load(QueryBuilder<?> qb, Object owner, String collectionProperty, Class<?> itemType) {
						return qb.list();
					}
				};
			}
			
			InverseCollectionProperties inverseCollectionProperty = PersistenceUtils.getInverseCollectionProperty(getSessionFactory(), object.getClass(), collectionProperty);
			QueryBuilder<?> qb = getQueryBuilderForCollection(object, collectionProperty, inverseCollectionProperty);
			List<?> list = fetcher.load(qb, object, collectionProperty, inverseCollectionProperty.type);
			PersistenceUtils.setProperty(object, collectionProperty, list);
		}
//		for (String fetch : fetchs.keySet()) {
//			try {
//				Object proxyObj = QueryBuilderUtils.getProperty(object, fetch);
//				
//				Hibernate.initialize(proxyObj);
//				if(fetchsDAO.contains(fetch) && proxyObj instanceof Collection){
//					Collection newCollection = null;
//					//TODO A QUERY SERÁ FEITA DUAS VEZES.. OTIMIZAR
//					Collection<?> proxy = (Collection) proxyObj;
//					if(proxy.iterator().hasNext()){
//						newCollection = new ArrayList(); //changed in 2012-08-15, used ListSet
//						String idProperty = null;
//						for (Object object2 : proxy) {
//							if(idProperty == null){
//								idProperty = QueryBuilderUtils.getIdPropertyName(object2.getClass(), sessionProvider.getSessionFactory());
//							}
//							newCollection.add(load(idProperty, object2));
//						}
//					}
//					QueryBuilderUtils.setProperty(object, fetch, newCollection);
//				}
//			} catch (Exception e) {
//				throw new QueryBuilderException("Erro ao tentar fazer fetch de "+fetch+" em "+from.getFromClass().getName(), e);
//			}
//		}
	}

	protected QueryBuilder<?> getQueryBuilderForCollection(Object parent, String collectionProperty, InverseCollectionProperties inverseCollectionProperty) {
		QueryBuilder<?> queryBuilder = new QueryBuilder<Object>(hibernateSessionProvider);
		queryBuilder.from(inverseCollectionProperty.type)
					.where(queryBuilder.getAlias()+"."+inverseCollectionProperty.property +" = ?", parent);
		return queryBuilder;
	}

	protected Query createQuery(Session session) {
		Query query;
		String queryString = null;
		try {
			queryString = getQuery();
			query = session.createQuery(queryString);
		} catch (NullPointerException e) {
			throw new RuntimeException("NullPointerException when creating query \n\t"+queryString, e);
		}
		for (int i = 0; i < where.getParameters().size(); i++) {
			Object value = where.getParameters().get(i);
			if(value instanceof Collection<?>){
				query.setParameterList("param"+i, (Collection<?>)value);
			} else {
				query.setParameter("param"+i, value);
			}
		}
//		for (String cod : where.getNamedParameters().keySet()) {
//			Collection<?> collection = where.getNamedParameters().get(cod);
//			query.setParameterList(cod, collection);
//		}
		return query;
	}
	
	/**
	 * Cria uma query do hibernate com os parametros passados
	 * @return
	 */
	public String getQuery(){
		//TODO VERIFICAR SE A QUERY FOI CONSTRUIDA DE FORMA CORRETA
		if(from == null){
			throw new RuntimeException("The FROM clause is missing on query builder");
		}
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(select);
		stringBuilder.append(" ");
		stringBuilder.append(from);
		stringBuilder.append(" ");
		for (Join join : joins) {
			stringBuilder.append(join);
			stringBuilder.append(" ");
		}
		
		stringBuilder.append(where);
		
		if(groupBy != null){
			stringBuilder.append(" ");
			stringBuilder.append(groupBy);
		}
		
		if(orderby!=null){
			stringBuilder.append(" ORDER BY ");
			stringBuilder.append(orderby);
		}
		String hibernateQuery = stringBuilder.toString();
		
        log.info(hibernateQuery);
        return hibernateQuery;
	}

	public static class Select{
		private String select;
		public Select(String select) {	this.select = select;}
		public String toString(){return "SELECT "+select;}
		public String getValue() {return select;}
	}
    
	public static class From{
		private String alias;
		private Class<?> from;
		public Class<?> getFromClass() {return from;}
		public String getAlias() {return alias;}
		public From(Class<?> from, String alias){this.from = from; this.alias = alias;}
		public String toString(){return "FROM "+from.getName()+" "+alias;}
	}
	
	public static class Join{
		private JoinMode joinMode;
		private boolean fetch;
		private String path;
		public Join(JoinMode mode, boolean fetch, String path) {
			joinMode = mode;
			this.fetch = fetch;	
			this.path = path;
		}
		public boolean isFetch() {return fetch;	}
		public JoinMode getJoinMode() {	return joinMode;}
		public String getPath() {return path;}
		public String toString(){
			return " "+ joinMode+" JOIN "+(fetch?"FETCH ":"")+path;
		}
		public String dontFetchToString(){
			return " "+joinMode+" JOIN "+path;
		}
	}
	
	public static enum JoinMode {
		LEFT_OUTER, INNER, RIGHT_OUTER;
		
		public String toString() {
			return name().replace('_', ' ');
		};
	}
	
	public static class GroupBy {
		private String groupBy;
		private String having;
		
		public GroupBy(String groupBy, String having){
			this.groupBy = groupBy;
			this.having = having;
		}
		
		public String getValue() {
			return groupBy;
		}
		
		public String toString(){
			String string = "GROUP BY "+groupBy;
			if(having != null){
				string += " HAVING "+having;
			}
			return string;
		}
	}
	
	public static class Where{		
		private StringBuilder stringBuilder = new StringBuilder();
		private List<Object> parameters = new ArrayList<Object>();
		boolean inParentesis;
		public List<Object> getParameters() {
			return parameters;
		}
		private String convertToNamedParameter(String clause) {
			int count = parameters.size();
			while(clause.contains("?")){
				clause = clause.replaceFirst("\\?", ":param"+(count++));
			}
			return clause;
		}
		public void or() {if(stringBuilder.length()>0 && !inParentesis)append(" OR "); }
		public void and(){if(stringBuilder.length()>0 && !inParentesis)append(" AND ");}
		public void append(String clause, Object parameter){inParentesis = false;stringBuilder.append(convertToNamedParameter(clause)); parameters.add(parameter);}
		public void append(String clause, Object[] parameters){inParentesis = false;stringBuilder.append(convertToNamedParameter(clause)); for (Object parameter : parameters) {this.parameters.add(parameter);}}
		public void append(String clause){
			if(clause.contains("?")){
				throw new QueryBuilderException("Invalid query, no parameters set for clause where "+clause);
			}
			if(")".equals(clause) && inParentesis){
				inParentesis = stringBuilder.substring(stringBuilder.length()-1, stringBuilder.length()).equals("( ");
				
				//se abriu e fechou parenteses .. cancelar os parenteses
				stringBuilder.delete(stringBuilder.length() -2 , stringBuilder.length());
				stringBuilder.append(" 1=1 ");
				
				
				return;
			}
			if("(".equals(clause)){
				inParentesis = true;
			} else {
				inParentesis = false;
			}
			stringBuilder.append(clause);stringBuilder.append(" ");
		}
		public String toString(){
			if(stringBuilder.length() == 0){
				return "";
			} else {
				StringBuilder builder = new StringBuilder();
				builder.append("WHERE ");
				builder.append(stringBuilder);
				return builder.toString();	
			}
		}
		
		@Override
		public QueryBuilder.Where clone() {
			Where clone = new Where();
			clone.stringBuilder.append(this.stringBuilder.toString());
			clone.parameters.addAll(this.parameters);
			return clone;
		}
	}

	public From getFrom() {
		return from;
	}

	public void setFrom(From from) {
		this.from = from;
	}

	public List<Join> getJoins() {
		return joins;
	}

	public void setJoins(List<Join> joins) {
		this.joins = joins;
	}
	
	public String getOrderby() {
		return orderby;
	}

	public Select getSelect() {
		return select;
	}

	public void setSelect(Select select) {
		this.select = select;
	}

	public Where getWhere() {
		return where;
	}

	public void setWhere(Where where) {
		this.where = where;
	}
	
	public GroupBy getGroupBy() {
		return groupBy;
	}
	
	public void setGroupBy(GroupBy groupBy) {
		this.groupBy = groupBy;
	}

	public String getAlias() {
		return from.getAlias();
	}

	public QueryBuilder<E> setFirstResult(int firstResult) {
		this.firstResult = firstResult;
		return this;
	}


	public QueryBuilder<E> setMaxResults(int maxResults) {
		this.maxResults = maxResults;
		return this;
	}

	public <X> QueryBuilder<X> createNew(Class<X> class1) {
		return new QueryBuilder<X>(this.hibernateSessionProvider, this.persistenceContext);
	}

	private SessionFactory getSessionFactory() {
		return hibernateSessionProvider.getSessionFactory();
	}

	
	private static String uncapitalize(String name) {
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

}
