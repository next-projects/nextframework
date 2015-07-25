/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2009 the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * You may obtain a copy of the license at
 * 
 *     http://www.gnu.org/copyleft/lesser.html
 * 
 */
package org.nextframework.types;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

public class InscricaoEstadual implements UserType, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String value;

	/**
	 * 
	 * @deprecated Utilize o método que recebe uma String, esse método só existe por causa do hibernate
	 */
	@Deprecated
	public InscricaoEstadual(){
	}
	
	public InscricaoEstadual(String inscricaoEstadual){
		if(inscricaoEstadual == null) throw new NullPointerException();
		checkPattern(inscricaoEstadual);
		inscricaoEstadual = removeSymbols(inscricaoEstadual);
		value = inscricaoEstadual.trim().equals("")?null:inscricaoEstadual;
	}
	
	private void checkPattern(String value) throws IllegalArgumentException {
		if(!value.trim().equals("") && !value.matches("\\d{13}-?\\d{1}")){
			throw new IllegalArgumentException("Não foi possível converter \"" + value + "\" para uma inscrição estadual válida");
		}
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	protected InscricaoEstadual clone() throws CloneNotSupportedException {
		return new InscricaoEstadual(value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof InscricaoEstadual)){
			return false;
		}
		if(this.value == null && ((InscricaoEstadual)obj).value == null){
			return true;
		} else if(this.value == null || ((InscricaoEstadual)obj).value == null){
			return false;
		}
		return this.value.equals(((InscricaoEstadual)obj).value);
	}

	@Override
	public int hashCode() {
		if(value == null) return super.hashCode();
		return value.hashCode();
	}

	@Override
	public String toString() {
		if(TypeUtils.isEmpty(value)){
			return "";
		} else {
			try {
				StringBuilder builder = new StringBuilder(value);

				builder.insert(13, '-');
				
				return builder.toString();
			} catch (IndexOutOfBoundsException e) {
				//System.out.println("\n************************\nInscrição Estadual inválida: "+value);
				return value;
			}
		}
	}

	public int[] sqlTypes() {
		return new int[]{Types.VARCHAR};
	}

	public Class<InscricaoEstadual> returnedClass() {
		return InscricaoEstadual.class;
	}

	public boolean equals(Object x, Object y) throws HibernateException {
		if(x == null){
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
			return new InscricaoEstadual();
		}
		return new InscricaoEstadual(value);
	}

	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
		if(value instanceof InscricaoEstadual){
			String value2 = ((InscricaoEstadual)value).getValue();
			if(TypeUtils.isEmpty(value2)){
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

	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	public boolean isMutable() {
		return false;
	}

	public Serializable disassemble(Object value) throws HibernateException {
		return (InscricaoEstadual)value;
	}

	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

}
