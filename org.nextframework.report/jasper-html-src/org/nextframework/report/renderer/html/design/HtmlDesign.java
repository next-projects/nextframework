package org.nextframework.report.renderer.html.design;

public class HtmlDesign {

	private HtmlTag tag = new HtmlTag("div");

	public HtmlTag getTag() {
		return tag;
	}

	public void add(HtmlTag tag) {
		tag.getChildren().add(tag);
	}

	@Override
	public String toString() {
		return tag.toString();
	}

}