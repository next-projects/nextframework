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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.core.web.NextWeb;
import org.nextframework.exception.NextException;
import org.nextframework.util.Util;
import org.nextframework.view.BaseTag;
import org.nextframework.view.BeanTag;
import org.nextframework.view.PropertyTag;

/**
 * @author rogelgarcia
 * @since 07/02/2006
 * @version 1.1
 */
public class DetailTag extends TemplateTag {

	protected String name;
	private String fullNestedName;
	protected String indexProperty;

	private Class<?> detailClass;
	private String detailDysplayName;
	private Object itens;
	private String detailVar;

	private String tableId;
	private String colspan = "1";
	private String beforeNewLine;
	private String onNewLine;
	private String onDelete;

	private Boolean showActionColumn = true;
	private String actionColumnName;

	private Boolean showDeleteButton = true;
	private String deleteLinkLabel;
	private Boolean showNewLineButton = true;
	private String newLineButtonLabel;

	@Override
	protected void doComponent() throws Exception {

		montarFullNestedName();

		BeanTag beanTag = findParent(BeanTag.class, true);
		BeanDescriptor beanDescriptor = beanTag.getBeanDescriptor();
		PropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(fullNestedName);

		String propertyPrefix = beanTag.getPropertyPrefix();
		String propertyIndex = beanTag.getPropertyIndex();
		String separator = propertyPrefix != null || propertyIndex != null ? "." : "";
		fullNestedName = Util.strings.emptyIfNull(propertyPrefix) + (propertyIndex != null ? "[" + propertyIndex + "]" : "") + separator + fullNestedName;

		Type type = propertyDescriptor.getType();
		if (type instanceof ParameterizedType) {
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type type2 = parameterizedType.getActualTypeArguments()[0];
			if (type2 instanceof Class<?>) {
				detailClass = (Class<?>) type2;
			} else if (type2 instanceof ParameterizedType) {
				detailClass = (Class<?>) ((ParameterizedType) type2).getRawType();
			} else {
				throw new NextException("Tipo não suportado " + type2);
			}
		} else {
			throw new NextException("A propriedade " + fullNestedName + " de " + beanDescriptor.getTargetClass().getName() + " deveria ser uma lista genérica");
		}

		detailDysplayName = Util.beans.getDisplayName(propertyDescriptor, NextWeb.getRequestContext().getLocale());
		itens = propertyDescriptor.getValue();
		if (Util.strings.isEmpty(detailVar)) {
			detailVar = Util.strings.uncaptalize(detailClass.getSimpleName());
		}
		tableId = (Util.strings.isNotEmpty(id) ? id : "detalhe_" + Util.strings.uncaptalize(detailClass.getSimpleName()) + "_" + generateUniqueId());

		beforeNewLine = beforeNewLine == null ? "" : beforeNewLine.trim();
		if (!beforeNewLine.endsWith(";")) {
			beforeNewLine = beforeNewLine + ";";
		}

		onNewLine = onNewLine == null ? "" : onNewLine.trim();

		onDelete = onDelete == null ? "return true;" : onDelete.trim();

		if (actionColumnName == null) {
			actionColumnName = getDefaultViewLabel("actionColumnName", "Ação");
		}

		if (deleteLinkLabel == null) {
			deleteLinkLabel = getDefaultViewLabel("deleteLinkLabel", "Remover");
		}

		if (newLineButtonLabel == null) {
			newLineButtonLabel = getDefaultViewLabel("newLineButtonLabel", "Adicionar Registro");
		}

		pushAttribute("Tdetalhe", this); //Legacy
		includeJspTemplate();
		popAttribute("Tdetalhe");

	}

	@SuppressWarnings("unchecked")
	protected void montarFullNestedName() {
		BaseTag firstParent = findFirst(PropertyTag.class, BeanTag.class);
		String separator = name.startsWith("[") ? "" : ".";
		if (firstParent instanceof PropertyTag) {
			fullNestedName = ((PropertyTag) firstParent).getFullNestedName() + separator + name;
		} else {
			fullNestedName = name;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFullNestedName() {
		return fullNestedName;
	}

	public void setFullNestedName(String fullNestedName) {
		this.fullNestedName = fullNestedName;
	}

	public String getIndexProperty() {
		return indexProperty;
	}

	public void setIndexProperty(String indexProperty) {
		this.indexProperty = indexProperty;
	}

	public Class<?> getDetailClass() {
		return detailClass;
	}

	public void setDetailClass(Class<?> detailClass) {
		this.detailClass = detailClass;
	}

	public String getDetailDysplayName() {
		return detailDysplayName;
	}

	public void setDetailDysplayName(String detailDysplayName) {
		this.detailDysplayName = detailDysplayName;
	}

	public Object getItens() {
		return itens;
	}

	public void setItens(Object itens) {
		this.itens = itens;
	}

	public String getDetailVar() {
		return detailVar;
	}

	public void setDetailVar(String detailVar) {
		this.detailVar = detailVar;
	}

	public String getTableId() {
		return tableId;
	}

	public void setTableId(String tabelaId) {
		this.tableId = tabelaId;
	}

	public String getColspan() {
		return colspan;
	}

	public void setColspan(String colspan) {
		this.colspan = colspan;
	}

	public String getBeforeNewLine() {
		return beforeNewLine;
	}

	public void setBeforeNewLine(String beforeNewLine) {
		this.beforeNewLine = beforeNewLine;
	}

	public String getActionColumnName() {
		return actionColumnName;
	}

	public void setActionColumnName(String actionColumnName) {
		this.actionColumnName = actionColumnName;
	}

	@Deprecated
	public void setNomeColunaAcao(String nomeColunaAcao) {
		this.actionColumnName = nomeColunaAcao;
	}

	@Deprecated
	public String getNomeColunaAcao() {
		return actionColumnName;
	}

	public String getOnDelete() {
		return onDelete;
	}

	public void setOnDelete(String onDelete) {
		this.onDelete = onDelete;
	}

	@Deprecated
	public String getOnRemove() {
		return onDelete;
	}

	@Deprecated
	public void setOnRemove(String onRemove) {
		this.onDelete = onRemove;
	}

	public String getOnNewLine() {
		return onNewLine;
	}

	public void setOnNewLine(String onNewLine) {
		this.onNewLine = onNewLine;
	}

	public Boolean getShowActionColumn() {
		return showActionColumn;
	}

	public void setShowActionColumn(Boolean showActionColumn) {
		this.showActionColumn = showActionColumn;
	}

	@Deprecated
	public Boolean getShowColunaAcao() {
		return showActionColumn;
	}

	@Deprecated
	public void setShowColunaAcao(Boolean showColunaAcao) {
		this.showActionColumn = showColunaAcao;
	}

	public Boolean getShowDeleteButton() {
		return showDeleteButton;
	}

	public void setShowDeleteButton(Boolean showDeleteButton) {
		this.showDeleteButton = showDeleteButton;
	}

	@Deprecated
	public Boolean getShowBotaoRemover() {
		return showDeleteButton;
	}

	@Deprecated
	public void setShowBotaoRemover(Boolean showDeleteButton) {
		this.showDeleteButton = showDeleteButton;
	}

	public String getDeleteLinkLabel() {
		return deleteLinkLabel;
	}

	public void setDeleteLinkLabel(String deleteLinkLabel) {
		this.deleteLinkLabel = deleteLinkLabel;
	}

	public Boolean getShowNewLineButton() {
		return showNewLineButton;
	}

	public void setShowNewLineButton(Boolean showNewLineButton) {
		this.showNewLineButton = showNewLineButton;
	}

	@Deprecated
	public Boolean getShowBotaoNovaLinha() {
		return showNewLineButton;
	}

	@Deprecated
	public void setShowBotaoNovaLinha(Boolean showBotaoNovaLinha) {
		this.showNewLineButton = showBotaoNovaLinha;
	}

	public String getNewLineButtonLabel() {
		return newLineButtonLabel;
	}

	public void setNewLineButtonLabel(String newLineButtonLabel) {
		this.newLineButtonLabel = newLineButtonLabel;
	}

}