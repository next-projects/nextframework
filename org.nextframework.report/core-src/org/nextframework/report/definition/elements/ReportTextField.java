package org.nextframework.report.definition.elements;

import org.nextframework.report.definition.elements.style.ReportAlignment;


public class ReportTextField extends ReportTextElement {

	String expression;
	String pattern;
	String patternExpression;
	String type = "field";
		
	public ReportTextField(String expression, ReportAlignment alignment) {
		this(expression);
		getStyle().setAlignment(alignment);
	}
	
	public ReportTextField(String expression) {
		this.expression = expression;
	}

	public ReportTextField(String type, String expression) {
		this.type = type;
		this.expression = expression;
	}
	
	public ReportTextField(String expression, int width) {
		this(expression);
		setWidth(width);
	}
	public ReportTextField(String type, String expression, int width) {
		this.type = type;
		this.expression = expression;
		this.width = width;
	}
	
	/**
	 * Returns if the expression is a field reference. Otherwise it is a parameter reference.
	 * The expression is a parameter reference when the expression starts with "param."
	 * @return
	 */
	public boolean isFieldReference(){
		return expression != null && !expression.startsWith("param.") && !isLiteral();
	}
	
	public boolean isLiteral(){
		return expression != null && (expression.contains("\"") || expression.contains("\'"));
	}
	
	public String getExpression() {
		return expression;
	}
	
	public void setExpression(String expression) {
		this.expression = expression;
	}

	public String getPatternExpression() {
		return patternExpression;
	}

	public ReportTextField setPatternExpression(String patternExpression) {
		this.patternExpression = patternExpression;
		return this;
	}

	public String getType() {
		return type;
	}

	public String getPattern() {
		return pattern;
	}

	public ReportTextField setPattern(String pattern) {
		this.pattern = pattern;
		return this;
	}
	
	public ReportTextField setHeight(int height) {
		this.height = height;
		return this;
	}
	
	@Override
	public String toString() {
		return "TextField: "+expression;
	}

	@Override
	public String getDescriptionName() {
		return "TextField";
	}
}
