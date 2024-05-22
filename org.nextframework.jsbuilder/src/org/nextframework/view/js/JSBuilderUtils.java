package org.nextframework.view.js;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class JSBuilderUtils {

	public static String join(List<Object> list, String separator) {
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			result += list.get(i);
			if (i + 1 < list.size()) {
				result += separator;
			}
		}
		return result;
	}

	public static Object convertToJavascriptValue(Object value) {
		boolean aspas = false;
		if (value instanceof Calendar) {
			value = ((Calendar) value).getTime();
		}
		if (value instanceof Date) {
			value = "new Date(" + ((Date) value).getTime() + ")";
		} else {
			aspas = value instanceof String;
		}
		if (value instanceof JavascriptReferenciable) {
			JavascriptReferenciable referenciable = (JavascriptReferenciable) value;
			value = referenciable.getReference();
		}
		if (value == null) {
			value = "null";
		}
		if (aspas) {
			value = escapeQuotes(value.toString());
			value = "\"" + value + "\"";
		}
		return value;
	}

	private static String escapeQuotes(String string) {
		if (string == null) {
			return "";
		}
		return string.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
	}

}
