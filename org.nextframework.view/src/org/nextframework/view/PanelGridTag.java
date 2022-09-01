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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.nextframework.util.Util;
import org.nextframework.view.template.PropertyConfigTag;
import org.nextframework.view.template.PropertyTag;

/**
 * @author rogelgarcia
 * @since 30/01/2006
 * @version 1.1
 */
public class PanelGridTag extends BaseTag implements AcceptPanelRenderedBlock {

	protected List<PanelRenderedBlock> blocks = new ArrayList<PanelRenderedBlock>();

	protected Boolean flatMode = false;

	protected Integer columns = null;

	protected String styleClass;

	protected String style;

	protected String rowStyleClasses;

	protected String rowStyles;

	protected String columnStyleClasses;

	protected String columnStyles;

	protected Integer colspan;

	protected String propertyRenderAs;

	protected Boolean useParentPanelGridProperties = true;

	@SuppressWarnings("unchecked")
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

		if (flatMode == null) {
			PanelGridTag findFirst = findParent(PanelGridTag.class);
			if (findFirst != null) {
				flatMode = findFirst.getFlatMode();
			}
			if (flatMode == null) {
				flatMode = false;
			}
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

		doBody();

		String mainTag = flatMode ? "div" : "table";
		String rowTag = flatMode ? "div" : "tr";
		String blockTag = flatMode ? "div" : "td";

		{
			String classString = styleClass != null ? " class=\"" + styleClass + "\"" : "";
			String styleString = style != null ? " style=\"" + style + "\"" : "";
			getOut().println("<" + mainTag + classString + styleString + getDynamicAttributesToString() + ">");
		}

		CyclicIterator rowStyleClassIterator = Util.strings.isEmpty(rowStyleClasses) ? new CyclicIterator(null) : new CyclicIterator(rowStyleClasses.split(","));
		CyclicIterator rowStyleIterator = Util.strings.isEmpty(rowStyles) ? new CyclicIterator(null) : new CyclicIterator(rowStyles.split(","));
		CyclicIterator columnStyleClassIterator = Util.strings.isEmpty(columnStyleClasses) ? new CyclicIterator(null) : new CyclicIterator(columnStyleClasses.split(","));
		CyclicIterator columnStyleIterator = Util.strings.isEmpty(columnStyles) ? new CyclicIterator(null) : new CyclicIterator(columnStyles.split(","));

		int remainingColumns = columns;
		int rowCount = 0;
		for (PanelRenderedBlock block : blocks) {

			Integer colspanBlock = asInteger(block.getProperties().remove("colspan"));
			colspanBlock = colspanBlock != null ? colspanBlock : 1;

			if (remainingColumns - colspanBlock < 0) {

				remainingColumns = columns;

				getOut().println("</" + rowTag + ">");

			}

			if (remainingColumns == columns) {

				String styleClass = rowStyleClassIterator.next();
				String rowStyleClass = styleClass != null ? " class=\"" + styleClass + "\"" : "";
				String style = rowStyleIterator.next();
				String rowStyle = style != null ? " style=\"" + style + "\"" : "";

				getOut().print("<" + rowTag + rowStyleClass + rowStyle + ">");

				rowCount++;

				columnStyleClassIterator.reset();
				columnStyleIterator.reset();

			}

			{

				String colspan = "";
				if (!flatMode) {
					colspan = " colspan=\"" + colspanBlock + "\"";
				}

				String columnStyleClass = columnStyleClassIterator.next();
				if (block.getProperties().containsKey("class")) {
					String blockClass = (String) block.getProperties().remove("class");
					if (blockClass != null) {
						columnStyleClass = (Util.strings.isNotEmpty(columnStyleClass) ? columnStyleClass + " " : "") + blockClass;
					}
				}

				String classString = "";
				if (columnStyleClass != null) {
					columnStyleClass = columnStyleClass.replaceAll("\\{CS\\}", colspanBlock.toString());
					classString = " class=\"" + columnStyleClass + "\"";
				}

				String columnStyle = columnStyleIterator.next();
				if (block.getProperties().containsKey("style")) {
					String blockStyle = (String) block.getProperties().remove("style");
					if (blockStyle != null) {
						columnStyle = (Util.strings.isNotEmpty(columnStyle) ? columnStyle + "; " : "") + blockStyle;
					}
				}
				String styleString = columnStyle != null ? " style=\"" + columnStyle + "\"" : "";

				getOut().print("<" + blockTag + colspan + classString + styleString + getDynamicAttributesToString(block.getProperties()) + ">");
				getOut().print(block.body);
				getOut().println("</" + blockTag + ">");

				if (colspanBlock.intValue() > 1) {
					for (int i = 0; i < (colspanBlock - 1); i++) {
						columnStyleClassIterator.next();
					}
				}

			}

			remainingColumns -= colspanBlock;

		}

		if (!flatMode) {
			while (remainingColumns-- > 0 && rowCount > 1) {
				getOut().print("<td><!-- VAZIO --></td>");
			}
		}

		if (rowCount > 0) {
			getOut().println("</" + rowTag + ">");
		}
		getOut().println("</" + mainTag + ">");

	}

	public Integer asInteger(Object value) {
		try {
			if (value instanceof String) {
				return Integer.valueOf((String) value);
			}
			if (value instanceof Integer) {
				return (Integer) value;
			}
			return 1;
		} catch (NumberFormatException e) {
			return 1;
		} catch (NullPointerException e) {
			return 1;
		}
	}

	@Override
	protected void addPanelProperties(Map<String, Object> properties) {
		if (colspan != null) {
			properties.put("colspan", colspan);
		}
	}

	public boolean addBlock(PanelRenderedBlock o) {
		return blocks.add(o);
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

	public Boolean getUseParentPanelGridProperties() {
		return useParentPanelGridProperties;
	}

	public void setUseParentPanelGridProperties(Boolean useParentPanelGridProperties) {
		this.useParentPanelGridProperties = useParentPanelGridProperties;
	}

	private static class CyclicIterator implements Iterator<String> {

		private String[] strings;

		int i = 0;

		public CyclicIterator(String[] strings) {
			this.strings = strings;
		}

		public boolean hasNext() {
			return true;
		}

		public String next() {
			if (strings == null || strings.length == 0)
				return null;
			if (i >= strings.length) {
				reset();
			}
			return strings[i++];
		}

		public void reset() {
			i = 0;
		}

		@SuppressWarnings("unused")
		public String[] getStrings() {
			return strings;
		}

		public void remove() {

		}

	}

}
