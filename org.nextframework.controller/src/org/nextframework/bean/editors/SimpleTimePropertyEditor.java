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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.nextframework.types.SimpleTime;

public class SimpleTimePropertyEditor extends PropertyEditorSupport {

	private String pattern = "HH:mm";

	private DateFormat dateFormat = new SimpleDateFormat(pattern);

	private boolean lenient = false;

	public SimpleTimePropertyEditor() {

	}

	public SimpleTimePropertyEditor(String pattern) {
		this.pattern = pattern;
		dateFormat = new SimpleDateFormat(this.pattern);
	}

	public SimpleTimePropertyEditor(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}

	public boolean isLenient() {
		return lenient;
	}

	public void setLenient(boolean lenient) {
		this.lenient = lenient;
	}

	@Override
	public Object getValue() {
		Object object = super.getValue();
		if (object instanceof Date) {
			SimpleTime hora = new SimpleTime(((Date) object).getTime());
			return hora;
		}
		return object;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (text != null && !text.trim().equals("")) {
			try {
				dateFormat.setLenient(lenient);
				SimpleTime hora = new SimpleTime(dateFormat.parse(text).getTime());
				setValue(hora);
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
		return dateFormat.format((SimpleTime) getValue());
	}

}
