package org.nextframework.report.renderer.html.builder;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;

import org.apache.commons.codec.binary.Base64;
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
import org.nextframework.report.definition.elements.style.ReportBasicStyle;
import org.nextframework.report.definition.elements.style.ReportItemStyle;
import org.nextframework.report.renderer.html.builder.config.HtmlReportViewConfigurator;
import org.nextframework.report.renderer.html.design.HtmlDesign;
import org.nextframework.report.renderer.html.design.HtmlTag;
import org.nextframework.report.renderer.jasper.JasperReportsRenderer;
import org.nextframework.report.renderer.jasper.builder.ChartDrawRenderer;
import org.nextframework.report.renderer.jasper.builder.MappedJasperPrint;
import org.nextframework.report.renderer.jasper.builder.MappedJasperReport;
import org.nextframework.service.ServiceFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.imageio.plugins.png.PNGMetadata;

import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintImage;
import net.sf.jasperreports.engine.Renderable;
import net.sf.jasperreports.engine.fill.JRTemplatePrintFrame;
import net.sf.jasperreports.engine.fill.JRTemplatePrintLine;
import net.sf.jasperreports.engine.fill.JRTemplatePrintText;
import net.sf.jasperreports.engine.type.ImageTypeEnum;
import net.sf.jasperreports.engine.type.ModeEnum;

public class HtmlReportBuilderImpl implements HtmlReportBuilder {

	private static long UID_SEQUENCE = 0;

	public HtmlReportViewConfigurator getConfigurator() {
		return ServiceFactory.getService(HtmlReportViewConfigurator.class);
	}

	@Override
	public HtmlDesign getHtmlDesign(ReportDefinition definition) {
		definition.setParameter(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
		MappedJasperPrint mappedJasperPrint = JasperReportsRenderer.renderAsMappedJasperPrint(definition);
		return createHtmlDesign(mappedJasperPrint);
	}

	@Override
	public HtmlDesign getHtmlDesign(ReportItem item) {

		ReportDefinition definition = new ReportDefinition();
		definition.addItem(item, definition.getSectionDetail(), 0);
		definition.setData(Arrays.asList(new Object()));
		definition.setReportName("RI_" + (UID_SEQUENCE++));

		MappedJasperPrint mappedJasperPrint = JasperReportsRenderer.renderAsMappedJasperPrint(definition);

		HtmlDesign htmlDesign = new HtmlDesign();
		UnpagedJasperPrint2 unpagedJasperPrint = new UnpagedJasperPrint2(mappedJasperPrint);
		List<PrintElement> elements = unpagedJasperPrint.getPrintElements();
		HtmlTag tag = htmlDesign.getTag();
		for (PrintElement element : elements) {
			tag.getChildren().add(getHtmlTagForElement(element));
		}

		return htmlDesign;
	}

	private HtmlDesign createHtmlDesign(MappedJasperPrint mappedJasperPrint) {

		ReportDefinition reportDefinition = mappedJasperPrint.getMappedJasperReport().getReportDefinition();

		HtmlDesign htmlDesign = new HtmlDesign();
		HtmlTag htmlTag = htmlDesign.getTag();
		htmlTag.getAttributes().put("id", reportDefinition.getReportName());

//		UnpagedJasperPrint unpagedJasperPrint = new UnpagedJasperPrint(mappedJasperPrint);
		UnpagedJasperPrint2 unpagedJasperPrint2 = new UnpagedJasperPrint2(mappedJasperPrint);
		List<PrintElement> elements = unpagedJasperPrint2.getPrintElements();

		printElements(htmlTag, reportDefinition, elements);

		HtmlTag javascript = new HtmlTag("script");
		javascript.getAttributes().put("type", "text/javascript");

		javascript.setInnerHTML("enableReport('" + reportDefinition.getReportName() + "');");
		if (reportDefinition.getParameters().get(ENALBE_SUBREPORT) != null) {
			javascript.setInnerHTML("enableReport('" + reportDefinition.getParameters().get(ENALBE_SUBREPORT) + "');");
		}

		htmlTag.getChildren().add(javascript);

		return htmlDesign;
	}

	private void printElements(HtmlTag containerTag, ReportDefinition definition, List<PrintElement> elements) {

		containerTag.getAttributes().put("id", definition.getReportName());
		containerTag.getAttributes().put("data-report-name", definition.getReportName());

		HtmlTag table = new HtmlTag("table");
		containerTag.getChildren().add(table);
		getConfigurator().configureTable(containerTag, table);

		int maxRow = getMaxRow(elements) + 1;
		Map<Integer, Map<Integer, PrintElement>> elementsRowsColsMap = getElementsMap(elements);
		Integer fullwidth = getFullWidth(definition);
		SerialMap serialMap = getSerialMapForDefinition(definition);

		ReportSection lastSection = null;
		for (int row = 0; row < maxRow; row++) {

			Map<Integer, PrintElement> elementsColsMap = elementsRowsColsMap.get(row);
			if (elementsColsMap == null) {
				continue;
			}

			HtmlTag tr = new HtmlTag("tr");
			ReportSection trSection = null;

			int colspan = 1;
			for (int column = 0; column < definition.getColumns().size(); column += colspan) {

				HtmlTag td = new HtmlTag("td");
				td.getAttributes().put("valign", "top");

				PrintElement element = elementsColsMap.get(column);
				if (element != null) {

					ReportItem reportItem = element.getReportItem();

					trSection = reportItem.getRow().getSection();
					boolean isFirstRowOfSection = lastSection == null || !lastSection.equals(trSection);
					boolean isFirstRowOfBlock = lastSection == null || !lastSection.equals(trSection);
					getConfigurator().configureTr(tr, reportItem.getRow().getStyleClass(), trSection, isFirstRowOfSection, isFirstRowOfBlock);

					lastSection = trSection;
					serialMap.setCurrentObjectLevel(trSection);

					colspan = element.getColspan() != null ? element.getColspan() : 1;
					if (colspan == 1) {
						if (!definition.getColumn(column).isWidthAuto() && column > 0) {
							//first column has auto width
							td.getStyle().put("width", ((Math.round((definition.getColumn(column).getWidth() / fullwidth.doubleValue()) * 10000)) / 100.0) + "%");
						}
					}

					HtmlTag tag = getHtmlTagForElement(element);
					td.getChildren().add(tag);

					Color backgroundColor = reportItem.getStyle().getBackgroundColor();
					if (reportItem instanceof ReportOverlapComposite) {
						JRPrintFrame frame = (JRPrintFrame) element.getJrPrintElement();
						if (frame.getElements().size() > 0) {
							backgroundColor = frame.getElements().get(0).getBackcolor();
						}
					}
					if (backgroundColor == null) {
						backgroundColor = trSection.getStyle().getBackgroundColor();
					}
					if (backgroundColor != null) {
						td.getStyle().put("background-color", toRgb(backgroundColor));
					}

					Color foregroundColor = reportItem.getStyle().getForegroundColor();
					if (foregroundColor == null) {
						foregroundColor = trSection.getStyle().getForegroundColor();
					}
					if (foregroundColor != null) {
						td.getStyle().put("color", toRgb(foregroundColor));
					}

					ReportBasicStyle style = reportItem.getStyle();
					if (style.getAlignment() != null) {
						td.getStyle().put("text-align", style.getAlignment().toString().toLowerCase());
					}
					if (style.getPaddingLeft() > 0) {
						td.getStyle().put("padding-left", style.getPaddingLeft() + "px");
					}
					if (style.getPaddingRight() > 0) {
						td.getStyle().put("padding-right", style.getPaddingRight() + "px");
					}

					moveStyle(tag, td, "padding-left");
					moveStyle(tag, td, "padding-right");
					moveStyle(tag, td, "padding-top");
					moveStyle(tag, td, "padding-bottom");
					moveStyle(tag, td, "border-left");
					moveStyle(tag, td, "border-right");
					moveStyle(tag, td, "border-top");
					moveStyle(tag, td, "border-bottom");

					getConfigurator().configureTd(td);

				} else {
					colspan = 1;
				}

				if (colspan > 1) {
					td.getAttributes().put("colspan", colspan);
				}

				tr.getChildren().add(td);

			}

			if (trSection != null) {
				List<Integer> levelFor = serialMap.getLevelFor(trSection);
				tr.getAttributes().put("hierarchy", join(levelFor.iterator(), ","));
				tr.getAttributes().put("hierarchydepth", levelFor.size());
				table.getChildren().add(tr);
			}

		}

	}

	private int getMaxRow(List<PrintElement> elements) {
		int maxRow = 0;
		for (PrintElement printElement : elements) {
			if (maxRow < printElement.getRow()) {
				maxRow = printElement.getRow();
			}
		}
		return maxRow;
	}

	private Map<Integer, Map<Integer, PrintElement>> getElementsMap(List<PrintElement> elements) {
		Map<Integer, Map<Integer, PrintElement>> rowsColsMap = new HashMap<Integer, Map<Integer, PrintElement>>();
		for (PrintElement printElement : elements) {
			if (printElement.getColumn() != null) {
				Map<Integer, PrintElement> colsMap = rowsColsMap.get(printElement.getRow());
				if (colsMap == null) {
					colsMap = new HashMap<Integer, PrintElement>();
					rowsColsMap.put(printElement.getRow(), colsMap);
				}
				colsMap.put(printElement.getColumn(), printElement);
			}
		}
		return rowsColsMap;
	}

	private Integer getFullWidth(ReportDefinition definition) {
		Integer fullwidth = definition.getStyle().getPageWidth();
		if (fullwidth == null) {
			fullwidth = 0;
			for (int column = 0; column < definition.getColumns().size(); column++) {
				if (!definition.getColumn(column).isWidthAuto()) {
					fullwidth += definition.getColumn(column).getWidth();
				}
			}
		}
		return fullwidth;
	}

	private SerialMap getSerialMapForDefinition(ReportDefinition definition) {
		SerialMap map = new SerialMap();
		map.setLevel(definition.getSectionTitle(), 0);
		map.setLevel(definition.getSectionPageHeader(), 0);
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
		map.setLevel(definition.getSectionSummaryDataHeader(), 0);
		map.setLevel(definition.getSectionSummaryDataDetail(), 0);
		map.setLevel(definition.getSectionFirstPageHeader(), 0);
		map.setLevel(definition.getSectionLastPageFooter(), 0);
		map.setLevel(definition.getSectionSummary(), 0);
		return map;
	}

	private HtmlTag getHtmlTagForElement(PrintElement printElement) {
		HtmlTag htmlTag;
		if (printElement instanceof GroupPrintElements && printElement.getReportItem() instanceof Subreport) {
			htmlTag = new HtmlTag("div");
			GroupPrintElements group = (GroupPrintElements) printElement;
			printElements(htmlTag, group.getGroupDefinition(), group.getPrintElements());
		} else {
			htmlTag = createHtmlTag(printElement, printElement.getMappedJasperReport());
		}
		return htmlTag;
	}

	private HtmlTag createHtmlTag(PrintElement printElement, MappedJasperReport mappedJasperReport) {

		JRPrintElement jrPrintElement = printElement.getJrPrintElement();
		if (jrPrintElement instanceof JRTemplatePrintText) {

			return createHtmlTextTag((JRTemplatePrintText) jrPrintElement, mappedJasperReport);

		} else if (jrPrintElement instanceof JRTemplatePrintFrame) {

			return createHtmlFrameTag((GroupPrintElements) printElement, (JRTemplatePrintFrame) jrPrintElement);

		} else if (jrPrintElement instanceof JRTemplatePrintLine) {

			return new HtmlTag(null);

		} else if (jrPrintElement instanceof JRPrintImage) {

			return createHtmlImageTag(printElement, (JRPrintImage) jrPrintElement, mappedJasperReport);

		}

		return createUnknownTag(jrPrintElement);
	}

	private HtmlTag createHtmlTextTag(JRTemplatePrintText jrPrintElement, MappedJasperReport mappedJasperReport) {

		HtmlTag tag = new HtmlTag("div");

		String fullText = removeWhiteSpaces(jrPrintElement.getFullText());
		tag.setInnerHTML(fullText);

		ReportItem reportItem = mappedJasperReport.getMappedKeys().get(jrPrintElement.getKey());
		if (reportItem != null && fullText != null && !fullText.isEmpty()) {

			if (reportItem instanceof ReportLabel) {
				String text = ((ReportLabel) reportItem).getText();
				text = removeWhiteSpaces(text);
				tag.setInnerHTML(text);
			}

			if (reportItem instanceof ReportTextElement) {

				ReportTextElement reportTextElement = ((ReportTextElement) reportItem);

				ReportItemStyle style = reportTextElement.getStyle();
				if (Boolean.TRUE.equals(style.getBold())) {
					tag.getStyle().put("font-weight", "bold");
				}
				if (style.getForegroundColor() != null) {
					tag.getStyle().put("color", toRgb(style.getForegroundColor()));
				}
				if (style.getBackgroundColor() != null) {
					tag.getStyle().put("background-color", toRgb(style.getBackgroundColor()));
				}
				if (style.getFontSize() != null) {
					tag.getStyle().put("font-size", (style.getFontSize() + 4) + "px");
				}
				if (style.getAlignment() != null) {
					tag.getStyle().put("text-align", style.getAlignment().toString());
				}

				configureBorder(tag, style.getBorderBottom(), "bottom");
				configureBorder(tag, style.getBorderLeft(), "left");
				configureBorder(tag, style.getBorderRight(), "right");
				configureBorder(tag, style.getBorderTop(), "top");

				configurePadding(tag, style.getPaddingBottom(), "bottom");
				configurePadding(tag, style.getPaddingLeft(), "left");
				configurePadding(tag, style.getPaddingRight(), "right");
				configurePadding(tag, style.getPaddingTop(), "top");

			}

		}

		getConfigurator().configureTextTag(tag);

		return tag;
	}

	private String removeWhiteSpaces(String fullText) {
		if (fullText != null) {
			fullText = fullText.replace("\n", "<BR>");
			if (fullText.startsWith(" ")) {
				fullText = "&nbsp;" + fullText.substring(1);
			}
			if (fullText.equals("")) {
				fullText = "&nbsp;";
			}
		}
		return fullText;
	}

	private HtmlTag createHtmlFrameTag(GroupPrintElements group, JRTemplatePrintFrame jrPrintElement) {

		HtmlTag tag = new HtmlTag("div");

		List<PrintElement> elements = group.getPrintElements();
		int lastY = 0;
		int totalWidth = jrPrintElement.getWidth();

		for (Iterator<PrintElement> iterator = elements.iterator(); iterator.hasNext();) {
			PrintElement subElement = iterator.next();

			HtmlTag subTag = getHtmlTagForElement(subElement);

			tag.getChildren().add(subTag);

			if (!subTag.getTagName().equals("img")) {
				subTag.getStyle().put("width", ((int) Math.floor(subElement.getJrPrintElement().getWidth() * 100.0 / totalWidth)) + "%");
			}
			subTag.getStyle().put("float", "left");
			if (subElement.getJrPrintElement().getY() != lastY) {
				subTag.getStyle().put("clear ", "left");
			}

			lastY = subElement.getJrPrintElement().getY();

		}

		if (jrPrintElement.getModeValue() == ModeEnum.OPAQUE) {
			tag.getStyle().put("background-color", toRgb(jrPrintElement.getBackcolor()));
		}

		tag.getAttributes().put("data-report-key", jrPrintElement.getKey());

		getConfigurator().configureFrameTag(tag);

		return tag;
	}

	private HtmlTag createHtmlImageTag(PrintElement printElement, JRPrintImage jrPrintImage, MappedJasperReport mappedJasperReport) {

		HtmlTag tag = new HtmlTag("div");

		ReportItem reportItem = mappedJasperReport.getMappedKeys().get(jrPrintImage.getKey());
		if (reportItem instanceof ReportChart) {

			tag = new HtmlTag("div");
			String id = printElement.getMappedJasperReport().getReportDefinition().getReportName() + "_" + jrPrintImage.getSourceElementId() + "_" + printElement.getUniqueId();

			Renderable renderer = jrPrintImage.getRenderable();
			if (renderer instanceof ChartDrawRenderer) {

				//byte[] chartDataDecoded = Base64.decode(chartData);
				Chart chart = ((ChartDrawRenderer) renderer).getChart();
				chart.setId("chart" + id);
				tag.setInnerHTML("<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>" +
						"<script type=\"text/javascript\">" + ChartRendererFactory.getRendererForOutput(ChartRendererGoogleTools.TYPE).renderChart(chart) + "</script>" +
						"<div id=\"" + "chart" + id + "\"></div>");

			} else {

				try {

					String chartData = null;

					byte[] imageData = renderer.getImageData(DefaultJasperReportsContext.getInstance());
					if (imageData != null) {

						ImageReader imageReader = ImageIO.getImageReadersByFormatName("png").next();
						imageReader.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(imageData)), true);
						IIOMetadata metadata = imageReader.getImageMetadata(0);
						com.sun.imageio.plugins.png.PNGMetadata pngmeta = (PNGMetadata) metadata;
						NodeList childNodes = pngmeta.getStandardTextNode().getChildNodes();

						for (int i = 0; i < childNodes.getLength(); i++) {
							Node node = childNodes.item(i);
							String keyword = node.getAttributes().getNamedItem("keyword").getNodeValue();
							String value = node.getAttributes().getNamedItem("value").getNodeValue();
							if ("chart-google-data".equals(keyword)) {
								chartData = value;
							}
						}

					}

					if (chartData != null) {
						tag.setInnerHTML("<script type=\"text/javascript\" src=\"https://www.google.com/jsapi\"></script>" +
								"<script type=\"text/javascript\">" + chartData.replace("%ID%", "chart" + id) + "</script>" +
								"<div id=\"" + "chart" + id + "\"></div>");
					} else {
						tag.setInnerHTML(" ");
					}

				} catch (Exception e) {
					tag.setInnerHTML(e.getMessage());
				}

			}

		}

		if (tag.getInnerHTML() == null) {

			tag = new HtmlTag("img");

			try {
				Renderable renderer = jrPrintImage.getRenderable();
				if (renderer != null) {
					String encode = new String(Base64.encodeBase64(renderer.getImageData(DefaultJasperReportsContext.getInstance())));
					if (renderer.getImageTypeValue() == ImageTypeEnum.PNG) {
						tag.getAttributes().put("src", "data:image/png;base64," + encode);
					} else if (renderer.getImageTypeValue() == ImageTypeEnum.JPEG) {
						tag.getAttributes().put("src", "data:image/jpeg;base64," + encode);
					} else if (renderer.getImageTypeValue() == ImageTypeEnum.GIF) {
						tag.getAttributes().put("src", "data:image/gif;base64," + encode);
					}
				}
			} catch (JRException e) {
				tag.setInnerHTML(e.getMessage());
			}

		}

		tag.getAttributes().put("data-report-key", jrPrintImage.getKey());

		getConfigurator().configureImageTag(tag);

		return tag;
	}

	private HtmlTag createUnknownTag(JRPrintElement jrPrintElement) {
		HtmlTag tag = new HtmlTag("div");
		tag.setInnerHTML(jrPrintElement.getClass().getSimpleName());
		tag.getAttributes().put("data-report-key", jrPrintElement.getKey());
		return tag;
	}

	private String toRgb(Color backgroundColor) {
		return "#" + twoDigits(Integer.toHexString(backgroundColor.getRed())) +
				twoDigits(Integer.toHexString(backgroundColor.getGreen())) +
				twoDigits(Integer.toHexString(backgroundColor.getBlue()));
	}

	private String twoDigits(String hexString) {
		if (hexString.length() == 1) {
			return "0" + hexString;
		}
		return hexString;
	}

	public void configureBorder(HtmlTag tag, Border b, String type) {
		if (b != null && b.getWidth() > 0) {
			tag.getStyle().put("border-" + type, b.getWidth() + "px solid " + toRgb(b.getColor()));
		}
	}

	public void configurePadding(HtmlTag tag, Integer padding, String type) {
		if (padding != null && padding > 0) {
			tag.getStyle().put("padding-" + type, padding + "px ");
		}
	}

	public void moveStyle(HtmlTag from, HtmlTag to, String styleName) {
		Object value = from.getStyle().remove(styleName);
		if (value != null) {
			to.getStyle().put(styleName, value);
		}
	}

	private String join(Iterator<Integer> iterator, String separator) {
		StringBuilder builder = new StringBuilder();
		while (iterator.hasNext()) {
			builder.append(iterator.next());
			if (iterator.hasNext()) {
				builder.append(separator);
			}
		}
		return builder.toString();
	}

}
