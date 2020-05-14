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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.message.MessageResolver;
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
				throw new RuntimeException("Parametros inv·lidos: " + parameters);
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
		Class clazz = o.getClass();
		while (clazz.getName().contains("$$")) {
			clazz = clazz.getSuperclass();
		}
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

	public String toStringDescription(Object value) {
		return toStringDescription(value, null, null, null);
	}

	public String toStringDescription(Object value, String formatDate, String formatNumber) {
		return toStringDescription(value, formatDate, formatNumber, null);
	}

	public String toStringDescription(Object value, String formatDate, String formatNumber, MessageResolver resolver) {

		if (value == null) {
			return "";
		}

		formatDate = formatDate == null ? "dd/MM/yyyy" : formatDate;
		formatNumber = formatNumber == null ? "#,##0.##" : formatNumber;

		if (value instanceof Calendar) {
			value = ((Calendar) value).getTime();
		}

		if (value instanceof String) {
			return (String) value;
		} else if (value instanceof Date) { //FIXME
			DateFormat dateFormat = new SimpleDateFormat(formatDate);
			return dateFormat.format(value);
		} else if (value instanceof Number) {
			NumberFormat numberFormat = new DecimalFormat(formatNumber);
			return numberFormat.format(value);
		} else if (value instanceof MessageSourceResolvable) {
			return resolver.message((MessageSourceResolvable) value);
		} else if (value.getClass().isArray()) {
			Object[] array = (Object[]) value;
			String description = "";
			if (array.length > 0) {
				for (Object o : array) {
					description += (description.length() == 0 ? "" : ", ") + toStringDescription(o, formatDate, formatNumber);
				}
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
		Object description = beanDescriptor.getDescription();
		if (description == null && beanDescriptor.getDescriptionPropertyName() == null) {
			description = value.toString();
		}

		return description != null ? description.toString() : "";
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
//        return org.apache.commons.lang.StringUtils.replaceChars(string, "¡…Õ”⁄¿»Ã“Ÿ¬ Œ‘€ƒÀœ÷‹√’«·ÈÌÛ˙‡ËÏÚ˘‚ÍÓÙ˚‰ÎÔˆ¸„ıÁ", "AEIOUAEIOUAEIOUAEIOUAOCaeiouaeiouaeiouaeiouaoc");
	}

	public String toString(Object o) {
		if (o == null) {
			return null;
		} else {
			if (o instanceof String) {
				return (String) o;
			} else if (o instanceof Date) {
				DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
				return dateFormat.format(o);
			} else if (o instanceof Double) {
				return new DecimalFormat("#,##0.00").format((Double) o);
			}
			return o.toString();
		}
	}

	public String toString(Object o, String def) {
		if (o == null) {
			return def;
		} else {
			return o.toString();
		}
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

	public static String onlyNumbers(String s) {
		int i;
		String numeros = "0123456789";
		int tamanho = s.length();
		StringBuilder sb = new StringBuilder();
		String caractere = "";
		for (i = 0; i < tamanho; i++) {
			caractere = s.substring(i, i + 1);
			if (numeros.indexOf(caractere) != -1)
				sb.append(caractere);
		}
		return sb.toString();
	} //SoNumero

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

	public String replaceString(MessageResolver resolver, String original) {
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
					String message = resolver.message(code);
					nova = nova.substring(0, indexBegin) + message + nova.substring(indexEnd + REPLACE_CLOSE.length(), nova.length());
				}
			}
		} while (indexBegin > -1 && indexEnd > -1);
		return nova;
	}

}