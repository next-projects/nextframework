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
package org.nextframework.util;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Formattable;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.text.MaskFormatter;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.core.standard.Next;
import org.nextframework.exception.NextException;
import org.springframework.context.MessageSourceResolvable;

/**
 * @author rogelgarcia | marcusabreu
 * @since 22/01/2006
 * @version 1.1
 */
public class StringUtils {

	public String uncaptalize(String str) {
		return org.springframework.util.StringUtils.uncapitalize(str);
	}

	public String captalize(String str) {
		return org.springframework.util.StringUtils.capitalize(str);
	}

	public boolean isEmpty(String str) {
		return str == null || str.equals("") || str.trim().equals("");
	}

	public boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	public boolean equals(String txt1, String txt2) {
		if (txt1 == null && txt2 == null) {
			return true;
		}
		if (txt1 == null || txt2 == null) {
			return false;
		}
		return txt1.equals(txt2);
	}

	/**
	 * Separa parametros no formato key=value;
	 * @param parameters
	 * @return
	 */
	public Map<String, String> parseParameters(String parameters) {
		HashMap<String, String> hashMap = new HashMap<String, String>();
		if (parameters == null) {

			return hashMap;
		}
		String[] split = parameters.split(";");
		for (String string : split) {
			String[] split2 = string.split("=");
			if (split2.length != 2) {
				throw new NextException("Parametros inv·lidos: " + parameters);
			}
			hashMap.put(split2[0], split2[1]);
		}
		return hashMap;
	}

	/**
	 * Separa as palavras da string toda vez que trocar o case
	 * @param string
	 * @return
	 */
	public String separateOnCase(String string) {
		if (string.length() <= 1) {
			return string;
		}
		char[] toCharArray = string.substring(1).toCharArray();
		StringBuilder builder = new StringBuilder();
		builder.append(string.charAt(0));
		for (char c : toCharArray) {
			if (Character.isUpperCase(c)) {
				builder.append(" ");
			}
			builder.append(c);
		}
		return builder.toString();
	}

	public List<String> splitFieldsAsList(String fields) {
		if (fields == null) {
			return null;
		}
		String[] fieldsArray = splitFields(fields);
		return new ArrayList<String>(Arrays.asList(fieldsArray));
	}

	public String[] splitFields(String fields) {
		if (fields == null) {
			return null;
		}
		return fields.split("\\s*[,|;]\\s*");
	}

	/**
	 * Cria uma string com o nome da classe e o parametro id
	 * Ex.: pacote.Classe[campoid=5]
	 * @param o
	 * @return
	 */
	public String toStringIdStyled(Object o) {
		return toStringIdStyled(o, false);
	}

	@SuppressWarnings("all")
	public String toStringIdStyled(Object o, boolean includeDescription) {

		if (o == null) {
			return "<null>";
		}

		BeanDescriptor beanDescriptor = BeanDescriptorFactory.forBean(o);
		String idProperty = beanDescriptor.getIdPropertyName();
		if (idProperty == null) {
			//CODIGO INSERIDO EM 16 DE NOVEMBRO DE 2006 PARA O AJAX SUPORTAR LISTAS QUE NAO S√O DE BEANS (EX LISTA DE INTEIROS)		
			if (o.getClass().isEnum()) {
				Enum e = (Enum) o;
				return e.name();
			}
			return o.toString();
		}

		Object value = beanDescriptor.getId();
		if (value == null) {
			value = "<null>";
		}

		StringBuilder stringBuilder = new StringBuilder();
		Class clazz = Util.objects.getRealClass(o.getClass());
		stringBuilder.append(clazz.getName());
		stringBuilder.append("[");

		stringBuilder.append(idProperty);
		stringBuilder.append("=");
		stringBuilder.append(value);

		if (includeDescription) {
			Object description = beanDescriptor.getDescription();
			if (description != null) {
				String descriptionPropertyName = beanDescriptor.getDescriptionPropertyName();
				stringBuilder.append(";");//TODO refactor. defined in ServletRequestDataBinderNext
				stringBuilder.append(descriptionPropertyName);
				stringBuilder.append("=");
				stringBuilder.append(description);
			}
		}
		stringBuilder.append("]");

		return stringBuilder.toString();
	}

	public String toString(Object o) {
		if (o == null) {
			return null;
		}
		return toStringDescription(o, null, null, null, null);
	}

	public String toStringDescription(Object value) {
		return toStringDescription(value, null, null, null, null);
	}

	public String toStringDescription(Object value, String formatDate, String formatNumber, String formatString) {
		return toStringDescription(value, formatDate, formatNumber, formatString, null);
	}

	public String toStringDescription(Object value, Locale locale) {
		return toStringDescription(value, null, null, null, locale);
	}

	@SuppressWarnings("rawtypes")
	public String toStringDescription(Object value, String formatDate, String formatNumber, String formatString, Locale locale) {

		if (value == null) {
			return "";
		}

		if (value instanceof Calendar) {
			value = ((Calendar) value).getTime();
		} else if (Collection.class.isAssignableFrom(value.getClass())) {
			value = ((Collection) value).toArray();
		}

		if (value instanceof String) {
			if (Util.strings.isNotEmpty(formatString)) {
				try {
					MaskFormatter mf = new MaskFormatter(formatString);
					mf.setValueContainsLiteralCharacters(false);
					return mf.valueToString((String) value);
				} catch (ParseException e) {
					throw new NextException("Erro ao aplicar m·scara '" + formatString + "' em '" + value + "'!");
				}
			}
			return (String) value;
		} else if (value instanceof Date || value instanceof java.sql.Date || value instanceof Timestamp) {
			if (Util.strings.isEmpty(formatDate)) {
				if (value instanceof Time) {
					formatDate = "HH:mm:ss";
				} else if (value instanceof Date || value instanceof java.sql.Date || value instanceof Timestamp || value instanceof Calendar) {
					formatDate = "dd/MM/yyyy";
				}
			}
			DateFormat dateFormat = new SimpleDateFormat(formatDate);
			return dateFormat.format(value);
		} else if (value instanceof Number) {
			if (Util.strings.isEmpty(formatNumber)) {
				if (value instanceof Double || value instanceof Float || value instanceof BigDecimal) {
					formatNumber = "#,##0.00";
				} else {
					formatNumber = "#,##0.##";
				}
			}
			NumberFormat numberFormat = new DecimalFormat(formatNumber);
			String valueToString = numberFormat.format(value);
			if (valueToString.startsWith(",")) {
				valueToString = "0" + valueToString;
			}
			return valueToString;
		} else if (value instanceof Formattable) {
			Formatter fmt = new Formatter();
			((Formattable) value).formatTo(fmt, 0, -1, -1);
			return fmt.out().toString();
		} else if (value instanceof Throwable) {
			return Util.exceptions.getExceptionDescription((Throwable) value, locale);
		} else if (value instanceof MessageSourceResolvable) {
			return Next.getMessageSource().getMessage((MessageSourceResolvable) value, locale);
		} else if (value.getClass().isArray()) {
			if (value.getClass().getComponentType().isPrimitive()) {
				if (value instanceof boolean[]) {
					return Arrays.toString((boolean[]) value);
				} else if (value instanceof byte[]) {
					return Arrays.toString((byte[]) value);
				} else if (value instanceof short[]) {
					return Arrays.toString((short[]) value);
				} else if (value instanceof char[]) {
					return Arrays.toString((char[]) value);
				} else if (value instanceof int[]) {
					return Arrays.toString((int[]) value);
				} else if (value instanceof long[]) {
					return Arrays.toString((long[]) value);
				} else if (value instanceof float[]) {
					return Arrays.toString((float[]) value);
				} else if (value instanceof double[]) {
					return Arrays.toString((double[]) value);
				} else {
					throw new IllegalArgumentException("Unexpected type: " + value.getClass().getComponentType());
				}
			}
			Object[] array = (Object[]) value;
			String description = "";
			for (Object o : array) {
				description += (description.length() == 0 ? "" : ", ") + toStringDescription(o, formatDate, formatNumber, formatString, locale);
			}
			return description;
		}

		try {
			Class<?> horaClass = Class.forName("org.nextframework.types.SimpleTime");
			if (horaClass != null && horaClass.isAssignableFrom(value.getClass())) {
				return value.toString();
			}
		} catch (ClassNotFoundException e) {
			//e.printStackTrace();
		}

		BeanDescriptor beanDescriptor = BeanDescriptorFactory.forBean(value);
		if (beanDescriptor.getDescriptionPropertyName() != null) {
			value = beanDescriptor.getDescription();
		} else {
			value = value.toString();
		}

		return toStringDescription(value, formatDate, formatNumber, formatString, locale);
	}

	public String getMessage(String code, Locale locale) {
		return getMessage(Util.objects.newMessage(code), locale);
	}

	public String getMessage(String code, Object[] args, Locale locale) {
		return getMessage(Util.objects.newMessage(code, args), locale);
	}

	public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		return getMessage(Util.objects.newMessage(code, args, defaultMessage), locale);
	}

	public String getMessage(MessageSourceResolvable msr, Locale locale) {
		return Next.getMessageSource().getMessage(msr, locale);
	}

	public String removeAccents(String string) {
		//copied from apache commons lang
		String str = string;
		String searchChars = "¡…Õ”⁄¿»Ã“Ÿ¬ Œ‘€ƒÀœ÷‹√’«·ÈÌÛ˙‡ËÏÚ˘‚ÍÓÙ˚‰ÎÔˆ¸„ıÁ";
		String replaceChars = "AEIOUAEIOUAEIOUAEIOUAOCaeiouaeiouaeiouaeiouaoc";
		if (isEmpty(str) || isEmpty(searchChars)) {
			return str;
		}
		boolean modified = false;
		int replaceCharsLength = replaceChars.length();
		int strLength = str.length();
		StringBuilder buf = new StringBuilder(strLength);
		for (int i = 0; i < strLength; i++) {
			char ch = str.charAt(i);
			int index = searchChars.indexOf(ch);
			if (index >= 0) {
				modified = true;
				if (index < replaceCharsLength) {
					buf.append(replaceChars.charAt(index));
				}
			} else {
				buf.append(ch);
			}
		}
		if (modified) {
			return buf.toString();
		}
		return str;
	}

	/**
	 * Faz o escape de aspas simples
	 */
	public String escape(String string) {
		return escape(string, true, true, false, true);
	}

	/**
	 * Faz o escape de aspas duplas
	 */
	public String escapeQuotes(String string) {
		return escape(string, true, false, true, true);
	}

	public String escape(String string, boolean backslash, boolean singleQuote, boolean doubleQuotes, boolean newLine) {
		if (string == null) {
			return "";
		}
		if (backslash) {
			string = string.replace("\\", "\\\\");
		}
		if (singleQuote) {
			string = string.replace("'", "\\'");
		}
		if (doubleQuotes) {
			string = string.replace("\"", "\\\"");
		}
		if (newLine) {
			string = string.replace("\n", "\\n").replace("\r", "");
		}
		return string;
	}

	public String onlyAlphanumerics(String s) {
		if (s == null) {
			return null;
		}
		return s.replaceAll("[^A-Za-z0-9]", "");
	}

	public String onlyNumbers(String s) {
		if (s == null) {
			return null;
		}
		return s.replaceAll("[^0-9]", "");
	}

	public String stringCheia(String stringOriginal, String caractereCompleta, int tamanho, boolean direita) {
		if (stringOriginal == null) {
			stringOriginal = "";
		}
		String result = stringOriginal;
		if (caractereCompleta == null) {
			caractereCompleta = "";
		}
		if (caractereCompleta.equals(""))
			caractereCompleta += "0";
		while (result.length() < tamanho) {
			if (direita)
				result += caractereCompleta;
			else
				result = caractereCompleta + result;
		}
		if (result.length() > tamanho) {
			if (direita)
				result = result.substring(0, tamanho);
			else
				result = result.substring(result.length() - tamanho, result.length());
		}
		return result.toUpperCase();
	} //StringCheia

	public String emptyIfNull(String propertyPrefix) {
		return propertyPrefix != null ? propertyPrefix : "";
	}

	public static final String REPLACE_OPEN = "{";
	public static final String REPLACE_CLOSE = "}";

	public String replaceString(String original, Locale locale) {
		if (Util.strings.isEmpty(original)) {
			return original;
		}
		String nova = original;
		int indexBegin = -1;
		int indexEnd = -1;
		do {
			indexBegin = nova.indexOf(REPLACE_OPEN, indexBegin + 1);
			if (indexBegin > -1) {
				indexEnd = nova.indexOf(REPLACE_CLOSE, indexBegin + REPLACE_OPEN.length());
				if (indexEnd > -1) {
					String code = nova.substring(indexBegin + REPLACE_OPEN.length(), indexEnd);
					String message = Next.getMessageSource().getMessage(code, null, locale);
					nova = nova.substring(0, indexBegin) + message + nova.substring(indexEnd + REPLACE_CLOSE.length(), nova.length());
				}
			}
		} while (indexBegin > -1 && indexEnd > -1);
		return nova;
	}

}