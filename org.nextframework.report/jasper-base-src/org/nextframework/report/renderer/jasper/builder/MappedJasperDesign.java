package org.nextframework.report.renderer.jasper.builder;

import java.util.LinkedHashMap;
import java.util.Map;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.elements.ReportItem;

import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JasperDesign;

public class MappedJasperDesign {

	JasperDesign jasperDesign;
	ReportDefinition reportDefinition;

	Map<String, ReportItem> mappedKeys = new LinkedHashMap<String, ReportItem>();
	Map<String, JRDesignElement> mappedKeysJRElements = new LinkedHashMap<String, JRDesignElement>();

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
