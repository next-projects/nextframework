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

import java.beans.PropertyEditorSupport;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimePropertyEditor extends PropertyEditorSupport {

	private String pattern = "HH:mm";

	private DateFormat dateFormat = new SimpleDateFormat(pattern);

	private boolean lenient = false;

	public TimePropertyEditor() {

	}

	public TimePropertyEditor(String pattern) {
		this.pattern = pattern;
		dateFormat = new SimpleDateFormat(this.pattern);
	}

	public TimePropertyEditor(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public boolean isLenient() {
		return lenient;
	}

	public void setLenient(boolean lenient) {
		this.lenient = lenient;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (text != null && !text.trim().equals("")) {
			try {
				dateFormat.setLenient(lenient);
				Time time = new Time(dateFormat.parse(text).getTime());
				setValue(time);
			} catch (ParseException e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			setValue(null);
		}
	}

	@Override
	public String getAsText() {
		if (getValue() == null)
			return "";
		return dateFormat.format((Time) getValue());
	}

	public Object getValue() {
		Object object = super.getValue();
		if (object instanceof Date) {
			Time timestamp = new Time(((Date) object).getTime());
			return timestamp;
		}
		return object;
	}

}
