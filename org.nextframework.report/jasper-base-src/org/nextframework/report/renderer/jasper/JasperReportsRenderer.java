package org.nextframework.report.renderer.jasper;

import java.io.InputStream;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.renderer.ReportRendererFactory;
import org.nextframework.report.renderer.jasper.builder.JasperDesignBuilder;
import org.nextframework.report.renderer.jasper.builder.MappedJasperDesign;
import org.nextframework.report.renderer.jasper.builder.MappedJasperPrint;
import org.nextframework.report.renderer.jasper.builder.MappedJasperReport;
import org.springframework.beans.BeanUtils;

@SuppressWarnings("unchecked")
public class JasperReportsRenderer extends AbstractJasperReportsRenderer implements JasperRenderer {

	static {
		ReportRendererFactory.registerRenderer(new JasperReportsRenderer(JasperReportsRenderer.JRXML));
		ReportRendererFactory.registerRenderer(new JasperReportsRenderer(JasperReportsRenderer.JASPER_DESIGN));
		ReportRendererFactory.registerRenderer(new JasperReportsRenderer(JasperReportsRenderer.JASPER_PRINT));
		ReportRendererFactory.registerRenderer(new JasperReportsRenderer(JasperReportsRenderer.JASPER_REPORT));
		ReportRendererFactory.registerRenderer(new JasperReportsRenderer(JasperReportsRenderer.MAPPED_JASPER_DESIGN));
		ReportRendererFactory.registerRenderer(new JasperReportsRenderer(JasperReportsRenderer.MAPPED_JASPER_PRINT));
		ReportRendererFactory.registerRenderer(new JasperReportsRenderer(JasperReportsRenderer.MAPPED_JASPER_REPORT));
		ReportRendererFactory.registerRenderer(new JasperReportsRenderer(JasperReportsRenderer.PDF));
	}

	private static JasperDesignBuilder instantiateBuilder(String string) {
		try {
			return (JasperDesignBuilder) Class.forName(string).newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String TEMPLATE_DEFAULT = (JasperReportsRenderer.class.getName() + "Template").replace('.', '/') + ".jrxml";

	public JasperReportsRenderer(String output) {
		super(output);
	}

	@Override
	public JasperDesign convertToJasperDesign(ReportDefinition report) throws JRException {
		MappedJasperDesign mappedJasperDesign = getOutputAsMappedJasperDesign(report);
		return mappedJasperDesign.getJasperDesign();
	}

	@Override
	public JasperReport convertToJasperReport(JasperDesign jasperDesign) throws JRException {
		return JasperCompileManager.compileReport(jasperDesign);
	}

	@Override
	protected MappedJasperDesign getOutputAsMappedJasperDesign(ReportDefinition report) {
		JasperDesign design = readTemplateDesign(getParameter(report, TEMPLATE, String.class),
				getParameter(report, TEMPLATE_INPUTSTREAM, InputStream.class),
				TEMPLATE_DEFAULT);
		JasperDesignBuilder builder = createJasperBuilder(report, getParameter(report, JASPERBUILDER_CLASS, Class.class));
		builder.setDefinition(report);
		builder.setTemplate(design);
		MappedJasperDesign mappedJasperDesign = builder.getMappedJasperDesign();
		return mappedJasperDesign;
	}

	public MappedJasperReport getOutputAsMappedJasperReport(MappedJasperDesign mappedJasperDesign) throws JRException {
		MappedJasperReport mappedJasperReport = new MappedJasperReport();
		mappedJasperReport.setReportDefinition(mappedJasperDesign.getReportDefinition());
		mappedJasperReport.setJasperDesign(mappedJasperDesign.getJasperDesign());
		mappedJasperReport.setMappedKeys(mappedJasperDesign.getMappedKeys());
		mappedJasperReport.setMappedKeysJRElements(mappedJasperDesign.getMappedKeysJRElements());
		mappedJasperReport.setJasperReport(convertToJasperReport(mappedJasperDesign.getJasperDesign()));
		return mappedJasperReport;
	}

	public MappedJasperPrint getOutputAsMappedJasperPrint(MappedJasperReport mappedJasperReport) throws JRException {
		ReportDefinition definition = mappedJasperReport.getReportDefinition();
		JasperReport jasperReport = mappedJasperReport.getJasperReport();
		JasperDataConverter converter = new JasperDataConverter();
		JasperDataParametersResult parametersMap = converter.getParametersMap(definition);
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametersMap.getParameters(), converter.getDataSource(definition));
		MappedJasperPrint mappedJasperPrint = new MappedJasperPrint();
		mappedJasperPrint.setJasperPrint(jasperPrint);
		mappedJasperPrint.setMappedJasperReport(mappedJasperReport);
		mappedJasperPrint.setSubreports(parametersMap.getSubreports());
		return mappedJasperPrint;
	}

	public JasperDesignBuilder createJasperBuilder(ReportDefinition report, Class<? extends JasperDesignBuilder> parameterClass) {
		if (parameterClass != null) {
			return BeanUtils.instantiate(parameterClass);
		}
		return instantiateBuilder("org.nextframework.report.renderer.jasper.builder.JasperDesignBuilderImpl");
	}

	static <E> E getParameter(ReportDefinition report, String parameter, Class<E> class1) {
		Object value = report.getRenderParameters().get(parameter);
		if (value != null && !(class1.isAssignableFrom(value.getClass()))) {
			throw new IllegalArgumentException("Report parameter '" + parameter + "' must be of type " + class1.getName() + ". Value found: " + parameter);
		}
		return (E) value;
	}

	static JasperDesign readTemplateDesign(String template, InputStream inputStream, String defaultTemplate) {
		if (inputStream == null) {
			if (template != null) {
				inputStream = JasperReportsRenderer.class.getClassLoader().getResourceAsStream(template);
			} else {
				inputStream = JasperReportsRenderer.class.getClassLoader().getResourceAsStream(defaultTemplate);
			}
		}
		if (inputStream == null) {
			throw new RuntimeException("No templates found on path " + (template != null ? template : defaultTemplate));
		}
		try {
			return JRXmlLoader.load(inputStream);
		} catch (JRException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected MappedJasperPrint getOutputAsMappedJasperPrint(ReportDefinition report) throws JRException {
		return getOutputAsMappedJasperPrint(getOutputAsMappedJasperReport(report));
	}

	@Override
	protected MappedJasperReport getOutputAsMappedJasperReport(ReportDefinition report) throws JRException {
		return getOutputAsMappedJasperReport(getOutputAsMappedJasperDesign(report));
	}

	@Override
	protected byte[] getOutputAsPdf(JasperReport jasperReport, ReportDefinition report) throws JRException {
		JasperDataConverter converter = new JasperDataConverter();
		JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, converter.getParametersMap(report).getParameters(), converter.getDataSource(report));
		return JasperExportManager.exportReportToPdf(jasperPrint);
	}

}
