package org.nextframework.report.renderer.jasper;

import java.io.ByteArrayOutputStream;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.renderer.ReportRenderer;
import org.nextframework.report.renderer.ReportRendererFactory;
import org.nextframework.report.renderer.jasper.builder.MappedJasperDesign;
import org.nextframework.report.renderer.jasper.builder.MappedJasperPrint;
import org.nextframework.report.renderer.jasper.builder.MappedJasperReport;

public abstract class AbstractJasperReportsRenderer implements ReportRenderer {

	public static final String PDF = "PDF";
	public static final String JRXML = "JRXML";
	public static final String JASPER_DESIGN = "JASPER_DESIGN";
	public static final String JASPER_REPORT = "JASPER_REPORT";
	public static final String JASPER_PRINT = "JASPER_PRINT";
	public static final String MAPPED_JASPER_DESIGN = "MAPPED_JASPER_DESIGN";
	public static final String MAPPED_JASPER_REPORT = "MAPPED_JASPER_REPORT";
	public static final String MAPPED_JASPER_PRINT = "MAPPED_JASPER_PRINT";

	private String output;

	public AbstractJasperReportsRenderer(String output) {
		this.output = output;
	}

	@Override
	public String getOutputType() {
		return output;
	}

	public static MappedJasperReport renderAsMappedJasperReport(ReportDefinition report) {
		return (MappedJasperReport) ReportRendererFactory.getRendererForOutput(MAPPED_JASPER_REPORT).renderReport(report);
	}

	public static MappedJasperPrint renderAsMappedJasperPrint(ReportDefinition report) {
		return (MappedJasperPrint) ReportRendererFactory.getRendererForOutput(MAPPED_JASPER_PRINT).renderReport(report);
	}

	public static MappedJasperDesign renderAsMappedJasperDesign(ReportDefinition report) {
		return (MappedJasperDesign) ReportRendererFactory.getRendererForOutput(MAPPED_JASPER_DESIGN).renderReport(report);
	}

	public static JasperDesign renderAsJasperDesign(ReportDefinition report) {
		return (JasperDesign) ReportRendererFactory.getRendererForOutput(JASPER_DESIGN).renderReport(report);
	}

	public static byte[] renderAsJRXML(ReportDefinition report) {
		return (byte[]) ReportRendererFactory.getRendererForOutput(JRXML).renderReport(report);
	}

	public static JasperReport renderAsJasperReport(ReportDefinition report) {
		return (JasperReport) ReportRendererFactory.getRendererForOutput(JASPER_REPORT).renderReport(report);
	}

	public static JasperPrint renderAsJasperPrint(ReportDefinition report) {
		return (JasperPrint) ReportRendererFactory.getRendererForOutput(JASPER_PRINT).renderReport(report);
	}

	public static byte[] renderAsPDF(ReportDefinition report) {
		return (byte[]) ReportRendererFactory.getRendererForOutput(PDF).renderReport(report);
	}

	@Override
	public Object renderReport(ReportDefinition report) {
		try {
			if (getOutputType().equals(MAPPED_JASPER_DESIGN)) {
				return getOutputAsMappedJasperDesign(report);
			}
			if (getOutputType().equals(MAPPED_JASPER_PRINT)) {
				return getOutputAsMappedJasperPrint(report);
			}
			if (getOutputType().equals(MAPPED_JASPER_REPORT)) {
				return getOutputAsMappedJasperReport(report);
			}
			JasperDesign jasperDesign = convertToJasperDesign(report);
			if (getOutputType().equals(JASPER_DESIGN)) {
				return jasperDesign;
			}
			if (getOutputType().equals(JRXML)) {
				return getOutputAsJRXML(jasperDesign);
			}
			if (getOutputType().equals(JASPER_REPORT)) {
				return convertToJasperReport(jasperDesign);
			}
			if (getOutputType().equals(PDF)) {
				return getOutputAsPdf(convertToJasperReport(jasperDesign), report);
			}
		} catch (JRException e) {
			throw new RuntimeException(e);
		}
		throw new UnsupportedOperationException(getOutputType() + " not supported for renderer " + this.getClass().getName());
	}

	protected abstract byte[] getOutputAsPdf(JasperReport outputAsJasperReport, ReportDefinition report) throws JRException;

	protected abstract JasperReport convertToJasperReport(JasperDesign jasperDesign) throws JRException;

	protected abstract JasperDesign convertToJasperDesign(ReportDefinition report) throws JRException;

	protected abstract MappedJasperDesign getOutputAsMappedJasperDesign(ReportDefinition report) throws JRException;

	protected abstract MappedJasperPrint getOutputAsMappedJasperPrint(ReportDefinition report) throws JRException;

	protected abstract MappedJasperReport getOutputAsMappedJasperReport(ReportDefinition report) throws JRException;

	public byte[] getOutputAsJRXML(JasperDesign jd) throws JRException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		JasperCompileManager.writeReportToXmlStream(jd, baos);
		return baos.toByteArray();
	}

}
