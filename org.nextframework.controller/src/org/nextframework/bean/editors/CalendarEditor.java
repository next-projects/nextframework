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
package org.nextframework.bean.editors;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;

public class CalendarEditor extends CustomDateEditor {

	DateFormat dateFormat;

	public CalendarEditor(DateFormat dateFormat, boolean allowEmpty, int exactDateLength) {
		super(dateFormat, allowEmpty, exactDateLength);
		this.dateFormat = dateFormat;
	}

	public CalendarEditor(DateFormat dateFormat, boolean allowEmpty) {
		super(dateFormat, allowEmpty);
		this.dateFormat = dateFormat;
	}

	@Override
	public Object getValue() {
		Object object = super.getValue();
		if (object instanceof Date) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime((Date) object);
			return calendar;
		}
		return object;
	}

	@Override
	public String getAsText() {
		Calendar value = (Calendar) getValue();
		return (value != null ? this.dateFormat.format(value.getTime()) : "");
	}

}
