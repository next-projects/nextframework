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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.nextframework.controller.resource.Resource;

//TODO TODOS OS RECURSOS SALVOS NA SESSAO SÓ SÃO ELIMINADOS QUANDO A SESSÃO EXPIRA
// FAZER CÓDIGO PARA ELIMINAR EM DETERMINADO TEMPO
public class ResourceUtil {
	
	private static final String RESOURCE_COUTER = "NEXT.RESOURCE.COUNTER";
	private static final String RESOURCE_MAP = "NEXT.RESOURCE.MAP";
	
	
	
	@SuppressWarnings("unchecked")
	public static Integer save(HttpSession session, Resource resource){
		if(resource == null){
			throw new NullPointerException("O recurso não foi informado.");
		}
		Integer number = null;
		Map<Integer, Resource> map = null;
		synchronized (session) {
			//number
			Object attribute = session.getAttribute(RESOURCE_COUTER);
			if(attribute == null){
				attribute = 1;
				number = 1;
				session.setAttribute(RESOURCE_COUTER, attribute);
			} else {
				number = (Integer) attribute;
				number++;
				session.setAttribute(RESOURCE_COUTER, number);
			}

			//resourcemap
			map = (Map<Integer, Resource>) session.getAttribute(RESOURCE_MAP);
			if(map == null){
				map = new HashMap<Integer, Resource>();
				session.setAttribute(RESOURCE_MAP, map);
			}
		}
		map.put(number, resource);
		return number;
	}
	
	@SuppressWarnings("unchecked")
	public static Resource get(HttpSession session, Integer id){
		Map<Integer, Resource> map = null;
		synchronized (session) {
			//resourcemap
			map = (Map<Integer, Resource>) session.getAttribute(RESOURCE_MAP);
			if(map == null){
				map = new HashMap<Integer, Resource>();
				session.setAttribute(RESOURCE_MAP, map);
			}
		}
	
		return map.remove(id);
	}
	

}

