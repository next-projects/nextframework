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
package org.nextframework.util;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NextFormater {

	protected SimpleDateFormat simpleDateFormatTime = new SimpleDateFormat("HH:mm");
	protected SimpleDateFormat simpleDateFormatTimestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	protected SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("dd/MM/yyyy");

	private static NextFormater instance;

	public static NextFormater getInstance() {
		if (instance == null) {
			instance = new NextFormater();
		}
		return instance;
	}

	public String format(Object o) {
		String nullValue = "";
		return format(o, nullValue);
	}

	private String format(Object o, String nullValue) {
		Class<?> horaClass = null;
		try {
			horaClass = Class.forName("org.nextframework.types.SimpleTime");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (o == null) {
			return nullValue;
		} else if (o instanceof String) {
			return (String) o;
		} else if (horaClass != null && horaClass.isAssignableFrom(o.getClass())) {
			return o.toString();
		} else if (o instanceof Time) {
			return simpleDateFormatTime.format((Time) o);
		} else if (o instanceof Timestamp) {
			return simpleDateFormatTimestamp.format((Timestamp) o);
		} else if (o instanceof Date) {
			return simpleDateFormatDate.format((Date) o);
		} else {
			return o.toString();
		}
	}

}
