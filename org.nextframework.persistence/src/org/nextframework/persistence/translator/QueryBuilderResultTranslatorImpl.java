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
package org.nextframework.persistence.translator;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.persistence.Transient;

import org.hibernate.SessionFactory;
import org.hibernate.usertype.UserType;
import org.nextframework.persistence.PersistenceUtils;
import org.nextframework.persistence.QueryBuilder;
import org.nextframework.persistence.QueryBuilder.Join;
import org.nextframework.persistence.QueryBuilderException;

public class QueryBuilderResultTranslatorImpl implements QueryBuilderResultTranslator {

	private SessionFactory sessionFactory;
	private String resultAlias;
	private ObjectTreeBuilder treeBuilder = new ObjectTreeBuilder(this);
	private ObjectMapper mapper = new ObjectMapper();
	private String[] finalSelectedProperties;

	@Override
	public void init(SessionFactory sessionFactory, QueryBuilder<?> queryBuilder) {

		this.resultAlias = queryBuilder.getTranslatorAlias();

		Set<String> aliases = new HashSet<String>();
		List<AliasMap> aliasMaps = new ArrayList<AliasMap>();

		aliases.add(queryBuilder.getFrom().getAlias());
		aliasMaps.add(new AliasMap(queryBuilder.getFrom().getAlias(), null, queryBuilder.getFrom().getFromClass()));

		for (Join join : queryBuilder.getJoins()) {
			String[] joinpath = join.getPath().split(" +");
			if (join.isFetch()) {
				throw new QueryBuilderException("É necessário utilizar joins sem Fetch quando especificar os campos a serem selecionados. Erro no join: " + join);
			}
			if (joinpath.length < 2) {
				throw new QueryBuilderException("É necessário informar um alias para todos os joins quando especificar os campos a serem selecionados. Erro no join: " + join);
			}
			if (queryBuilder.getIgnoreJoinPaths().contains(joinpath[1])) {
				continue;
			}
			if (!aliases.add(joinpath[1])) {
				throw new QueryBuilderException("O alias " + joinpath[1] + " está repetido. Erro no join: " + join);
			}
			if (joinpath[0].split("\\.").length > 2) {
				throw new QueryBuilderException("Não é possível ter propriedade de propriedade nos joins: " + joinpath[0]);
			}
			aliasMaps.add(new AliasMap(joinpath[1], joinpath[0], null));
		}

		String selectString = queryBuilder.getSelect().getValue();
		int indexOfDistinct = selectString.indexOf("distinct ");
		if (indexOfDistinct >= 0) {
			if (selectString.substring(0, indexOfDistinct).trim().equals("")) {
				selectString = selectString.substring(indexOfDistinct + 9);
			}
		}

		String[] selectedProperties = selectString.split("(\\s*)?[,](\\s*)?");
		for (int j = 0; j < selectedProperties.length; j++) {
			String property = selectedProperties[j];
			int indexOfAs = property.indexOf(" as");
			if (indexOfAs > 0) {
				selectedProperties[j] = property.substring(0, indexOfAs);
			}
			selectedProperties[j] = selectedProperties[j].trim();
		}

		validateProperties(selectedProperties, aliases);

		init(sessionFactory, selectedProperties, aliasMaps.toArray(new AliasMap[aliasMaps.size()]));

	}

	protected void validateProperties(String[] selectedProperties, Set<String> aliases) {

		Set<String> fullProperties = new HashSet<String>();
		for (String property : selectedProperties) {
			if (aliases.contains(property)) {
				fullProperties.add(property);
			}
		}

		Set<String> uniqueProperties = new HashSet<String>();
		for (String property : selectedProperties) {
			if (!uniqueProperties.add(property)) {
				throw new QueryBuilderException("O campo \"" + property + "\" do select está repetido.");
			}
			String[] propertyParts = property.split("\\.");
			if (propertyParts.length > 2) {
				throw new QueryBuilderException("Não é possível ter propriedade de propriedade no select: " + property);
			}
			String propertyOwner = propertyParts[0];
			if (!aliases.contains(propertyOwner)) {
				throw new QueryBuilderException("O campo \"" + property + "\" do select não é válido, pois seu alias não foi declarado.");
			}
			if (property.contains(".") && fullProperties.contains(propertyOwner)) {
				throw new QueryBuilderException("O campo \"" + property + "\" do select não é válido, pois já existe uma declaração de propriedade completa (sem ponto) com o alias \"" + propertyOwner + "\".");
			}
		}

	}

	public void init(SessionFactory sessionFactory, String[] selectedProperties, AliasMap[] aliasMaps) {

		this.sessionFactory = sessionFactory;

		List<String> extraFields = organizeAliasMaps(aliasMaps, selectedProperties);
		this.treeBuilder.init(aliasMaps);

		this.finalSelectedProperties = createFinalSelectedProperties(selectedProperties, extraFields);

		this.mapper.init(aliasMaps, this.finalSelectedProperties);

	}

	protected List<String> organizeAliasMaps(AliasMap[] aliasMaps, String[] selectedProperties) {

		List<String> extraFields = new ArrayList<String>();

		for (AliasMap aliasMap : aliasMaps) {

			checkAliasMap(aliasMaps, aliasMap);

			Integer propertyIndex = lookForProperty(aliasMap.getAlias(), selectedProperties);
			if (propertyIndex != null) {
				aliasMap.setPkPropertyIndex(propertyIndex);
				aliasMap.setFullProperty(true);
			} else {
				String pkProperty = getPkProperty(aliasMap);
				propertyIndex = lookForProperty(pkProperty, selectedProperties);
				if (propertyIndex == null) {
					extraFields.add(pkProperty);
					propertyIndex = extraFields.size() + selectedProperties.length - 1;
				}
				aliasMap.setPkPropertyIndex(propertyIndex);
			}

		}

		return extraFields;
	}

	private void checkAliasMap(AliasMap[] aliasMaps, AliasMap aliasMap) {
		if (aliasMap.getType() == null) {
			Type type = getAliasType(aliasMaps, aliasMap.getPath(), false);
			if (type instanceof Class<?>) {
				if (((Class<?>) type).isInterface()) {
					type = getAliasType(aliasMaps, aliasMap.getPath(), true);
				}
				aliasMap.setType((Class<?>) type);
			} else if (type instanceof ParameterizedType) {
				ParameterizedType parameterizedType = (ParameterizedType) type;
				if (Set.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
					aliasMap.setCollectionType((Class<?>) parameterizedType.getActualTypeArguments()[0]);
					aliasMap.setType(LinkedHashSet.class);
				} else if (List.class.isAssignableFrom((Class<?>) parameterizedType.getRawType())) {
					aliasMap.setCollectionType((Class<?>) parameterizedType.getActualTypeArguments()[0]);
					aliasMap.setType(ArrayList.class);
				} else {
					throw new QueryBuilderException("Tipo não suportado: " + parameterizedType.getRawType() + " alias: " + aliasMap.getAlias());
				}
				//precisamos fazer o array de dependencias porque é do tipo collection
				if (aliasMap.getDependencias() == null) {
					aliasMap.setDependencias(getDependencias(aliasMaps, aliasMap));
				}
			} else {
				throw new QueryBuilderException("Tipo não suportado: " + type + " alias: " + aliasMap.getAlias());
			}
		}
	}

	private Type getAliasType(AliasMap[] aliasMaps, String path, boolean checkSessionFactory) {
		String ownerProperty = path.substring(0, path.indexOf('.'));
		String property = path.substring(path.indexOf('.') + 1);
		AliasMap ownerAliasMap = getAliasMap(aliasMaps, ownerProperty);
		checkAliasMap(aliasMaps, ownerAliasMap);
		Class<?> ownerpropertyType = ownerAliasMap.getOwner();
		if (checkSessionFactory) {
			return PersistenceUtils.getPropertyAssociationType(sessionFactory, ownerpropertyType, property);
		}
		return PersistenceUtils.getPropertyDescriptor(ownerpropertyType, property).getReadMethod().getGenericReturnType();
	}

	public static AliasMap getAliasMap(AliasMap[] aliasMaps, String alias) {
		for (AliasMap aliasMap : aliasMaps) {
			if (aliasMap.getAlias().equals(alias)) {
				return aliasMap;
			}
		}
		return null;
	}

	private Set<AliasMap> getDependencias(AliasMap[] aliasMaps, AliasMap aliasMap) {
		Set<AliasMap> dependencias = new HashSet<AliasMap>();
		if (aliasMap.getPath() == null) {//root nao tem dependencias
			//acho que esse código não é necessário.. 
			dependencias.add(aliasMap);
			return dependencias;
		}
		String ownerProperty = aliasMap.getPath().split("\\.")[0];
		for (AliasMap aliasMap2 : aliasMaps) {
			if (aliasMap2.getAlias().equals(ownerProperty)) {
				dependencias.add(aliasMap);
				if (aliasMap2.getDependencias() == null) {
					aliasMap2.setDependencias(getDependencias(aliasMaps, aliasMap2));
				}
				dependencias.addAll(aliasMap2.getDependencias());
			}
		}
		return dependencias;
	}

	private Integer lookForProperty(String property, String[] selectedProperties) {
		for (int i = 0; i < selectedProperties.length; i++) {
			if (selectedProperties[i].equals(property)) {
				return i;
			}
		}
		return null;
	}

	private String getPkProperty(AliasMap aliasMap) {
		String pkname = PersistenceUtils.getIdPropertyName(aliasMap.getType(), sessionFactory);
		return aliasMap.getAlias() + "." + pkname;
	}

	private String[] createFinalSelectedProperties(String[] selectedProperties, List<String> extraFields) {
		String[] finalSelectedProperties = new String[selectedProperties.length + extraFields.size()];
		System.arraycopy(selectedProperties, 0, finalSelectedProperties, 0, selectedProperties.length);
		String[] extraFields2 = extraFields.toArray(new String[extraFields.size()]);
		System.arraycopy(extraFields2, 0, finalSelectedProperties, selectedProperties.length, extraFields2.length);
		return finalSelectedProperties;
	}

	public String getFinalSelect() {
		String finalSelect = "";
		for (String property : finalSelectedProperties) {
			finalSelect += (finalSelect.isEmpty() ? "" : ", ") + property;
		}
		return finalSelect;
	}

	/**
	 * Thread-safe
	 */
	public List<?> translate(List<?> values) {
		List<Object> list = new ArrayList<Object>();
		ObjectTreeBuilder treeBuilder = getThreadSafeTreeBuilder();
		for (Object object : values) {
			if (!(object instanceof Object[])) {// provavelmente deve ter sido selecionada uma propriedade apenas
				object = new Object[] { object };
			}
			Object translate = translate((Object[]) object, treeBuilder);
			if (translate != null) {
				list.add(translate);
			}
		}
		return list;
	}

	private ObjectTreeBuilder getThreadSafeTreeBuilder() {
		return treeBuilder.getThreadSafeInstance();
	}

	public Object translate(Object[] values, ObjectTreeBuilder treeBuilder) {
		ObjectTree objectTree = treeBuilder.buildObjectTree(values);
		mapper.map(values, objectTree);
		if (objectTree.isNew) {
			if (resultAlias != null) {
				Object object = objectTree.aliasObjectMap.get(resultAlias);
				if (object == null) {
					throw new QueryBuilderException("Tentativa de achar um objeto falhou ao traduzir o resultado. Alias não encontrado: " + resultAlias + ".");
				}
				return object;
			}
			return objectTree.root;
		} else {
			return null;
		}
	}

	/**
	 * Não é Thread-safe a chamada deve ser synchronizada do lado de fora
	 */
	public Object translate(Object[] values) {
		ObjectTree objectTree = treeBuilder.buildObjectTree(values);
		mapper.map(values, objectTree);
		if (objectTree.isNew) {
			if (resultAlias != null) {
				Object object = objectTree.aliasObjectMap.get(resultAlias);
				if (object == null) {
					throw new QueryBuilderException("Tentativa de achar um objeto falhou ao traduzir o resultado. Alias não encontrado: " + resultAlias + ".");
				}
				return object;
			}
			return objectTree.root;
		} else {
			return null;
		}
	}

	protected Object newInstance(Class<?> clazz) throws InstantiationException, IllegalAccessException {
		return clazz.newInstance();
	}

}

/**
 * Cria a arvore de objetos (Mantém cache das operacoes a serem feitas)
 * @author fumec
 *
 */
class ObjectTreeBuilder {

	private QueryBuilderResultTranslatorImpl qbrt;
	private List<ObjectCreator> creators = new ArrayList<ObjectCreator>();

	ObjectTreeBuilder(QueryBuilderResultTranslatorImpl qbrt) {
		this.qbrt = qbrt;
	}

	void init(AliasMap[] aliasMaps) {
		for (AliasMap aliasMap : aliasMaps) {
			if (aliasMap.getPath() == null) {
				creators.add(new RootObjectCreator(aliasMap));
			} else if (aliasMap.getCollectionType() != null) {
				creators.add(new CollectionObjectCreator(aliasMap, aliasMaps));
			} else {
				creators.add(new ReferenceObjectCreator(aliasMap, aliasMaps));
			}
		}
	}

	public ObjectTreeBuilder getThreadSafeInstance() {
		ObjectTreeBuilder objectTreeBuilder = new ObjectTreeBuilder(qbrt);
		for (ObjectCreator creator : creators) {
			objectTreeBuilder.creators.add(creator.getThreadSafeObjectCretor());
		}
		return objectTreeBuilder;
	}

	ObjectTree buildObjectTree(Object[] values) {
		ObjectTree objectTree = new ObjectTree();
		for (ObjectCreator creator : creators) {
			CreateResult create = creator.create(objectTree, values);
			String alias = creator.getAlias();
			objectTree.aliasObjectMap.put(alias, create.object);
			if (creator instanceof RootObjectCreator) {
				objectTree.root = create.object;
				objectTree.isNew = create.isNew;
			}
		}
		for (ObjectCreator creator : creators) {
			creator.setMapping(objectTree);
		}
		return objectTree;
	}

	/**
	 * Cria o Objeto que será usado no resultado (cria os beans)
	 * @author rogelgarcia
	 */
	interface ObjectCreator {

		CreateResult create(ObjectTree objectTree, Object[] values);

		String getAlias();

		void setMapping(ObjectTree objectTree);

		ObjectCreator getThreadSafeObjectCretor();

	}

	class CreateResult {

		boolean isNew = false;
		Object object;

		public CreateResult(Object object) {
			super();
			this.object = object;
		}

		public CreateResult(boolean isNew, Object object) {
			super();
			this.isNew = isNew;
			this.object = object;
		}

	}

	class ReferenceObjectCreator implements ObjectCreator {

		private String alias;
		private Class<?> clazz;
		private String path;
		private String ownerAlias;
		private Method setter;
		private Object lastCreatedObject;
		private int pkPropertyIndex;
		private boolean fullProperty;

		public ReferenceObjectCreator(AliasMap aliasMap, AliasMap[] aliasMaps) {
			this.clazz = aliasMap.getType();
			this.alias = aliasMap.getAlias();
			this.path = aliasMap.getPath();
			this.ownerAlias = path.substring(0, path.indexOf('.'));
			String property = path.substring(path.indexOf('.') + 1);
			Class<?> ownerClass = QueryBuilderResultTranslatorImpl.getAliasMap(aliasMaps, ownerAlias).getOwner();
			this.setter = PersistenceUtils.getPropertyDescriptor(ownerClass, property).getWriteMethod();
			this.pkPropertyIndex = aliasMap.getPkPropertyIndex();
			this.fullProperty = aliasMap.isFullProperty();
		}

		public CreateResult create(ObjectTree objectTree, Object[] values) {
			try {
				Object object = null;
				if (values[pkPropertyIndex] != null) {
					if (fullProperty) {
						object = values[pkPropertyIndex];
					} else {
						object = qbrt.newInstance(clazz);
					}
				}
				this.lastCreatedObject = object;
				CreateResult createResult = new CreateResult(false, object);
				return createResult;
			} catch (InstantiationException e) {
				if (Collection.class.isAssignableFrom(this.clazz)) {
					throw new QueryBuilderException("Erro ao criar o objeto da query: " + this.clazz.getName() + "  " + path + ". Joins com tipos Collection (Set, List) não são suportados", e);
				} else {
					throw new QueryBuilderException("Erro ao criar o objeto da query: " + this.clazz.getName() + "  " + path, e);
				}
			} catch (IllegalAccessException e) {
				throw new QueryBuilderException("Erro ao criar o objeto da query. " + this.clazz.getSimpleName() + "  " + path, e);
			}
		}

		public String getAlias() {
			return alias;
		}

		public void setMapping(ObjectTree objectTree) {
			Object owner = objectTree.aliasObjectMap.get(ownerAlias);
			if (owner != null) {
				try {
					setter.invoke(owner, lastCreatedObject);
				} catch (Exception e) {
					throw new QueryBuilderException(e);
				}
			}
		}

		public ObjectCreator getThreadSafeObjectCretor() {
			return this;
		}

	}

	class CollectionObjectCreator implements ObjectCreator {

		private String alias;
		private Class<?> collectionItemClass;
		private String path;
		private String ownerAlias;
		private Method setter;
		private Object lastCreatedObject;
		private int pkPropertyIndex;
		private Class<?> collectionClass;
		private Method getter;
		private Set<AliasMap> dependencias;

		private Map<Map<String, Object>, Object> objects = new HashMap<Map<String, Object>, Object>();

		public CollectionObjectCreator() {

		}

		public CollectionObjectCreator(AliasMap aliasMap, AliasMap[] aliasMaps) {
			this.collectionItemClass = aliasMap.getCollectionType();
			this.collectionClass = aliasMap.getType();
			this.alias = aliasMap.getAlias();
			this.path = aliasMap.getPath();
			this.ownerAlias = path.substring(0, path.indexOf('.'));
			String property = path.substring(path.indexOf('.') + 1);
			Class<?> ownerClass = QueryBuilderResultTranslatorImpl.getAliasMap(aliasMaps, ownerAlias).getOwner();
			this.setter = PersistenceUtils.getPropertyDescriptor(ownerClass, property).getWriteMethod();
			this.getter = PersistenceUtils.getPropertyDescriptor(ownerClass, property).getReadMethod();
			this.pkPropertyIndex = aliasMap.getPkPropertyIndex();
			this.dependencias = aliasMap.getDependencias();
		}

		public CreateResult create(ObjectTree objectTree, Object[] values) {
			try {
				Object newInstance = null;
				Map</*ALIAS*/String, /*VALORPK*/Object> chave = new HashMap</*ALIAS*/String, /*VALORPK*/Object>();
				for (AliasMap dp : dependencias) {
					chave.put(dp.getAlias(), values[dp.getPkPropertyIndex()]);
				}
				boolean usenull = false;
				if (values[pkPropertyIndex] != null && ((newInstance = objects.get(chave)) == null)) {
					newInstance = collectionItemClass.newInstance();
					objects.put(chave, newInstance);
				} else {
					usenull = true;
				}
				this.lastCreatedObject = newInstance;
				if (usenull) {
					lastCreatedObject = null;
				}
				CreateResult createResult = new CreateResult(false, newInstance);
				return createResult;
			} catch (InstantiationException e) {
				if (Collection.class.isAssignableFrom(this.collectionItemClass)) {
					throw new QueryBuilderException("Erro ao criar o objeto da query: " + this.collectionItemClass.getName() + "  " + path + ". Joins com tipos Collection (Set, List) não são suportados", e);
				} else {
					throw new QueryBuilderException("Erro ao criar o objeto da query: " + this.collectionItemClass.getName() + "  " + path, e);
				}
			} catch (IllegalAccessException e) {
				throw new QueryBuilderException("Erro ao criar o objeto da query. " + this.collectionItemClass.getSimpleName() + "  " + path, e);
			}
		}

		public String getAlias() {
			return alias;
		}

		@SuppressWarnings("unchecked")
		public void setMapping(ObjectTree objectTree) {
			Object owner = objectTree.aliasObjectMap.get(ownerAlias);
			try {
				if (owner == null && objectTree.aliasObjectMap.get(alias) == null) {
					return;
					//throw new NullPointerException("Não é possível achar o alias: "+owneralias+" . Tentando configurar alias: "+alias+" ("+objectTree.getAliasObject().get(alias)+")"+" root: "+objectTree.getRoot());
				}
				Collection<Object> collection = (Collection<Object>) getter.invoke(owner);
				if (collection == null) {
					collection = (Collection<Object>) collectionClass.newInstance();
				}
				if (lastCreatedObject != null) {
					collection.add(lastCreatedObject);
				}
				setter.invoke(owner, collection);
			} catch (Exception e) {
				throw new QueryBuilderException(e);
			}
		}

		public ObjectCreator getThreadSafeObjectCretor() {
			CollectionObjectCreator collectionObjectCreator = new CollectionObjectCreator();
			collectionObjectCreator.alias = alias;
			collectionObjectCreator.collectionItemClass = collectionItemClass;
			collectionObjectCreator.path = path;
			collectionObjectCreator.ownerAlias = ownerAlias;
			collectionObjectCreator.setter = setter;
			collectionObjectCreator.lastCreatedObject = lastCreatedObject;
			collectionObjectCreator.pkPropertyIndex = pkPropertyIndex;
			collectionObjectCreator.collectionClass = collectionClass;
			collectionObjectCreator.getter = getter;
			collectionObjectCreator.dependencias = dependencias;
			return collectionObjectCreator;
		}

	}

	class RootObjectCreator implements ObjectCreator {

		private Class<?> clazz;
		private String alias;
		private Map<Object, Object> resultados = new HashMap<Object, Object>();
		private int pkPropertyIndex;
		private boolean fullProperty;

		public RootObjectCreator(AliasMap aliasMap) {
			this.clazz = aliasMap.getType();
			this.alias = aliasMap.getAlias();
			this.pkPropertyIndex = aliasMap.getPkPropertyIndex();
			this.fullProperty = aliasMap.isFullProperty();
		}

		public RootObjectCreator() {
		}

		public CreateResult create(ObjectTree objectTree, Object[] values) {
			try {
				Object id = values[pkPropertyIndex];
				Object object = resultados.get(id);
				boolean isNew = false;
				if (object == null) {
					if (fullProperty) {
						object = id;
					} else {
						object = qbrt.newInstance(clazz);
					}
					resultados.put(id, object);
					isNew = true;
				}
				CreateResult createResult = new CreateResult(isNew, object);
				return createResult;
			} catch (InstantiationException e) {
				throw new QueryBuilderException("Erro ao criar o objeto raiz da query. " + this.clazz.getSimpleName(), e);
			} catch (IllegalAccessException e) {
				throw new QueryBuilderException("Erro ao criar o objeto raiz da query. " + this.clazz.getSimpleName(), e);
			}
		}

		public String getAlias() {
			return alias;
		}

		public void setMapping(ObjectTree objectTree) {
			//o objeto root nao precisa fazer mapeamento de nada
		}

		public ObjectCreator getThreadSafeObjectCretor() {
			RootObjectCreator rootObjectCreator = new RootObjectCreator();
			rootObjectCreator.clazz = clazz;
			rootObjectCreator.alias = alias;
			rootObjectCreator.pkPropertyIndex = pkPropertyIndex;
			return rootObjectCreator;
		}

	}

}

class ObjectMapper {

	List<PropertyMapper> mappers = new ArrayList<PropertyMapper>();

	void init(AliasMap[] aliasMaps, String[] propriedades) {
		for (int i = 0; i < propriedades.length; i++) {

			String full = propriedades[i];
			StringTokenizer stringTokenizer = new StringTokenizer(full, ".");
			String ownerAlias = stringTokenizer.nextToken();
			String property = stringTokenizer.hasMoreTokens() ? stringTokenizer.nextToken() : null;
			if (property == null) {
				continue;
			}

			AliasMap ownerAliasMap = QueryBuilderResultTranslatorImpl.getAliasMap(aliasMaps, ownerAlias);
			Class<?> ownerClass = ownerAliasMap != null ? ownerAliasMap.getOwner() : null;
			if (ownerClass == null) {
				throw new QueryBuilderException("Não foi encontrada a classe para o alias '" + ownerAlias + "'");
			}
			PropertyDescriptor propertyDescriptor = PersistenceUtils.getPropertyDescriptor(ownerClass, property);
			Method method = propertyDescriptor.getWriteMethod();
			if (method == null) {
				boolean isTransient = false;
				if (propertyDescriptor.getReadMethod() != null && (propertyDescriptor.getReadMethod().isAnnotationPresent(Transient.class))) {
					isTransient = true;
				}
				throw new QueryBuilderException("No setter method found for '" + property + "' in class " + ownerClass.getName() + ". " + (isTransient ? " This property is transient and must not be used on queries." : ""));
			}

			PropertyMapper propertyMapper = new PropertyMapper();
			propertyMapper.index = i;
			propertyMapper.alias = ownerAlias;
			propertyMapper.setter = method;

			mappers.add(propertyMapper);
		}
	}

	void map(Object[] values, ObjectTree objectTree) {
		for (PropertyMapper mapper : mappers) {
			mapper.map(values, objectTree);
		}
	}

	class PropertyMapper {

		int index;
		String alias;
		Method setter;

		void map(Object[] values, ObjectTree objectTree) {
			Object value = values[index];
			boolean usertype = value instanceof UserType;
			boolean hasValue = (!usertype && value != null) || (usertype && ((UserType) value).toString() != null && ((UserType) value).toString().length() > 0);
			//TODO MELHORARA A FORMA DE VERIFICAR SE UM USERTYPE É NULO
			Object object = objectTree.aliasObjectMap.get(alias);
			if (object != null) {
				try {
					setter.invoke(object, value);
				} catch (Exception e) {
					throw new QueryBuilderException("Erro ao configurar propriedade de " + alias + " ... " + setter, e);
				}
			} else if (hasValue) {
				throw new QueryBuilderException("Erro ao configurar propriedade de " + alias + " ... " + setter + ". O objeto com alias " + alias + " não foi criado!! Valor: " + value);
			}
		}

	}

}

/**
 * Contém o mapa com os alias e os objetos criados (POJO)
 * @author rogelgarcia
 */
class ObjectTree {

	Map<String, Object> aliasObjectMap = new HashMap<String, Object>();
	Object root;
	boolean isNew;

}
