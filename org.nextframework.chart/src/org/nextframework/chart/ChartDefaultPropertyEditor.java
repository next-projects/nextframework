package org.nextframework.chart;

import java.beans.PropertyEditorSupport;
import java.io.Serializable;
import java.sql.Time;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formattable;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;

public class ChartDefaultPropertyEditor extends PropertyEditorSupport implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final String TIME_PATTERN = "HH:mm:ss";
	private static final String DATE_PATTERN = "dd/MM/yyyy";
	private static final String INTEGER_NUMBER_PATTERN = "#,##0";
	private static final String DECIMAL_NUMBER_PATTERN = "#,##0.00";

	protected DateFormat timeFormat = new SimpleDateFormat(TIME_PATTERN);
	protected DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
	protected NumberFormat integerNumberFormat = new DecimalFormat(INTEGER_NUMBER_PATTERN);
	protected NumberFormat decimalNumberFormat = new DecimalFormat(DECIMAL_NUMBER_PATTERN);

	public ChartDefaultPropertyEditor() {

	}

	public ChartDefaultPropertyEditor(String pattern) {
		if (pattern.contains("0") || pattern.contains("#")) {
			integerNumberFormat = decimalNumberFormat = new DecimalFormat(pattern);
		} else {
			timeFormat = dateFormat = new SimpleDateFormat(pattern);
		}
	}

	@Override
	public String getAsText() {
		if (getValue() == null) {
			return "";
		}
		if (getValue() instanceof String) {
			return (String) getValue();
		}
		if (getValue() instanceof Calendar) {
			setValue(((Calendar) getValue()).getTime());
		}
		if (getValue() instanceof Time) {
			setValue(timeFormat.format(getValue()));
		} else if (getValue() instanceof Date) {
			setValue(dateFormat.format(getValue()));
		} else if (getValue() instanceof Long || getValue() instanceof Integer || getValue() instanceof Short || getValue() instanceof Byte) {
			setValue(integerNumberFormat.format(getValue()));
		} else if (getValue() instanceof Double || getValue() instanceof Float) {
			setValue(decimalNumberFormat.format(getValue()));
		} else if (getValue() instanceof Formattable) {
			setValue(String.format("%s", getValue()));
		} else if (!getValue().getClass().getName().startsWith("java")) {
			BeanDescriptor bd = BeanDescriptorFactory.forBean(getValue());
			if (bd.getDescriptionPropertyName() != null) {
				Object description = bd.getDescription();
				if (description != null) {
					setValue(description);
				} else {
					setValue(getValue().getClass().getSimpleName() + "#" + getValue().hashCode());
				}
			}
		}
		return "" + getValue();
	}

}
