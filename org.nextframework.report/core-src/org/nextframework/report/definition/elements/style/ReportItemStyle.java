package org.nextframework.report.definition.elements.style;

import java.awt.Color;

public class ReportItemStyle extends ReportBasicStyle {

	private Integer fontSize;
	private Boolean bold;
	private Boolean italic;
	private String design;

	public ReportItemStyle() {
	}

	public ReportItemStyle(boolean bold) {
		this.bold = bold;
	}

	public ReportItemStyle(int fontSize) {
		this.fontSize = fontSize;
	}

	public ReportItemStyle setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
		return this;
	}

	public ReportItemStyle setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}

	public Integer getFontSize() {
		return fontSize;
	}

	public ReportItemStyle setFontSize(Integer fontSize) {
		this.fontSize = fontSize;
		return this;
	}

	public Boolean getBold() {
		return bold;
	}

	public Boolean getItalic() {
		return italic;
	}

	public ReportItemStyle setBold(Boolean bold) {
		this.bold = bold;
		return this;
	}

	public ReportItemStyle setItalic(Boolean italic) {
		this.italic = italic;
		return this;
	}

	public String getDesign() {
		return design;
	}

	public void setDesign(String design) {
		this.design = design;
	}

	@Override
	public ReportItemStyle setAlignment(ReportAlignment alignment) {
		return (ReportItemStyle) super.setAlignment(alignment);
	}

	@Override
	public ReportItemStyle setBorderBottom(Border borderBottom) {
		return (ReportItemStyle) super.setBorderBottom(borderBottom);
	}

	@Override
	public ReportItemStyle setBorderLeft(Border borderLeft) {
		return (ReportItemStyle) super.setBorderLeft(borderLeft);
	}

	@Override
	public ReportItemStyle setBorderRight(Border borderRight) {
		return (ReportItemStyle) super.setBorderRight(borderRight);
	}

	@Override
	public ReportItemStyle setBorderTop(Border borderTop) {
		return (ReportItemStyle) super.setBorderTop(borderTop);
	}

	@Override
	public ReportItemStyle setPaddingBottom(int paddingBottom) {
		return (ReportItemStyle) super.setPaddingBottom(paddingBottom);
	}

	@Override
	public ReportItemStyle setPaddingLeft(int paddingLeft) {
		return (ReportItemStyle) super.setPaddingLeft(paddingLeft);
	}

	@Override
	public ReportItemStyle setPaddingRight(int paddingRight) {
		return (ReportItemStyle) super.setPaddingRight(paddingRight);
	}

	@Override
	public ReportItemStyle setPaddingTop(int paddingTop) {
		return (ReportItemStyle) super.setPaddingTop(paddingTop);
	}

	public ReportItemStyle setPadding(int padding) {
		setPaddingBottom(padding);
		setPaddingLeft(padding);
		setPaddingRight(padding);
		setPaddingTop(padding);
		return this;
	}

}
