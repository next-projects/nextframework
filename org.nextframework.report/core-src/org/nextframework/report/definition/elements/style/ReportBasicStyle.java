package org.nextframework.report.definition.elements.style;

import java.awt.Color;

public class ReportBasicStyle {
	
	Color backgroundColor = null;
	Color foregroundColor = null;
	
	Border borderLeft;
	Border borderRight;
	Border borderTop;
	Border borderBottom;

	int paddingLeft = 0;
	int paddingRight = 0;
	int paddingTop = 0;
	int paddingBottom = 0;
	
	ReportAlignment alignment;

	public ReportAlignment getAlignment() {
		return alignment;
	}
	public ReportBasicStyle setAlignment(ReportAlignment alignment) {
		this.alignment = alignment;
		return this;
	}
	
	public ReportBasicStyle setForegroundColor(Color foregroundColor) {
		this.foregroundColor = foregroundColor;
		return this;
	}
	
	public ReportBasicStyle setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}
	public Color getForegroundColor() {
		return foregroundColor;
	}
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
	public Border getBorderLeft() {
		return borderLeft;
	}
	public Border getBorderRight() {
		return borderRight;
	}
	public Border getBorderTop() {
		return borderTop;
	}
	public Border getBorderBottom() {
		return borderBottom;
	}
	public int getPaddingLeft() {
		return paddingLeft;
	}
	public int getPaddingRight() {
		return paddingRight;
	}
	public int getPaddingTop() {
		return paddingTop;
	}
	public int getPaddingBottom() {
		return paddingBottom;
	}
	public ReportBasicStyle setBorder(Border border) {
		setBorderBottom(border);
		setBorderTop(border);
		setBorderLeft(border);
		setBorderRight(border);
		return this;
	}
	public ReportBasicStyle setBorderLeft(Border borderLeft) {
		this.borderLeft = borderLeft;
		return this;
	}
	public ReportBasicStyle setBorderRight(Border borderRight) {
		this.borderRight = borderRight;
		return this;
	}
	public ReportBasicStyle setBorderTop(Border borderTop) {
		this.borderTop = borderTop;
		return this;
	}
	public ReportBasicStyle setBorderBottom(Border borderBottom) {
		this.borderBottom = borderBottom;
		return this;
	}
	public ReportBasicStyle setPaddingLeft(int paddingLeft) {
		this.paddingLeft = paddingLeft;
		return this;
	}
	public ReportBasicStyle setPaddingRight(int paddingRight) {
		this.paddingRight = paddingRight;
		return this;
	}
	public ReportBasicStyle setPaddingTop(int paddingTop) {
		this.paddingTop = paddingTop;
		return this;
	}
	public ReportBasicStyle setPaddingBottom(int paddingBottom) {
		this.paddingBottom = paddingBottom;
		return this;
	}
	public ReportBasicStyle setPadding(int padding) {
		setPaddingBottom(padding);
		setPaddingLeft(padding);
		setPaddingRight(padding);
		setPaddingTop(padding);
		return this;
	}

	
}
