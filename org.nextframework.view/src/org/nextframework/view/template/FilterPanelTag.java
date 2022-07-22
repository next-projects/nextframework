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

import org.nextframework.controller.crud.CrudContext;
import org.nextframework.persistence.PageAndOrder;

/**
 * @author rogelgarcia
 * @since 03/02/2006
 * @version 1.1
 */
public class FilterPanelTag extends TemplateTag {

	protected String name = "filter";
	protected String sectionTitle;

	protected Boolean showSubmit = true;
	protected String submitUrl = null;
	protected String submitAction = null;
	protected Boolean validateForm = true;
	protected String submitLabel = null;

	protected String sectionTitleStyleClass;
	protected String panelStyleClass;
	protected String actionBarStyleClass;
	protected String buttonStyleClass;

	@Override
	protected void doComponent() throws Exception {

		if (submitAction == null) {
			if (findParent(ReportViewTag.class) != null) {
				submitAction = "generate";
			}
		}

		if (sectionTitle == null) {
			sectionTitle = getDefaultViewLabel("sectionTitle", "Pesquisar");
		}

		if (submitLabel == null) {
			submitLabel = getDefaultViewLabel("submitLabel", "Pesquisar");
		}

		CrudContext crudContext = CrudContext.getCurrentInstance();

		PageAndOrder filter = crudContext != null && getRequest().getAttribute(name) == null ? crudContext.getListModel().getFilter() : null;
		if (filter != null) {
			pushAttribute(name, filter);
		}
		includeJspTemplate();
		if (filter != null) {
			popAttribute(name);
		}

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSectionTitle() {
		return sectionTitle;
	}

	public void setSectionTitle(String sectionTitle) {
		this.sectionTitle = sectionTitle;
	}

	public Boolean getShowSubmit() {
		return showSubmit;
	}

	public void setShowSubmit(Boolean showSubmit) {
		this.showSubmit = showSubmit;
	}

	public String getSubmitUrl() {
		return submitUrl;
	}

	public void setSubmitUrl(String submitUrl) {
		this.submitUrl = submitUrl;
	}

	public String getSubmitAction() {
		return submitAction;
	}

	public void setSubmitAction(String submitAction) {
		this.submitAction = submitAction;
	}

	public Boolean getValidateForm() {
		return validateForm;
	}

	public void setValidateForm(Boolean validateForm) {
		this.validateForm = validateForm;
	}

	public String getSubmitLabel() {
		return submitLabel;
	}

	public void setSubmitLabel(String submitLabel) {
		this.submitLabel = submitLabel;
	}

	public String getSectionTitleStyleClass() {
		return sectionTitleStyleClass;
	}

	public void setSectionTitleStyleClass(String sectionTitleStyleClass) {
		this.sectionTitleStyleClass = sectionTitleStyleClass;
	}

	public String getPanelStyleClass() {
		return panelStyleClass;
	}

	public void setPanelStyleClass(String panelStyleClass) {
		this.panelStyleClass = panelStyleClass;
	}

	public String getActionBarStyleClass() {
		return actionBarStyleClass;
	}

	public void setActionBarStyleClass(String actionBarStyleClass) {
		this.actionBarStyleClass = actionBarStyleClass;
	}

	public String getButtonStyleClass() {
		return buttonStyleClass;
	}

	public void setButtonStyleClass(String buttonStyleClass) {
		this.buttonStyleClass = buttonStyleClass;
	}

}