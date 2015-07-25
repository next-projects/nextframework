package org.nextframework.persistence;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.AssociationType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.Type;
import org.nextframework.service.ServiceException;
import org.nextframework.service.ServiceFactory;


public class PersistenceUtils {
	
	private static final char PROPERTY_SEPARATOR = '.';

	static HibernateSessionProvider getSessionProvider() {
		return getSessionProvider(PersistenceConfiguration.DEFAULT_CONFIG);
	}
	static HibernateSessionProvider getSessionProvider(String persistenceContext) {
		PersistenceConfiguration config = PersistenceConfiguration.getConfig(persistenceContext);
		if(config == null){
			throw new NullPointerException("Cannot find PersistenceConfiguration for context "+persistenceContext);
		}
		HibernateSessionProvider configSessionProvider = config.getSessionProvider();
		return configSessionProvider != null? 
				  configSessionProvider : createSessionProviderUsingServiceFactory();
	}

	//TODO CONFIGURE TRANSACTION, SEARCH FOR FACTORY
	static HibernateSessionProvider createSessionProviderUsingServiceFactory(){
		try {
			return ServiceFactory.getService(HibernateSessionProvider.class);
		} catch (NoClassDefFoundError e) {
			throw new QueryBuilderException("No HibernateSessionProvider available. Try using PersistenceConfiguration.getConfig().setSessionProvider(...) OR " +
					"include nextframework-core jar and configure a ServiceFactory.", e);
		} catch(RuntimeException e){
			try {
				if (e instanceof ServiceException) { //as this exception is not a required dependency it could not exist in the first catch clause
					throw new QueryBuilderException("No HibernateSessionProvider available. Try using PersistenceConfiguration.getConfig().setSessionProvider(...) OR " +
							"register the HibernateSessionProvider as a service.", e);
				}
			} catch (NoClassDefFoundError e2) {}
			throw e;
		}
	}

	public static String removeAccents(String string) {
		String source = "¡…Õ”⁄¿»Ã“Ÿ¬ Œ‘€ƒÀœ÷‹√’«·ÈÌÛ˙‡ËÏÚ˘‚ÍÓÙ˚‰ÎÔˆ¸„ıÁ";
		String target = "AEIOUAEIOUAEIOUAEIOUAOCaeiouaeiouaeiouaeiouaoc";
		return translate(string, source, target);
	}

	/*
	 * Copied from CharSetUtils (commons lang) under Apache License http://www.apache.org/licenses/LICENSE-2.0
	 */
	static String translate(String str, String searchChars, String replaceChars) {
		if (str == null || str.length() == 0) {
			return str;
		}
		StringBuffer buffer = new StringBuffer(str.length());
		char[] chrs = str.toCharArray();
		char[] withChrs = replaceChars.toCharArray();
		int sz = chrs.length;
		int withMax = replaceChars.length() - 1;
		for (int i = 0; i < sz; i++) {
			int idx = searchChars.indexOf(chrs[i]);
			if (idx != -1) {
				if (idx > withMax) {
					idx = withMax;
				}
				buffer.append(withChrs[idx]);
			} else {
				buffer.append(chrs[i]);
			}
		}
		return buffer.toString();
	}

	public static String getIdPropertyName(Class<?> fromClass, SessionFactory sessionFactory) {
		return getClassMetadata(fromClass, sessionFactory).getIdentifierPropertyName();
	}
	
	@SuppressWarnings("deprecation")
	public static Serializable getId(Object entity, SessionFactory sessionFactory) {
		return getClassMetadata(entity.getClass(), sessionFactory).getIdentifier(entity);
	}

	private static ClassMetadata getClassMetadata(Class<? extends Object> class1, SessionFactory sessionFactory) {
		if(class1.getSimpleName().contains("$$")){
			//this is a generated class.. get the user class
			class1 = class1.getSuperclass();
		}
		return sessionFactory.getClassMetadata(class1);
	}

	public static Object getProperty(Object object, String property) {
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor(object, property);
		try {
			return propertyDescriptor.getReadMethod().invoke(object);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getTargetException());
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Error setting reading " + property + " of " + object, e);
		}
	}

	public static void setProperty(Object object, String property, Object value) {
		int indexOfSubPath = property.indexOf(PROPERTY_SEPARATOR);
		if(indexOfSubPath > 0){//we have a subproperty here
			String base = property.substring(0, indexOfSubPath);
			String token = property.substring(indexOfSubPath + 1);
			PropertyDescriptor basePropertyDescriptor = getPropertyDescriptor(object, base);
			Method readMethod = basePropertyDescriptor.getReadMethod();
			if(readMethod == null){
				throw new IllegalArgumentException("Property "+base+" of path "+property+" of object "+object+" is not readable");
			}
			Object baseValue;
			try {
				baseValue = readMethod.invoke(object);
			} catch (Exception e) {
				throw new IllegalArgumentException("Error reading "+base+" of path "+property+" of object "+object, e);
			}
			if(baseValue == null){
				Class<?> baseType = readMethod.getReturnType();
				try {
					baseValue = baseType.newInstance();
				} catch (Exception e) {
					throw new IllegalArgumentException("Error instanciating "+baseType+" for "+base+" of path "+property+" of object "+object, e);
				}
				setProperty(object, base, baseValue);
			}
			//configure deeper object
			object = baseValue;
			property = token;
		}
		PropertyDescriptor propertyDescriptor = getPropertyDescriptor(object, property);
		try {
			if(value instanceof Collection){
				Class<?> propertyType = propertyDescriptor.getWriteMethod().getParameterTypes()[0];
				if(!propertyType.isAssignableFrom(value.getClass())){
					if(propertyType.isAssignableFrom(Set.class)){
						value = new LinkedHashSet<Object>((Collection<?>)value);
					} else if(propertyType.isAssignableFrom(List.class)){
						value = new ArrayList<Object>((Collection<?>)value);
					}
				}
			}
			propertyDescriptor.getWriteMethod().invoke(object, value);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getTargetException());
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException("Error setting property "+property+" of "+object, e);
		}
	}

	public static PropertyDescriptor getPropertyDescriptor(Object object, String property) {
		if(object == null){
			throw new IllegalArgumentException("Object cannot be null");
		}
		Class<? extends Object> clazz = object.getClass();
		return getPropertyDescriptor(clazz, property);
	}

	public static PropertyDescriptor getPropertyDescriptor(Class<? extends Object> clazz, String property) {
		PropertyDescriptor propertyDescriptor = null;
		if(clazz == null){
			throw new IllegalArgumentException("Class cannot be null");
		}
		try {
			for (PropertyDescriptor pd : Introspector.getBeanInfo(clazz).getPropertyDescriptors()) {
				if(pd.getName().equals(property)){
					propertyDescriptor = pd;
					break;
				}
			}
		} catch (IntrospectionException e) {
			throw new IllegalArgumentException("Cannot get property "+property+" of "+clazz, e);
		}
		if(propertyDescriptor == null){
			throw new IllegalArgumentException("Invalid property "+property+" of "+clazz);
		}
		return propertyDescriptor;
	}

	static Class<?> getPropertyType(Object object, String property) {
		return getPropertyDescriptor(object.getClass(), property).getReadMethod().getReturnType();
	}

	public static class InverseCollectionProperties {
		public Class<?> type;
		public String property;
	}
	
	public static Class<?> getPropertyAssociationType(SessionFactory sessionFactory, Class<?> clazz, String property) {
		Class<?> result = null;
		ClassMetadata classMetadata = getClassMetadata(clazz, sessionFactory);
		if(classMetadata == null){
			throw new PersistenceException("Class "+clazz.getName()+" is not mapped. ");
		}
		org.hibernate.type.Type propertyType = classMetadata.getPropertyType(property);
		if (propertyType instanceof AssociationType) {
			AssociationType associationType = (AssociationType) propertyType;
			String associatedEntityName = associationType.getAssociatedEntityName((SessionFactoryImplementor) sessionFactory);
			AbstractEntityPersister abstractEntityPersister = (AbstractEntityPersister)sessionFactory.getClassMetadata(associatedEntityName);
			result = abstractEntityPersister.getEntityType().getReturnedClass();
		} else {
			throw new PersistenceException("Property \""+property+"\" of "+clazz+" is not an association type (i.e. ManyToOne, OneToMany, AnyType, etc)");
		}
		return result;
	}
	
	
	public static InverseCollectionProperties getInverseCollectionProperty(SessionFactory sessionFactory, Class<? extends Object> clazz, String collectionProperty) {
		SessionFactoryImplementor sessionFactoryImplementor;
		String[] keyColumnNames;
		Class<?> returnedClass;
		try {
			sessionFactoryImplementor = (SessionFactoryImplementor) sessionFactory;
			ClassMetadata classMetadata = getClassMetadata(clazz, sessionFactory);
			if(classMetadata == null){
				throw new PersistenceException("Class "+clazz.getName()+" is not mapped. ");
			}
			CollectionType ct = (CollectionType) classMetadata.getPropertyType(collectionProperty);
			AbstractCollectionPersister collectionMetadata = (AbstractCollectionPersister) sessionFactoryImplementor.getCollectionMetadata(ct.getRole());
			keyColumnNames = ((AbstractCollectionPersister) collectionMetadata).getKeyColumnNames();
			returnedClass = ct.getElementType(sessionFactoryImplementor).getReturnedClass();
		} catch (ClassCastException e) {
			throw new PersistenceException("Property \""+collectionProperty+"\" of "+clazz+" is not a mapped as a collection.");
		}
		
		AbstractEntityPersister collectionItemMetadata = (AbstractEntityPersister) sessionFactoryImplementor.getClassMetadata(returnedClass);
		Type[] propertyTypes = collectionItemMetadata.getPropertyTypes();
		String[] propertyNames = collectionItemMetadata.getPropertyNames();
		for (int i = 0; i < propertyTypes.length; i++) {
			Type type = propertyTypes[i];
			String propertyName = propertyNames[i];
			String[] propertyColumnNames = collectionItemMetadata.getPropertyColumnNames(propertyName);
			InverseCollectionProperties inverseCollectionProperties = getInverseCollectionProperties(sessionFactoryImplementor, clazz, returnedClass, keyColumnNames, propertyColumnNames, type, propertyName);
			if(inverseCollectionProperties != null){
				return inverseCollectionProperties;
			}
		}
		//check id
		Type identifierType = collectionItemMetadata.getIdentifierType();
		String identifierName = collectionItemMetadata.getIdentifierPropertyName();
		String[] identifierColumnNames = collectionItemMetadata.getIdentifierColumnNames();
		InverseCollectionProperties inverseCollectionProperties = getInverseCollectionProperties(sessionFactoryImplementor, clazz, returnedClass, keyColumnNames, identifierColumnNames, identifierType, identifierName);
		if(inverseCollectionProperties != null){
			return inverseCollectionProperties;
		}
		throw new PersistenceException("Collection "+collectionProperty+" of "+clazz+" does not have an inverse path!");
	}
	
	@SuppressWarnings("unchecked")
	private static InverseCollectionProperties getInverseCollectionProperties(SessionFactoryImplementor sessionFactoryImplementor, Class<? extends Object> clazz, Class<?> returnedClass, String[] keyColumnNames,	String[] propertyColumnNames, Type propertyType, String propertyName) {
		if(propertyType instanceof ManyToOneType){
			ManyToOneType mtot = (ManyToOneType) propertyType;
			if(mtot.getReturnedClass().isAssignableFrom(clazz)){
				//if propertyColumnNames == keyColumnNames
				if(Arrays.deepEquals(propertyColumnNames, keyColumnNames)){
					InverseCollectionProperties inverseCollectionProperties = new InverseCollectionProperties();
					inverseCollectionProperties.property = propertyName;
					inverseCollectionProperties.type = returnedClass;
					return inverseCollectionProperties;
				}
			}
		}
		if(propertyType instanceof ComponentType){
			ComponentType ct = (ComponentType) propertyType;
			String[] propertyNames = ct.getPropertyNames();
			Type[] propertyTypes = ct.getSubtypes();
			int beginIndex = 0;
			for (int i = 0; i < propertyTypes.length; i++) {
				Type subType = propertyTypes[i];
				String subPropertyName = propertyNames[i];
				int columnSpan = subType.getColumnSpan(sessionFactoryImplementor);
				String[] propertyColumnNamesMapping = new String[columnSpan];
				System.arraycopy(propertyColumnNames, beginIndex, propertyColumnNamesMapping, 0, columnSpan);
				InverseCollectionProperties inverseCollectionProperties = getInverseCollectionProperties(sessionFactoryImplementor, clazz, returnedClass, keyColumnNames, propertyColumnNamesMapping, subType, subPropertyName);
				if(inverseCollectionProperties != null){
					inverseCollectionProperties.property = propertyName+"."+inverseCollectionProperties.property;
					return inverseCollectionProperties;
				}
				beginIndex += columnSpan;
			}
		}
		return null;
	}
	/**
	 * Removes from the src all elements present in toRemove collection
	 * @param list
	 * @param itens
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List removeFromCollectionUsingId(SessionFactory sessionFactory, Collection src, Collection toRemove) {
		List<Object> result = new ArrayList<Object>();
		for (Object object : src) {
			if(!contains(sessionFactory, toRemove, object)){
				result.add(object);
			}
		}
		return result;
	}
	
	@SuppressWarnings("rawtypes") 
	private static boolean contains(SessionFactory sessionFactory, Collection collection, Object object) {
		Serializable id2 = getId(object, sessionFactory);
		for (Object item : collection) {
			Serializable id1 = getId(item, sessionFactory);
			if(id1.equals(id2)){
				return true;
			}
		}
		return false;
	}

	
}
