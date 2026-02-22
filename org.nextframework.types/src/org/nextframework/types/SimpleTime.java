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
package org.nextframework.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;

public class SimpleTime extends java.sql.Time implements UserType<SimpleTime> {

	private static final long serialVersionUID = -654280595599726647L;

	public SimpleTime() {
		super(0);
	}

	public SimpleTime(long time) {
		super(time);
	}

	public SimpleTime(String time) {
		super(0);
		if (time == null)
			throw new NullPointerException();
		checkPattern(time);
		if (time.length() == 3) {
			time = "0" + time;
		}
		setTime(SimpleTime.valueOf(time + ":00").getTime());
	}

	@Override
	public String toString() {
		Calendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(getTime());
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		String hourString;
		String minuteString;
		if (hour < 10) {
			hourString = "0" + hour;
		} else {
			hourString = Integer.toString(hour);
		}
		if (minute < 10) {
			minuteString = "0" + minute;
		} else {
			minuteString = Integer.toString(minute);
		}
		return (hourString + ":" + minuteString);
	}

	private void checkPattern(String value) throws IllegalArgumentException {
		if (!value.trim().equals("") && !value.matches("\\d{1,2}:\\d{2}")) {
			throw new IllegalArgumentException("Não foi possível converter \"" + value + "\" para uma hora válida");
		}
	}

	@Override
	public SimpleTime assemble(Serializable cached, Object owner) {
		return (SimpleTime) cached;
	}

	@Override
	public SimpleTime deepCopy(SimpleTime value) {
		return value;
	}

	@Override
	public Serializable disassemble(SimpleTime value) {
		return value;
	}

	@Override
	public boolean equals(SimpleTime x, SimpleTime y) {
		if (x != null) {
			return x.equals(y);
		}
		return (x == null && y == null);
	}

	@Override
	public int hashCode(SimpleTime x) {
		return x != null ? x.hashCode() : 0;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public SimpleTime replace(SimpleTime original, SimpleTime target, Object owner) {
		return original;
	}

	@Override
	public Class<SimpleTime> returnedClass() {
		return SimpleTime.class;
	}

	@Override
	public int getSqlType() {
		return Types.TIMESTAMP;
	}

	@Override
	public SimpleTime nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
		try {
			Timestamp timestamp = rs.getTimestamp(position);
			if (timestamp == null) {
				return null;
			} else {
				return new SimpleTime(timestamp.getTime());
			}
		} catch (Exception e) {
			String msg = "Uma propriedade do tipo Hora não tem seu campo no banco com o tipo time, timestamp ou date.";
			throw new RuntimeException(msg, e);
		}
	}

	@Override
	public void nullSafeSet(PreparedStatement st, SimpleTime value, int index, WrapperOptions options) throws SQLException {
		if (value != null) {
			st.setTimestamp(index, new Timestamp(value.getTime()));
		} else {
			st.setNull(index, Types.TIMESTAMP);
		}
	}

}
