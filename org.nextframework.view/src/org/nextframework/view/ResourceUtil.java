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

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.nextframework.controller.resource.Resource;

public class ResourceUtil {

	private static final String RESOURCE_MAP = "NEXT.RESOURCE.MAP";
	private static final String RESOURCE_COUTER = "NEXT.RESOURCE.COUNTER";
	private static final String RESOURCE_DATE = "NEXT.RESOURCE.DATE";

	public static Integer save(HttpSession session, Resource resource) {

		if (resource == null) {
			throw new NullPointerException("O recurso não foi informado.");
		}

		Integer number = null;
		synchronized (session) {

			Map<Integer, Resource> map = getResourceMap(session);
			number = getNextId(session);
			map.put(number, resource);

			Map<Integer, Calendar> dateMap = getResourceDateMap(session);
			dateMap.put(number, Calendar.getInstance());

			cleanOldResources(map, dateMap);

		}

		return number;
	}

	@SuppressWarnings("unchecked")
	private static Map<Integer, Resource> getResourceMap(HttpSession session) {
		Map<Integer, Resource> map = (Map<Integer, Resource>) session.getAttribute(RESOURCE_MAP);
		if (map == null) {
			map = new HashMap<Integer, Resource>();
			session.setAttribute(RESOURCE_MAP, map);
		}
		return map;
	}

	private static Integer getNextId(HttpSession session) {
		Integer number = (Integer) session.getAttribute(RESOURCE_COUTER);
		number = number == null ? 1 : number + 1;
		session.setAttribute(RESOURCE_COUTER, number);
		return number;
	}

	@SuppressWarnings("unchecked")
	private static Map<Integer, Calendar> getResourceDateMap(HttpSession session) {
		Map<Integer, Calendar> map = (Map<Integer, Calendar>) session.getAttribute(RESOURCE_DATE);
		if (map == null) {
			map = new HashMap<Integer, Calendar>();
			session.setAttribute(RESOURCE_DATE, map);
		}
		return map;
	}

	private static void cleanOldResources(Map<Integer, Resource> map, Map<Integer, Calendar> dateMap) {

		Calendar limite = Calendar.getInstance();
		limite.add(Calendar.MINUTE, -10);

		Set<Integer> idsMortos = new HashSet<Integer>();
		for (Entry<Integer, Calendar> entry : dateMap.entrySet()) {
			if (entry.getValue().before(limite)) {
				idsMortos.add(entry.getKey());
			}
		}

		for (Integer id : idsMortos) {
			map.remove(id);
			dateMap.remove(id);
		}

	}

	public static Resource get(HttpSession session, Integer id) {

		Resource resource = null;
		synchronized (session) {
			Map<Integer, Resource> map = getResourceMap(session);
			resource = map.remove(id);
		}

		return resource;
	}

}