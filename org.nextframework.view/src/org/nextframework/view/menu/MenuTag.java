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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.nextframework.authorization.Authorization;
import org.nextframework.authorization.Role;
import org.nextframework.authorization.User;
import org.nextframework.core.web.NextWeb;
import org.nextframework.util.Util;
import org.nextframework.view.BaseTag;

public class MenuTag extends BaseTag {

	private static final String MENU_CACHE_MAP = MenuTag.class.getName() + "_cache";

	private String menupath;
	private Menu menu;
	private String orientation;
	private String panelStyleClass;
	private String subPanelStyleClass;

	@Override
	public void doComponent() throws Exception {

		if (menupath == null && menu == null) {
			throw new RuntimeException("Nenhum menu foi definido");
		}

		String code = null;
		if (menu != null) {
			code = getMenuCodeFromMenu(menu);
		} else if (menupath != null) {
			code = getMenuCodeFromPath(menupath);
		}

		getOut().println(code);

	}

	private String getMenuCodeFromPath(String menupath) throws Exception {

		User user = Authorization.getUserLocator().getUser();
		Locale locale = NextWeb.getRequestContext().getLocale();

		String menuCode = getCachedMenuCode(menupath, user, locale);
		if (menuCode != null) {
			return menuCode;
		}

		Menu menu = MenuResolver.carregaMenu(menupath, user, locale);
		menuCode = getMenuCodeFromMenu(menu);

		setCachedMenuCode(menupath, user, locale, menuCode);

		return menuCode;
	}

	private String getMenuCodeFromMenu(Menu menu) {
		MenuBuilder menuBuilder = null;
		if (getViewConfig().isUseBootstrap()) {
			menuBuilder = new MenuBuilderBootstrap(getRequest().getContextPath(), orientation, panelStyleClass, subPanelStyleClass);
		} else {
			menuBuilder = new MenuBuilderJS(generateUniqueId(), getRequest().getContextPath(), orientation, panelStyleClass);
		}
		return menuBuilder.build(menu);
	}

	public String getCachedMenuCode(String menupath, User user, Locale locale) {
		MenuCache menuCache = getCacheMap().get(menupath);
		if (menuCache != null && !resetMenu(menuCache, user, locale)) {
			return menuCache.menuCode;
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private Map<String, MenuCache> getCacheMap() {
		Map<String, MenuCache> menuCacheMap = (Map<String, MenuCache>) getRequest().getSession().getAttribute(MENU_CACHE_MAP);
		if (menuCacheMap == null) {
			menuCacheMap = new HashMap<String, MenuCache>();
			getRequest().getSession().setAttribute(MENU_CACHE_MAP, menuCacheMap);
		}
		return menuCacheMap;
	}

	private boolean resetMenu(MenuCache menuCache, User user, Locale locale) {
		if (!Util.objects.equals(menuCache.user, user) || !Util.objects.equals(menuCache.locale, locale)) {
			return true;
		}
		if (Authorization.getAuthorizationDAO().getLastUpdateTime() > menuCache.time) {
			Role[] roles = Authorization.getAuthorizationDAO().findUserRoles(user);
			for (Role role : roles) {
				if (Authorization.getAuthorizationDAO().getLastUpdateTime(role) > menuCache.time) {
					return true;
				}
			}
		}
		return false;
	}

	public void setCachedMenuCode(String menupath, User user, Locale locale, String menuCode) {
		MenuCache menuCache = new MenuCache(user, locale, menuCode, System.currentTimeMillis());
		getCacheMap().put(menupath, menuCache);
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

	public String getPanelStyleClass() {
		return panelStyleClass;
	}

	public void setPanelStyleClass(String panelStyleClass) {
		this.panelStyleClass = panelStyleClass;
	}

	public String getSubPanelStyleClass() {
		return subPanelStyleClass;
	}

	public void setSubPanelStyleClass(String subPanelStyleClass) {
		this.subPanelStyleClass = subPanelStyleClass;
	}

	private class MenuCache {

		private String menuCode;
		private User user;
		private Locale locale;
		private long time;

		public MenuCache(User user, Locale locale, String menuCode, long time) {
			this.menuCode = menuCode;
			this.user = user;
			this.locale = locale;
			this.time = time;
		}

	}

}