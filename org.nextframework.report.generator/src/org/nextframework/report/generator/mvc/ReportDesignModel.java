package org.nextframework.report.generator.mvc;

import java.util.LinkedHashSet;
import java.util.Set;

import org.nextframework.report.generator.ReportElement;

public class ReportDesignModel {

	private Integer id;

	private Class<?> selectedType;//this can be different of usertype if generated in runtime
	private Class<?> selectedGeneratedType;//usertype is the type that exists in the project

	private String reportTitle;

	private Boolean reportPublic = false;

	private ReportElement reportElement;
	private Set<String> properties = new LinkedHashSet<String>();

	private String reportXml;

	public ReportElement getReportElement() {
		return reportElement;
	}

	public void setReportElement(ReportElement reportElement) {
		this.reportElement = reportElement;
	}

	public Boolean getReportPublic() {
		return reportPublic;
	}

	public void setReportPublic(Boolean reportPublic) {
		this.reportPublic = reportPublic;
	}

	public String getReportTitle() {
		return reportTitle;
	}

	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Class<?> getSelectedType() {
		return selectedType;
	}

	public Set<String> getProperties() {
		return properties;
	}

	public void setSelectedType(Class<?> selectedType) {
		this.selectedType = selectedType;
	}

	public void setProperties(Set<String> properties) {
		this.properties = properties;
	}

	public String getReportXml() {
		return reportXml;
	}

	public void setReportXml(String reportXml) {
		this.reportXml = reportXml;
	}

	public Class<?> getSelectedGeneratedType() {
		return selectedGeneratedType;
	}

	public void setSelectedGeneratedType(Class<?> selectedUserType) {
		this.selectedGeneratedType = selectedUserType;
	}

}
