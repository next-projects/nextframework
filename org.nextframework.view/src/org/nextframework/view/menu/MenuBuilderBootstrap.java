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

import org.nextframework.exception.NextException;
import org.nextframework.util.Util;

public class MenuBuilderBootstrap implements MenuBuilder {

	public static final String ICONE_PADRAO = "&nbsp;&nbsp;&nbsp;&nbsp;";

	protected String urlPrefix;
	protected String orientation;
	protected String panelStyleClass;
	protected String subPanelStyleClass;

	public MenuBuilderBootstrap(String urlPrefix, String orientation, String panelStyleClass, String subPanelStyleClass) {

		this.urlPrefix = urlPrefix;
		this.orientation = orientation;
		this.panelStyleClass = panelStyleClass;
		this.subPanelStyleClass = subPanelStyleClass;

		if (Util.strings.isNotEmpty(orientation) && (!orientation.equals("dropstart") && !orientation.equals("dropend") && !orientation.equals("dropup") && !orientation.equals("right"))) {
			throw new NextException("Property 'orientation' must be one of: dropstart, dropend, dropup or right. Value found: " + orientation);
		}

	}

	@Override
	public String build(Menu menu) {

		StringBuilder code = new StringBuilder();

		code.append("<ul class=\"navbar-nav");

		if (Util.strings.isNotEmpty(panelStyleClass)) {
			code.append(" " + panelStyleClass);
		}

		code.append("\">");

		for (Iterator<Menu> iter = menu.getSubmenus().iterator(); iter.hasNext();) {
			Menu submenu = iter.next();
			build(code, submenu, false);
		}

		code.append("</ul>");

		return code.toString();
	}

	private void build(StringBuilder code, Menu menu, boolean isDropdown) {

		if (menu.getTitle() != null && menu.getTitle().startsWith("---")) {
			code.append("<li><hr class=\"dropdown-divider\"></li>");
			return;
		}

		//if (!isDropdown) {
		if (menu.containSubMenus() && !isDropdown) {
			code.append("<li class=\"nav-item dropdown");
			if (Util.strings.isNotEmpty(orientation) && orientation.startsWith("drop")) {
				code.append(" " + orientation);
			}
			code.append("\">");
		} else {
			code.append("<li class=\"nav-item\">");
		}

		code.append("<a");

		if (!isDropdown) {
			code.append(" class=\"nav-link");
		} else {
			code.append(" class=\"dropdown-item");
		}

		if (menu.containSubMenus() && !isDropdown) {
			code.append(" dropdown-toggle");
		}

		code.append("\"");

		if (menu.containSubMenus() && !isDropdown) {
			code.append(" data-bs-toggle=\"dropdown\" aria-expanded=\"false\"");
		}

		String url = menu.getUrl();
		if (urlPrefix != null && Util.strings.isNotEmpty(url) && !url.startsWith("javascript:")) {
			url = urlPrefix + url;
		}
		if (Util.strings.isEmpty(url)) {
			url = "#";
		}
		code.append(" href=\"" + url + "\"");

		if (Util.strings.isNotEmpty(menu.getTarget()) && !menu.containSubMenus()) {
			code.append(" target=\"" + menu.getTarget() + "\"");
		}

		if (Util.strings.isNotEmpty(menu.getDescription())) {
			code.append(" title=\"" + menu.getDescription() + "\"");
		}

		code.append(">");

		if (Util.strings.isNotEmpty(menu.getIcon())) {
			code.append("<i class=\"" + menu.getIcon() + "\"></i>");
		}

		if (Util.strings.isNotEmpty(menu.getTitle())) {
			code.append(menu.getTitle());
		}

		code.append("</a>");

		if (menu.containSubMenus()) {

			code.append("<ul");

			if (!isDropdown) {

				code.append(" class=\"dropdown-menu");

				if (Util.strings.isNotEmpty(subPanelStyleClass)) {
					code.append(" " + subPanelStyleClass);
				}

				if (Util.strings.isNotEmpty(orientation) && "right".equals(orientation)) {
					code.append(" dropdown-menu-end");
				}

				code.append("\"");

			}

			code.append(">");

			for (Iterator<Menu> iter = menu.getSubmenus().iterator(); iter.hasNext();) {
				Menu submenu = iter.next();
				build(code, submenu, true);
			}

			code.append("</ul>");
		}

		code.append("</li>");

	}

}