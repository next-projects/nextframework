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

import org.nextframework.types.hibernate.CepUserType;

public class Cep extends CepUserType implements Serializable {

	private static final long serialVersionUID = 1L;
	private String value;

	/**
	 * 
	 * @deprecated Utilize o método que recebe uma String, esse método só existe por causa do hibernate
	 */
	@Deprecated
	public Cep(){
	}
	
	public Cep(String cep){
		if(cep == null) throw new NullPointerException();
		checkPattern(cep);
		cep = removeSymbols(cep);
		value = cep.trim().equals("")?null:cep;
	}
	
	private void checkPattern(String value) throws IllegalArgumentException {
		if(!value.trim().equals("") && !value.matches("\\d{5}[-\\.]?\\d{3}")){
			throw new IllegalArgumentException("Não foi possível converter \"" + value + "\" para um CEP válido");
		}
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	protected Cep clone() throws CloneNotSupportedException {
		return new Cep(value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Cep)){
			return false;
		}
		if(this.value == null && ((Cep)obj).value == null){
			return true;
		} else if(this.value == null || ((Cep)obj).value == null){
			return false;
		}
		return this.value.equals(((Cep)obj).value);
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

				builder.insert(5, '-');
				
				return builder.toString();
			} catch (IndexOutOfBoundsException e) {
				//System.out.println("\n************************\nCEP inválido: "+value);
				return value;
			}
		}
	}

	private String removeSymbols(String value2) {
		return value2.replace(".", "").replace("-", "");
	}


}
