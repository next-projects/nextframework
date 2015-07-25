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

	//atributos
	protected String name;
	
	protected String indexProperty;
	
	//extra
	private String fullNestedName;
	private Class<?> detailClass;
	private String detailDysplayName;
	private Object itens;
	private String tableId;
	private String detailVar;
	private String beforeNewLine;
	private String actionColumnName = "Ação";
	
	private String onDelete = "return true;";
	private String onNewLine;

	private Boolean showActionColumn = true;
	private Boolean showButtonDelete = true;
	private Boolean showButtonNewLine = true;


	public Boolean getShowButtonNewLine() {
		return showButtonNewLine;
	}

	public void setShowButtonNewLine(Boolean showBotaoNovaLinha) {
		this.showButtonNewLine = showBotaoNovaLinha;
	}
	
	@Deprecated
	public Boolean getShowBotaoNovaLinha() {
		return showButtonNewLine;
	}
	
	@Deprecated
	public void setShowBotaoNovaLinha(Boolean showBotaoNovaLinha) {
		this.showButtonNewLine = showBotaoNovaLinha;
	}

	
	public String getIndexProperty() {
		return indexProperty;
	}

	public String getActionColumnName() {
		return actionColumnName;
	}

	public void setNomeColunaAcao(String nomeColunaAcao) {
		this.actionColumnName = nomeColunaAcao;
	}
	
	public String getNomeColunaAcao() {
		return actionColumnName;
	}
	
	public void setActionColumnName(String nomeColunaAcao) {
		this.actionColumnName = nomeColunaAcao;
	}


	public Boolean getShowButtonDelete() {
		return showButtonDelete;
	}

	public void setShowButtonDelete(Boolean showBotaoRemover) {
		this.showButtonDelete = showBotaoRemover;
	}
	
	@Deprecated
	public Boolean getShowBotaoRemover() {
		return showButtonDelete;
	}
	
	@Deprecated
	public void setShowBotaoRemover(Boolean showBotaoRemover) {
		this.showButtonDelete = showBotaoRemover;
	}


	public Boolean getShowActionColumn() {
		return showActionColumn;
	}

	public void setShowActionColumn(Boolean showColunaAcao) {
		this.showActionColumn = showColunaAcao;
	}
	
	@Deprecated
	public Boolean getShowColunaAcao() {
		return showActionColumn;
	}
	@Deprecated
	public void setShowColunaAcao(Boolean showColunaAcao) {
		this.showActionColumn = showColunaAcao;
	}


	@Override
	protected void doComponent() throws Exception {
		montarFullNestedName();
		BeanTag beanTag = findParent(BeanTag.class, true);
		BeanDescriptor beanDescriptor = beanTag.getBeanDescriptor();
		PropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(fullNestedName);
		//copiar o prefixo e o property index do bean em questao
		String propertyPrefix = beanTag.getPropertyPrefix();
		String propertyIndex = beanTag.getPropertyIndex();
		String separator = propertyPrefix != null || propertyIndex != null? ".":"";
		fullNestedName = Util.strings.emptyIfNull(propertyPrefix) 
						+ (propertyIndex != null ? "["+propertyIndex+"]" : "") + separator + fullNestedName;
		
		Type type = propertyDescriptor.getType();
		if(type instanceof ParameterizedType){
			ParameterizedType parameterizedType = (ParameterizedType) type;
			Type type2 = parameterizedType.getActualTypeArguments()[0];
			if(type2 instanceof Class<?>){
				detailClass = (Class<?>) type2;
			} else if(type2 instanceof ParameterizedType){
				detailClass = (Class<?>)((ParameterizedType)type2).getRawType();
			} else {
				throw new Exception("Tipo não suportado "+type2);
			}
		} else {
			throw new Exception("A propriedade "+fullNestedName+" de "+beanDescriptor.getTargetClass().getName()+" deveria ser uma lista genérica");
		}
		
		// verificar carregamento lazy

		//fim do carregamento lazy
		
		
		detailDysplayName = propertyDescriptor.getDisplayName();
		itens = propertyDescriptor.getValue();
		detailVar = Util.strings.uncaptalize(detailClass.getSimpleName());
		tableId = (Util.strings.isNotEmpty(id)? id : "detalhe_" + detailVar + "_" + generateUniqueId());
		pushAttribute("Tdetalhe", this);
		includeJspTemplate();
		popAttribute("Tdetalhe");
	}
	
	
	@SuppressWarnings("unchecked")
	protected void montarFullNestedName() {
//		PropertyTag propertyTag = findParent(PropertyTag.class);
//		if(propertyTag != null){
//			String parentFullNestedName = propertyTag.getFullNestedName();
//			if(!name.startsWith("[")){
//				name = "." +name;
//			}
//			fullNestedName = parentFullNestedName + name;
//		} else {
//			fullNestedName = name;
//		}
		BaseTag firstParent = findFirst(PropertyTag.class, BeanTag.class);
		String separator = name.startsWith("[")?"":".";
		if(firstParent instanceof PropertyTag){
			fullNestedName = ((PropertyTag)firstParent).getFullNestedName()+separator+name;
		} else {
			fullNestedName = name;	
		}
	}


	public Class<?> getDetailClass() {
		return detailClass;
	}


	public String getDetailDysplayName() {
		return detailDysplayName;
	}


	public String getFullNestedName() {
		return fullNestedName;
	}


	public Object getItens() {
		return itens;
	}


	public String getName() {
		return name;
	}


	public String getTableId() {
		return tableId;
	}
	
	public String getBeforeNewLine() {
		if(beforeNewLine == null){
			return "";
		} 
		
		if(!beforeNewLine.trim().endsWith(";"))
				beforeNewLine = beforeNewLine + ";";
		
		return beforeNewLine;
	}


	public void setDetailClass(Class<?> detailClass) {
		this.detailClass = detailClass;
	}


	public void setDetailDysplayName(String detailDysplayName) {
		this.detailDysplayName = detailDysplayName;
	}


	public void setFullNestedName(String fullNestedName) {
		this.fullNestedName = fullNestedName;
	}


	public void setItens(Object itens) {
		this.itens = itens;
	}


	public void setName(String name) {
		this.name = name;
	}


	public void setTableId(String tabelaId) {
		this.tableId = tabelaId;
	}


	public String getDetailVar() {
		return detailVar;
	}
	
	public void setBeforeNewLine(String beforeNewLine) {
		this.beforeNewLine = beforeNewLine;
	}

	public void setDetailVar(String detailVar) {
		this.detailVar = detailVar;
	}


	public String getOnNewLine() {
		if(onNewLine == null){
			return "";
		}
		return onNewLine;
	}


	public void setOnNewLine(String onNewLine) {
		this.onNewLine = onNewLine;
	}


	public String getOnDelete() {
		return onDelete;
	}
	public void setOnDelete(String onRemove) {
		this.onDelete = onRemove;
	}
	@Deprecated
	public String getOnRemove() {
		return onDelete;
	}
	@Deprecated
	public void setOnRemove(String onRemove) {
		this.onDelete = onRemove;
	}
	
	public void setIndexProperty(String indexProperty) {
		this.indexProperty = indexProperty;
	}
}
