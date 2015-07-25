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
import org.nextframework.persistence.QueryBuilderException;

public class QueryBuilderResultTranslatorImpl implements QueryBuilderResultTranslator {
	
	boolean debug = false;
	SessionFactory sessionFactory;
	String resultAlias;
	
	ObjectTreeBuilder treeBuilder = new ObjectTreeBuilder();
	ObjectMapper mapper = new ObjectMapper();
	List<String> extraFields = new ArrayList<String>();

	public void init(SessionFactory sessionFactory, String[] selectedProperties, AliasMap[] aliasMaps) {
		this.sessionFactory = sessionFactory;
		//verificar alias iguais
		Set<String> aliases = new HashSet<String>();
		for (AliasMap map : aliasMaps) {
			if(!aliases.add(map.alias)) {
				throw new RuntimeException("Alias duplicado na query: "+map.alias);
			}
		}
		
		organizeAliasMaps(aliasMaps, selectedProperties);
		treeBuilder.init(aliasMaps);
		String[] newSelectedProperties = new String[selectedProperties.length + extraFields.size()];
		System.arraycopy(selectedProperties, 0, newSelectedProperties, 0, selectedProperties.length);
		String[] extraFields2 = getExtraFields();
		System.arraycopy(extraFields2, 0, newSelectedProperties, selectedProperties.length, extraFields2.length);
		selectedProperties = newSelectedProperties;
		mapper.init(aliasMaps, selectedProperties);
	}

	private void organizeAliasMaps(AliasMap[] aliasMaps, String[] selectedProperties) {
		for (int i = 0; i < aliasMaps.length; i++) {
			AliasMap aliasMap = aliasMaps[i];
			if(aliasMap.type == null){
				Type type = getAliasType(aliasMaps, aliasMap.path);
				
				if(type instanceof Class<?>){
					aliasMap.type = (Class<?>) type;
					if (aliasMap.type.isInterface()) {
						String ownerproperty = aliasMap.path.substring(0, aliasMap.path.indexOf('.'));
						Class<?> clazz = getOwner(aliasMaps, ownerproperty);
						aliasMap.type = PersistenceUtils.getPropertyAssociationType(sessionFactory, clazz, aliasMap.alias);
					}
				} else if(type instanceof ParameterizedType){
					ParameterizedType parameterizedType = (ParameterizedType) type;
					if(Set.class.isAssignableFrom((Class<?>)parameterizedType.getRawType())){
						aliasMap.collectionType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
						aliasMap.type = LinkedHashSet.class;
					} else if(List.class.isAssignableFrom((Class<?>)parameterizedType.getRawType())){
						aliasMap.collectionType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
						aliasMap.type = ArrayList.class;
					} else {
						throw new RuntimeException("Tipo não suportado: "+parameterizedType.getRawType()+" alias: "+aliasMap.alias);
					}
					//precisamos fazer o array de dependencias porque é do tipo collection
					if (aliasMap.dependencias == null) {
						if(debug){
							System.out.println("\nProcurando dependencias (método inicial) de "+aliasMap);
						}
						aliasMap.dependencias = getDependencias(aliasMaps, aliasMap);
					}
				} else{
					throw new RuntimeException("Tipo não suportado: "+type+" alias: "+aliasMap.alias);
				}
				
			}
			if(aliasMap.pkPropertyIndex == -1){
				aliasMap.pkPropertyIndex = lookForPk(aliasMap, selectedProperties);
			}
		}		
	}



	private Set<AliasMap> getDependencias(AliasMap[] aliasMaps, AliasMap aliasMap) {
		if(aliasMap.dependencias != null){
			if(debug){
				System.out.println("\nJá foi configurado as dependencias de "+aliasMap);
			}
			return aliasMap.dependencias;
		}
		Set<AliasMap> dependencias = new HashSet<AliasMap>();
		if(aliasMap.path == null){//root nao tem dependencias
			//acho que esse código não é necessário.. 
			dependencias.add(aliasMap);
			if(debug){
				System.out.println("\nRoot encontrado "+aliasMap+". Retornando dependencia simples");
			}
			return dependencias;
		}
		String ownerProperty = aliasMap.path.split("\\.")[0];
		if(debug){
			System.out.println("\nEntrando no loop para procurar alias "+ownerProperty+" referenciado em "+aliasMap);
		}
		for (int i = 0; i < aliasMaps.length; i++) {
			AliasMap map = aliasMaps[i];
			if(map.alias.equals(ownerProperty)){
				dependencias.add(aliasMap);
				if(debug){
					System.out.println("\nEncontrado alias "+ownerProperty+". Procurando dependencias de "+ownerProperty);
				}
				dependencias.addAll(getDependencias(aliasMaps, map));
			}
		}
		return dependencias;
	}

	private int lookForPk(AliasMap aliasMap, String[] selectedProperties) {
//		Class type = aliasMap.getType();
//		if(aliasMap.collectionType != null){
//			type = aliasMap.collectionType;
//		}
		//String pkname = "id"; //id property is universal in hibernate
		String pkname = PersistenceUtils.getIdPropertyName(aliasMap.type, sessionFactory);
		String fullProperty = aliasMap.alias+"."+pkname;

		for (int i = 0; i < selectedProperties.length; i++) {
			if(selectedProperties[i].equals(fullProperty)){
				return i;
			}
		}
		extraFields.add(fullProperty);
		return extraFields.size() + selectedProperties.length - 1;
	}

	
	public static Type getAliasType(AliasMap[] aliasMaps, String path) {
		Type type = null;
		String ownerproperty = path.substring(0, path.indexOf('.'));
		String property = path.substring(path.indexOf('.') + 1);
		Class<?> ownerpropertyType = getOwner(aliasMaps, ownerproperty);

		if(property.contains(".")){
			throw new RuntimeException("não é possível ter propriedade de propriedade nos joins: "+path); 
		}
		type = PersistenceUtils.getPropertyDescriptor(ownerpropertyType, property).getReadMethod().getGenericReturnType();
		return type;
	}
	
	/**
	 * Thread-safe
	 * 
	 */
	public List<?> translate(List<?> values) {
		List<Object> list = new ArrayList<Object>();
		ObjectTreeBuilder treeBuilder = getThreadSafeTreeBuilder();
		for (Object object : values) {
			if (!(object instanceof Object[])) {// provavelmente deve ter sido selecionada uma propriedade apenas
				object = new Object[] { object };
			}
			Object translate = translate((Object[]) object, treeBuilder);
			if(translate != null){
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
			if(resultAlias != null){
				Object object = objectTree.getAliasObject().get(resultAlias);
				if(object == null){
					throw new QueryBuilderException("Tentativa de achar um objeto falhou ao traduzir o resultado. Alias não encontrado: "+resultAlias+".");
				}
				return object;
			}
			return objectTree.getRoot();
		} else {
			return null;
		}
	}

	/**
	 * Não é Thread-safe a chamada deve ser synchronizada do lado de fora
	 * 
	 */
	public Object translate(Object[] values) {
		ObjectTree objectTree = treeBuilder.buildObjectTree(values);
		mapper.map(values, objectTree);
		if (objectTree.isNew) {
			if(resultAlias != null){
				Object object = objectTree.getAliasObject().get(resultAlias);
				if(object == null){
					throw new QueryBuilderException("Tentativa de achar um objeto falhou ao traduzir o resultado. Alias não encontrado: "+resultAlias+".");
				}
				return object;
			}
			return objectTree.getRoot();
		} else {
			return null;
		}
	}

	public String[] getExtraFields() {
		return extraFields.toArray(new String[extraFields.size()]);
	}

	
	//métodos utilitários
	
	/**
	 * Retorna o tipo da classe do alias informado
	 */
	public static Class<?> getOwner(AliasMap[] aliasMaps, String owneralias) {
		Class<?> owner = null;
		for (int j = 0; j < aliasMaps.length; j++) {
			AliasMap aliasMap = aliasMaps[j];
			if(aliasMap.alias.equals(owneralias)){
				if(aliasMap.type == null){
					Type type = getAliasType(aliasMaps, aliasMap.path);
					if(type instanceof Class<?>){
						aliasMap.type = (Class<?>) type;
						//if (aliasMap.type.isInterface()) {
						//	String ownerproperty = aliasMap.path.substring(0, aliasMap.path.indexOf('.'));
						//	Class<?> clazz = getOwner(aliasMaps, ownerproperty);
						//	aliasMap.type = PersistenceUtils.getPropertyAssociationType(sessionFactory, clazz, aliasMap.alias);
						//}
					} else if(type instanceof ParameterizedType){
						ParameterizedType parameterizedType = (ParameterizedType) type;
						if(Set.class.isAssignableFrom((Class<?>)parameterizedType.getRawType())){
							aliasMap.collectionType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
							aliasMap.type = LinkedHashSet.class;
						} else if(List.class.isAssignableFrom((Class<?>)parameterizedType.getRawType())){
							aliasMap.collectionType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
							aliasMap.type = ArrayList.class;
						} else {
							throw new RuntimeException("Tipo não suportado: "+parameterizedType.getRawType()+" alias: "+aliasMap.alias);
						}
					} else{
						throw new RuntimeException("Tipo não suportado: "+type+" alias: "+aliasMap.alias);
					}
				}
				if(aliasMap.collectionType != null){
					owner = aliasMap.collectionType;
				} else {
					owner = aliasMap.type;	
				}
				
				break;
			}
		}
		return owner;
	}

	public String getResultAlias() {
		return resultAlias;
	}

	public void setResultAlias(String resultAlias) {
		this.resultAlias = resultAlias;
	}





	
}
/**
 * Cria a arvore de objetos (Mantém cache das operacoes a serem feitas)
 * @author fumec
 *
 */
class ObjectTreeBuilder {
	

	
	List<ObjectCreator> creators = new ArrayList<ObjectCreator>();
	
	void init(AliasMap[] aliasMaps){
		for (AliasMap map : aliasMaps) {
			if(map.path == null){
				creators.add(new RootObjectCreator(map.type, map.alias, map.pkPropertyIndex));
			} else if(map.collectionType != null){
				creators.add(new CollectionObjectCreator(map, map.collectionType, map.type, map.alias, map.path, aliasMaps, map.pkPropertyIndex));
			} else{
				creators.add(new ReferenceObjectCreator(map.type, map.alias, map.path, aliasMaps, map.pkPropertyIndex));
			}
		}
	}
	
	
	public ObjectTreeBuilder getThreadSafeInstance(){
		ObjectTreeBuilder objectTreeBuilder = new ObjectTreeBuilder();
		for (ObjectCreator creator : creators) {
			objectTreeBuilder.creators.add(creator.getThreadSafeObjectCretor());
		}
		return objectTreeBuilder;
	}

	ObjectTree buildObjectTree(Object[] values){
		ObjectTree objectTree = new ObjectTree();
		for (ObjectCreator creator : creators) {
			CreateResult create = creator.create(objectTree, values);
			String alias = creator.getAlias();
			objectTree.aliasObject.put(alias, create.object);
			if(creator instanceof RootObjectCreator){
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
	 *
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
		private String owneralias;
		private Method setter;
		private Object lastCreatedObject;
		private int i;

		public ReferenceObjectCreator(Class<?> clazz, String alias, String path, AliasMap[] aliasMaps, int i){
			this.alias = alias;
			this.clazz = clazz;
			this.path = path;
			this.owneralias = path.substring(0, path.indexOf('.'));
			this.i = i;
			String property = path.substring(path.indexOf('.') + 1);
			
			Class<?> owner = QueryBuilderResultTranslatorImpl.getOwner(aliasMaps, owneralias);
			this.setter = PersistenceUtils.getPropertyDescriptor(owner, property).getWriteMethod(); 
		}

		public CreateResult create(ObjectTree objectTree, Object[] values) {
			try {
				Object newInstance = null;
				if (values[i] != null) {
					newInstance = clazz.newInstance();
				}
				this.lastCreatedObject = newInstance;
				CreateResult createResult = new CreateResult(false, newInstance);
				return createResult;
			}
			catch (InstantiationException e) {
				if(Collection.class.isAssignableFrom(this.clazz)){
					throw new RuntimeException("Erro ao criar o objeto da query: "+this.clazz.getName()+ "  "+path+". Joins com tipos Collection (Set, List) não são suportados", e);
				} else {
					throw new RuntimeException("Erro ao criar o objeto da query: "+this.clazz.getName()+ "  "+path, e);	
				}
				
			}
			catch (IllegalAccessException e) {
				throw new RuntimeException("Erro ao criar o objeto da query. "+this.clazz.getSimpleName()+"  "+path, e);
			}
		}

		public String getAlias() {
			return alias;
		}

		public void setMapping(ObjectTree objectTree) {
			if (lastCreatedObject != null) {
				Object owner = objectTree.aliasObject.get(owneralias);
				try {
					setter.invoke(owner, lastCreatedObject);
				} catch (Exception e) {
					throw new RuntimeException(e);
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
		private String owneralias;
		private Method setter;
		private Object lastCreatedObject;
		private int i;
		private Class<?> collectionClass;
		private Method getter;
		private Set<AliasMap> dependencias;
		
		private Map</*ID*/Map</*ALIAS*/String,/*VALORPK*/Object>, /*VALUE*/Object> objects = new HashMap</*ID*/Map</*ALIAS*/String,/*VALORPK*/Object>, /*VALUE*/Object>();
		
		public CollectionObjectCreator(){
			
		}

		public CollectionObjectCreator(AliasMap map, Class<?> collectionItemClass, Class<?> collectionClass, String alias, String path, AliasMap[] aliasMaps, int i){
			this.alias = alias;
			this.collectionItemClass = collectionItemClass;
			this.collectionClass = collectionClass;
			this.path = path;
			this.owneralias = path.substring(0, path.indexOf('.'));
			this.i = i;
			String property = path.substring(path.indexOf('.') + 1);
			
			Class<?> owner = QueryBuilderResultTranslatorImpl.getOwner(aliasMaps, owneralias);
			
			this.setter = PersistenceUtils.getPropertyDescriptor(owner, property).getWriteMethod();
			this.getter = PersistenceUtils.getPropertyDescriptor(owner, property).getReadMethod();	
			this.dependencias = map.dependencias;
		}

		public CreateResult create(ObjectTree objectTree, Object[] values) {
			try {
				Object newInstance = null;
				Map</*ALIAS*/String,/*VALORPK*/Object> chave = new HashMap</*ALIAS*/String,/*VALORPK*/Object>();
				for (AliasMap dp : dependencias) {
					chave.put(dp.alias, values[dp.pkPropertyIndex]);
				}
				boolean usenull = false;
				if (values[i] != null && ((newInstance = objects.get(chave)) == null)) {
					newInstance = collectionItemClass.newInstance();
					objects.put(chave, newInstance);
				} else {
					usenull = true;
				}
				this.lastCreatedObject = newInstance;
				if(usenull){
					lastCreatedObject = null;
				}
				CreateResult createResult = new CreateResult(false, newInstance);
				return createResult;
			}
			catch (InstantiationException e) {
				if(Collection.class.isAssignableFrom(this.collectionItemClass)){
					throw new RuntimeException("Erro ao criar o objeto da query: "+this.collectionItemClass.getName()+ "  "+path+". Joins com tipos Collection (Set, List) não são suportados", e);
				} else {
					throw new RuntimeException("Erro ao criar o objeto da query: "+this.collectionItemClass.getName()+ "  "+path, e);	
				}
				
			}
			catch (IllegalAccessException e) {
				throw new RuntimeException("Erro ao criar o objeto da query. "+this.collectionItemClass.getSimpleName()+"  "+path, e);
			}
		}

		public String getAlias() {
			return alias;
		}

		@SuppressWarnings("unchecked")
		public void setMapping(ObjectTree objectTree) {
			
			Object owner = objectTree.aliasObject.get(owneralias);
			try {
				if(owner == null && objectTree.getAliasObject().get(alias) == null){
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
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
			
		}

		public ObjectCreator getThreadSafeObjectCretor() {
			CollectionObjectCreator collectionObjectCreator = new CollectionObjectCreator();
			collectionObjectCreator.alias = alias;
			collectionObjectCreator.collectionItemClass = collectionItemClass;
			collectionObjectCreator.path = path;
			collectionObjectCreator.owneralias = owneralias;
			collectionObjectCreator.setter = setter;
			collectionObjectCreator.lastCreatedObject = lastCreatedObject;
			collectionObjectCreator.i = i;
			collectionObjectCreator.collectionClass = collectionClass;
			collectionObjectCreator.getter = getter;
			collectionObjectCreator.dependencias = dependencias;
			return collectionObjectCreator;
		}
	}
	
	class RootObjectCreator implements ObjectCreator {
		
		private Class<?> clazz;
		private String alias;
		private Map</*ID*/Object, /*BEAN*/Object> resultados = new HashMap<Object, Object>();
		private int i;

		public RootObjectCreator(Class<?> clazz, String alias, int i){
			this.clazz = clazz;
			this.alias = alias;
			this.i = i;
		}

		public RootObjectCreator() {
		}

		public CreateResult create(ObjectTree objectTree, Object[] values) {
			try {
				Object id = values[i];
				Object object = resultados.get(id);
				boolean isNew = false;
				if(object == null){
					object = clazz.newInstance();
					resultados.put(id, object);
					isNew = true;
				}
				
				CreateResult createResult = new CreateResult(isNew, object);
				return createResult;
			}
			catch (InstantiationException e) {
				throw new RuntimeException("Erro ao criar o objeto raiz da query. "+this.clazz.getSimpleName(), e);
			}
			catch (IllegalAccessException e) {
				throw new RuntimeException("Erro ao criar o objeto raiz da query. "+this.clazz.getSimpleName(), e);
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
			rootObjectCreator.i = i;
			return rootObjectCreator;
		}
	
	}
}

class ObjectMapper {
	
	List<PropertyMapper> mappers = new ArrayList<PropertyMapper>();
	
	void init(AliasMap[] aliasMaps, String[] propriedades){
		for (int i = 0; i < propriedades.length; i++) {
			String full = propriedades[i];
			StringTokenizer stringTokenizer = new StringTokenizer(full, ".");
			String owneralias = stringTokenizer.nextToken();
			String property = stringTokenizer.nextToken();
			if(stringTokenizer.hasMoreTokens()){
				throw new RuntimeException("não é possível ter uma propriedade de propriedade " +full);
			}
			Class<?> owner = QueryBuilderResultTranslatorImpl.getOwner(aliasMaps, owneralias);
			if(owner == null){
				throw new QueryBuilderException("Não foi encontrada a classe para o alias '"+owneralias+"'");
			}
			PropertyDescriptor propertyDescriptor = PersistenceUtils.getPropertyDescriptor(owner, property);
			Method method = propertyDescriptor.getWriteMethod();
			PropertyMapper propertyMapper = new PropertyMapper();
			propertyMapper.index = i;
			propertyMapper.alias = owneralias;
			if(method == null){
				boolean isTransient = false;
				if(propertyDescriptor.getReadMethod() != null && (propertyDescriptor.getReadMethod().isAnnotationPresent(Transient.class))){
					isTransient = true;
				}
				throw new RuntimeException("No setter method found for '"+property+"' in class "+owner.getName()+". "+(isTransient?" This property is transient and must not be used on queries.":""));
			}
			propertyMapper.setter = method;
			mappers.add(propertyMapper);
		}
	}
	
	void map(Object[] values, ObjectTree objectTree){
		for (PropertyMapper mapper : mappers) {
			mapper.map(values, objectTree);
		}
	}
	
	class PropertyMapper {
		
		int index;
		String alias;
		Method setter;
		
		void map(Object[] values, ObjectTree objectTree){
			Object value = values[index];
			boolean instanceofusertype = value instanceof UserType;
			//TODO MELHORARA A FORMA DE VERIFICAR SE UM USERTYPE É NULO
			if ((!instanceofusertype && value != null) || (instanceofusertype && ((UserType)value).toString() != null && ((UserType)value).toString().length() > 0)) {
				Object object = objectTree.getAliasObject().get(alias);
				if(object == null){
//					if(!((value instanceof Money) && ((Money)value).toLong() == 0)){ //what is that?
						throw new RuntimeException("Erro ao configurar propriedade de " + alias + " ... " + setter+". O objeto com alias "+alias+" não foi criado!! Valor: "+value);
//					}
				} else {
					try {
						setter.invoke(object, value);
					} catch (Exception e) {
						throw new RuntimeException("Erro ao configurar propriedade de " + alias + " ... " + setter, e);
					}		
				}
			}
		}
	}
	
	
}
/**
 * Contém o mapa com os alias e os objetos criados (POJO)
 * @author rogelgarcia
 *
 */
class ObjectTree {
	
	Map<String, Object> aliasObject = new HashMap<String, Object>();
	Object root;
	boolean isNew;
	
	public Map<String, Object> getAliasObject() {
		return aliasObject;
	}
	
	public Object getRoot() {
		return root;
	}
}