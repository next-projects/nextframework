package org.nextframework.view.components;

import java.text.DecimalFormat;

import javax.servlet.jsp.JspException;

import org.nextframework.util.Util;

public class InputTagNumberComponent extends InputTagComponent {

	@Override
	public void validateTag() {
	}

	@Override
	public void prepare() {
		super.prepare();
		if (inputTag.getId() == null) {
			inputTag.setId(inputTag.generateUniqueId());
		}
		alignToRight();
	}

	protected void alignToRight() {
		Object style = inputTag.getDAAtribute("style", false);
		style = "text-align: right;" + (style != null ? " " + style : "");
		try {
			inputTag.setDynamicAttribute(null, "style", style);
		} catch (JspException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getValueAsString() {
		if (Util.strings.isNotEmpty(inputTag.getPattern()) && inputTag.getValue() != null) {
			DecimalFormat decimalFormat = new DecimalFormat(inputTag.getPattern());
			String formatedValue = decimalFormat.format(inputTag.getValue());
			if (formatedValue.startsWith(",")) {
				formatedValue = "0" + formatedValue;
			} else if (formatedValue.startsWith("-,")) {
				formatedValue = "-0" + formatedValue.substring(1);
			}
			return formatedValue;
		}
		return super.getValueAsString();
	}

}
