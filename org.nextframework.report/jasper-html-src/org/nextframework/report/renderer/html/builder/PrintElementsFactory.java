package org.nextframework.report.renderer.html.builder;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintLine;
import net.sf.jasperreports.engine.type.BandTypeEnum;

import org.nextframework.report.renderer.jasper.builder.JasperDesignBuilder;
import org.nextframework.report.renderer.jasper.builder.MappedJasperPrint;
import org.nextframework.report.renderer.jasper.builder.MappedJasperReport;

public class PrintElementsFactory {

	private MappedJasperPrint mappedJasperPrint;

	private Map<String, MappedJasperReport> mappings;

	public PrintElementsFactory(MappedJasperPrint mappedJasperPrint) {
		this.mappedJasperPrint = mappedJasperPrint;
		this.mappings = getReportMappgins();
	}

	private int idCounter = 0;

	public PrintElement createPrintElement(JRPrintElement jrPrintElement, int pageIndex) {
		int pageHeight = mappedJasperPrint.getJasperPrint().getPageHeight();
		if (!renderElement(jrPrintElement)) {
			return null;
		}
		PrintElement printElement = newPrintElementInstance(jrPrintElement, pageIndex);
		printElement.setJrPrintElement(jrPrintElement);
		printElement.setUniqueId(nextId());
		printElement.setY(jrPrintElement.getY() + (pageIndex * pageHeight));
		String reportName = jrPrintElement.getOrigin().getReportName();
		if (reportName == null) {
			printElement.setMappedJasperReport(mappedJasperPrint.getMappedJasperReport());
		} else {
			printElement.setMappedJasperReport(mappings.get(reportName));
		}
		return printElement;
	}

	public PrintElement newPrintElementInstance(JRPrintElement jrPrintElement, int pageIndex) {
		if (jrPrintElement instanceof JRPrintFrame) {
			GroupPrintElements groupPrintElements = new GroupPrintElements();
			List<JRPrintElement> elements = ((JRPrintFrame) jrPrintElement).getElements();
			for (JRPrintElement subElement : elements) {
				PrintElement pe = createPrintElement(subElement, pageIndex);
				if (pe != null) {
					groupPrintElements.getPrintElements().add(pe);
				}
			}
			return groupPrintElements;
		}
		return new PrintElement();
	}

	public String nextId() {
		return "i" + (idCounter++);
	}

	private boolean renderElement(JRPrintElement element) {
		if (element.getKey() == null) {
			return false;
		}
		if (element.getOrigin().getBandTypeValue() == BandTypeEnum.TITLE) {
			return false;
		}
		if (element instanceof JRPrintLine) {
			Color color = ((JRPrintLine) element).getForecolor();
			if (JasperDesignBuilder.LINE_BREAK.equals(color)) {
				return false;
			}
		}
		if (element.getKey().startsWith(JasperDesignBuilder.BACKGROUND_FRAME_KEY)) {
			return false;
		}
		return true;
	}

	public Map<String, MappedJasperReport> getReportMappgins() {
		Map<String, MappedJasperReport> mappings = new HashMap<String, MappedJasperReport>();
		for (MappedJasperReport mappedJasperReport : mappedJasperPrint.getSubreports()) {
			String reportName = mappedJasperReport.getReportDefinition().getReportName();
			mappings.put(reportName, mappedJasperReport);
		}
		mappings.put(mappedJasperPrint.getMappedJasperReport().getReportDefinition().getReportName(), mappedJasperPrint.getMappedJasperReport());
		return mappings;
	}

}
