package org.nextframework.report.renderer.jasper.builder;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.design.JasperDesign;

public class TemplateReader {

	private JasperDesign template;
	List<JRChild> templateElements = new ArrayList<JRChild>();

	public TemplateReader(JasperDesign template) {
		this.template = template;
	}

	public List<JRChild> getTitleElements() {
		return template.getTitle().getChildren();
	}

}
