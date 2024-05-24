package org.nextframework.report.definition.elements;

import org.nextframework.report.definition.elements.style.ReportAlignment;

public class ReportLabel extends ReportTextElement {

	private Object content;
	private String type = "label";

	public ReportLabel(Object text, ReportAlignment alignment) {
		this(text);
		getStyle().setAlignment(alignment);
	}

	public ReportLabel(Object content) {
		this.content = content;
	}

	public ReportLabel(Object content, int width) {
		this.content = content;
		this.width = width;
	}

	public ReportLabel(String type, Object content) {
		this.type = type;
		this.content = content;
	}

	public ReportLabel(String type, Object content, int width) {
		this.content = content;
		this.type = type;
		this.width = width;
	}

	public void setContent(Object content) {
		this.content = content;
	}

	public Object getContent() {
		return content;
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
		return "Label: " + content;
	}

	@Override
	public String getDescriptionName() {
		return "Label";
	}

}
