package org.nextframework.report.generator.data;

import org.nextframework.report.definition.elements.style.ReportAlignment;

public interface FieldFormatter {

	String format(Object o);
	
	ReportAlignment getAlignment();
}
