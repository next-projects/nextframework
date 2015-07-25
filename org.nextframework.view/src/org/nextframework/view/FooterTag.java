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

import javax.servlet.jsp.JspException;

import org.nextframework.view.DataGridTag.Status;

/**
 * @author rogelgarcia
 * @since 27/01/2006
 * @version 1.1
 */
public class FooterTag extends ColumnChildTag {
	
	@Override
	protected boolean doTd() {
		return false;
	}

	@Override
	protected void doColumnChild(Status status) throws JspException, IOException {
		String bodyContent = getBody();
		String fullBody = "<td" + getDynamicAttributesToString() + ">" + bodyContent + "</td>";
		columnTag.setFooter(fullBody);
	}

	@Override
	protected boolean acceptStatus(Status status) {
		return status == Status.FOOTER;
	}

	@Override
	protected void register() {
		dataGrid.setRenderFooter(true);
	}


}
