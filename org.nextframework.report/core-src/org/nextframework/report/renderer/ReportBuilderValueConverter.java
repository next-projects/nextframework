package org.nextframework.report.renderer;

import java.util.Calendar;

public class ReportBuilderValueConverter implements ValueConverter {

	public Object apply(Object obj) {
		if (obj instanceof Calendar) {
			return ((Calendar) obj).getTime();
		}
		return obj;
	}

}
