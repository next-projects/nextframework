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

/**
 * @author rogelgarcia
 * @since 07/02/2006
 * @version 1.1
 */
public class GroupTag extends ComboTag {

	protected Integer colspan;

	protected String legend;

	protected Boolean showBorder = true;

	protected Boolean useParentPanelGridProperties = true;

	// panelgridproperties

	protected int columns = 1;

	protected String style;

	protected String styleClass;

	protected String rowStyleClasses;

	protected String rowStyles;

	protected String columnStyleClasses;

	protected String columnStyles;

	@Override
	protected void doComponent() throws Exception {

		if (Util.booleans.isTrue(useParentPanelGridProperties)) {
			PanelGridTag parentPanel = findParent(PanelGridTag.class);
			if (parentPanel != null) {
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

		PanelTag panelTag = new PanelTag();
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
		getDynamicAttributesMap().remove("id");//nao duplicar o id
		panelGridTag.setColumns(getColumns());
		panelGridTag.setStyle(getStyle());
		panelGridTag.setStyleClass(getStyleClass());
		panelGridTag.setRowStyleClasses(getRowStyleClasses());
		panelGridTag.setRowStyles(getRowStyles());
		panelGridTag.setColumnStyleClasses(getColumnStyleClasses());
		panelGridTag.setColumnStyles(getColumnStyles());
		panelGridTag.setUseParentPanelGridProperties(false);

		TextTag text = null;

		if (colspan != null) {
			panelTag.setDynamicAttribute(null, "colspan", colspan);
		}

		TagHolder panelHolder = new TagHolder(panelTag);
		TagHolder panelGridHolder = new TagHolder(panelGridTag);
		TagHolder textHolder = null;
		if (showBorder) {
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
			textHolder = new TagHolder(text);
		}

		if (textHolder != null) {
			panelHolder.addChild(textHolder);
			textHolder.addChild(panelGridHolder);
		} else {
			panelHolder.addChild(panelGridHolder);
		}

		panelGridTag.setJspBody(getJspBody());

		invoke(panelHolder);

	}

	public Integer getColspan() {
		return colspan;
	}

	public int getColumns() {
		return columns;
	}

	public String getColumnStyleClasses() {
		return columnStyleClasses;
	}

	public String getColumnStyles() {
		return columnStyles;
	}

	public String getLegend() {
		return legend;
	}

	public String getRowStyleClasses() {
		return rowStyleClasses;
	}

	public String getRowStyles() {
		return rowStyles;
	}

	public Boolean getShowBorder() {
		return showBorder;
	}

	public String getStyle() {
		return style;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setColspan(Integer colspan) {
		this.colspan = colspan;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	public void setColumnStyleClasses(String columnStyleClasses) {
		this.columnStyleClasses = columnStyleClasses;
	}

	public void setColumnStyles(String columnStyles) {
		this.columnStyles = columnStyles;
	}

	public void setLegend(String legend) {
		this.legend = legend;
	}

	public void setRowStyleClasses(String rowStyleClasses) {
		this.rowStyleClasses = rowStyleClasses;
	}

	public void setRowStyles(String rowStyles) {
		this.rowStyles = rowStyles;
	}

	public void setShowBorder(Boolean showBorder) {
		this.showBorder = showBorder;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
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
