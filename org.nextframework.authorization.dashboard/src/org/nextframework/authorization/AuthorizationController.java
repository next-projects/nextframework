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
package org.nextframework.authorization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextframework.authorization.web.impl.WebPermissionLocator;
import org.nextframework.classmanager.ClassManagerFactory;
import org.nextframework.controller.Action;
import org.nextframework.controller.Controller;
import org.nextframework.controller.DefaultAction;
import org.nextframework.controller.Input;
import org.nextframework.controller.MultiActionController;
import org.nextframework.core.standard.ApplicationContext;
import org.nextframework.core.web.DefaultWebRequestContext;
import org.nextframework.core.web.WebRequestContext;
import org.nextframework.service.ServiceFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.servlet.ModelAndView;

public class AuthorizationController extends MultiActionController {
	
	TransactionTemplate transactionTemplate;
	
	public void setTransactionTemplate(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	
	@Action("salvar")
	@Input("list")
	public ModelAndView salvar(final WebRequestContext request, final AuthorizationProcessFilter authorizationFilter) {
		final Role role = authorizationFilter.getRole();
		if (role != null) {
			//@SuppressWarnings("unchecked")
			//Enumeration parameterNames = request.getServletRequest().getParameterNames();
			//while(parameterNames.hasMoreElements()){
			//	System.out.println(parameterNames.nextElement());
			//}
			PermissionLocator permissionLocator = Authorization.getPermissionLocator();
			synchronized (request.getSession().getServletContext()) { //TODO FIXME FIX THIS SYNC
				//TODO FIXME CHECK HOW TO FIX THIS
				if(permissionLocator instanceof WebPermissionLocator){
					((WebPermissionLocator)permissionLocator).clearCache();
				}
//				permissionLocator.clearCache();
				Collection<List<AuthorizationProcessItemFilter>> values = authorizationFilter.getGroupAuthorizationMap().values();
				final List<AuthorizationProcessItemFilter> authorizationItemFilters = new ArrayList<AuthorizationProcessItemFilter>();
				for (List<AuthorizationProcessItemFilter> value : values) {
					authorizationItemFilters.addAll(value);
				}
				transactionTemplate.execute(new TransactionCallback<Object>(){

					public Object doInTransaction(TransactionStatus status) {
						//TODO FIXME USE DI
						ResourceAuthorizationMapper authorizationMapper = ServiceFactory.getService(ResourceAuthorizationMapper.class);
						for (AuthorizationProcessItemFilter filter : authorizationItemFilters) {
//							ControlMapping controlMapping = controlMappingLocator.getControlMapping(filter.getPath());
							AuthorizationModule authorizationModule = authorizationMapper.getAuthorizationModule(filter.getPath());
							Map<String, String> defaultPermissionMap = getDefaultPermissionMap(authorizationModule);
							Map<String, String> permissionMap = filter.getPermissionMap();
							Set<String> defaultKeySet = defaultPermissionMap.keySet();
							for (String string : defaultKeySet) {
								if(permissionMap.get(string) == null){
									permissionMap.put(string, defaultPermissionMap.get(string));
								}
							}
							 Authorization.getAuthorizationDAO().savePermission(filter.getPath(), role, permissionMap);
						}
						return null;
					}});

			}
		}
		//reseta os menus
//		request.getSession().setAttribute(MenuTag.MENU_CACHE_MAP, null);
		((DefaultWebRequestContext)request).setLastAction("");
		return list(request, authorizationFilter);
	}

	@DefaultAction
	@Input("")
	public ModelAndView list(WebRequestContext request, AuthorizationProcessFilter authorizationFilter) {
		authorizationFilter.setGroupAuthorizationMap(new HashMap<String, List<AuthorizationProcessItemFilter>>());
		request.setAttribute("roles", Authorization.getAuthorizationDAO().findAllRoles());
		request.setAttribute("filtro", authorizationFilter);
		
		if(authorizationFilter.getRole() != null){
			Map<String, AuthorizationModule> mapaGroupModule = new HashMap<String, AuthorizationModule>();
			Map<String, List<AuthorizationProcessItemFilter>> groupAuthorizationMap = authorizationFilter.getGroupAuthorizationMap();
			
			@SuppressWarnings("all")
			Class[] controllerClasses = findControllerClasses(request.getWebApplicationContext());
			for (Class<?> controllerClass : controllerClasses) {
				Controller controller = controllerClass.getAnnotation(Controller.class);
				String[] paths = controller.path();
//				ControlMappingLocator controlMappingLocator = request.getWebApplicationContext().getConfig().getControlMappingLocator();
				
				
				//TODO FIXME USE DI
				//usar o authorizationModule já configurado, apenas o primeiro path é necessário para reconhecer o controller
				AuthorizationModule authorizationModule = ServiceFactory.getService(ResourceAuthorizationMapper.class).getAuthorizationModule(paths[0]);//controller.authorizationModule().newInstance();
				
				mapaGroupModule.put(authorizationModule.getAuthorizationGroupName(), authorizationModule);
				if(!(authorizationModule instanceof HasAccessAuthorizationModule)){
					AuthorizationProcessItemFilter[] authorizationProcessItemFilters = getAuthorizationProcessItemFilter(authorizationFilter.getRole(), controller, authorizationModule);
					for (AuthorizationProcessItemFilter authorizationProcessItemFilter : authorizationProcessItemFilters) {
						AuthorizationProcessItemFilter authorizationItemFilter = authorizationProcessItemFilter;
						List<AuthorizationProcessItemFilter> list = getAuthorizationListForModule(groupAuthorizationMap, authorizationModule);
						list.add(authorizationItemFilter);													
					}

				}
			}
			request.setAttribute("mapaGroupModule", mapaGroupModule);
		}
		request.setAttribute("authorizationProcessItemFilterClass", AuthorizationProcessItemFilter.class);
		return getModelAndView();
	}

	/**
	 * Subclasses devem sobrescrever para atualizar o caminho do JSP
	 * @return
	 */
	protected ModelAndView getModelAndView() {
		return new ModelAndView("process/autorizacao");
	}

	protected List<AuthorizationProcessItemFilter> getAuthorizationListForModule(Map<String, List<AuthorizationProcessItemFilter>> groupAuthorizationMap, AuthorizationModule authorizationModule) {
		String authorizationGroupName = authorizationModule.getAuthorizationGroupName();
		List<AuthorizationProcessItemFilter> list = groupAuthorizationMap.get(authorizationGroupName);
		if(list == null){
			list = new ArrayList<AuthorizationProcessItemFilter>();
			groupAuthorizationMap.put(authorizationGroupName, list);
		}
		return list;
	}

	protected AuthorizationProcessItemFilter[] getAuthorizationProcessItemFilter(Role role, Controller controller, AuthorizationModule authorizationModule) {
		String[] paths = controller.path();
		List<AuthorizationProcessItemFilter> authorizationItemFilters = new ArrayList<AuthorizationProcessItemFilter>();
		for (String path : paths) {
			Permission permission =  Authorization.getAuthorizationDAO().findPermission(role, path);
			AuthorizationProcessItemFilter authorizationItemFilter = new AuthorizationProcessItemFilter();
			authorizationItemFilter.setAuthorizationModule(authorizationModule);
			authorizationItemFilter.setDescription(translatePath(path));
			authorizationItemFilter.setPath(path);
			if(permission == null){			
				permission =  Authorization.getAuthorizationDAO().savePermission(path, role, getDefaultPermissionMap(authorizationModule));
			}
			authorizationItemFilter.setPermissionMap(permission.getPermissionMap());
			authorizationItemFilters.add(authorizationItemFilter);
		}
		return authorizationItemFilters.toArray(new AuthorizationProcessItemFilter[authorizationItemFilters.size()]);
	}
	
	protected String translatePath(String string) {
		return string;
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

	@SuppressWarnings("all")
	protected Class[] findControllerClasses(ApplicationContext applicationContext) {
		return ClassManagerFactory.getClassManager().getClassesWithAnnotation(Controller.class);
	}

}
