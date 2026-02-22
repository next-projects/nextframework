package org.nextframework.types.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;
import org.nextframework.types.Phone;
import org.nextframework.types.TypeUtils;

public class PhoneUserType implements UserType<Phone> {

	@Override
	public int getSqlType() {
		return Types.VARCHAR;
	}

	@Override
	public Class<Phone> returnedClass() {
		return Phone.class;
	}

	@Override
	public boolean equals(Phone x, Phone y) {
		if ((x == null || x.getValue() == null) && (y == null || y.getValue() == null)) {
			return true;
		} else if (x == null || y == null) {
			return false;
		}
		return x.equals(y);
	}

	@Override
	public int hashCode(Phone x) {
		return x.hashCode();
	}

	@Override
	public Phone nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
		String value = rs.getString(position);
		if (TypeUtils.isEmpty(value)) {
			return null;
		}
		return new Phone(value);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Phone value, int index, WrapperOptions options) throws SQLException {
		if (value != null) {
			String value2 = value.getValue();
			if (TypeUtils.isEmpty(value2)) {
				st.setNull(index, Types.VARCHAR);
			} else {
				st.setString(index, removeSymbols(value2));
			}
		} else {
			st.setNull(index, Types.VARCHAR);
		}
	}

	@Override
	public Phone deepCopy(Phone value) {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Phone value) {
		return value != null ? value.getValue() : null;
	}

	@Override
	public Phone assemble(Serializable cached, Object owner) {
		return cached != null ? new Phone((String) cached) : null;
	}

	@Override
	public Phone replace(Phone original, Phone target, Object owner) {
		return original;
	}

	private String removeSymbols(String value2) {
		return value2.replaceAll("\\(|\\)| ", "").replace("-", "");
	}

}
