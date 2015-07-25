package org.nextframework.report.renderer.html.builder;


public class HtmlItemCreator {
	/*
	public HtmlItem getHtmlItem(HtmlDesign design, KeyInfo keyInfo, int rowIndex, JRPrintElement jrPrintElement) {
		HtmlItem htmlItem = new HtmlItem();
		if(keyInfo != null){
			htmlItem.setColumn(keyInfo.getColumn());
			htmlItem.setColspan(keyInfo.getColspan());
		}
		htmlItem.setPixelX(jrPrintElement.getX());
		htmlItem.setPixelY(jrPrintElement.getY());
		htmlItem.setKey(jrPrintElement.getKey());
		htmlItem.setReportItem(design.getMappedJasperDesign().getMappedKeys().get(jrPrintElement.getKey()));
		htmlItem.setRow(rowIndex);
		htmlItem.setWidth(jrPrintElement.getWidth());
		
		if(jrPrintElement instanceof JRTemplatePrintText){
			JRTemplatePrintText jrTemplatePrintText = (JRTemplatePrintText) jrPrintElement;
			configurePrintText(design, htmlItem, jrTemplatePrintText);
		} else if(jrPrintElement instanceof JRTemplatePrintFrame){
			JRTemplatePrintFrame jrTemplatePrintFrame = (JRTemplatePrintFrame) jrPrintElement;
			configurePrintFrame(design, htmlItem, jrTemplatePrintFrame);
		} else if(jrPrintElement instanceof JRTemplatePrintLine){
			JRTemplatePrintLine jrTemplatePrintLine = (JRTemplatePrintLine) jrPrintElement;
			HtmlTag htmlTag = new HtmlTag("div");
			htmlTag.getStyle().put("height", "1px");
			htmlTag.getStyle().put("width", "100%");
			htmlTag.getStyle().put("border-bottom-width", (int)(jrTemplatePrintLine.getDefaultLineWidth()*1));
			htmlTag.getStyle().put("border-bottom-style", "solid");
			htmlTag.getStyle().put("border-bottom-color", "black");
			htmlTag.setInnerHTML("");
			htmlItem.setHtmlTag(htmlTag);
		} else if(jrPrintElement instanceof JRTemplatePrintRectangle){
			JRTemplatePrintRectangle jrTemplatePrintRectangle = (JRTemplatePrintRectangle) jrPrintElement;
			HtmlTag htmlTag = new HtmlTag("DIV");
			htmlTag.setInnerHTML(jrPrintElement.getClass()+" RECT "+(jrPrintElement).getKey());
			
			htmlItem.setPixelHeight(jrTemplatePrintRectangle.getHeight());
			htmlItem.setHtmlTag(htmlTag);
		} else {
			HtmlTag htmlTag = new HtmlTag("span");
			htmlTag.setInnerHTML((jrPrintElement).getKey());
			htmlItem.setHtmlTag(htmlTag);
		}
		return htmlItem;
	}

	private void configurePrintFrame(HtmlDesign design, HtmlItem htmlItem, JRTemplatePrintFrame jrTemplatePrintFrame) {
		HtmlTag container = new HtmlTag("div");
		container.getStyle().put("float", "left");
		HtmlTag div = new HtmlTag("div");
		
		if(htmlItem.getReportItem() != null && !htmlItem.getReportItem().isWidthAuto() && ! htmlItem.getReportItem().isWidthPercent()){
			container.getStyle().put("width", htmlItem.getReportItem().getWidth());
		} else {
			JasperDesign jasperDesign = design.getMappedJasperDesign().getJasperDesign();
			int pageWidth = jasperDesign.getPageWidth();
			pageWidth -= jasperDesign.getLeftMargin() + jasperDesign.getRightMargin();
			int width = jrTemplatePrintFrame.getWidth();
			if(width == pageWidth){
				container.getStyle().put("width", "100%");
			} else {
				if(htmlItem.getReportItem() != null && (htmlItem.getReportItem().isWidthAuto() || htmlItem.getReportItem().isWidthPercent())){
					container.getStyle().put("width", "100%");
				} else {
					container.getStyle().put("width", width);
				}
			}
		}
		
		container.getChildren().add(div);
		
		List<JRPrintElement> elements = jrTemplatePrintFrame.getElements();
		int lastPixelY = 0;
		for (JRPrintElement jrPrintElement2 : elements) {
			HtmlItem child = getHtmlItem(design, getKeyInfo(jrPrintElement2), -1, jrPrintElement2);
			if(child.getPixelY() != lastPixelY){
				div = new HtmlTag("div");
				container.getChildren().add(div);
				
				lastPixelY = child.getPixelY();
			}
			div.getChildren().add(child.getHtmlTag());
		}
		htmlItem.setHtmlTag(container);
	}

	private void configurePrintText(HtmlDesign design, HtmlItem htmlItem, JRTemplatePrintText jrTemplatePrintText) {
		String fullText = jrTemplatePrintText.getFullText();
		
		StringBuilder builder = new StringBuilder();
		builder.append(fullText.trim());
		HtmlTag container = new HtmlTag("div");
		container.getStyle().put("float", "left");
		if(htmlItem.getReportItem() != null && !htmlItem.getReportItem().isWidthAuto() && ! htmlItem.getReportItem().isWidthPercent()){
			container.getStyle().put("width", htmlItem.getReportItem().getWidth());
		}
		
		configureItemWithParent(container, htmlItem);
		container.setInnerHTML(builder.toString());
		
		htmlItem.setHtmlTag(container);
	}

	public KeyInfo getKeyInfo(JRPrintElement jrPrintElement) {
		if(jrPrintElement.getKey() == null){
			return null;
		}
		return new KeyInfo(jrPrintElement.getKey());
	}
	
	public static void configureItemWithParent(HtmlTag parent, HtmlItem htmlItem) {
		ReportItem reportItem = htmlItem.getReportItem();
		if(reportItem instanceof ReportTextElement){
			ReportItemStyle style = ((ReportTextElement)reportItem).getStyle();
			if(Boolean.TRUE.equals(style.getBold())){
				parent.getStyle().put("font-weight", "bold");
			}
			if(style.getFontSize() != null){
				parent.getStyle().put("font-size", style.getFontSize());
			}
			if(style.getAlignment() != null){
				parent.getStyle().put("text-align", style.getAlignment().toString().toLowerCase());
			}
		}
	}*/
}
