package org.nextframework.report.renderer.html.builder;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.Renderable;
import net.sf.jasperreports.engine.fill.JRTemplatePrintFrame;
import net.sf.jasperreports.engine.fill.JRTemplatePrintLine;
import net.sf.jasperreports.engine.fill.JRTemplatePrintText;
import net.sf.jasperreports.engine.type.ImageTypeEnum;
import net.sf.jasperreports.engine.type.ModeEnum;

import org.nextframework.chart.Chart;
import org.nextframework.chart.ChartRendererFactory;
import org.nextframework.chart.google.ChartRendererGoogleTools;
import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportGroup;
import org.nextframework.report.definition.ReportSection;
import org.nextframework.report.definition.elements.ReportChart;
import org.nextframework.report.definition.elements.ReportItem;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportOverlapComposite;
import org.nextframework.report.definition.elements.ReportTextElement;
import org.nextframework.report.definition.elements.Subreport;
import org.nextframework.report.definition.elements.style.Border;
import org.nextframework.report.definition.elements.style.ReportAlignment;
import org.nextframework.report.definition.elements.style.ReportBasicStyle;
import org.nextframework.report.definition.elements.style.ReportItemStyle;
import org.nextframework.report.renderer.html.design.HtmlDesign;
import org.nextframework.report.renderer.html.design.HtmlTag;
import org.nextframework.report.renderer.jasper.JasperReportsRenderer;
import org.nextframework.report.renderer.jasper.builder.ChartDrawRenderer;
import org.nextframework.report.renderer.jasper.builder.MappedJasperPrint;
import org.nextframework.report.renderer.jasper.builder.MappedJasperReport;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.imageio.plugins.png.PNGMetadata;
import com.sun.org.apache.xml.internal.security.utils.Base64;

public class HtmlReportBuilderImpl implements HtmlReportBuilder {
	
	private static long UID_SEQUENCE = 0;

	@Override
	public HtmlDesign getHtmlDesign(ReportDefinition definition) {
		return createHtmlDesign(JasperReportsRenderer.renderAsMappedJasperPrint(definition));
	}
	
	@Override
	public HtmlDesign getHtmlDesign(ReportItem item) {
		ReportDefinition definition = new ReportDefinition();
		definition.addItem(item, definition.getSectionDetail(), 0);
		definition.setData(Arrays.asList(new Object()));
		definition.setReportName("RI_"+(UID_SEQUENCE++));
		
		MappedJasperPrint mappedJasperPrint = JasperReportsRenderer.renderAsMappedJasperPrint(definition);
		
		HtmlDesign htmlDesign = new HtmlDesign(mappedJasperPrint);
		UnpagedJasperPrint2 unpagedJasperPrint = new UnpagedJasperPrint2(mappedJasperPrint);
		List<PrintElement> elements = unpagedJasperPrint.getPrintElements();
		HtmlTag tag = htmlDesign.getTag();
		for (PrintElement element : elements) {
			tag.getChildren().add(getHtmlTagForElement(element));
		}
		
		return htmlDesign;
	}

	private HtmlDesign createHtmlDesign(MappedJasperPrint mappedJasperPrint) {
		HtmlDesign htmlDesign = new HtmlDesign(mappedJasperPrint);
		UnpagedJasperPrint unpagedJasperPrint = new UnpagedJasperPrint(mappedJasperPrint);
		UnpagedJasperPrint2 unpagedJasperPrint2 = new UnpagedJasperPrint2(mappedJasperPrint);
//		System.out.println(unpagedJasperPrint2.printElements);
		List<PrintElement> elements = unpagedJasperPrint2.getPrintElements();
		HtmlTag tag = htmlDesign.getTag();
		tag.getAttributes().put("id", mappedJasperPrint.getMappedJasperReport().getReportDefinition().getReportName());
		//tag.getStyle().put("width", "900px");
		tag.getStyle().put("font-family", "verdana");
		HtmlTag linkCss = new HtmlTag("link");
		linkCss.getAttributes().put("href", "./"+ mappedJasperPrint.getMappedJasperReport().getReportDefinition().getReportName()+".css");
		linkCss.getAttributes().put("type", "text/css");
		linkCss.getAttributes().put("rel", "StyleSheet");
//		tag.getChildren().add(linkCss);
		
		HtmlTag linkJavascript = new HtmlTag("script");
		linkJavascript.getAttributes().put("language", "javascript");
		linkJavascript.getAttributes().put("src", "./"+ mappedJasperPrint.getMappedJasperReport().getReportDefinition().getReportName()+".js");
		
//		tag.getChildren().add(linkJavascript);
		//<link rel="StyleSheet"        href="${app}/resource/css/default.css" type="text/css">	
		//printElements2(tag, mappedJasperPrint.getMappedJasperReport().getReportDefinition(), elements);
		printElements(tag, mappedJasperPrint.getMappedJasperReport().getReportDefinition(), elements);
		
		HtmlTag javascript = new HtmlTag("script");
		javascript.getAttributes().put("type", "text/javascript");
		
		
		ReportDefinition reportDefinition = mappedJasperPrint.getMappedJasperReport().getReportDefinition();
		javascript.setInnerHTML("enableReport('"+reportDefinition.getReportName()+"');");
		if(reportDefinition.getParameters().get(ENALBE_SUBREPORT) != null) {
			javascript.setInnerHTML("enableReport('"+reportDefinition.getParameters().get(ENALBE_SUBREPORT)+"');");
		}
		
		tag.getChildren().add(javascript);
		
		return htmlDesign;
	}
	
	private void printElements(HtmlTag tag, ReportDefinition definition, List<PrintElement> elements) {
		Integer fullwidth = definition.getStyle().getPageWidth();
		if (fullwidth == null) {
			fullwidth = 0;
			for (int column = 0; column < definition.getColumns().size(); column++) {
				if (!definition.getColumn(column).isWidthAuto()) {
					fullwidth += definition.getColumn(column).getWidth();
				}
			}
		}
//		fullwidth = fullwidth < 555 ? 555 : fullwidth;
		
		SerialMap serialMap = getSerialMapForDefinition(definition);
		tag.getAttributes().put("data-report-name", definition.getReportName());
		tag.getAttributes().put("id", definition.getReportName());
		HtmlTag table = new HtmlTag("table");
		table.getStyleClass().add("report-table");
		table.getAttributes().put("cellspacing", "0");
		table.getAttributes().put("cellpadding", "0");
		table.getStyle().put("width", "100%");
		table.getStyle().put("border-collapse", "collapse");
		//table.getAttributes().put("border", 1);
		tag.getChildren().add(table);
		int maxRow = getMaxRow(elements)+1;
		
		ReportSection lastSection = null;
		for (int row = 0; row < maxRow; row++) {
			HtmlTag tr = new HtmlTag("tr");
			ReportSection trSection = null;
			int colspan = 1;
			for (int column = 0; column < definition.getColumns().size(); column+=colspan) {
				//ORIGINAL PLACE OF CODE A
				
				HtmlTag td = new HtmlTag("td");
				td.getAttributes().put("valign", "top");
				
				PrintElement element = getElementForColumnAndRow(elements, column, row);
				
				if(element != null){
					colspan = element.getColspan() != null? element.getColspan() : 1;
					
					if(colspan == 1){
						if(!definition.getColumn(column).isWidthAuto() && column > 0){
							//first column has auto width
							td.getStyle().put("width", ((Math.round((definition.getColumn(column).getWidth()/fullwidth.doubleValue())*10000))/100.0) + "%");
						}
					}
					
					trSection = element.getReportItem().getRow().getSection();
					if(lastSection == null || !lastSection.equals(trSection)){
						tr.getStyleClass().add("firstRowOfSection"+trSection.getType());
					}
					if(element.getReportItem().getRow().getRowNumber() == 0){
						tr.getStyleClass().add("firstRowOfBlock"+trSection.getType());
					}
					lastSection = trSection;
					serialMap.setCurrentObjectLevel(trSection);
					HtmlTag htmlTag = getHtmlTagForElement(element);
					td.getChildren().add(htmlTag);
					if(element.getReportItem() != null){
						ReportItem reportItem = element.getReportItem();
						tr.getStyleClass().add(reportItem.getRow().getSection().getType().toString());
						if(reportItem.getRow() == null){
							td.getStyle().put("background-color", "#FFAAAA");
						} else {
							Color backgroundColor =  reportItem.getStyle().getBackgroundColor();
							if(reportItem instanceof ReportOverlapComposite){
								JRPrintFrame frame = (JRPrintFrame) element.getJrPrintElement();
								if(frame.getElements().size() > 0){
									backgroundColor = frame.getElements().get(0).getBackcolor();
								}
							}
							if(backgroundColor != null){
								td.getStyle().put("background-color", toRgb(backgroundColor));
							} else {
								backgroundColor = reportItem.getRow().getSection().getStyle().getBackgroundColor();
								if(backgroundColor != null){
									td.getStyle().put("background-color", toRgb(backgroundColor));
								}
							}
							Color foregroundColor = reportItem.getStyle().getForegroundColor();
							if(foregroundColor != null){
								td.getStyle().put("color", toRgb(foregroundColor));
							} else {
								foregroundColor = reportItem.getRow().getSection().getStyle().getForegroundColor();
								if(foregroundColor != null){
									td.getStyle().put("color", toRgb(foregroundColor));
								}
							}
							
							if(reportItem.getRow().getStyleClass() != null){
								tr.getStyleClass().add(reportItem.getRow().getStyleClass());
							}
						}
						
						ReportBasicStyle style = reportItem.getStyle();
						int paddingLeft = style.getPaddingLeft();
						int paddingRight = style.getPaddingRight();
						if(paddingLeft > 0){
							td.getStyle().put("padding-left", paddingLeft+"px");
						}
						if(paddingRight > 0){
							td.getStyle().put("padding-right", paddingRight+"px");
						}
						moveStyle(htmlTag, td, "border-left");
						moveStyle(htmlTag, td, "border-right");
						moveStyle(htmlTag, td, "border-top");
						moveStyle(htmlTag, td, "border-bottom");
						
						moveStyle(htmlTag, td, "padding-left");
						moveStyle(htmlTag, td, "padding-right");
						moveStyle(htmlTag, td, "padding-top");
						moveStyle(htmlTag, td, "padding-bottom");

						if(style instanceof ReportItemStyle){
							ReportAlignment alignment = ((ReportItemStyle) style).getAlignment();
							if(alignment != null){
								td.getStyle().put("text-align", alignment.toString().toLowerCase());
							}
						}
					}
				} else {
					colspan = 1;
				}
				if(colspan > 1){
					td.getAttributes().put("colspan", colspan);
				}
				tr.getChildren().add(td);
			}
			if(trSection != null){
				List<Integer> levelFor = serialMap.getLevelFor(trSection);
				tr.getAttributes().put("hierarchy", join(levelFor.iterator(), ","));
				tr.getAttributes().put("hierarchydepth", levelFor.size());
				table.getChildren().add(tr);
			}
		}
		
//		if(row == 0){ CODE A
		
		
//		HtmlTag tr = new HtmlTag("tr");
//		for (int column = 0; column < definition.getColumns().size(); column++) {
//			HtmlTag td = new HtmlTag("td");
//			if(!definition.getColumn(column).isWidthAuto() && column < definition.getColumns().size()-1){
//				td.getStyle().put("width", Math.round((definition.getColumn(column).getWidth()/fullwidth.doubleValue())*100) + "%");
//			}
//			tr.getChildren().add(td);
//		}
//		table.getChildren().add(tr);
	}

	public void moveStyle(HtmlTag from, HtmlTag to, String styleName) {
		Object value = from.getStyle().remove(styleName);
		if(value != null){
			to.getStyle().put(styleName, value);
		}
	}

	private String join(Iterator<Integer> iterator, String separator) {
		StringBuilder builder = new StringBuilder();
		while(iterator.hasNext()){
			builder.append(iterator.next());
			if(iterator.hasNext()){
				builder.append(separator);
			}
		}
		return builder.toString();
	}

	private SerialMap getSerialMapForDefinition(ReportDefinition definition) {
		SerialMap map = new SerialMap();
		map.setLevel(definition.getSectionLastPageFooter(), 0);
		map.setLevel(definition.getSectionSummary(), 0);
		map.setLevel(definition.getSectionTitle(), 0);
		map.setLevel(definition.getSectionFirstPageHeader(), 0);
		map.setLevel(definition.getSectionPageHeader(), 0);
		map.setLevel(definition.getSectionSummaryDataHeader(), 0);
		map.setLevel(definition.getSectionSummaryDataDetail(), 0);
		
		map.setLevel(definition.getSectionPageFooter(), 0);
		map.setLevel(definition.getSectionColumnHeader(), 0);
		map.setLevel(definition.getSectionColumnFooter(), 0);
		List<ReportGroup> groups = definition.getGroups();
		for (int i = 0; i < groups.size(); i++) {
			map.setLevel(groups.get(i).getSectionHeader(), i);
			map.setLevel(groups.get(i).getSectionDetail(), i);
			map.setLevel(groups.get(i).getSectionFooter(), i);
		}
		map.setLevel(definition.getSectionDetailHeader(), groups.size());
		map.setLevel(definition.getSectionDetail(), groups.size());
		
		return map;
	}

	private String toRgb(Color backgroundColor) {
		return "#"+twoDigits(Integer.toHexString(backgroundColor.getRed())) +
				   twoDigits(Integer.toHexString(backgroundColor.getGreen())) +
				   twoDigits(Integer.toHexString(backgroundColor.getBlue()));
	}
	
	private String twoDigits(String hexString) {
		if(hexString.length() == 1){
			return "0"+hexString;
		}
		return hexString;
	}

	private PrintElement getElementForColumnAndRow(List<PrintElement> elements, int column, int row) {
		PrintElement element = null; 
		for (PrintElement printElement : elements) {
			if(printElement.getColumn() != null && printElement.getColumn().equals(column) && printElement.getRow() == row){
				element = printElement;
				break;
			}
		}
		return element;
	}

	private HtmlTag getHtmlTagForElement(PrintElement printElement) {
		HtmlTag htmlTag;
		if(printElement instanceof GroupPrintElements && printElement.getReportItem() instanceof Subreport){
			htmlTag = new HtmlTag("div");
			GroupPrintElements group = (GroupPrintElements) printElement;
			//htmlTag.getStyle().put("padding-left", "10px");
			printElements(htmlTag, group.getGroupDefinition(), group.getPrintElements());
		} else {
			htmlTag = createHtmlTag(printElement);
		}
		return htmlTag;
	}

	private int getMaxRow(List<PrintElement> elements) {
		int maxRow = 0;
		for (PrintElement printElement : elements) {
			if(maxRow < printElement.getRow()){
				maxRow = printElement.getRow();
			}
		}
		return maxRow;
	}
	
	private HtmlTag createHtmlTag(PrintElement printElement) {
//		ReportDefinition source = printElement.getSource();
//		tag.setInnerHTML(
//				"<div style='width: 40px; float:left'>" + (printElement.getRow()) + "</div> " + 
//				"<div style='width: 40px; float:left'>" + printElement.getColumn() + "</div> "+ 
//				"<div style='width: 220px; float:left'>"+ printElement.getJrPrintElement().getClass().getSimpleName() + "</div> " + 
//				"<div style='width: 220px; float:left'>"+ (source != null ? source.getReportName() : "NULL") + "</div> " + 
//				"<div style='width: 100px; float:left'>CY: " + printElement.getY()+ "</div> " + 
//				"<div style='width: 100px; float:left'>OY: " + printElement.getJrPrintElement().getY() + "</div> "+
//				"<div style='width: 500px; '>" + printElement.getJrPrintElement().getKey() + "</div> ");
		HtmlTag tag = createHtmlTag(printElement, printElement.getMappedJasperReport());
		return tag;
	}

	private HtmlTag createHtmlTag(PrintElement printElement, MappedJasperReport mappedJasperReport) {
		JRPrintElement jrPrintElement = printElement.getJrPrintElement();
		HtmlTag tag = new HtmlTag("div");
		if(jrPrintElement instanceof JRTemplatePrintText){
			String fullText = ((JRTemplatePrintText)jrPrintElement).getFullText();
			fullText = removeWhiteSpaces(fullText);
			tag.setInnerHTML(fullText);
			if(fullText != null && !fullText.isEmpty()){//fullText should not be null, this is probably a mistake in the production of the report by the developer
				ReportItem reportItem = mappedJasperReport.getMappedKeys().get(jrPrintElement.getKey());
				if(reportItem != null){
					if(reportItem instanceof ReportLabel){
						String text = ((ReportLabel) reportItem).getText();
						text = removeWhiteSpaces(text);
						tag.setInnerHTML(text);
					}
					if(reportItem instanceof ReportTextElement){
						ReportTextElement reportTextElement = ((ReportTextElement)reportItem);
						ReportItemStyle style = reportTextElement.getStyle();
						if(Boolean.TRUE.equals(style.getBold())){
							tag.getStyle().put("font-weight", "bold");
						}
						if(style.getForegroundColor() != null){
							tag.getStyle().put("color", toRgb(style.getForegroundColor()));
						}
						if(style.getBackgroundColor() != null){
							tag.getStyle().put("background-color", toRgb(style.getBackgroundColor()));
						}
						if(style.getFontSize() != null){
							tag.getStyle().put("font-size", (style.getFontSize()+4)+"px");
						}
						if(style.getAlignment() != null){
							tag.getStyle().put("text-align", style.getAlignment().toString());
						}
						class BorderConfig{
							HtmlTag tag;
							public BorderConfig(HtmlTag tag) {
								this.tag = tag;
							}
							public void configure(Border b, String type){
								if(b != null && b.getWidth() > 0){
									tag.getStyle().put("border-"+type, b.getWidth()+"px solid "+toRgb(b.getColor()));
								}
							}
						}
						class PaddingConfig {
							HtmlTag tag;
							public PaddingConfig(HtmlTag tag) {
								this.tag = tag;
							}
							public void configure(Integer padding, String type){
								if(padding != null && padding > 0){
									tag.getStyle().put("padding-"+type, padding+"px ");
								}
							}
						}
						BorderConfig config = new BorderConfig(tag);
						config.configure(style.getBorderBottom(), "bottom");
						config.configure(style.getBorderLeft(),   "left");
						config.configure(style.getBorderRight(),  "right");
						config.configure(style.getBorderTop(),    "top");
						
						PaddingConfig pconfig = new PaddingConfig(tag);
						pconfig.configure(style.getPaddingBottom(), "bottom");
						pconfig.configure(style.getPaddingLeft(),   "left");
						pconfig.configure(style.getPaddingRight(),  "right");
						pconfig.configure(style.getPaddingTop(),    "top");
					}
				}
			}
		} else if(jrPrintElement instanceof JRTemplatePrintFrame){
//			ReportItem reportItem = mappedJasperReport.getMappedKeys().get(jrPrintElement.getKey());
			//if it is a printFrame the printElement must be a groupelement
			GroupPrintElements group = (GroupPrintElements) printElement;
			List<PrintElement> elements = group.getPrintElements();
			int lastY = 0;
			int totalWidth = jrPrintElement.getWidth();
//			HtmlTag lastSubTag = new HtmlTag(null);
			for (Iterator<PrintElement> iterator = elements.iterator(); iterator.hasNext();) {
				PrintElement subElement = iterator.next();
				HtmlTag subTag = getHtmlTagForElement(subElement);
				tag.getChildren().add(subTag);
				if(!subTag.getTagName().equals("img")){
					subTag.getStyle().put("width", ((int)Math.floor(subElement.getJrPrintElement().getWidth() *100.0 / totalWidth))+"%");
				}
				subTag.getStyle().put("float", "left");
				if(subElement.getJrPrintElement().getY() != lastY){
					subTag.getStyle().put("clear ", "left");
				}
//				lastSubTag = subTag;
				lastY = subElement.getJrPrintElement().getY(); 
			}
			if(jrPrintElement.getModeValue() == ModeEnum.OPAQUE){
				tag.getStyle().put("background-color", toRgb(jrPrintElement.getBackcolor()));
			}
			tag.getAttributes().put("data-report-key", jrPrintElement.getKey());
		} else if(jrPrintElement instanceof JRTemplatePrintLine){
			return new HtmlTag(null);
		} else if(jrPrintElement instanceof JRPrintImage){
			ReportItem reportItem = mappedJasperReport.getMappedKeys().get(jrPrintElement.getKey());
			boolean chartRendered = false;
			if(reportItem instanceof ReportChart) {
				tag = new HtmlTag("div");
				tag.setInnerHTML("CHART");
				
				JRPrintImage jrPrintImage = (JRPrintImage) jrPrintElement;
				//tag.getStyle().put("width", jrPrintImage.getWidth() + "px");
				//tag.getStyle().put("height", jrPrintImage.getHeight() + "px");
				Renderable renderer = jrPrintImage.getRenderable();
				
				try {
					byte[] imageData = renderer.getImageData(DefaultJasperReportsContext.getInstance());

					
					String id = printElement.getMappedJasperReport().getReportDefinition().getReportName() + "_" + jrPrintElement.getSourceElementId() + "_" + printElement.getUniqueId();
					tag = new HtmlTag("div");
	                if(renderer instanceof ChartDrawRenderer){
	                	//byte[] chartDataDecoded = Base64.decode(chartData);
	                	Chart chart = ((ChartDrawRenderer) renderer).getChart();
						chart.setId("chart"+id);
	                	tag.setInnerHTML("<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>" +
	                			"<script type=\"text/javascript\">"+ChartRendererFactory.getRendererForOutput(ChartRendererGoogleTools.TYPE).renderChart(chart)+"</script>" +
	                			"<div id=\""+"chart"+id+"\"></div>");
	                	chartRendered = true;
	                } else {
	                	
						String chartData = null;
						if(imageData != null){
							ImageReader imageReader = ImageIO.getImageReadersByFormatName("png").next();
							
							imageReader.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(imageData)), true);
							
							// read metadata of first image
							IIOMetadata metadata = imageReader.getImageMetadata(0);
							
							com.sun.imageio.plugins.png.PNGMetadata pngmeta = (PNGMetadata) metadata;
							NodeList childNodes = pngmeta.getStandardTextNode().getChildNodes();
							
							for (int i = 0; i < childNodes.getLength(); i++) {
								Node node = childNodes.item(i);
								String keyword = node.getAttributes().getNamedItem("keyword").getNodeValue();
								String value = node.getAttributes().getNamedItem("value").getNodeValue();
								if("chart-google-data".equals(keyword)){
									chartData = value;
								}
							}
						}
						
						if(chartData != null) {
							tag.setInnerHTML("<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>" +
									"<script type=\"text/javascript\">"+chartData.replace("%ID%", "chart"+id)+"</script>" +
									"<div id=\""+"chart"+id+"\"></div>");
							chartRendered = true;
						}
	                	
	                }
				} catch (Exception e) {
					e.printStackTrace();
				}
			} 
			if(!chartRendered){
				tag = new HtmlTag("img");
				
				JRPrintImage jrPrintImage = (JRPrintImage) jrPrintElement;
				//tag.getStyle().put("width", jrPrintImage.getWidth() + "px");
				//tag.getStyle().put("height", jrPrintImage.getHeight() + "px");
				Renderable renderer = jrPrintImage.getRenderable();
				try {
					if(renderer != null){
						String encode = Base64.encode(renderer.getImageData(DefaultJasperReportsContext.getInstance()));
						if(renderer.getImageTypeValue() == ImageTypeEnum.PNG){
							tag.getAttributes().put("src", "data:image/png;base64,"+encode);
						} else if(renderer.getImageTypeValue() == ImageTypeEnum.JPEG){
							tag.getAttributes().put("src", "data:image/jpeg;base64,"+encode);
						} else if(renderer.getImageTypeValue() == ImageTypeEnum.GIF){
							tag.getAttributes().put("src", "data:image/gif;base64,"+encode);
						}
					}
				} catch (JRException e) {
					
				}
			}
			tag.getAttributes().put("data-report-key", jrPrintElement.getKey());
		} else {
			tag.setInnerHTML(jrPrintElement.getClass().getSimpleName());
			tag.getAttributes().put("data-report-key", jrPrintElement.getKey());
		}
		return tag;
	}

	private String removeWhiteSpaces(String fullText) {
		if(fullText != null){
			fullText = fullText.replace("\n", "<BR>");
			if(fullText.startsWith(" ")){
				fullText = "&nbsp;" + fullText.substring(1);
			}
			if(fullText.equals("")){
				fullText = "&nbsp;";
			}
		}
		return fullText;
	}

	@SuppressWarnings("unused")
	private void printElements2(HtmlTag tag, ReportDefinition definition, List<PrintElement> elements) {
		int i = 0;
		for (PrintElement printElement : elements) {
			HtmlTag div = new HtmlTag("div");
			ReportDefinition source = printElement.getSource();
			div.setInnerHTML(
					"<div style='width: 40px; float:left; font-size: 10px'>" + (i++) + "</div> " + 
					"<div style='width: 80px; float:left; font-size: 10px'> row: " + printElement.getRow() + "</div> " + 
					"<div style='width: 80px; float:left; font-size: 10px'> row (ri): " + printElement.getReportItem().getRow() + "</div> " + 
					"<div style='width: 120px; float:left; font-size: 10px'>"+ printElement.getJrPrintElement().getOrigin().getReportName() + "</div> " + 
					"<div style='width: 220px; float:left; font-size: 10px'>"+ (source != null ? source.getReportName() : "NULL") + "</div> " + 
					"<div style='width: 100px; float:left'>CY: " + printElement.getY()+ "</div> " + 
					"<div style='width: 100px; float:left'>OY: " + printElement.getJrPrintElement().getY() + "</div> "+ 
					"<div style='width: 500px; '>" + printElement.getJrPrintElement().getKey() + "</div> ");
			tag.getChildren().add(div);
//			if(printElement.getJrPrintElement() instanceof JRPrintFrame){
//				HtmlTag innerDiv = new HtmlTag("div");
//				innerDiv.getStyle().put("padding-left", "200px");
//				
//				List<JRPrintElement> innerElements = ((JRPrintFrame)printElement.getJrPrintElement()).getElements();
//				for (JRPrintElement jrPrintElement : innerElements) {
//					HtmlTag e = new HtmlTag("div");
//					innerDiv.getChildren().add(e);
//					e.setInnerHTML("<div style='float:left; width: 100px'>"+jrPrintElement.getClass().getSimpleName().substring(15) + "</div>"+
//							"<div style='float:left; width: 450px; font-size: 10px'>"+definition.getReportName()+" <> "+
//							jrPrintElement.getOrigin().getReportName() + "</div>"+jrPrintElement.getKey());
//				}
//				tag.getChildren().add(innerDiv);
//			}
			if (printElement instanceof GroupPrintElements) {
				HtmlTag subDiv = new HtmlTag("div");
				subDiv.getStyle().put("margin-left", "10px");
				subDiv.getStyle().put("border", "1px solid blue");
				if(printElement.getReportItem() instanceof Subreport){
					subDiv.getStyle().put("border", "1px solid red");
				}
				printElements2(subDiv, ((GroupPrintElements)printElement).getGroupDefinition(), ((GroupPrintElements) printElement).getPrintElements());
				tag.getChildren().add(subDiv);
			}
		}
	}
/*
	private HtmlDesign createHtmlDesign(JasperPrint jasperPrint, MappedJasperReport mappedJasperDesign) {
		HtmlItemCreator htmlItemCreator = new HtmlItemCreator();
		HtmlDesign htmlDesign = new HtmlDesign();//(mappedJasperDesign);
		List<JRPrintPage> pages = jasperPrint.getPages();
		int rowIndex = -1;
		int lastRowY = -1;
		KeyInfo lastKeyInfo = null;
		for (JRPrintPage jrPrintPage : pages) {
			List<JRPrintElement> elements = jrPrintPage.getElements();
			for (JRPrintElement jrPrintElement : elements) {
				if(jrPrintElement.getKey() != null){
					KeyInfo keyInfo = htmlItemCreator.getKeyInfo(jrPrintElement);
					if(keyInfo.getReportSectionType() != ReportSectionType.TITLE){
						int currentRowY = jrPrintElement.getY();
						if(currentRowY != lastRowY){
							rowIndex++;
						} else if(lastKeyInfo != null){
							if(lastKeyInfo.getRowIndex() != keyInfo.getRowIndex()){
								rowIndex++;
							}
						}
						//System.out.println("row "+rowIndex+" " +keyInfo.getColumn());
						//System.out.println(jrPrintElement + " \t "+jrPrintElement.getY() + "   \t" + jrPrintElement.getX()+"  "+jrPrintElement.getKey());
						HtmlItem htmlItem = htmlItemCreator.getHtmlItem(htmlDesign, keyInfo, rowIndex, jrPrintElement);
						//System.out.println(htmlItem);
						htmlDesign.addHtmlItem(htmlItem);
						
						
						lastKeyInfo = keyInfo;
						lastRowY = currentRowY;
					}
				}
			}
		}
		return htmlDesign;
	}
*/

}
