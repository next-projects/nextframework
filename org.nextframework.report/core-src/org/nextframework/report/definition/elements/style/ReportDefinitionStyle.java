package org.nextframework.report.definition.elements.style;

public class ReportDefinitionStyle implements Cloneable {

	String design = "default";

	Boolean noMargin = false;
	Boolean noTitle = false;

	Integer pageWidth;
	Integer pageHeight;

	public Integer getPageWidth() {
		return pageWidth;
	}

	public Integer getPageHeight() {
		return pageHeight;
	}

	public void setPageWidth(Integer pageWidth) {
		this.pageWidth = pageWidth;
	}

	public void setPageHeight(Integer pageHeight) {
		this.pageHeight = pageHeight;
	}

	public String getDesign() {
		return design;
	}

	public void setDesign(String design) {
		this.design = design;
	}

	@Override
	public ReportDefinitionStyle clone() {
		try {
			return (ReportDefinitionStyle) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public Boolean getNoMargin() {
		return noMargin;
	}

	public Boolean getNoTitle() {
		return noTitle;
	}

	public void setNoMargin(Boolean noMargin) {
		this.noMargin = noMargin;
	}

	public void setNoTitle(Boolean noTitle) {
		this.noTitle = noTitle;
	}

}
