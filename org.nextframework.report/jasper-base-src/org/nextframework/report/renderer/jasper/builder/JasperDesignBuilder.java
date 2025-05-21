package org.nextframework.report.renderer.jasper.builder;

import java.awt.Color;

import net.sf.jasperreports.engine.design.JasperDesign;

import org.nextframework.report.definition.ReportDefinition;

public interface JasperDesignBuilder {

	public static final String BACKGROUND_FRAME_KEY = "BACKGROUND-FRAME";
	public static final Color LINE_BREAK = new Color(254, 1, 1);

	MappedJasperDesign getMappedJasperDesign();

	void setDefinition(ReportDefinition definition);

	void setTemplate(JasperDesign design);

}
