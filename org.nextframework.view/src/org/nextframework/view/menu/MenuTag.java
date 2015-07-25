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
package org.nextframework.view.menu;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.nextframework.authorization.Authorization;
import org.nextframework.authorization.User;
import org.nextframework.view.BaseTag;
import org.nextframework.view.WebUtils;

public class MenuTag extends BaseTag {
	
	private static final String MENU_CACHE_MAP = MenuTag.class.getName() + "_cache";
	private static final String MENU_CACHE_MAP_TIME = MenuTag.class.getName() + "_cache_time";
	private static final String MENU_CACHE_MAP_USER = MenuTag.class.getName() + "_cache_user";
	
	protected Menu menu;
	protected String menupath;
	protected String orientation = "hbr";

	@Override
	public void doComponent() throws JspException, IOException {
		
		if(menupath == null && menu == null){
			throw new RuntimeException("Nenhum menu foi definido");
		}
		
		String cachedCode = null;
		if (menu != null) {
			cachedCode = getMenuCodeFromMenu(menu);
		}else {
			cachedCode = getMenuCodeFromPath();
		}
		
		
		String menuId = generateUniqueId();
		String divId = generateUniqueId();
		cachedCode = "var "+menuId+" = \n"+cachedCode+";";
		String drawCode = "cmDraw ('"+divId+"', "+menuId+", '"+orientation+"', cmThemeOffice, 'ThemeOffice');";

		getOut().print("<span class=\"menuClass\" id=\""+divId+"\">");
		getOut().print("</span>");
		
		getOut().println("<script language=\"JavaScript\">");
		getOut().println(cachedCode);
		getOut().println(drawCode);
		getOut().println("</script>");
	}
	
	@SuppressWarnings("unchecked")
	private String getMenuCodeFromPath(){
		
		HttpServletRequest request = getRequest();
		
		//menu cache completo
		Map<String, String> menuCacheMap = (Map<String, String>) request.getSession().getAttribute(MENU_CACHE_MAP);
		Long time = (Long) request.getSession().getAttribute(MENU_CACHE_MAP_TIME);
		User user = (User) request.getSession().getAttribute(MENU_CACHE_MAP_USER);
		if(menuCacheMap == null || MenuResolver.resetMenu(time, user)){
			menuCacheMap = new HashMap<String, String>();
			request.getSession().setAttribute(MENU_CACHE_MAP, menuCacheMap);
			request.getSession().setAttribute(MENU_CACHE_MAP_TIME, System.currentTimeMillis());
			request.getSession().setAttribute(MENU_CACHE_MAP_USER, Authorization.getUserLocator().getUser());
		}
		
		//Verifica se a URL é reescrita para forçar releitura e adaptação do sufixo
		boolean urlSufix = !WebUtils.rewriteUrl("url").equals("url");
		
		//cada menu separadamente
		String menuCode = menuCacheMap.get(menupath);
		if (menuCode != null && !urlSufix ) {
			log.debug("Using cached menu... " + menupath);
			return menuCode;
		}
		
		Menu menu = MenuResolver.getMenu(request, menupath, false);
		
		menuCode = getMenuCodeFromMenu(menu);
		menuCacheMap.put(menupath, menuCode);
		
		return menuCode;
	}
	
	private String getMenuCodeFromMenu(Menu menu){
		
		HttpServletRequest request = getRequest();
		
		MenuBuilder menuBuilder = new MenuBuilder();
		menuBuilder.setUrlPrefix(request.getContextPath());
		String menuCode = menuBuilder.build(menu);
		
		return menuCode;
	}

	public String getMenupath() {
		return menupath;
	}
	public void setMenupath(String menupath) {
		this.menupath = menupath;
	}
	
	public Menu getMenu() {
		return menu;
	}
	public void setMenu(Menu menu) {
		this.menu = menu;
	}
	
	public String getOrientation() {
		return orientation;
	}
	public void setOrientation(String orientation) {
		this.orientation = orientation;
	}
		
}