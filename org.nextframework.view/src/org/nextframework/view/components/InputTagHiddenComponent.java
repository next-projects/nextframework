package org.nextframework.view.components;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.nextframework.exception.NextException;
import org.nextframework.util.Util;
import org.nextframework.view.TagUtils;

public class InputTagHiddenComponent extends InputTagComponent {

	@Override
	public void validateTag() {
	}

	@Override
	public void prepare() {
		super.prepare();
		if (Util.strings.isEmpty(inputTag.getPattern()) && (isDateOrTime())) {
			inputTag.setPattern("dd/MM/yyyy HH:mm:ss");
		}
	}

	public boolean isDateOrTime() {
		return helper.isDateOrTime(inputTag.getValue()) || helper.isDateOrTime(inputTag.getAutowiredType());
	}

	@Override
	protected void configureValidation(boolean disabled) {
		if (inputTag.isForceValidation()) {
			super.configureValidation(disabled);
		}
	}

	@Override
	public boolean isToPrintRequired() {
		return false;
	}

	@Override
	public String getValueAsString() {
		if (inputTag.getValue() == null) {
			return "<null>";
		}
		return super.getValueAsString();
	}

	public String getBooleanDescriptionToString() {
		if (inputTag.getWrite() != null && inputTag.getWrite()) {
			String booleanDescription = null;
			try {
				// se nao for boolean utilizar o método normal
				if ((inputTag.getValue() instanceof Boolean || inputTag.getValue() == null) && Util.strings.isNotEmpty(inputTag.getTrueFalseNullLabels())) {
					String[] split = inputTag.getTrueFalseNullLabels().split(",");
					String trueString = split[0];
					String falseString = split[1];
					String nullString = "";
					if (split.length == 3) {
						nullString = split[2];
					}
					if (inputTag.getValue() == null) {
						booleanDescription = nullString;
					} else if (inputTag.getValue() instanceof Boolean) {
						if (((Boolean) inputTag.getValue())) {
							booleanDescription = trueString;
						} else {
							booleanDescription = falseString;
						}
					}
					return booleanDescription;
				} else if (inputTag.getValue() instanceof Boolean) {
					if (((Boolean) inputTag.getValue())) {
						booleanDescription = "Sim";
					} else {
						booleanDescription = "Não";
					}
					return booleanDescription;
				} else if ((inputTag.getValue() instanceof Number) && Util.strings.isNotEmpty(inputTag.getPattern())) {
					DecimalFormat decimalFormat = new DecimalFormat(inputTag.getPattern());
					return decimalFormat.format(inputTag.getValue());
				} else if ((inputTag.getValue() instanceof Date || inputTag.getValue() instanceof Calendar) && Util.strings.isNotEmpty(inputTag.getPattern())) {
					DateFormat dateFormat = new SimpleDateFormat(inputTag.getPattern());
					Object value = inputTag.getValue();
					if (value instanceof Calendar) {
						value = ((Calendar) value).getTime();
					}
					return dateFormat.format(value);
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new NextException("trueFalseNullLabels inválido " + inputTag.getTrueFalseNullLabels() + ". Esse atributo deve ser uma string separada por vírgula indicando o valor de TRUE FALSE e NULL. ex.: sim,não,vazio");
			}
			return TagUtils.getObjectDescriptionToString(inputTag.getValue());
		} else {
			return "";
		}
	}

}
