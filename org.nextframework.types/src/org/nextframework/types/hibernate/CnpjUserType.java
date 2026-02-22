package org.nextframework.types.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;
import org.nextframework.types.Cnpj;
import org.nextframework.types.TypeUtils;

public class CnpjUserType implements UserType<Cnpj> {

	@Override
	public int getSqlType() {
		return Types.VARCHAR;
	}

	@Override
	public Class<Cnpj> returnedClass() {
		return Cnpj.class;
	}

	@Override
	public boolean equals(Cnpj x, Cnpj y) {
		if ((x == null || x.getValue() == null) && (y == null || y.getValue() == null)) {
			return true;
		} else if (x == null || y == null) {
			return false;
		}
		return x.equals(y);
	}

	@Override
	public int hashCode(Cnpj x) {
		return x.hashCode();
	}

	@Override
	public Cnpj nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
		String value = rs.getString(position);
		if (TypeUtils.isEmpty(value)) {
			return null;
		}
		return new Cnpj(value, Cnpj.AUTO_VALIDATION);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Cnpj value, int index, WrapperOptions options) throws SQLException {
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
	public Cnpj deepCopy(Cnpj value) {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Cnpj value) {
		return value != null ? value.getValue() : null;
	}

	@Override
	public Cnpj assemble(Serializable cached, Object owner) {
		return cached != null ? new Cnpj((String) cached, Cnpj.AUTO_VALIDATION) : null;
	}

	@Override
	public Cnpj replace(Cnpj original, Cnpj target, Object owner) {
		return original;
	}

	private String removeSymbols(String value2) {
		return value2.replace(".", "").replace("-", "").replace("/", "");
	}

}
