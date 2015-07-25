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
/*
 * Criado em 21/03/2005
 *
 */
package org.nextframework.authorization.web.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.authorization.Authorization;
import org.nextframework.authorization.AuthorizationDAO;
import org.nextframework.authorization.AuthorizationItem;
import org.nextframework.authorization.AuthorizationModule;
import org.nextframework.authorization.Permission;
import org.nextframework.authorization.PermissionLocator;
import org.nextframework.authorization.ResourceAuthorizationMapper;
import org.nextframework.authorization.Role;
import org.nextframework.authorization.User;
import org.nextframework.service.ServiceFactory;
import org.nextframework.web.WebContext;


/**
 * @author rogelgarcia
 */
public class WebPermissionLocator implements PermissionLocator {
	
	static Log log = LogFactory.getLog(PermissionLocator.class);

	private static final String CACHE_ROLES = "CACHE_ROLES";
	
	protected AuthorizationDAO authorizationDAO;
	
	public void setAuthorizationDAO(AuthorizationDAO authorizationDAO) {
		this.authorizationDAO = authorizationDAO;
	}
	
	public AuthorizationDAO getAuthorizationDAO() {
		return authorizationDAO;
	}

    Map<String, Map<Role, Permission>> cache = new HashMap<String, Map<Role, Permission>>();
    
    public synchronized Permission[] getPermissions(User user, String resource) {
    	if(user == null){
    		throw new NullPointerException();
    	}
    	if(authorizationDAO == null){
    		authorizationDAO = Authorization.getAuthorizationDAO();
    	}
    	Map<User, Role[]> cacheRoles = getCacheRoles();    
        //tentar achar roles no cache
    	Role[] userRoles = cacheRoles.get(user);
		if (userRoles == null) {
			// buscar do banco
			userRoles = authorizationDAO.findUserRoles(user);
			cacheRoles.put(user, userRoles);
		}

        Permission[] permissions = new Permission[userRoles.length];
        for (int i = 0; i < userRoles.length; i++) {
        	String controlName = resource;
        	Role role = userRoles[i];
        	
            //tentar cache
        	Map<Role, Permission> mapRolePermission;
			mapRolePermission = cache.get(resource);
        	if(mapRolePermission!=null){
        		Permission permission = mapRolePermission.get(role);
        		if(permission != null){ //achou no cache, pular o resto do loop
        			log.debug("Using cached permission: "+permission);
        			permissions[i] = permission;
        			continue;
        		}
        	} else {
        		//criar o mapa, mais tarde nesse loop será populado
        		mapRolePermission = new HashMap<Role, Permission>();
				cache.put(resource, mapRolePermission);
        	}
            
            //tentar banco
			permissions[i] = authorizationDAO.findPermission(role, controlName);
            
            if(permissions[i]==null){
            	//criar mapa de autorizacao default
            	AuthorizationModule authorizationModule = ServiceFactory.getService(ResourceAuthorizationMapper.class).getAuthorizationModule(resource);
            	
				Map<String, String> defaultPermissionMap = getDefaultPermissionMap(authorizationModule);
            	//adicionar permissao faltante no banco
            	log.debug("Criando permissao... control="+controlName+", role="+role.getName());
            	permissions[i] = authorizationDAO.savePermission(controlName, role, defaultPermissionMap);
            	
            	if(permissions[i] == null){
            		throw new IllegalStateException("The AuthorizationDAO has returned null for method savePermission. user="+user.getUsername()+", resource="+resource+". Check your AuthorizationDAO implementation. ("+authorizationDAO+")");
            	}
                /*
            	throw new InconsistencyException("Inconsistencia de dados: " +
                		"Não há permissão para o papel '"+role.getName()+"' para acessar o Controller '"+control.getName()+"'");
                */
            }
            
			cache.get(resource).put(role, permissions[i]);
        }
        return permissions;
    }


	@SuppressWarnings("unchecked")
	private Map<User, Role[]> getCacheRoles() {
		Map<User, Role[]> cache = (Map<User, Role[]>) WebContext.getRequest().getSession().getAttribute(CACHE_ROLES);
		if(cache == null){
			cache = new HashMap<User, Role[]>();
			WebContext.getRequest().getSession().setAttribute(CACHE_ROLES, cache);
		}
		return cache;
	}


	private Map<String, String> getDefaultPermissionMap(AuthorizationModule authorizationModule) {
		AuthorizationItem[] authorizationItens = authorizationModule.getAuthorizationItens();
		Map<String, String> defaultPermissionMap = new HashMap<String, String>();
		
		for (AuthorizationItem item : authorizationItens) {
			String id = item.getId();
			if(item.getValues()== null || item.getValues().length == 0) throw new IllegalArgumentException("Os valores possíveis de um item de autorização não pode ser um array vazio ou null");
			String valorMaisRestritivo = item.getValues()[item.getValues().length-1];
			defaultPermissionMap.put(id, valorMaisRestritivo);
		}
		return defaultPermissionMap;
	}


    public synchronized void reset() {
    	cache = new HashMap<String, Map<Role, Permission>>();
    }


	public void clearCache() {
		reset();
	}

}
