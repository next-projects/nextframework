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

/**
 * @author rogelgarcia
 * @since 03/02/2006
 * @version 1.1
 */
public class FilterTableTag extends TemplateTag {

	protected Integer columns;
	protected Integer colspan;
	protected String width;

	protected String styleClass;
	protected String style;
	protected String columnStyleClasses;
	protected String columnStyles;
	protected String rowStyleClasses;
	protected String rowStyles;

	protected Boolean propertyRenderAsDouble;
	protected Boolean propertyShowLabel;

	protected Boolean showSubmit = true;
	protected String submitUrl = null;
	protected String submitAction = null;
	protected Boolean validateForm = true;
	protected String submitLabel = null;

	@Override
	protected void doComponent() throws Exception {

		if (propertyShowLabel == null) {
			propertyShowLabel = propertyRenderAsDouble == null || !propertyRenderAsDouble;
		}

		if (submitAction == null) {
			if (findParent(ReportViewTag.class) != null) {
				submitAction = "generate";
			}
		}

		if (submitLabel == null) {
			submitLabel = getDefaultViewLabel("submitLabel", "Pesquisar");
		}

		pushAttribute("TabelaFiltroTag", this); //Legacy
		includeJspTemplate();
		popAttribute("TabelaFiltroTag");

	}

	public Integer getColumns() {
		return columns;
	}

	public void setColumns(Integer columns) {
		this.columns = columns;
	}

	public Integer getColspan() {
		return colspan;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
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

}
