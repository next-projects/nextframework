package org.nextframework.report.renderer.jasper.builder;

import java.util.Map;

import net.sf.jasperreports.engine.JRChild;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.elements.ReportItem;

public abstract class AbstractJasperDesignBuilder implements JasperDesignBuilder {

	protected static interface ElementFinder<E extends JRDesignElement> {

		boolean collect(E e);

	}

	protected ReportDefinition definition;
	protected JasperDesign template;

	public AbstractJasperDesignBuilder() {
	}

	public ReportDefinition getDefinition() {
		return definition;
	}

	public void setDefinition(ReportDefinition definition) {
		this.definition = definition;
	}

	public JasperDesign getTemplate() {
		return template;
	}

	public void setTemplate(JasperDesign template) {
		this.template = template;
	}

	@Override
	public MappedJasperDesign getMappedJasperDesign() {
		MappedJasperDesign mappedJasperDesign = new MappedJasperDesign();
		mappedJasperDesign.setJasperDesign(getJasperDesign());
		mappedJasperDesign.setReportDefinition(definition);
		mappedJasperDesign.setMappedKeys(getMappedKeys());
		mappedJasperDesign.setMappedKeysJRElements(getMappedKeysJRElements());
		return mappedJasperDesign;
	}

	protected abstract Map<String, JRDesignElement> getMappedKeysJRElements();

	protected abstract Map<String, ReportItem> getMappedKeys();

	protected abstract JasperDesign getJasperDesign();

	protected String getDesign(JRChild jrChild) {
		String designProperty = ((JRDesignElement) jrChild).getPropertiesMap().getProperty("design");
		return designProperty != null ? designProperty : "default";
	}

}
