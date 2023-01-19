package org.nextframework.report.renderer.html.design;

import org.nextframework.report.renderer.jasper.builder.MappedJasperPrint;

public class HtmlDesign {

	MappedJasperPrint mappedJasperPrint;
	HtmlTag tag = new HtmlTag("div");

	public HtmlDesign(MappedJasperPrint mappedJasperPrint) {
		this.mappedJasperPrint = mappedJasperPrint;
	}

	public void setTag(HtmlTag tag) {
		this.tag = tag;
	}

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