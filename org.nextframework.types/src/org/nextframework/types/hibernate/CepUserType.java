package org.nextframework.types.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.nextframework.types.Cep;
import org.nextframework.types.TypeUtils;

public class CepUserType implements UserType {

	public int[] sqlTypes() {
		return new int[]{Types.VARCHAR};
	}

	public Class<Cep> returnedClass() {
		return Cep.class;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		if((x == null || ((Cep)x).getValue() == null) && (y == null || ((Cep)y).getValue() == null)){
			return true;
		} else if (x == null || y == null){
			return false;
		}
		return x.equals(y);
	}

	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
		String value = rs.getString(names[0]);
		if(value == null){
			return new Cep();
		}
		Cep cep = new Cep();
		cep.setValue(value);
		return cep;
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
		if(value instanceof Cep){
			String value2 = ((Cep)value).getValue();
			if(TypeUtils.isEmpty(value2)){
				st.setNull(index, Types.VARCHAR);
			} else {
				st.setString(index, removeSymbols(value2));	
			}
				
		} else {
			st.setNull(index, Types.VARCHAR);
		}
	}

	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	public boolean isMutable() {
		return false;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (Cep)value;
	}

	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}
	
	private String removeSymbols(String value2) {
		return value2.replace(".", "").replace("-", "");
	}

}
