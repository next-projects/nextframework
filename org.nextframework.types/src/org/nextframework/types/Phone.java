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

/**
 * 
 * @deprecated Use PhoneBrazil class
 */
public class Phone implements Serializable {

	private static final long serialVersionUID = 1L;
	private String value;

	/**
	 * 
	 * @deprecated Utilize o método que recebe uma String, esse método só existe por causa do hibernate
	 */
	@Deprecated
	public Phone(){
	}
	
	public Phone(String telefone){
		if(telefone == null) throw new NullPointerException();
		checkPattern(telefone);
		telefone = removeSymbols(telefone);
		value = telefone;
	}
	
	private void checkPattern(String value) throws IllegalArgumentException {
		if(!value.trim().equals("") && !value.matches("(\\(\\d{2}\\)( )?)?\\d{4,5}-\\d{4}|\\d{8}|\\d{10}")){
			throw new IllegalArgumentException("Não foi possível converter \"" + value + "\" para um telefone válido");
		}
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	protected Phone clone() throws CloneNotSupportedException {
		return new Phone(value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Phone)){
			return false;
		}
		if(this.value == null && ((Phone)obj).value == null){
			return true;
		} else if(this.value == null || ((Phone)obj).value == null){
			return false;
		}
		return this.value.equals(((Phone)obj).value);
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
				int size = value.length();
				builder.insert(size==10?6:4, '-');
				if (size == 10) {
					builder.insert(2, ' ');
					builder.insert(2, ')');
					builder.insert(0, '(');
				}
				return builder.toString();
			} catch (IndexOutOfBoundsException e) {
				//System.out.println("\n************************\nTelefone inválido: "+value);
				return value;
			}
		}
	}

	private String removeSymbols(String value2) {
		return value2.replaceAll("\\(|\\)| ", "").replace("-", "");
	}

	public static void main(String[] args) {
		new Phone("(31) 9898-0909");
		new Phone("(31) 99898-0909");
	}
}
