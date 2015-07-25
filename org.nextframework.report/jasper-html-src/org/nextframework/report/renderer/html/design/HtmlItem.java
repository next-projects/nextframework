package org.nextframework.report.renderer.html.design;

import org.nextframework.report.definition.elements.ReportItem;

public class HtmlItem {

	int row;
	int column;
	int colspan = 1;
	String key;
	int pixelY;
	int pixelX;
	int pixelHeight = 0;
	int width;
	ReportItem reportItem;
	HtmlTag htmlTag;
	
	public int getPixelHeight() {
		return pixelHeight;
	}
	public void setPixelHeight(int pixelHeight) {
		this.pixelHeight = pixelHeight;
	}
	public HtmlTag getHtmlTag() {
		return htmlTag;
	}
	public void setHtmlTag(HtmlTag htmlTag) {
		this.htmlTag = htmlTag;
	}
	public ReportItem getReportItem() {
		return reportItem;
	}
	public void setReportItem(ReportItem reportItem) {
		this.reportItem = reportItem;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getPixelY() {
		return pixelY;
	}
	public int getPixelX() {
		return pixelX;
	}
	public void setPixelY(int pixelY) {
		this.pixelY = pixelY;
	}
	public void setPixelX(int pixelX) {
		this.pixelX = pixelX;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getRow() {
		return row;
	}
	public int getColumn() {
		return column;
	}
	public int getColspan() {
		return colspan;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public void setColspan(int colspan) {
		this.colspan = colspan;
	}
	@Override
	public String toString() {
		return htmlTag.toString();
	}
}
