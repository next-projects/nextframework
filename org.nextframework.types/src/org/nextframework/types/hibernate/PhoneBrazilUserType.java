package org.nextframework.types.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;
import org.nextframework.types.PhoneBrazil;
import org.nextframework.types.TypeUtils;

public class PhoneBrazilUserType implements UserType<PhoneBrazil> {

	@Override
	public int getSqlType() {
		return Types.VARCHAR;
	}

	@Override
	public Class<PhoneBrazil> returnedClass() {
		return PhoneBrazil.class;
	}

	@Override
	public boolean equals(PhoneBrazil x, PhoneBrazil y) {
		if ((x == null || x.getValue() == null) && (y == null || y.getValue() == null)) {
			return true;
		} else if (x == null || y == null) {
			return false;
		}
		return x.equals(y);
	}

	@Override
	public int hashCode(PhoneBrazil x) {
		return x.hashCode();
	}

	@Override
	public PhoneBrazil nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
		String value = rs.getString(position);
		if (TypeUtils.isEmpty(value)) {
			return null;
		}
		return new PhoneBrazil(value);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, PhoneBrazil value, int index, WrapperOptions options) throws SQLException {
		if (value != null) {
			String value2 = value.getValue();
			if (value2 == null) {
				st.setNull(index, Types.VARCHAR);
			} else {
				st.setString(index, removeSymbols(value2));
			}
		} else {
			st.setNull(index, Types.VARCHAR);
		}
	}

	@Override
	public PhoneBrazil deepCopy(PhoneBrazil value) {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(PhoneBrazil value) {
		return value;
	}

	@Override
	public PhoneBrazil assemble(Serializable cached, Object owner) {
		return (PhoneBrazil) cached;
	}

	@Override
	public PhoneBrazil replace(PhoneBrazil original, PhoneBrazil target, Object owner) {
		return original;
	}

	private String removeSymbols(String value2) {
		return value2.replaceAll("\\(|\\)| |\\-", "");
	}

}
