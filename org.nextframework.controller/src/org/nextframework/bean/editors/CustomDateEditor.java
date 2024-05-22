package org.nextframework.bean.editors;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.springframework.util.StringUtils;

public class CustomDateEditor extends org.springframework.beans.propertyeditors.CustomDateEditor {

	DateFormat smartDateFormat = new SimpleDateFormat("yyyy,MM,dd,HH,mm,ss,SSS");

	boolean useSmartDetection = true;

	public CustomDateEditor(DateFormat dateFormat, boolean allowEmpty, int exactDateLength) {
		super(dateFormat, allowEmpty, exactDateLength);
//		smartDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-3"));
	}

	public CustomDateEditor(DateFormat dateFormat, boolean allowEmpty) {
		super(dateFormat, allowEmpty);
//		smartDateFormat.setTimeZone(TimeZone.getTimeZone("GMT-3"));
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (useSmartDetection && text.length() > 4) {
			char[] chars = text.toCharArray();
			boolean first4Digits = true;
			for (int i = 0; i < 4; i++) {
				if (!Character.isDigit(chars[i])) {
					first4Digits = false;
				}
			}
			if (first4Digits) {
				String[] textParts = text.split("[/\\- :\\.]");
				String[] parts = new String[7];
				for (int i = 0; i < parts.length; i++) {
					parts[i] = "00";
				}
				parts[6] = "000";
				System.arraycopy(textParts, 0, parts, 0, textParts.length);
				boolean validParts = true;
				for (int i = 0; i < parts.length && validParts; i++) {
					switch (i) {
						case 0: //year
							validParts = parts[i].length() == 4;
							break;
						case 1: //month
							validParts = parts[i].length() == 2;
							break;
						case 2: //date
							validParts = parts[i].length() == 2;
							break;
						case 3: //hour
							validParts = parts[i].length() == 2;
							break;
						case 4: //minute
							validParts = parts[i].length() == 2;
							break;
						case 5: //second
							validParts = parts[i].length() == 2;
							break;
						case 6: //milisecond
							validParts = parts[i].length() <= 3;
							break;
						default:
							validParts = false;
					}
				}
				if (validParts) {
					String newText = StringUtils.arrayToCommaDelimitedString(parts);
					try {
						setValue(smartDateFormat.parse(newText));
					} catch (ParseException ex) {
						throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
					}
					return;
				}
			}
		}
		super.setAsText(text);
	}

	public boolean isUseSmartDetection() {
		return useSmartDetection;
	}

	public void setUseSmartDetection(boolean useSmartDetection) {
		this.useSmartDetection = useSmartDetection;
	}

}
