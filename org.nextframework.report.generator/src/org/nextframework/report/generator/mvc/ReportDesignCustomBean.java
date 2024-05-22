package org.nextframework.report.generator.mvc;

public interface ReportDesignCustomBean {

	public Integer getId();

	public String getXml();

	public Boolean getReportPublic();

	public void setId(Integer id);

	public void setXml(String xml);

	public void setReportPublic(Boolean reportPublic);

}