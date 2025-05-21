/**
 * 
 */
package org.nextframework.view;

import java.util.HashMap;
import java.util.Map;

public class InputTagType {

	private String type;

	private InputTagType(String type) {
		this.type = type;
		enums.put(type, this);
	}

	@Override
	public String toString() {
		return type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		InputTagType other = (InputTagType) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	/**
	 * Creates a new type or return existing one if already created
	 * @param type
	 * @return
	 */
	public static InputTagType getType(String type) {
		try {
			return valueOf(type);
		} catch (IllegalArgumentException e) {
			return new InputTagType(type);
		}
	}

	private static Map<String, InputTagType> enums = new HashMap<String, InputTagType>();

	public static InputTagType valueOf(String typeString) {
		InputTagType type = enums.get(typeString);
		if (type == null) {
			throw new IllegalArgumentException("no type found for " + typeString);
		} else {
			return type;
		}
	}

	public static final InputTagType SUGGEST = new InputTagType("SUGGEST");
	public static final InputTagType BUTTON = new InputTagType("BUTTON");
	public static final InputTagType CHECKBOX = new InputTagType("CHECKBOX");
	public static final InputTagType CHECKLIST = new InputTagType("CHECKLIST");
	public static final InputTagType FILE = new InputTagType("FILE");
	public static final InputTagType HIDDEN = new InputTagType("HIDDEN");
	public static final InputTagType IMAGE = new InputTagType("IMAGE");
	public static final InputTagType PASSWORD = new InputTagType("PASSWORD");
	public static final InputTagType RADIO = new InputTagType("RADIO");
	public static final InputTagType RESET = new InputTagType("RESET");
	public static final InputTagType SUBMIT = new InputTagType("SUBMIT");
	public static final InputTagType TEXT = new InputTagType("TEXT");
	public static final InputTagType DATE = new InputTagType("DATE");
	public static final InputTagType TIME = new InputTagType("TIME");
	public static final InputTagType FLOAT = new InputTagType("FLOAT");
	public static final InputTagType INTEGER = new InputTagType("INTEGER");
	public static final InputTagType MONEY = new InputTagType("MONEY");
	public static final InputTagType CPF = new InputTagType("CPF");
	public static final InputTagType CNPJ = new InputTagType("CNPJ");
	public static final InputTagType CEP = new InputTagType("CEP");
	public static final InputTagType INSCRICAO_ESTADUAL = new InputTagType("INSCRICAO_ESTADUAL");
	public static final InputTagType CREDIT_CARD = new InputTagType("CREDIT_CARD");
	public static final InputTagType SELECT_ONE = new InputTagType("SELECT_ONE");
	public static final InputTagType SELECT_MANY = new InputTagType("SELECT_MANY");
	public static final InputTagType SELECT_MANY_BOX = new InputTagType("SELECT_MANY_BOX");
	public static final InputTagType SELECT_MANY_POPUP = new InputTagType("SELECT_MANY_POPUP");
	public static final InputTagType SELECT_ONE_BUTTON = new InputTagType("SELECT_ONE_BUTTON");
	public static final InputTagType SELECT_ONE_RADIO = new InputTagType("SELECT_ONE_RADIO");
	public static final InputTagType SELECT_ONE_INSERT = new InputTagType("SELECT_ONE_INSERT");
	public static final InputTagType TEXT_AREA = new InputTagType("TEXT_AREA");
	public static final InputTagType PHONE = new InputTagType("PHONE");
	public static final InputTagType HTML = new InputTagType("HTML");

}
