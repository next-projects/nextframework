package org.nextframework.report.renderer.jasper.builder;

import java.util.List;

import net.sf.jasperreports.engine.JasperPrint;

public class MappedJasperPrint {

	JasperPrint jasperPrint;
	
	MappedJasperReport mappedJasperReport;
	
	List<MappedJasperReport> subreports;
	
	public List<MappedJasperReport> getSubreports() {
		return subreports;
	}

	public void setSubreports(List<MappedJasperReport> subreports) {
		this.subreports = subreports;
	}

	public JasperPrint getJasperPrint() {
		return jasperPrint;
	}

	public MappedJasperReport getMappedJasperReport() {
		return mappedJasperReport;
	}

	public void setJasperPrint(JasperPrint jasperPrint) {
		this.jasperPrint = jasperPrint;
	}

	public void setMappedJasperReport(MappedJasperReport mappedJasperReport) {
		this.mappedJasperReport = mappedJasperReport;
	}
	
	
	
}
