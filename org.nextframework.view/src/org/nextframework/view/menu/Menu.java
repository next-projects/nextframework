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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.nextframework.util.Util;

public class Menu implements Cloneable, Serializable {

	private static final long serialVersionUID = -6149860610100626337L;
	
	private List<Menu> submenus = new ArrayList<Menu>();
	private Menu parent;
	
	private String icon;
	private String title;
	private String url;
	private String target;
	private String description;
	
	public boolean addMenu(Menu menu) {
		menu.setParent(this);
		return submenus.add(menu);
	}
	
	public boolean containSubMenus(){
		return submenus.size() != 0;
	}
	
	public String getDescription() {
		return description;
	}
	public String getIcon() {
		return icon;
	}
	public Menu getParent() {
		return parent;
	}
	public List<Menu> getSubmenus() {
		return submenus;
	}
	public String getTarget() {
		return target;
	}
	public String getTitle() {
		return title;
	}
	public String getUrl() {
		return url;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public void setParent(Menu parent) {
		this.parent = parent;
	}
	public void setSubmenus(List<Menu> submenus) {
		this.submenus = submenus;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	@Override
	public Object clone(){
		Menu clone = new Menu();
		clone.setIcon(this.getIcon());
		clone.setTitle(this.getTitle());
		clone.setUrl(this.getUrl());
		clone.setTarget(this.getTarget());
		clone.setDescription(this.getDescription());
		if (Util.collections.isNotEmpty(submenus)) {
			for (Menu menuFilho : submenus) {
				Menu cloneMenuFilho = (Menu) menuFilho.clone();
				clone.addMenu(cloneMenuFilho);
			}
		}
		return clone;
	}

}
