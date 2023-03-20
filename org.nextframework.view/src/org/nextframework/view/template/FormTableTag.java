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

import org.nextframework.util.Util;

/**
 * @author rogelgarcia e marcusabreu
 */
public class FormTableTag extends TemplateTag {

	private static final String SUBCOMPONENT_NORMAL = "NORMAL";
	private static final String SUBCOMPONENT_FLAT = "FLAT";

	protected Integer colspan;
	protected Integer columns;
	protected Boolean flatMode;

	protected String styleClass;
	protected String style;
	protected String rowStyleClasses;
	protected String rowStyles;
	protected String columnStyleClasses;
	protected String columnStyles;

	protected String propertyRenderAs;
	protected Boolean propertyShowLabel;

	@Override
	protected String getSubComponentName() {
		boolean flatMode = this.flatMode != null ? this.flatMode : getViewConfig().isDefaultFlatMode();
		return flatMode ? SUBCOMPONENT_FLAT : SUBCOMPONENT_NORMAL;
	}

	@Override
	protected void doComponent() throws Exception {

		if (flatMode == null) {
			flatMode = getViewConfig().isDefaultFlatMode();
		}

		if (columns == null) {
			columns = getViewConfig().getDefaultColumns();
		}

		if (propertyRenderAs == null) {
			propertyRenderAs = getViewConfig().getDefaultPropertyRenderAs();
		}

		if (propertyShowLabel == null) {
			propertyShowLabel = PropertyTag.SINGLE.equalsIgnoreCase(propertyRenderAs);
		}

		includeJspTemplate();

	}

	public Integer getColspan() {
		return colspan;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	public Integer getColumns() {
		return columns;
	}

	public void setColumns(Integer columns) {
		this.columns = columns;
	}

	public Boolean getFlatMode() {
		return flatMode;
	}

	public void setFlatMode(Boolean flatMode) {
		this.flatMode = flatMode;
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

	public String getPropertyRenderAs() {
		return propertyRenderAs;
	}

	public void setPropertyRenderAs(String propertyRenderAs) {
		this.propertyRenderAs = propertyRenderAs;
	}

	@Deprecated
	public Boolean getPropertyRenderAsDouble() {
		return PropertyTag.DOUBLE.equalsIgnoreCase(propertyRenderAs);
	}

	@Deprecated
	public void setPropertyRenderAsDouble(Boolean propertyRenderAsDouble) {
		if (Util.booleans.isTrue(propertyRenderAsDouble)) {
			this.propertyRenderAs = PropertyTag.DOUBLE;
		} else {
			this.propertyRenderAs = PropertyTag.SINGLE;
		}
	}

	public Boolean getPropertyShowLabel() {
		return propertyShowLabel;
	}

	public void setPropertyShowLabel(Boolean propertyShowLabel) {
		this.propertyShowLabel = propertyShowLabel;
	}

}