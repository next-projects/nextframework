package org.nextframework.report.definition.elements;

import org.nextframework.report.definition.elements.style.ReportAlignment;


public class ReportLabel extends ReportTextElement {

	String text;
	String type = "label";
	
	public ReportLabel(String text, ReportAlignment alignment) {
		this(text);
		getStyle().setAlignment(alignment);
	}
	public ReportLabel(String text) {
		this.text = text;
	}
	
	public ReportLabel(String text, int width) {
		this.text = text;
		this.width = width;
	}

	public ReportLabel(String type, String text) {
		this.type = type;
		this.text = text;
	}
	
	public ReportLabel(String type, String text, int width) {
		this.text = text;
		this.type = type;
		this.width = width;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	public String getType() {
		return type;
	}
	public ReportLabel setHeight(int height) {
		this.height = height;
		return this;
	}
	
	@Override
	public ReportLabel setColspan(int colspan) {
		return (ReportLabel) super.setColspan(colspan);
	}
	@Override
	public ReportLabel setWidth(int width) {
		return (ReportLabel) super.setWidth(width);
	}
	
	@Override
	public String toString() {
		return "Label: "+text;
	}
	@Override
	public String getDescriptionName() {
		return "Label";
	}
}
