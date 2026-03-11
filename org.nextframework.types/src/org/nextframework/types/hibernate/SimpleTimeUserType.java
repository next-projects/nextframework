package org.nextframework.types.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.nextframework.types.SimpleTime;

public class SimpleTimeUserType implements UserType<SimpleTime> {

	@Override
	public int getSqlType() {
		return Types.TIMESTAMP;
	}

	@Override
	public Class<SimpleTime> returnedClass() {
		return SimpleTime.class;
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
	public SimpleTime nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
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
	public void nullSafeSet(PreparedStatement st, SimpleTime value, int index, SharedSessionContractImplementor session) throws SQLException {
		if (value != null) {
			st.setTimestamp(index, new Timestamp(value.getTime()));
		} else {
			st.setNull(index, Types.TIMESTAMP);
		}
	}

	@Override
	public SimpleTime deepCopy(SimpleTime value) {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(SimpleTime value) {
		return value;
	}

	@Override
	public SimpleTime assemble(Serializable cached, Object owner) {
		return (SimpleTime) cached;
	}

	@Override
	public SimpleTime replace(SimpleTime original, SimpleTime target, Object owner) {
		return original;
	}

}
