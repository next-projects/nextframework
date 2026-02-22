package org.nextframework.types.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;
import org.nextframework.types.Cpf;
import org.nextframework.types.TypeUtils;

public class CpfUserType implements UserType<Cpf> {

	@Override
	public int getSqlType() {
		return Types.VARCHAR;
	}

	@Override
	public Class<Cpf> returnedClass() {
		return Cpf.class;
	}

	@Override
	public boolean equals(Cpf x, Cpf y) {
		if ((x == null || x.getValue() == null) && (y == null || y.getValue() == null)) {
			return true;
		} else if (x == null || y == null) {
			return false;
		}
		return x.equals(y);
	}

	@Override
	public int hashCode(Cpf x) {
		return x.hashCode();
	}

	@Override
	public Cpf nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
		String value = rs.getString(position);
		if (TypeUtils.isEmpty(value)) {
			return null;
		}
		return new Cpf(value, Cpf.AUTO_VALIDATION);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Cpf value, int index, WrapperOptions options) throws SQLException {
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
	public Cpf deepCopy(Cpf value) {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Cpf value) {
		return value != null ? value.getValue() : null;
	}

	@Override
	public Cpf assemble(Serializable cached, Object owner) {
		return cached != null ? new Cpf((String) cached, Cpf.AUTO_VALIDATION) : null;
	}

	@Override
	public Cpf replace(Cpf original, Cpf target, Object owner) {
		return original;
	}

	private static String removeSymbols(String value2) {
		return value2.replace(".", "").replace("-", "");
	}

}
