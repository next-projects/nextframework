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

import org.nextframework.types.hibernate.CpfUserType;

public class Cpf extends CpfUserType implements Document, Serializable {

	private static final long serialVersionUID = 2090169993922126029L;

	public static boolean AUTO_VALIDATION = true;

	private String value;

	/**
	 * @deprecated Utilize o método que recebe uma String, esse método só existe por causa do hibernate
	 */
	@Deprecated
	public Cpf() {
	}

	public Cpf(String cpf, boolean check) {
		if (cpf == null)
			throw new NullPointerException();
		checkPattern(cpf);
		cpf = removeSymbols(cpf);
		if (check && !cpfValido(cpf)) {
			throw new IllegalArgumentException("O CPF '" + cpf + "' não é válido");
		}
		value = cpf.trim().equals("") ? null : cpf;
	}

	public Cpf(String cpf) {
		this(cpf, true);
	}

	/**
	 * Realiza a validação do CPF.
	 * 
	 * @param strCPF
	 *            número de CPF a ser validado
	 * @return true se o CPF é válido e false se não é válido
	 */
	public static boolean cpfValido(String strCpf) {
		
		if (strCpf.length() > 11) {
			strCpf = removeSymbols(strCpf);
		}
		
		if (strCpf.length() != 11) {
			return false;
		}
		
		int d1, d2;
		int digito1, digito2, resto;
		int digitoCPF;
		String nDigResult;

		d1 = d2 = 0;
		digito1 = digito2 = resto = 0;

		for (int nCount = 1; nCount < strCpf.length() - 1; nCount++) {
			digitoCPF = Integer.valueOf(strCpf.substring(nCount - 1, nCount))
					.intValue();

			// multiplique a ultima casa por 2 a seguinte por 3 a seguinte por 4
			// e assim por diante.
			d1 = d1 + (11 - nCount) * digitoCPF;

			// para o segundo digito repita o procedimento incluindo o primeiro
			// digito calculado no passo anterior.
			d2 = d2 + (12 - nCount) * digitoCPF;
		} ;

		// Primeiro resto da divisão por 11.
		resto = (d1 % 11);

		// Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11
		// menos o resultado anterior.
		if (resto < 2)
			digito1 = 0;
		else
			digito1 = 11 - resto;

		d2 += 2 * digito1;

		// Segundo resto da divisão por 11.
		resto = (d2 % 11);

		// Se o resultado for 0 ou 1 o digito é 0 caso contrário o digito é 11
		// menos o resultado anterior.
		if (resto < 2)
			digito2 = 0;
		else
			digito2 = 11 - resto;

		// Digito verificador do CPF que está sendo validado.
		String nDigVerific = strCpf.substring(strCpf.length() - 2, strCpf
				.length());

		// Concatenando o primeiro resto com o segundo.
		nDigResult = String.valueOf(digito1) + String.valueOf(digito2);

		// comparar o digito verificador do cpf com o primeiro resto + o segundo
		// resto.
		return nDigVerific.equals(nDigResult);
	}

	private void checkPattern(String value) throws IllegalArgumentException {
		if (!value.trim().equals("") && !value.matches("\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}")) {
			throw new IllegalArgumentException("O CPF '" + value + "' não está no formato correto");
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	protected Cpf clone() throws CloneNotSupportedException {
		return new Cpf(value, AUTO_VALIDATION);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Cpf)) {
			return false;
		}
		if (this.value == null && ((Cpf) obj).value == null) {
			return true;
		} else if (this.value == null || ((Cpf) obj).value == null) {
			return false;
		}
		return this.value.equals(((Cpf) obj).value);
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
				builder.insert(9, '-');
				builder.insert(6, '.');
				builder.insert(3, '.');
				return builder.toString();
			} catch (IndexOutOfBoundsException e) {
				//System.out.println("\n************************\nCPF inválido: "+value);
				return value;
			}
		}
	}

	private static String removeSymbols(String value2) {
		return value2.replace(".", "").replace("-", "");
	}

	public boolean isNotEmpty() {
		return !TypeUtils.isEmpty(value);
	}

	public static void main(String[] args) {
		String c = "073.572.796-18";
		new Cpf(c);
		System.out.println(Cpf.cpfValido(c));
	}

}
