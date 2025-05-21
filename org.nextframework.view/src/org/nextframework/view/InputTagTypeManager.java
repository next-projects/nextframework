package org.nextframework.view;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.nextframework.types.Cep;
import org.nextframework.types.Cnpj;
import org.nextframework.types.Cpf;
import org.nextframework.types.File;
import org.nextframework.types.InscricaoEstadual;
import org.nextframework.types.Money;
import org.nextframework.types.Phone;
import org.nextframework.types.PhoneBrazil;
import org.nextframework.types.SimpleTime;

public class InputTagTypeManager {

	protected Map<Class<?>, InputTagType> classTypeMap = new LinkedHashMap<Class<?>, InputTagType>();

	public void registerTypeForClass(Class<?> key, InputTagType value) {
		classTypeMap.put(key, value);
	}

	public InputTagType getTypeForClass(Class<?> key) {
		return classTypeMap.get(key);
	}

	private static InputTagTypeManager instance = new InputTagTypeManager();

	public static InputTagTypeManager getInstance() {
		return instance;
	}

	/**
	 * Call this method before any view rendering to change the Class - Type mapping 
	 * @param instance
	 */
	public static void setInstance(InputTagTypeManager instance) {
		InputTagTypeManager.instance = instance;
	}

	private InputTagTypeManager() {

	}

	static {
		instance.registerTypeForClass(InscricaoEstadual.class, InputTagType.INSCRICAO_ESTADUAL);
		instance.registerTypeForClass(java.sql.Date.class, InputTagType.DATE);
		instance.registerTypeForClass(Date.class, InputTagType.DATE);
		instance.registerTypeForClass(Calendar.class, InputTagType.DATE);
		instance.registerTypeForClass(Collection.class, InputTagType.SELECT_MANY);
		instance.registerTypeForClass(Time.class, InputTagType.TIME);
		instance.registerTypeForClass(Timestamp.class, InputTagType.TIME);
		instance.registerTypeForClass(Boolean.class, InputTagType.CHECKBOX);
		instance.registerTypeForClass(boolean.class, InputTagType.CHECKBOX);
		instance.registerTypeForClass(Integer.class, InputTagType.INTEGER);
		instance.registerTypeForClass(Short.class, InputTagType.INTEGER);
		instance.registerTypeForClass(Long.class, InputTagType.INTEGER);
		instance.registerTypeForClass(Byte.class, InputTagType.INTEGER);
		instance.registerTypeForClass(BigInteger.class, InputTagType.INTEGER);
		instance.registerTypeForClass(Float.class, InputTagType.FLOAT);
		instance.registerTypeForClass(Double.class, InputTagType.FLOAT);
		instance.registerTypeForClass(BigDecimal.class, InputTagType.FLOAT);
		instance.registerTypeForClass(Cep.class, InputTagType.CEP);
		instance.registerTypeForClass(PhoneBrazil.class, InputTagType.PHONE);
		instance.registerTypeForClass(Phone.class, InputTagType.PHONE);
		instance.registerTypeForClass(Cpf.class, InputTagType.CPF);
		instance.registerTypeForClass(Cnpj.class, InputTagType.CNPJ);
		instance.registerTypeForClass(SimpleTime.class, InputTagType.TIME);
		instance.registerTypeForClass(Money.class, InputTagType.MONEY);
		instance.registerTypeForClass(File.class, InputTagType.FILE);
	}

}
