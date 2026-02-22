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
package org.nextframework.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.nextframework.core.standard.Next;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author rogelgarcia
 * @since 27/10/2005
 * @version 1.0
 */
public class WebContextMap extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;
	private HttpServletRequest httpRequest;

	public WebContextMap(HttpServletRequest request) {
		this.httpRequest = request;
	}

	public int size() {
		throw new UnsupportedOperationException("Método não implementado");
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean containsKey(Object key) {
		throw new UnsupportedOperationException("Método não implementado");
	}

	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException("Método não implementado");
	}

	public Object get(Object key) {
		//precedencia: request, session, application, beans do spring
		Object object = httpRequest.getAttribute(key.toString());
		if (object == null) {
			object = httpRequest.getSession().getAttribute(key.toString());
		}
		if (object == null) {
			object = httpRequest.getSession().getServletContext().getAttribute(key.toString());
		}
		if (object == null) {
			try {
				//TODO PROCURAR BEANS
				object = Next.getBeanFactory().getBean(key.toString());
			} catch (NoSuchBeanDefinitionException e) {
				//se nao tiver o bean, nao tem problema.. retorna null
			}
		}
		return object;
	}

	public Object put(String key, Object value) {
		Object attribute = httpRequest.getAttribute(key.toString());
		httpRequest.setAttribute(key.toString(), value);
		return attribute;
	}

	public Object remove(Object key) {
		Object attribute = httpRequest.getAttribute(key.toString());
		httpRequest.removeAttribute(key.toString());
		return attribute;
	}

	@SuppressWarnings("all")
	public void putAll(Map t) {
		throw new UnsupportedOperationException("Método não implementado");

	}

	public void clear() {
		throw new UnsupportedOperationException("Método não implementado");

	}

	public Set<String> keySet() {
		throw new UnsupportedOperationException("Método não implementado");
	}

	public Collection<Object> values() {
		throw new UnsupportedOperationException("Método não implementado");
	}

	public Set<Entry<String, Object>> entrySet() {
		throw new UnsupportedOperationException("Método não implementado");
	}

}
