package org.nextframework.chart;

public class ChartUtils {

	public static boolean hasText(String var) {
		CharSequence str = (CharSequence) var;
		if (!(str != null && str.length() > 0)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

}
