package org.nextframework.types.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.nextframework.types.InscricaoEstadual;
import org.nextframework.types.TypeUtils;

public class InscricaoEstadualUserType implements UserType<InscricaoEstadual> {

	@Override
	public int getSqlType() {
		return Types.VARCHAR;
	}

	@Override
	public Class<InscricaoEstadual> returnedClass() {
		return InscricaoEstadual.class;
	}

	@Override
	public boolean equals(InscricaoEstadual x, InscricaoEstadual y) {
		if (x == null) {
			return y == null;
		}
		return x.equals(y);
	}

	@Override
	public int hashCode(InscricaoEstadual x) {
		return x != null ? x.hashCode() : 0;
	}

	@Override
	public InscricaoEstadual nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
		String value = rs.getString(position);
		if (value == null) {
			return new InscricaoEstadual();
		}
		return new InscricaoEstadual(value);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, InscricaoEstadual value, int index, SharedSessionContractImplementor session) throws SQLException {
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

	private String removeSymbols(String value2) {
		return value2.replace(".", "").replace("-", "");
	}

	@Override
	public InscricaoEstadual deepCopy(InscricaoEstadual value) {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(InscricaoEstadual value) {
		return value;
	}

	@Override
	public InscricaoEstadual assemble(Serializable cached, Object owner) {
		return (InscricaoEstadual) cached;
	}

	@Override
	public InscricaoEstadual replace(InscricaoEstadual original, InscricaoEstadual target, Object owner) {
		return original;
	}

}
