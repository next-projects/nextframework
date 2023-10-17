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
import org.nextframework.util.Util;

/**
 * @author rogelgarcia | marcusabreu
 * @since 03/02/2006
 * @version 1.1
 */
public class ListTableTag extends TemplateTag {

	protected Object itens;
	protected String name;
	protected String statusName;
	protected Class<?> valueType;
	protected String groupProperty;

	protected boolean showViewLink = false;
	protected boolean showEditLink = true;
	protected boolean showDeleteLink = true;
	protected String actionColumnName;

	protected String selectLinkLabel;
	protected String viewLinkLabel;
	protected String updateLinkLabel;
	protected String deleteLinkLabel;
	protected String deleteLinkConfirmation;

	protected String pageLabel;
	protected Number currentPage;
	protected Number numberOfPages;

	protected String pagePanelStyleClass;

	@Override
	protected void doComponent() throws Exception {

		autowireValues();

		if (actionColumnName == null) {
			actionColumnName = getDefaultViewLabel("actionColumnName", "Ação");
		}

		if (selectLinkLabel == null) {
			selectLinkLabel = getDefaultViewLabel("selectLinkLabel", "Selecionar");
		}

		if (viewLinkLabel == null) {
			viewLinkLabel = getDefaultViewLabel("viewLinkLabel", "Consultar");
		}

		if (updateLinkLabel == null) {
			updateLinkLabel = getDefaultViewLabel("updateLinkLabel", "Editar");
		}

		if (deleteLinkLabel == null) {
			deleteLinkLabel = getDefaultViewLabel("deleteLinkLabel", "Excluir");
		}

		if (deleteLinkConfirmation == null) {
			deleteLinkConfirmation = getDefaultViewLabel("deleteLinkConfirmation", "Deseja realmente excluir esse registro?");
		}

		if (pageLabel == null) {
			pageLabel = getDefaultViewLabel("pageLabel", "Página");
		}

		if (name == null) {
			throw new IllegalArgumentException("O atributo name da tag ListTableTag não foi informado, e também não foi configurado por um CrudController. " +
					"Se estiver utilizando um controller do tipo CrudController verifique se os atributos estão sendo colocados no escopo corretamente " +
					"ou se você não sobrescreveu a funcionalidade padrão do controller.");
		}

		pushAttribute("TtabelaResultados", this);
		includeJspTemplate();
		popAttribute("TtabelaResultados");

	}

	private void autowireValues() {

		CrudContext crudContext = CrudContext.getCurrentInstance();

		if (itens == null) {
			itens = getRequest().getAttribute("lista");
			if (itens == null && crudContext != null) {
				itens = crudContext.getListModel().getList();
			}
		}

		if (Util.strings.isEmpty(name) && crudContext != null) {
			name = crudContext.getBeanName();
			if (Util.strings.isEmpty(name) && Util.objects.isNotEmpty(valueType)) {
				name = Util.strings.uncaptalize(valueType.getSimpleName());
			}
		}

		if (Util.strings.isEmpty(statusName) && Util.strings.isNotEmpty(name)) {
			statusName = name + "Status";
		}

		if (valueType == null && crudContext != null) {
			valueType = crudContext.getBeanClass();
		}

		if (currentPage == null) {
			currentPage = (Number) getRequest().getAttribute("currentPage");
			if (currentPage == null && crudContext != null && crudContext.getListModel().getFilter() != null) {
				currentPage = crudContext.getListModel().getFilter().getCurrentPage();
			}
		}

		if (numberOfPages == null) {
			numberOfPages = (Number) getRequest().getAttribute("numberOfPages");
			if (numberOfPages == null && crudContext != null && crudContext.getListModel().getFilter() != null) {
				numberOfPages = crudContext.getListModel().getFilter().getNumberOfPages();
			}
		}

	}

	public Object getItens() {
		return itens;
	}

	public void setItens(Object itens) {
		this.itens = itens;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public Class<?> getValueType() {
		return valueType;
	}

	public void setValueType(Class<?> valueType) {
		this.valueType = valueType;
	}

	public String getGroupProperty() {
		return groupProperty;
	}

	public void setGroupProperty(String groupProperty) {
		this.groupProperty = groupProperty;
	}

	public String getActionColumnName() {
		return actionColumnName;
	}

	public void setActionColumnName(String actionColumnName) {
		this.actionColumnName = actionColumnName;
	}

	public boolean isShowViewLink() {
		return showViewLink;
	}

	public void setShowViewLink(boolean showConsultarLink) {
		this.showViewLink = showConsultarLink;
	}

	@Deprecated
	public boolean isShowConsultarLink() {
		return showViewLink;
	}

	@Deprecated
	public void setShowConsultarLink(boolean showConsultarLink) {
		this.showViewLink = showConsultarLink;
	}

	public boolean isShowEditLink() {
		return showEditLink;
	}

	public void setShowEditLink(boolean showEditarLink) {
		this.showEditLink = showEditarLink;
	}

	@Deprecated
	public boolean isShowEditarLink() {
		return showEditLink;
	}

	@Deprecated
	public void setShowEditarLink(boolean showEditarLink) {
		this.showEditLink = showEditarLink;
	}

	public boolean isShowDeleteLink() {
		return showDeleteLink;
	}

	public void setShowDeleteLink(boolean showExcluirLink) {
		this.showDeleteLink = showExcluirLink;
	}

	@Deprecated
	public boolean isShowExcluirLink() {
		return showDeleteLink;
	}

	@Deprecated
	public void setShowExcluirLink(boolean showExcluirLink) {
		this.showDeleteLink = showExcluirLink;
	}

	public String getSelectLinkLabel() {
		return selectLinkLabel;
	}

	public void setSelectLinkLabel(String selectLinkLabel) {
		this.selectLinkLabel = selectLinkLabel;
	}

	public String getViewLinkLabel() {
		return viewLinkLabel;
	}

	public void setViewLinkLabel(String viewLinkLabel) {
		this.viewLinkLabel = viewLinkLabel;
	}

	public String getUpdateLinkLabel() {
		return updateLinkLabel;
	}

	public void setUpdateLinkLabel(String updateLinkLabel) {
		this.updateLinkLabel = updateLinkLabel;
	}

	public String getDeleteLinkLabel() {
		return deleteLinkLabel;
	}

	public void setDeleteLinkLabel(String deleteLinkLabel) {
		this.deleteLinkLabel = deleteLinkLabel;
	}

	public String getDeleteLinkConfirmation() {
		return deleteLinkConfirmation;
	}

	public void setDeleteLinkConfirmation(String deleteLinkConfirmation) {
		this.deleteLinkConfirmation = deleteLinkConfirmation;
	}

	public String getPageLabel() {
		return pageLabel;
	}

	public void setPageLabel(String pageLabel) {
		this.pageLabel = pageLabel;
	}

	public Number getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Number currentPage) {
		this.currentPage = currentPage;
	}

	public Number getNumberOfPages() {
		return numberOfPages;
	}

	public void setNumberOfPages(Number numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	public String getPagePanelStyleClass() {
		return pagePanelStyleClass;
	}

	public void setPagePanelStyleClass(String pagePanelStyleClass) {
		this.pagePanelStyleClass = pagePanelStyleClass;
	}

}
