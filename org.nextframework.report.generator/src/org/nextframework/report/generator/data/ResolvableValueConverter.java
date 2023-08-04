package org.nextframework.report.generator.data;

import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.nextframework.core.standard.Next;
import org.nextframework.report.renderer.ValueConverter;
import org.nextframework.util.Util;

public class ResolvableValueConverter extends PropertyEditorSupport implements ValueConverter {

	private Locale locale;
	private String[] trueFalseNullLabels;

	public ResolvableValueConverter(Locale locale) {
		this.locale = locale;
		this.trueFalseNullLabels = getTrueFalseNullLabels();
	}

	private String[] getTrueFalseNullLabels() {
		String trueString = Next.getMessageSource().getMessage(Util.objects.newMessage("org.nextframework.view.OutputTag.trueLabel", null, "Sim"), locale);
		String falseString = Next.getMessageSource().getMessage(Util.objects.newMessage("org.nextframework.view.OutputTag.falseLabel", null, "Não"), locale);
		String nullString = Next.getMessageSource().getMessage(Util.objects.newMessage("org.nextframework.view.OutputTag.nullLabel", null, ""), locale);
		return new String[] { trueString, falseString, nullString };
	}

	@Override
	public Object apply(Object obj) {
		if (obj == null) {
			return null;
		} else if (obj instanceof Boolean) {
			if (((Boolean) obj)) {
				return trueFalseNullLabels[0];
			}
			return trueFalseNullLabels[1];
		} else if (obj instanceof String || obj instanceof Number ||
				obj instanceof Date || obj instanceof java.sql.Date ||
				obj instanceof Timestamp) {
			return obj;
		} else if (obj instanceof Calendar) {
			return ((Calendar) obj).getTime();
		}
		return Util.strings.toStringDescription(obj, locale);
	}

	public String getAsText() {
		Object obj = apply(getValue());
		return obj != null ? String.valueOf(obj) : null;
	}

}