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

import java.util.Iterator;

import org.nextframework.util.Util;

public class MenuBuilderJS implements MenuBuilder {

	public static final String ICONE_PADRAO = "&nbsp;&nbsp;&nbsp;&nbsp;";

	protected String id;
	protected String urlPrefix;
	protected String orientation;
	protected String panelStyleClass;

	public MenuBuilderJS(String id, String urlPrefix, String orientation, String panelStyleClass) {
		this.id = id;
		this.urlPrefix = urlPrefix;
		this.orientation = Util.strings.isNotEmpty(orientation) ? orientation : "hbr";
		this.panelStyleClass = panelStyleClass;
	}

	@Override
	public String build(Menu menu) {

		StringBuilder code = new StringBuilder();

		String divId = "div_" + id;
		code.append("<span");
		if (Util.strings.isNotEmpty(panelStyleClass)) {
			code.append(" class=\"" + panelStyleClass + "\"");
		}
		code.append(" id=\"" + divId + "\"></span>");

		String menuId = "menu_" + id;
		code.append("<script language=\"JavaScript\">");
		code.append("var " + menuId + " = [");
		for (Iterator<Menu> iter = menu.getSubmenus().iterator(); iter.hasNext();) {
			Menu submenu = iter.next();
			build(code, submenu, iter.hasNext(), false);
		}
		code.append("];");

		code.append("cmDraw ('" + divId + "', " + menuId + ", '" + orientation + "', cmThemeOffice, 'ThemeOffice');");
		code.append("</script>");

		return code.toString();
	}

	private void build(StringBuilder code, Menu menu, boolean hasNext, boolean iconePadrao) {

		if (menu.getTitle() != null && menu.getTitle().startsWith("---")) {
			code.append("_cmSplit");
			if (hasNext) {
				code.append(',');
			}
			return;
		}
		openMenu(code);

		String icon = menu.getIcon();
		if (iconePadrao && Util.strings.isEmpty(icon)) {
			icon = ICONE_PADRAO;
		} else if (Util.strings.isNotEmpty(icon)) {
			icon = "<img src=\"" + icon + "\" align=\"absmiddle\">";
		}
		printItem(code, icon);
		code.append(',');

		printItem(code, menu.getTitle());
		code.append(',');

		String url = menu.getUrl();
		if (urlPrefix != null && Util.strings.isNotEmpty(url) && !url.startsWith("javascript:")) {
			url = urlPrefix + url;
		}

		//Verifica URL Sufix
		printItem(code, url);
		code.append(',');

		printItem(code, menu.getTarget());
		code.append(',');
		printItem(code, menu.getDescription());
		if (menu.containSubMenus()) {
			code.append(',');
			for (Iterator<Menu> iter = menu.getSubmenus().iterator(); iter.hasNext();) {
				Menu submenu = iter.next();
				build(code, submenu, iter.hasNext(), true);
			}
			closeMenu(code, hasNext);
		} else {
			closeMenu(code, hasNext);
		}

	}

	private void closeMenu(StringBuilder code, boolean hasNext) {
		code.append(']');
		if (hasNext) {
			code.append(',');
		}
	}

	private void printItem(StringBuilder code, String texto) {
		code.append('\'');
		code.append(texto);
		code.append('\'');
	}

	private void openMenu(StringBuilder code) {
		code.append('[');
	}

}