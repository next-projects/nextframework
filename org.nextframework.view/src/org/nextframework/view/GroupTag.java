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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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

	protected String style;

	protected String styleClass;

	protected String rowStyleClasses;

	protected String rowStyles;

	protected String columnStyleClasses;

	protected String columnStyles;

	protected Integer colspan;

	protected String propertyRenderAs;

	protected String legend;

	protected Boolean showBorder = true;

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

		PanelTag panelTag = new PanelTag();

		if (colspan != null) {
			panelTag.setDynamicAttribute(null, "colspan", colspan);
		}

		PanelGridTag panelGridTag = new PanelGridTag();

		HashMap<String, Object> dynamicAttributesMapPanelGrid = new HashMap<String, Object>(getDynamicAttributesMap());

		Set<String> keySet = new HashSet<String>(getDynamicAttributesMap().keySet());
		for (String key : keySet) {
			if (key.startsWith("panel")) {
				panelTag.setDynamicAttribute(null, key.substring("panel".length()), getDynamicAttributesMap().get(key));
				getDynamicAttributesMap().remove(key);
				dynamicAttributesMapPanelGrid.remove(key);
			}
		}
		for (String key : keySet) {
			if (key.startsWith("panelgrid")) {
				getDynamicAttributesMap().remove(key);
			}
		}

		panelGridTag.setDynamicAttributesMap(dynamicAttributesMapPanelGrid);
		panelGridTag.setFlatMode(getFlatMode());
		panelGridTag.setColumns(getColumns());
		panelGridTag.setStyle(getStyle());
		panelGridTag.setStyleClass(getStyleClass());
		panelGridTag.setRowStyleClasses(getRowStyleClasses());
		panelGridTag.setRowStyles(getRowStyles());
		panelGridTag.setColumnStyleClasses(getColumnStyleClasses());
		panelGridTag.setColumnStyles(getColumnStyles());
		panelGridTag.setPropertyRenderAs(getPropertyRenderAs());
		panelGridTag.setUseParentPanelGridProperties(false);
		getDynamicAttributesMap().remove("id");//nao duplicar o id

		TagHolder panelHolder = new TagHolder(panelTag);
		TagHolder panelGridHolder = new TagHolder(panelGridTag);

		if (showBorder) {

			TextTag text = null;
			if (legend != null) {
				for (String string : keySet) {
					if (string.startsWith("legend")) {
						Object value = getDynamicAttributesMap().remove(string);
						getDynamicAttributesMap().put(string.substring("legend".length()), value);
					}
				}
				text = new TextTag("<fieldset " + getDynamicAttributesToString() + "><legend>" + legend + "</legend>", "</fieldset>");
			} else {
				text = new TextTag("<fieldset>", "</fieldset>");
			}

			TagHolder fieldsetHolder = new TagHolder(text);
			fieldsetHolder.addChild(panelGridHolder);
			panelHolder.addChild(fieldsetHolder);

		} else {

			panelHolder.addChild(panelGridHolder);

		}

		panelGridTag.setJspBody(getJspBody());

		invoke(panelHolder);

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

	public Boolean getShowBorder() {
		return showBorder;
	}

	public void setShowBorder(Boolean showBorder) {
		this.showBorder = showBorder;
	}

	public Boolean getUseParentPanelGridProperties() {
		return useParentPanelGridProperties;
	}

	public void setUseParentPanelGridProperties(Boolean useParentPanelGridProperties) {
		this.useParentPanelGridProperties = useParentPanelGridProperties;
	}

}

class TextTag extends BaseTag {

	protected String parte1;
	protected String parte2;

	public TextTag(String parte1, String parte2) {
		this.parte1 = parte1;
		this.parte2 = parte2;
	}

	@Override
	protected void doComponent() throws Exception {
		getOut().println(parte1);
		doBody();
		getOut().println(parte2);
	}

}
