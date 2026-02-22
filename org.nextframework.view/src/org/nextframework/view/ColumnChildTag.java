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

import java.io.IOException;

import jakarta.servlet.jsp.JspException;

/**
 * @author rogelgarcia
 * @since 27/01/2006
 * @version 1.1
 */
public abstract class ColumnChildTag extends BaseTag {

	protected DataGridTag dataGrid;
	protected ColumnTag columnTag;

	@Override
	protected void doComponent() throws Exception {
		if (dataGrid == null) {
			dataGrid = findParent2(DataGridTag.class, true);
		}
		if (columnTag == null) {
			columnTag = findParent2(ColumnTag.class, true);
		}
		DataGridTag.Status status = dataGrid.getCurrentStatus();
		if (status == DataGridTag.Status.REGISTER) {
			register();
		} else if (acceptStatus(status)) {
			if (doTd()) {
				getOut().print("<td" + getDynamicAttributesToString() + ">");
			}
			doColumnChild(status);
			if (doTd()) {
				getOut().print("</td>");
			}
		}
	}

	protected boolean doTd() {
		return true;
	}

	protected abstract void register();

	protected abstract void doColumnChild(DataGridTag.Status status) throws JspException, IOException;

	protected abstract boolean acceptStatus(DataGridTag.Status status);

}
