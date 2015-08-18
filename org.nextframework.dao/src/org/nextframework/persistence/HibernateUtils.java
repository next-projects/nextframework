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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.springframework.orm.hibernate4.HibernateTemplate;

public class HibernateUtils {
	
	static Log log = LogFactory.getLog(HibernateUtils.class);

	public static Object getId(HibernateTemplate hibernateTemplate, Object bean){
		return PersistenceUtils.getId(bean, hibernateTemplate.getSessionFactory());
	}
	
	public static String getIdAttribute(HibernateTemplate hibernateTemplate, Class<? extends Object> class1){
		while(class1.getName().contains("$$")){
			class1 = class1.getSuperclass();
		}
		return hibernateTemplate.getSessionFactory().getClassMetadata(class1).getIdentifierPropertyName();
	}
	
	public static String getEntityName(HibernateTemplate hibernateTemplate, Class<? extends Object> class1){
		while(class1.getName().contains("$$")){
			class1 = class1.getSuperclass();
		}
		return hibernateTemplate.getSessionFactory().getClassMetadata(class1).getEntityName();
	}

	public static boolean isLazy(Object value) {
		if(value instanceof HibernateProxy){
			LazyInitializer hibernateLazyInitializer = ((HibernateProxy)value).getHibernateLazyInitializer();
			return hibernateLazyInitializer.isUninitialized();
		} else if(value instanceof PersistentCollection){
			return !((PersistentCollection)value).wasInitialized();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public static <E> E getLazyValue(E value) {
		if(value instanceof HibernateProxy){
			LazyInitializer hibernateLazyInitializer = ((HibernateProxy)value).getHibernateLazyInitializer();
			if(hibernateLazyInitializer.isUninitialized()){
				try {
					Class<?> superclass = value.getClass().getSuperclass();
					Serializable identifier = hibernateLazyInitializer.getIdentifier();

					value = loadValue(value, superclass, identifier);
				} catch (IllegalArgumentException e) {
				} catch (SecurityException e) {
				}
			}
		} else if(value instanceof PersistentCollection){
			try {
				PersistentCollection collection = (PersistentCollection) value;
				if (!collection.wasInitialized()) {
					Object owner = collection.getOwner();
					String role = collection.getRole();
					value = (E) DAOUtils.getDAOForClass(owner.getClass()).loadCollection(owner, role.substring(role.lastIndexOf('.') + 1));
					System.out.println("COLECAO LAZY " + owner.getClass().getSimpleName() + "." + role);
				}
			} catch (Exception e) {
				//se nao conseguir carregar o valor lazy, n�o fazer nada
			}
		}
		return value;
	}

	public static <E> E loadValue(E value, Class<?> superclass, Serializable identifier) {
		if (identifier != null) {
			GenericDAO<?> daoForClass = DAOUtils.getDAOForClass(superclass);
			Object newValue = daoForClass.loadById(identifier);
			//new QueryBuilder(Next.getObject(HibernateTemplate.class)).from(superclass).idEq(identifier).unique();
			if(newValue != null){
				value = (E) newValue;
			} else {
				log.warn("Cannot find "+superclass.getSimpleName()+" with id "+identifier+" in database.");										
			}
			log.warn("Loading object of " + superclass + " lazily. Use leftOuterJoinFetch on query to avoid this.");
		}
		return value;
	}
}