package org.nextframework.report.renderer.jasper.builder;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.elements.ReportItem;

public class MappedJasperReport {

	JasperReport jasperReport;
	JasperDesign jasperDesign;
	ReportDefinition reportDefinition;
	
	Map<String, ReportItem> mappedKeys = new LinkedHashMap<String, ReportItem>();
	Map<String, JRDesignElement> mappedKeysJRElements = new LinkedHashMap<String, JRDesignElement>();
	
	public JasperReport getJasperReport() {
		return jasperReport;
	}
	public void setJasperReport(JasperReport jasperReport) {
		this.jasperReport = jasperReport;
	}
	public ReportDefinition getReportDefinition() {
		return reportDefinition;
	}
	public void setReportDefinition(ReportDefinition reportDefinition) {
		this.reportDefinition = reportDefinition;
	}
	public JasperDesign getJasperDesign() {
		return jasperDesign;
	}
	public Map<String, ReportItem> getMappedKeys() {
		return mappedKeys;
	}
	public Map<String, JRDesignElement> getMappedKeysJRElements() {
		return mappedKeysJRElements;
	}
	public void setJasperDesign(JasperDesign jasperDesign) {
		this.jasperDesign = jasperDesign;
	}
	public void setMappedKeys(Map<String, ReportItem> mappedKeys) {
		this.mappedKeys = mappedKeys;
	}
	public void setMappedKeysJRElements(Map<String, JRDesignElement> mappedKeysJRElements) {
		this.mappedKeysJRElements = mappedKeysJRElements;
	}
	
}
