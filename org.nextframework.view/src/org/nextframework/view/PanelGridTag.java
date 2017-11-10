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

	protected Integer columns = null;

	protected String style;

	protected String styleClass;

	protected String rowStyleClasses;

	protected String rowStyles;
	
	protected String columnStyleClasses;

	protected String columnStyles;
	
	protected Integer colspan;
	
	protected Boolean propertyRenderAsDouble;
	
	protected Boolean useParentPanelGridProperties = true;

	Iterator<String> rowStyleIterator;

	Iterator<String> rowStyleClassIterator;
	
	Iterator<String> columnStyleIterator;

	Iterator<String> columnStyleClassIterator;
	


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
		if(columns == null || columns == 0/*forçado*/){
			columns = 1;
		}
		if (columns <= 0) {
			throw new IllegalArgumentException("O atributo columns da tag panelGrid deve ser positivo");
		}
		if(propertyRenderAsDouble == null){
			BaseTag findFirst = findFirst(PropertyConfigTag.class, PanelGridTag.class);
			if(findFirst instanceof PropertyConfigTag){
				this.propertyRenderAsDouble = PropertyTag.DOUBLE.equals(((PropertyConfigTag)findFirst).getRenderAs());	
			} else if(findFirst instanceof PanelGridTag){
				Boolean propertyRenderAsDouble = ((PanelGridTag)findFirst).getPropertyRenderAsDouble();
				this.propertyRenderAsDouble = propertyRenderAsDouble;
			}
			
		}
		doBody();
		{
			String styleString = style != null ? " style=\"" + style + "\"" : "";
			String classString = styleClass != null ? " class=\"" + styleClass + "\"" : "";

			getOut().println("<table" + styleString + classString + getDynamicAttributesToString() + ">");
		}
		rowStyleIterator = getRowStyleIterator();
		rowStyleClassIterator = getRowStyleClassIterator();
		
		columnStyleIterator = getColumnStyleIterator();
		columnStyleClassIterator = getColumnStyleClassIterator();

		int remainingColumns = columns;
		int rowCount = 0;
		for (PanelRenderedBlock block : blocks) {
			
			Integer colspanColumn = asInteger(block.getProperties().get("colspan"));
			
			if (remainingColumns <= 0) {
				remainingColumns = columns;
				getOut().println("</tr>");
			}
			if (remainingColumns == columns) {
				{
					String style = rowStyleIterator.next();
					String styleClass = rowStyleClassIterator.next();
					String styleString = style != null ? " style=\"" + style + "\"" : "";
					String classString = styleClass != null ? " class=\"" + styleClass + "\"" : "";
					getOut().print("<tr" + styleString + classString + ">");
					rowCount++;
				}
			}
			
			{
				
				String style = columnStyleIterator.next();
				String styleClass = columnStyleClassIterator.next();
				
				String styleClasses = (styleClass != null ? styleClass : "") + " ";
				
				if (block.getProperties().containsKey("class")) { 
					Object remove = block.getProperties().remove("class");
					styleClasses += remove;
				}

				//BUG 0003
				style = (style != null? style : "")+"; ";
				if(block.getProperties().containsKey("style")){
					style += block.getProperties().remove("style");
				}
								
				String styleString = " style=\"" + style + "\"";
				String classString = " class=\"" + styleClasses + "\"";//BUG 0006
				getOut().print("<td" + styleString + classString + getDynamicAttributesToString(block.getProperties()) + ">");
				getOut().print(block.body);
				getOut().println("</td>");
				
				if (colspanColumn.intValue() > 1) {
					for (int i = 0; i < (colspanColumn -1); i++) {
						columnStyleClassIterator.next();
					}
				}
				
			}

			remainingColumns -= Math.max(colspanColumn, 1);
		}
		while (remainingColumns-- > 0 && rowCount > 1) {
			getOut().print("<td>");
			getOut().print(
					"<!-- BLOCO VAZIO " + "(Não existe panels suficientes dentro do panel grid para satisfazer todas as colunas, " + "então esse panel foi criado para não quebrar a tabela) -->");
			getOut().println("</td>");
		}
		getOut().println("</tr>");
		getOut().println("</table>");
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

	private Iterator<String> getRowStyleClassIterator() {
		if (Util.strings.isEmpty(rowStyleClasses))
			return new CyclicIterator(null);
		return new CyclicIterator(rowStyleClasses.split(","));
	}

	private Iterator<String> getRowStyleIterator() {
		if (Util.strings.isEmpty(rowStyles))
			return new CyclicIterator(null);
		return new CyclicIterator(rowStyles.split(","));
	}
	
	private Iterator<String> getColumnStyleClassIterator() {
		if (Util.strings.isEmpty(columnStyleClasses))
			return new CyclicIterator(null);
		return new CyclicIterator(columnStyleClasses.split(","));
	}

	private Iterator<String> getColumnStyleIterator() {
		if (Util.strings.isEmpty(columnStyles))
			return new CyclicIterator(null);
		return new CyclicIterator(columnStyles.split(","));
	}

	public String getRowStyleClasses() {
		return rowStyleClasses;
	}

	public String getRowStyles() {
		return rowStyles;
	}

	public String getStyle() {
		return style;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setRowStyleClasses(String rowStyleClasses) {
		this.rowStyleClasses = rowStyleClasses;
	}

	public void setRowStyles(String rowStyles) {
		this.rowStyles = rowStyles;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public boolean addBlock(PanelRenderedBlock o) {
		return blocks.add(o);
	}

	public Integer getColumns() {
		return columns;
	}

	public void setColumns(Integer columns) {
		this.columns = columns;
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
				i = 0;
			}
			return strings[i++];
		}

		@SuppressWarnings("unused")
		public String[] getStrings() {
			return strings;
		}

		public void remove() {
		}

	}

	public String getColumnStyleClasses() {
		return columnStyleClasses;
	}

	public String getColumnStyles() {
		return columnStyles;
	}

	public void setColumnStyleClasses(String columnStyleClasses) {
		this.columnStyleClasses = columnStyleClasses;
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

	@Override
	protected void addPanelProperties(Map<String, Object> properties) {
		if(colspan != null){
			properties.put("colspan", colspan);
		}
	}

	public Boolean getPropertyRenderAsDouble() {
		return propertyRenderAsDouble;
	}

	public void setPropertyRenderAsDouble(Boolean propertyRenderAsDouble) {
		this.propertyRenderAsDouble = propertyRenderAsDouble;
	}

	public Boolean getUseParentPanelGridProperties() {
		return useParentPanelGridProperties;
	}

	public void setUseParentPanelGridProperties(Boolean useParentPanelGridProperties) {
		this.useParentPanelGridProperties = useParentPanelGridProperties;
	}

}
