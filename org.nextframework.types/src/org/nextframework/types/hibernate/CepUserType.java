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

	@Override
	public int[] sqlTypes() {
		return new int[]{Types.VARCHAR};
	}

	@Override
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

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {
		String value = rs.getString(names[0]);
		if(TypeUtils.isEmpty(value)){
			//return new Cep(); //Pq retornava vazio?
			return null;
		}
		return new Cep(value);
	}

	@Override
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
	
	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return ((Cep)value).getValue();
	}

	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return new Cep((String)cached);
	}

	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}
	

	private String removeSymbols(String value2) {
		return value2.replace(".", "").replace("-", "");
	}
}
