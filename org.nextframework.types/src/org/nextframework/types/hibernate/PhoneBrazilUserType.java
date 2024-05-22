package org.nextframework.types.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.nextframework.types.PhoneBrazil;
import org.nextframework.types.TypeUtils;

public class PhoneBrazilUserType implements UserType {

	public int[] sqlTypes() {
		return new int[] { Types.VARCHAR };
	}

	public Class<?> returnedClass() {
		return PhoneBrazil.class;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		if ((x == null || ((PhoneBrazil) x).getValue() == null) && (y == null || ((PhoneBrazil) y).getValue() == null)) {
			return true;
		} else if (x == null || y == null) {
			return false;
		}
		return x.equals(y);
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
		String value = rs.getString(names[0]);
		if (TypeUtils.isEmpty(value)) {
			return null;
		}
		return new PhoneBrazil(value);
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
		if (value instanceof PhoneBrazil) {
			String value2 = ((PhoneBrazil) value).getValue();
			if (value2 == null) {
				st.setNull(index, Types.VARCHAR);
			} else {
				st.setString(index, removeSymbols(value2));
			}
		} else {
			st.setNull(index, Types.VARCHAR);
		}
	}

	public Object deepCopy(Object value) throws HibernateException {
		if (value == null) {
			return new PhoneBrazil();
		}
		return value;
	}

	public boolean isMutable() {
		return false;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (PhoneBrazil) value;
	}

	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

	private String removeSymbols(String value2) {
		return value2.replaceAll("\\(|\\)| |\\-", "");
	}

}
