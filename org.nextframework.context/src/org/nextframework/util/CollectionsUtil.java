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
package org.nextframework.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.nextframework.exception.NextException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessorFactory;

public class CollectionsUtil {

	public String join(Collection<?> list, String separator) {
		return join(list, separator, null);
	}

	public String join(Collection<?> list, String separator, Locale locale) {
		String result = "";
		if (list != null && !list.isEmpty()) {
			Object first = list.iterator().next();
			for (Object o : list) {
				if (o == null) {
					continue;
				}
				result += (o == first ? "" : separator) + Util.strings.toStringDescription(o, locale);
			}
		}
		return result;
	}

	/**
	 * Verifica se determinada coleção possui uma propriedade com determinado valor. 
	 * Se a coleção for nula é retornado falso. <BR>
	 * Aceita nested properties
	 * @param collection
	 * @param property
	 * @param value
	 * @throws NullPointerException
	 * @return
	 */
	public boolean contains(Collection<?> collection, String property, Object value) throws NullPointerException {
		//TODO OTIMIZAR
		if (property == null) {
			throw new NullPointerException("O parametro property não deve ser nulo");
		}
		if (collection == null) {
			return false;
		}
		for (Object object : collection) {
			if (object != null) {
				BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(object);
				try {
					Object objectPropertyValue = beanWrapper.getPropertyValue(property);
					if (objectPropertyValue != null && objectPropertyValue.equals(object)) {
						return true;
					}
				} catch (IllegalArgumentException e) {
					throw new NextException("Problema ao adquirir proprieade " + property + " do bean " + object, e);
				}
			}
		}
		return false;
	}

	public List<?> getListProperty(Collection<?> collection, String property) {
		return getListProperty(collection, property, true, false);
	}

	/**
	 * Cria uma lista com uma propriedade de cada bean do collection fornecido
	 * 
	 * Ex.: Se tiver um collection de Pessoa e a pessoa tiver uma propriedade chamada nome
	 * Será extraido o nome de cada pessoa e montado uma lista
	 * A lista não pode conter null 
	 * Todos os objetos da lista devem ser da mesma classe.
	 * Aceita nested properties
	 * @param collection Coleção de beans de onde deve ser extraido a propriedade
	 * @param property propriedade que deve ser extraida de cada bean
	 * @return Uma lista com os objetos de cada propriedade do bean
	 */
	public List<?> getListProperty(Collection<?> collection, String property, boolean ignoreNullBeans, boolean exceptionOnNullBeans) {
		List<Object> list = new ArrayList<Object>();
		for (Iterator<?> iter = collection.iterator(); iter.hasNext();) {
			Object bean = iter.next();
			if (bean == null) {
				if (exceptionOnNullBeans) {
					throw new NullPointerException("null bean found in list");
				}
				if (!ignoreNullBeans) {
					list.add(null);
				}
				continue;
			}
			BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
			Object value = beanWrapper.getPropertyValue(property);
			if (value != null) {
				list.add(value);
			}
		}
		return list;
	}

	/**
	 * Cria um HashSet com uma propriedade de cada bean do collection fornecido
	 * 
	 * Ex.: Se tiver um collection de Pessoa e a pessoa tiver uma propriedade chamada nome
	 * Será extraido o nome de cada pessoa e montado uma lista
	 * A lista não pode conter null 
	 * Todos os objetos da lista devem ser da mesma classe.
	 * Aceita nested properties
	 * @param collection Coleção de beans de onde deve ser extraido a propriedade
	 * @param property propriedade que deve ser extraida de cada bean
	 * @return Uma lista com os objetos de cada propriedade do bean
	 */
	@SuppressWarnings("all")
	public Set<?> getSetProperty(Collection<?> collection, String property) {
		//TODO OTIMIZAR
		Set set = new HashSet();
		Iterator<?> iter = collection.iterator();
		if (iter.hasNext()) {
			Object next = iter.next();
			BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(next);
			set.add(beanWrapper.getPropertyValue(property));
			while (iter.hasNext()) {
				try {
					((BeanWrapperImpl) beanWrapper).setWrappedInstance(iter.next());
					set.add(beanWrapper.getPropertyValue(property));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
		return set;
	}

	/**
	 * Concatena todos os elementos de uma determinada collection e insere o token entre cada elemento
	 * @param collection Coleção a ser iteragida
	 * @param token String que deve ser usada entre cada elemento
	 * @return
	 */
	public String concatenate(Collection<?> collection, String token) {
		StringBuilder builder = new StringBuilder();
		for (Iterator<?> iter = collection.iterator(); iter.hasNext();) {
			Object o = iter.next();
			builder.append(o);
			if (iter.hasNext()) {
				builder.append(token);
			}
		}
		return builder.toString();
	}

	public String listAndConcatenate(Collection<?> collection, String property, String token) {
		return concatenate(getListProperty(collection, property), token);
	}

	public boolean contains(Collection<?> collection, Object elem) {
		for (Object object : collection) {
			if (object != null) {
				if (elem != null) {
					if (object.equals(elem)) {
						return true;
					}
				}
			} else {
				if (elem == null) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Faz o toString de um para utilizando um separadorConjunto para separar os conjuntos de chave e valor e
	 * um separadorChaveValor que separa a chave do valor
	 */
	public String toString(Map<?, ?> mapa, String separadorConjunto, String separadorChaveValor) {
		StringBuilder builder = new StringBuilder();
		Set<?> keySet = mapa.keySet();
		for (Iterator<?> iter = keySet.iterator(); iter.hasNext();) {
			Object object = iter.next();
			if (object != null) {
				builder.append(object.toString());
			}
			builder.append(separadorChaveValor);
			Object value = mapa.get(object);
			if (value != null) {
				builder.append(value.toString());
			}
			if (iter.hasNext()) {
				builder.append(separadorConjunto);
			}
		}
		return builder.toString();
	}

	public boolean isEmpty(Collection<?> col) {
		return col == null || col.isEmpty();
	}

	public boolean isNotEmpty(Collection<?> col) {
		return !isEmpty(col);
	}

	public boolean isEmpty(Map<?, ?> map) {
		return map == null || map.isEmpty();
	}

	public boolean isNotEmpty(Map<?, ?> map) {
		return !isEmpty(map);
	}

}