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

/**
 * @author rogelgarcia
 * @since 03/02/2006
 * @version 1.1
 */
public class FormTableTag extends TemplateTag {

	protected String title;
	protected Integer colspan;

	protected int columns = 2;

	protected String styleClass;
	protected String style;
	protected String columnStyleClasses;
	protected String columnStyles;
	protected String rowStyleClasses;
	protected String rowStyles;

	protected Boolean propertyRenderAsDouble = true;
	protected Boolean propertyShowLabel;

	@Override
	protected void doComponent() throws Exception {

		CrudContext crudContext = CrudContext.getCurrentInstance();

		if (title == null && crudContext != null && crudContext.hasCustomDisplayName()) {
			title = crudContext.getDisplayName();
		}
		if (title == null) {
			title = getDefaultViewLabel("title", null);
		}
		if (title == null && crudContext != null) {
			title = crudContext.getDisplayName();
		}

		if (propertyShowLabel == null) {
			propertyShowLabel = propertyRenderAsDouble == null || !propertyRenderAsDouble;
		}

		pushAttribute("TtabelaEntrada", this); //Legacy
		includeJspTemplate();
		popAttribute("TtabelaEntrada");

	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getColspan() {
		return colspan;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public String getColumnStyleClasses() {
		return columnStyleClasses;
	}

	public void setColumnStyleClasses(String columnStyleClasses) {
		this.columnStyleClasses = columnStyleClasses;
	}

	public String getColumnStyles() {
		return columnStyles;
	}

	public void setColumnStyles(String columnStyles) {
		this.columnStyles = columnStyles;
	}

	public String getRowStyleClasses() {
		return rowStyleClasses;
	}

	public void setRowStyleClasses(String rowStyleClasses) {
		this.rowStyleClasses = rowStyleClasses;
	}

	public String getRowStyles() {
		return rowStyles;
	}

	public void setRowStyles(String rowStyles) {
		this.rowStyles = rowStyles;
	}

	public Boolean getPropertyRenderAsDouble() {
		return propertyRenderAsDouble;
	}

	public void setPropertyRenderAsDouble(Boolean propertyRenderAsDouble) {
		this.propertyRenderAsDouble = propertyRenderAsDouble;
	}

	public Boolean getPropertyShowLabel() {
		return propertyShowLabel;
	}

	public void setPropertyShowLabel(Boolean propertyShowLabel) {
		this.propertyShowLabel = propertyShowLabel;
	}

}