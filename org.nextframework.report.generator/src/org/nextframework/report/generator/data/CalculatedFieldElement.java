package org.nextframework.report.generator.data;

public class CalculatedFieldElement {

	public static final String FORMAT_AS_NUMBER = "number";

	private String name;
	private String expression;
	private String displayName;
	private String formatAs;
	private String formatTimeDetail;
	private String processors;

	public CalculatedFieldElement(String name, String expression, String displayName, String formatAs, String formatTimeDetail, String processors) {
		this.name = name;
		this.expression = expression;
		this.displayName = displayName;
		this.formatAs = formatAs == null ? FORMAT_AS_NUMBER : formatAs;
		this.formatTimeDetail = formatTimeDetail == null ? "hours" : formatTimeDetail;
		this.processors = processors;
	}

	public String getFormatAs() {
		return formatAs;
	}

	public String getFormatTimeDetail() {
		return formatTimeDetail;
	}

	public String getName() {
		return name;
	}

	public String getExpression() {
		return expression;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getProcessors() {
		return processors;
	}

}
