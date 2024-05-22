package org.nextframework.report.renderer.jasper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.nextframework.report.renderer.jasper.builder.MappedJasperReport;

public class JasperDataParametersResult {

	Map<String, Object> parameters;

	List<MappedJasperReport> subreports = new ArrayList<MappedJasperReport>();

	public Map<String, Object> getParameters() {
		return parameters;
	}

	public List<MappedJasperReport> getSubreports() {
		return subreports;
	}

	public void setParameters(Map<String, Object> parameters) {
		this.parameters = parameters;
	}

	public void setSubreports(List<MappedJasperReport> subreports) {
		this.subreports = subreports;
	}

}
