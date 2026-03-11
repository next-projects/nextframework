package org.nextframework.types.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;
import org.nextframework.types.Money;

public class MoneyUserType implements UserType<Money> {

	@Override
	public int getSqlType() {
		return Types.BIGINT;
	}

	@Override
	public Class<Money> returnedClass() {
		return Money.class;
	}

	@Override
	public boolean equals(Money x, Money y) {
		if (x != null) {
			return x.compareTo(y) == 0;
		} else if (x == null && y == null) {
			return true;
		}
		return false;
	}

	@Override
	public int hashCode(Money x) {
		return x != null ? x.hashCode() : 0;
	}

	@Override
	public Money nullSafeGet(ResultSet rs, int position, SharedSessionContractImplementor session, Object owner) throws SQLException {
		Object obj = rs.getObject(position);
		if (obj == null) {
			return null;
		}
		if (!(obj instanceof Number)) {
			String msg = "O campo de uma propriedade do tipo Money não está com o tipo long no banco.";
			throw new RuntimeException(msg);
		}
		Long value = ((Number) obj).longValue();
		return new Money(value, true);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Money value, int index, SharedSessionContractImplementor session) throws SQLException {
		if (value != null) {
			if (value.isNull()) {
				st.setNull(index, Types.BIGINT);
			} else {
				st.setLong(index, value.toLong());
			}
		} else {
			st.setNull(index, Types.BIGINT);
		}
	}

	@Override
	public Money deepCopy(Money value) {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Money value) {
		return value;
	}

	@Override
	public Money assemble(Serializable cached, Object owner) {
		return (Money) cached;
	}

	@Override
	public Money replace(Money original, Money target, Object owner) {
		return original;
	}

}
