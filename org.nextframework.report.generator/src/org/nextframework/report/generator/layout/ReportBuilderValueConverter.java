package org.nextframework.report.generator.layout;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.nextframework.core.standard.Next;
import org.nextframework.util.Util;

public class ReportBuilderValueConverter extends org.nextframework.report.renderer.ReportBuilderValueConverter {

	private Locale locale;
	private String[] trueFalseNullLabels;

	public ReportBuilderValueConverter(Locale locale) {
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
			return trueFalseNullLabels[2];
		} else if (obj instanceof Boolean) {
			if (((Boolean) obj)) {
				return trueFalseNullLabels[0];
			}
			return trueFalseNullLabels[1];
		} else if (obj instanceof String || obj instanceof Number ||
				obj instanceof Date || obj instanceof java.sql.Date ||
				obj instanceof Timestamp || obj instanceof Calendar) {
			return obj;
		}

		return Util.strings.toStringDescription(obj, locale);
	}

}