package org.nextframework.report.renderer.jasper;

public interface JasperRenderer {
	
	/**
	 * Determines the template that will be used by the JasperRenderer.<BR>
	 * The value of the parameter must be accessible trough the classpath.
	 * <pre>Usage: reportDefinition.addRenderParameter(JasperReportsRenderer.TEMPLATE, "myTemplate.jrxml");</pre>
	 */
	public static final String TEMPLATE = JasperRenderer.class.getName() + "." + "TEMPLATE";
	public static final String TEMPLATE_INPUTSTREAM = JasperRenderer.class.getName() + "." + "TEMPLATE_INPUTSTREAM";
	public static final String JASPERBUILDER_CLASS = JasperRenderer.class.getName() + "." + "JASPERBUILDER_CLASS";
}
