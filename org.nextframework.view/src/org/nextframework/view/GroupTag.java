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
package org.nextframework.view;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.jsp.JspException;

import org.nextframework.util.Util;
import org.nextframework.view.combo.ComboTag;
import org.nextframework.view.template.PropertyConfigTag;

/**
 * @author rogelgarcia
 * @since 07/02/2006
 * @version 1.1
 */
public class GroupTag extends ComboTag {

	protected Boolean flatMode;

	protected Integer columns;

	protected String fieldsetStyle;

	protected String fieldsetStyleClass;

	protected String legendStyle;

	protected String legendStyleClass;

	protected String style;

	protected String styleClass;

	protected String rowStyleClasses;

	protected String rowStyles;

	protected String columnStyleClasses;

	protected String columnStyles;

	protected Integer colspan;

	protected String propertyRenderAs;

	protected String legend;

	protected Boolean useParentPanelGridProperties = true;

	@SuppressWarnings("unchecked")
	@Override
	protected void doComponent() throws Exception {

		if (Util.booleans.isTrue(useParentPanelGridProperties)) {
			PanelGridTag parentPanel = findParent(PanelGridTag.class);
			if (parentPanel != null) {
				if (flatMode == null) {
					flatMode = parentPanel.getFlatMode();
				}
				if (Util.strings.isEmpty(style)) {
					style = parentPanel.getStyle();
				}
				if (Util.strings.isEmpty(styleClass)) {
					styleClass = parentPanel.getStyleClass();
				}
				if (Util.strings.isEmpty(rowStyleClasses)) {
					rowStyleClasses = parentPanel.getRowStyleClasses();
				}
				if (Util.strings.isEmpty(rowStyles)) {
					rowStyles = parentPanel.getRowStyles();
				}
				if (Util.strings.isEmpty(columnStyleClasses)) {
					columnStyleClasses = parentPanel.getColumnStyleClasses();
				}
				if (Util.strings.isEmpty(columnStyles)) {
					columnStyles = parentPanel.getColumnStyles();
				}
			}
		}

		if (flatMode == null) {
			flatMode = false;
		}

		if (columns == null || columns == 0/*forçado*/) {
			columns = 1;
		}
		if (columns <= 0) {
			throw new IllegalArgumentException("O atributo columns da tag panelGrid deve ser positivo");
		}

		if (propertyRenderAs == null) {
			BaseTag findFirst = findFirst(PropertyConfigTag.class, PanelGridTag.class);
			if (findFirst instanceof PropertyConfigTag) {
				this.propertyRenderAs = ((PropertyConfigTag) findFirst).getRenderAs();
			} else if (findFirst instanceof PanelGridTag) {
				this.propertyRenderAs = ((PanelGridTag) findFirst).getPropertyRenderAs();
			}
		}

		//Panel

		PanelTag panelTag = new PanelTag();
		setDynamicAttributes("panel", panelTag);

		if (colspan != null) {
			panelTag.setDynamicAttribute(null, "colspan", colspan);
		}

		TagHolder panelHolder = new TagHolder(panelTag);

		//Fieldset and Legend

		SimpleTag fieldsetTag = new SimpleTag("fieldset", null);
		setDynamicAttributes("fieldset", fieldsetTag);
		fieldsetTag.setDynamicAttribute(null, "id", id);
		fieldsetTag.setDynamicAttribute(null, "style", fieldsetStyle);
		fieldsetTag.setDynamicAttribute(null, "class", fieldsetStyleClass);

		TagHolder fieldsetHolder = new TagHolder(fieldsetTag);
		panelHolder.addChild(fieldsetHolder);

		if (legend != null) {

			SimpleTag legendTag = new SimpleTag("legend", legend);
			setDynamicAttributes("legend", legendTag);
			legendTag.setDynamicAttribute(null, "style", legendStyle);
			legendTag.setDynamicAttribute(null, "class", legendStyleClass);

			TagHolder legendHolder = new TagHolder(legendTag);
			fieldsetHolder.addChild(legendHolder);

		}

		//Panelgrid

		PanelGridTag panelGridTag = new PanelGridTag();
		setDynamicAttributes("panelGrid", panelGridTag);
		panelGridTag.setFlatMode(flatMode);
		panelGridTag.setColumns(columns);
		panelGridTag.setStyle(style);
		panelGridTag.setStyleClass(styleClass);
		panelGridTag.setRowStyleClasses(rowStyleClasses);
		panelGridTag.setRowStyles(rowStyles);
		panelGridTag.setColumnStyleClasses(columnStyleClasses);
		panelGridTag.setColumnStyles(columnStyles);
		panelGridTag.setPropertyRenderAs(propertyRenderAs);
		panelGridTag.setUseParentPanelGridProperties(false);
		panelGridTag.setJspBody(getJspBody());

		TagHolder panelGridHolder = new TagHolder(panelGridTag);
		fieldsetHolder.addChild(panelGridHolder);

		invoke(panelHolder);

	}

	private void setDynamicAttributes(String prefix, BaseTag tag) throws JspException {
		Set<String> keySet = new HashSet<String>(getDynamicAttributesMap().keySet());
		for (String key : keySet) {
			if (key.startsWith(prefix)) {
				tag.setDynamicAttribute(null, key.substring(prefix.length()), getDynamicAttributesMap().get(key));
				getDynamicAttributesMap().remove(key);
			}
		}
	}

	public Boolean getFlatMode() {
		return flatMode;
	}

	public void setFlatMode(Boolean flatMode) {
		this.flatMode = flatMode;
	}

	public Integer getColumns() {
		return columns;
	}

	public void setColumns(Integer columns) {
		this.columns = columns;
	}

	public String getFieldsetStyle() {
		return fieldsetStyle;
	}

	public void setFieldsetStyle(String fieldsetStyle) {
		this.fieldsetStyle = fieldsetStyle;
	}

	public String getFieldsetStyleClass() {
		return fieldsetStyleClass;
	}

	public void setFieldsetStyleClass(String fieldsetStyleClass) {
		this.fieldsetStyleClass = fieldsetStyleClass;
	}

	public String getLegendStyle() {
		return legendStyle;
	}

	public void setLegendStyle(String legendStyle) {
		this.legendStyle = legendStyle;
	}

	public String getLegendStyleClass() {
		return legendStyleClass;
	}

	public void setLegendStyleClass(String legendStyleClass) {
		this.legendStyleClass = legendStyleClass;
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

	public Integer getColspan() {
		return colspan;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	public String getPropertyRenderAs() {
		return propertyRenderAs;
	}

	public void setPropertyRenderAs(String propertyRenderAs) {
		this.propertyRenderAs = propertyRenderAs;
	}

	public String getLegend() {
		return legend;
	}

	public void setLegend(String legend) {
		this.legend = legend;
	}

	public Boolean getUseParentPanelGridProperties() {
		return useParentPanelGridProperties;
	}

	public void setUseParentPanelGridProperties(Boolean useParentPanelGridProperties) {
		this.useParentPanelGridProperties = useParentPanelGridProperties;
	}

}

class SimpleTag extends BaseTag {

	protected String tag;
	protected String content;

	public SimpleTag(String tag, String content) {
		this.tag = tag;
		this.content = content;
	}

	@Override
	protected void doComponent() throws Exception {
		getOut().println("<" + tag + " " + getDynamicAttributesToString() + ">");
		if (this.content != null) {
			getOut().println(this.content);
		} else {
			doBody();
		}
		getOut().println("</" + tag + ">");
	}

}
