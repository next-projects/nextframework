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

import org.nextframework.util.Util;
import org.nextframework.view.BeanTag;
import org.nextframework.view.DataGridTag;
import org.nextframework.view.LogicalTag;

/**
 * @author rogelgarcia
 * @since 01/02/2006
 * @version 1.1
 */
public class BeanDataGridTag extends ComboTag implements LogicalTag {

	protected Object itens;
	protected Class<?> itemType;

	public Class<?> getItemType() {
		return itemType;
	}

	public void setItemType(Class<?> itemType) {
		this.itemType = itemType;
	}

	public Object getItens() {
		return itens;
	}

	public void setItens(Object itens) {
		this.itens = itens;
	}

	@Override
	protected void doComponent() throws Exception {

		DataGridTag dataGridTag = new DataGridTag();
		BeanTag beanTag = new BeanTag();

		dataGridTag.setVar(Util.strings.uncaptalize(itemType.getSimpleName()));
		dataGridTag.setItens(itens);

		beanTag.setName(Util.strings.uncaptalize(itemType.getSimpleName()));
		beanTag.setValueType(itemType);
		beanTag.setJspBody(getJspBody());

		TagHolder dataGridHolder = new TagHolder(dataGridTag);
		dataGridHolder.addChild(new TagHolder(beanTag));
		invoke(dataGridHolder);

	}

}
