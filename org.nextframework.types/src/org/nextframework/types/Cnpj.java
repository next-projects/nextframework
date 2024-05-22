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

import org.nextframework.types.hibernate.CnpjUserType;

public class Cnpj extends CnpjUserType implements Document, Serializable {

	private static final long serialVersionUID = 7515600575750828297L;

	public static boolean AUTO_VALIDATION = true;

	private String value;

	/**
	 * @deprecated Utilize o método que recebe uma String, esse método só existe por causa do hibernate
	 */
	@Deprecated
	public Cnpj() {
	}

	public Cnpj(String cnpj, boolean check) {
		if (cnpj == null)
			throw new NullPointerException();
		checkPattern(cnpj);
		String original = cnpj;
		cnpj = removeSymbols(cnpj);
		if (check && !cnpjValido(cnpj)) {
			throw new IllegalArgumentException("O CNPJ '" + original + "' não é válido");
		}
		value = cnpj.trim().equals("") ? null : cnpj;
	}

	public Cnpj(String cnpj) {
		this(cnpj, true);
	}

	/** Realiza a validação do CNPJ.
	*
	* @param   str_cnpj número de CNPJ a ser validado
	* @return  true se o CNPJ é válido e false se não é válido
	*/
	public static boolean cnpjValido(String str_cnpj) {

		if (str_cnpj.length() != 15 && str_cnpj.length() != 14) {
			return false;
		}
		if (str_cnpj.length() == 15) {
			str_cnpj = str_cnpj.substring(1, 15);
		}

		int soma = 0, dig;
		String cnpj_calc = str_cnpj.substring(0, 12);

		if (str_cnpj.length() != 14)
			return false;

		char[] chr_cnpj = str_cnpj.toCharArray();

		/* Primeira parte */
		for (int i = 0; i < 4; i++)
			if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9)
				soma += (chr_cnpj[i] - 48) * (6 - (i + 1));
		for (int i = 0; i < 8; i++)
			if (chr_cnpj[i + 4] - 48 >= 0 && chr_cnpj[i + 4] - 48 <= 9)
				soma += (chr_cnpj[i + 4] - 48) * (10 - (i + 1));
		dig = 11 - (soma % 11);

		cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);

		/* Segunda parte */
		soma = 0;
		for (int i = 0; i < 5; i++)
			if (chr_cnpj[i] - 48 >= 0 && chr_cnpj[i] - 48 <= 9)
				soma += (chr_cnpj[i] - 48) * (7 - (i + 1));
		for (int i = 0; i < 8; i++)
			if (chr_cnpj[i + 5] - 48 >= 0 && chr_cnpj[i + 5] - 48 <= 9)
				soma += (chr_cnpj[i + 5] - 48) * (10 - (i + 1));
		dig = 11 - (soma % 11);
		cnpj_calc += (dig == 10 || dig == 11) ? "0" : Integer.toString(dig);

		return str_cnpj.equals(cnpj_calc);
	}

	private void checkPattern(String value) throws IllegalArgumentException {
		if (!value.trim().equals("") && !value.matches("\\d{2,3}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}")) {
			throw new IllegalArgumentException("O CNPJ '" + value + "' não está no formato correto");
		}
	}

	public String getValue() {
		return value;
	}

	@Override
	protected Cnpj clone() throws CloneNotSupportedException {
		return new Cnpj(value, AUTO_VALIDATION);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Cnpj)) {
			return false;
		}
		if (this.value == null && ((Cnpj) obj).value == null) {
			return true;
		} else if (this.value == null || ((Cnpj) obj).value == null) {
			return false;
		}
		return this.value.equals(((Cnpj) obj).value);
	}

	@Override
	public int hashCode() {
		if (value == null)
			return super.hashCode();
		return value.hashCode();
	}

	@Override
	public String toString() {
		if (TypeUtils.isEmpty(value)) {
			return "";
		} else {
			try {
				StringBuilder builder = new StringBuilder(value);
				builder.insert(12, '-');
				builder.insert(8, '/');
				builder.insert(5, '.');
				builder.insert(2, '.');
				return builder.toString();
			} catch (IndexOutOfBoundsException e) {
				//System.out.println("\n************************\nCnpj inválido: "+value);
				return value;
			}
		}
	}

	private String removeSymbols(String value2) {
		return value2.replace(".", "").replace("-", "").replace("/", "");
	}

	public boolean isNotEmpty() {
		return !TypeUtils.isEmpty(value);
	}

	public static void main(String[] args) {
		String c = "006.213.117/0001-30";
		new Cnpj(c);
		System.out.println(Cnpj.cnpjValido(c));
	}

}
