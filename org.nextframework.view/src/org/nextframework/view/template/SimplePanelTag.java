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
package org.nextframework.view.template;

public class SimplePanelTag extends TemplateTag {

	protected String sectionTitle;

	protected String panelStyleClass;
	protected String sectionTitleStyleClass;
	protected String bodyStyleClass;
	protected String actionBarStyleClass;

	@Override
	protected void doComponent() throws Exception {
		includeJspTemplate();
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
	}

	public String getPanelStyleClass() {
		return panelStyleClass;
	}

	public void setPanelStyleClass(String panelStyleClass) {
		this.panelStyleClass = panelStyleClass;
	}

	public String getSectionTitleStyleClass() {
		return sectionTitleStyleClass;
	}

	public void setSectionTitleStyleClass(String sectionTitleStyleClass) {
		this.sectionTitleStyleClass = sectionTitleStyleClass;
	}

	public String getBodyStyleClass() {
		return bodyStyleClass;
	}

	public void setBodyStyleClass(String bodyStyleClass) {
		this.bodyStyleClass = bodyStyleClass;
	}

	public String getActionBarStyleClass() {
		return actionBarStyleClass;
	}

	public void setActionBarStyleClass(String actionBarStyleClass) {
		this.actionBarStyleClass = actionBarStyleClass;
	}

}