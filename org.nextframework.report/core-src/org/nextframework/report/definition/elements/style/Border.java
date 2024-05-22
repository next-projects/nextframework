package org.nextframework.report.definition.elements.style;

import java.awt.Color;

public class Border {

	int width = 0;
	Color color = Color.black;

	public Border(int width, Color color) {
		this.width = width;
		this.color = color;
	}

	public Border(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public Color getColor() {
		return color;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}
