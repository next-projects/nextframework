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

public class FilterPanelTag extends SimplePanelTag {

	protected String name = "filter";

	protected Boolean showSubmit = true;
	protected String submitUrl = null;
	protected String submitAction = null;
	protected Boolean validateForm = true;
	protected String submitLabel = null;

	protected String buttonStyleClass;

	@Override
	protected void doComponent() throws Exception {

		if (submitAction == null) {
			if (findParent(ReportViewTag.class) != null) {
				submitAction = "generate";
			}
		}

		if (sectionTitle == null) {
			sectionTitle = getDefaultViewLabel("sectionTitle", "Filtragem");
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

	public String getButtonStyleClass() {
		return buttonStyleClass;
	}

	public void setButtonStyleClass(String buttonStyleClass) {
		this.buttonStyleClass = buttonStyleClass;
	}

}