package org.nextframework.types;

import java.io.Serializable;

public class PhoneBrazil implements Serializable {

	private static final long serialVersionUID = 1L;
	private String value;

	/**
	 * 
	 * @deprecated Use String constructor (this one is to attend Hibernate)
	 */
	public PhoneBrazil() {
	}

	public PhoneBrazil(String telefone) {
		if (telefone == null)
			throw new NullPointerException();
		checkPattern(telefone);
		telefone = removeSymbols(telefone);
		value = telefone;
	}

	private void checkPattern(String value) throws IllegalArgumentException {
		if (value.length() > 15) {
			throw new IllegalArgumentException("O tamanho máximo do campo telefone é 15");
		}
		String cleanValue = removeSymbols(value);
		char[] charArray = cleanValue.toCharArray();
		for (int i = 0; i < charArray.length; i++) {
			char c = charArray[i];
			if (!Character.isDigit(c)) {
				throw new IllegalArgumentException("Não foi possível converter \"" + value
						+ "\" para um telefone válido. Caracter inválido \"" + c + "\"");
			}
		}
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	protected PhoneBrazil clone() throws CloneNotSupportedException {
		return new PhoneBrazil(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PhoneBrazil)) {
			return false;
		}
		if (this.value == null && ((PhoneBrazil) obj).value == null) {
			return true;
		} else if (this.value == null || ((PhoneBrazil) obj).value == null) {
			return false;
		}
		return this.value.equals(((PhoneBrazil) obj).value);
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
				int size = value.length();
				if (size == 10 || size == 11) {
					builder.insert(size == 10 ? 6 : 7, '-');
					builder.insert(2, ' ');
					builder.insert(2, ')');
					builder.insert(0, '(');
				}
				if (size == 8 || size == 9) {
					builder.insert(size == 8 ? 4 : 5, '-');
				}
				return builder.toString();
			} catch (IndexOutOfBoundsException e) {
				return value;
			}
		}
	}

	private String removeSymbols(String value2) {
		return value2.replaceAll("\\(|\\)| |\\-", "");
	}

}