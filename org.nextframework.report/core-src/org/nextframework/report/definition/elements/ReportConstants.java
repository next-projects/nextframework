package org.nextframework.report.definition.elements;

public interface ReportConstants {

	int CONFIG_CONSTANT = 1 << 31;

	int AUTO_WIDTH = CONFIG_CONSTANT | 1 << 30;

	int AUTO_HEIGHT = CONFIG_CONSTANT | 1 << 30;

	int PERCENT_WIDTH = CONFIG_CONSTANT | 1 << 29;

}
