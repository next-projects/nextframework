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
package org.nextframework.view.combo;

import org.nextframework.view.BeanTag;
import org.nextframework.view.LogicalTag;
import org.nextframework.view.PanelGridTag;

/**
 * @author rogelgarcia
 * @since 01/02/2006
 * @version 1.1
 */
public class BeanPanelGridTag extends ComboTag implements LogicalTag {

	protected Integer columns = 1;
	protected String name;
	protected Class<?> valueType;

	public Class<?> getValueType() {
		return valueType;
	}

	public void setValueType(Class<?> itemType) {
		this.valueType = itemType;
	}

	@Override
	protected void doComponent() throws Exception {

		PanelGridTag panelGridTag = new PanelGridTag();
		BeanTag beanTag = new BeanTag();

		panelGridTag.setColumns(columns);

		beanTag.setName(name);
		beanTag.setValueType(valueType);
		beanTag.setJspBody(getJspBody());

		TagHolder panelGridTagHolder = new TagHolder(panelGridTag);
		panelGridTagHolder.addChild(new TagHolder(beanTag));

		invoke(panelGridTagHolder);

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getColumns() {
		return columns;
	}

	public void setColumns(Integer columns) {
		this.columns = columns;
	}

}
