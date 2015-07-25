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
 * @author rogelgarcia
 * @since 03/02/2006
 * @version 1.1
 */
public class ListTableTag extends TemplateTag {
	
	protected boolean showEditLink = true;
	protected boolean showDeleteLink = true;
	protected boolean showViewLink = false;
	
	protected Object itens;
	protected String name;
	protected Class<?> valueType;
	
	protected Number currentPage;
	protected Number numberOfPages;

	public Class<?> getValueType() {
		return valueType;
	}

	public void setValueType(Class<?> valueType) {
		this.valueType = valueType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getItens() {
		return itens;
	}

	public void setItens(Object itens) {
		this.itens = itens;
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

	@Override
	protected void doComponent() throws Exception {
		autowireValues();
		pushAttribute("TtabelaResultados", this);
		if(name == null){
			throw new IllegalArgumentException("O atributo name da tag TabelaResultados não foi informado, e também não foi configurado por um CrudController. " +
					"Se estiver utilizando um controller do tipo CrudController verifique se os atributos estão sendo colocados no escopo corretamente " +
					"ou se você não sobrescreveu a funcionalidade padrão do controller.");
		}
		includeJspTemplate();
		popAttribute("TtabelaResultados");
	}

	private void autowireValues() {
		CrudContext crudContext = CrudContext.getCurrentInstance();
		if(itens == null){
			itens = getRequest().getAttribute("lista");
			if(itens == null && crudContext != null){
				itens = crudContext.getListModel().getList();
			}
		}
		if(valueType == null){
//			valueType = (Class<?>) getRequest().getAttribute("TEMPLATE_beanClass");
			valueType = CrudContext.getCurrentInstance().getBeanClass();
		}
		if(Util.strings.isEmpty(name)){
//			name = (String) getRequest().getAttribute("TEMPLATE_beanNameUncaptalized");
			name = CrudContext.getCurrentInstance().getBeanName();
			if(Util.strings.isEmpty(name) && Util.objects.isNotEmpty(valueType)){
				name = Util.strings.uncaptalize(valueType.getSimpleName());
			}
		}
		if(numberOfPages == null){
			numberOfPages = (Number) getRequest().getAttribute("numberOfPages");
			if(numberOfPages == null && crudContext != null){
				numberOfPages = crudContext.getListModel().getFilter().getNumberOfPages();
			}
		}
		if(currentPage == null){
			currentPage = (Number) getRequest().getAttribute("currentPage");
			if(currentPage == null && crudContext != null){
				currentPage = crudContext.getListModel().getFilter().getCurrentPage();
			}
		}
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

	public Number getCurrentPage() {
		return currentPage;
	}

	public Number getNumberOfPages() {
		return numberOfPages;
	}

	public void setCurrentPage(Number currentPage) {
		this.currentPage = currentPage;
	}

	public void setNumberOfPages(Number numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

}
