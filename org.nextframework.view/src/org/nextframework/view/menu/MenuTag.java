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

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.nextframework.authorization.Authorization;
import org.nextframework.authorization.User;
import org.nextframework.core.web.NextWeb;
import org.nextframework.view.BaseTag;

public class MenuTag extends BaseTag {

	private String menupath;
	private Menu menu;
	private String orientation = "hbr";

	@Override
	public void doComponent() throws Exception {

		if (menupath == null && menu == null) {
			throw new RuntimeException("Nenhum menu foi definido");
		}

		String cachedCode = null;
		if (menupath != null) {
			cachedCode = getMenuCodeFromPath();
		} else if (menu != null) {
			cachedCode = getMenuCodeFromMenu();
		}

		String menuId = generateUniqueId();
		String divId = generateUniqueId();
		cachedCode = "var " + menuId + " = \n" + cachedCode + ";";
		String drawCode = "cmDraw ('" + divId + "', " + menuId + ", '" + orientation + "', cmThemeOffice, 'ThemeOffice');";

		getOut().print("<span class=\"menuClass\" id=\"" + divId + "\">");
		getOut().print("</span>");

		getOut().println("<script language=\"JavaScript\">");
		getOut().println(cachedCode);
		getOut().println(drawCode);
		getOut().println("</script>");
	}

	private String getMenuCodeFromPath() throws Exception {

		HttpServletRequest request = getRequest();
		User user = Authorization.getUserLocator().getUser();
		Locale locale = NextWeb.getRequestContext().getLocale();

		String menuCode = (String) MenuCacheResolver.getMenu(request, menupath, user, locale);
		if (menuCode != null) {
			log.debug("Using cached menu... " + menupath);
			return menuCode;
		}

		Menu menu = MenuResolver.carregaMenu(request, menupath, user, locale);

		MenuBuilder menuBuilder = new MenuBuilder(getRequest().getContextPath());
		menuCode = menuBuilder.build(menu);

		MenuCacheResolver.setMenu(request, menuCode, menupath, user, locale);

		return menuCode;
	}

	private String getMenuCodeFromMenu() {
		MenuBuilder menuBuilder = new MenuBuilder(getRequest().getContextPath());
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