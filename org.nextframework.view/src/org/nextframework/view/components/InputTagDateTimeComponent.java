package org.nextframework.view.components;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.nextframework.core.config.ViewConfig;
import org.nextframework.service.ServiceFactory;
import org.nextframework.util.Util;
import org.nextframework.view.DataGridTag;
import org.nextframework.view.InputTagType;

public class InputTagDateTimeComponent extends InputTagComponent {

	private static final String DEFAULT_DATE_PATTERN = "dd/MM/yyyy";
	private static final String DEFAULT_TIME_PATTERN = "HH:mm";

	private static final DateFormat DEFAULT_DATE_FORMAT = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
	private static final DateFormat DEFAULT_TIME_FORMAT = new SimpleDateFormat(DEFAULT_TIME_PATTERN);

	@Override
	public void prepare() {
		super.prepare();
		if (inputTag.getId() == null) {
			inputTag.setId(inputTag.generateUniqueId());
		}
		if (Util.strings.isEmpty(inputTag.getPattern())) {
			if (selectedType == InputTagType.DATE) {
				inputTag.setPattern(DEFAULT_DATE_PATTERN);
			} else {
				inputTag.setPattern(DEFAULT_TIME_PATTERN);
			}
		}
		inputTag.getDynamicAttributesMap().put("maxlength", inputTag.getPattern().length());
		if (!inputTag.getDynamicAttributesMap().containsKey("size")) {
			inputTag.getDynamicAttributesMap().put("size", inputTag.getPattern().length() + 1);
		}
	}

	@Override
	public String getValueAsString() {
		Object value = inputTag.getValue();
		if (helper.isDateOrTime(value)) {
			if (value instanceof Calendar) {
				value = ((Calendar) value).getTime();
			}
			DateFormat dateFormat;
			if (inputTag.getPattern().equals(DEFAULT_DATE_PATTERN)) {
				dateFormat = DEFAULT_DATE_FORMAT; //most of the time this will be enough
			} else if (inputTag.getPattern().equals(DEFAULT_TIME_PATTERN)) {
				dateFormat = DEFAULT_TIME_FORMAT;
			} else {
				dateFormat = new SimpleDateFormat(inputTag.getPattern()); //this is expensive
			}
			return dateFormat.format(value);
		}
		return super.getValueAsString();
	}

	public String getCalendarPattern() {
		return inputTag.getPattern()
				.replace("dd", "%d")
				.replace("MM", "%m")
				.replace("yyyy", "%Y")
				.replace("HH", "%H")
				.replace("mm", "%M")
				.replace("ss", "%S");
	}

	public boolean isShowCalendarTime() {
		return inputTag.getPattern().contains("HH");
	}

	public boolean isShowCalendarSeconds() {
		return inputTag.getPattern().contains("ss");
	}

	public boolean isShowCalendar() {
		if (!ServiceFactory.getService(ViewConfig.class).isDefaultShowCalendar()) {
			return false;
		}
		DataGridTag dataGrid = inputTag.findParent2(DataGridTag.class, false);
		if (dataGrid != null && Util.booleans.isTrue(dataGrid.getDynaLine())) {
			//se tiver dentro de um datagrid que renderiza dynaline não funciona
			return false;
		}
		if ("true".equals(inputTag.getDAAtribute("nocalendar", true))) {
			return false;
		}
		Object readOnly = inputTag.getDAAtribute("readonly", false);
		if ("true".equals(readOnly) || "readonly".equals(readOnly)) {
			return false;
		}
		if (readOnly instanceof String) {
			if ("true".equalsIgnoreCase((String) readOnly) || "readonly".equalsIgnoreCase((String) readOnly)) {
				return false;
			}
		}
		return true;
	}

}
