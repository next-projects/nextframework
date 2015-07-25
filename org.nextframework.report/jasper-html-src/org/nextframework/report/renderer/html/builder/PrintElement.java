package org.nextframework.report.renderer.html.builder;

import java.util.List;

import net.sf.jasperreports.engine.JRPrintElement;
import net.sf.jasperreports.engine.JRPrintFrame;
import net.sf.jasperreports.engine.JRPrintText;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.elements.ReportItem;
import org.nextframework.report.renderer.jasper.builder.MappedJasperReport;

public class PrintElement {

	MappedJasperReport mappedJasperReport;
	JRPrintElement jrPrintElement;
	int y;
	int row;
	
	//transients
	KeyInfo keyInfo;
	private String uniqueId;
	ReportItem reportItem;
	
	public MappedJasperReport getMappedJasperReport() {
		return mappedJasperReport;
	}

	public void setMappedJasperReport(MappedJasperReport mappedJasperReport) {
		this.mappedJasperReport = mappedJasperReport;
	}

	public void setReportItem(ReportItem reportItem) {
		this.reportItem = reportItem;
	}
	public ReportItem getReportItem(){
		if(reportItem != null){
			return reportItem;
		}
		if(getJrPrintElement().getKey() == null){
			return null;
		}
		return getMappedJasperReport().getMappedKeys().get(getJrPrintElement().getKey());
	}
	
	public KeyInfo getKeyInfo() {
		if(keyInfo == null && jrPrintElement.getKey() != null){
			keyInfo = new KeyInfo(jrPrintElement.getKey());
		}
		return keyInfo;
	}
	
	public void setKeyInfo(KeyInfo keyInfo) {
		this.keyInfo = keyInfo;
	}
	
	public Integer getColumn(){
		KeyInfo keyInfo = getKeyInfo();
		if(keyInfo != null){
			return keyInfo.getColumn();
		}
		return null;
	}
	
	public Integer getColspan(){
		KeyInfo keyInfo = getKeyInfo();
		if(keyInfo != null){
			return keyInfo.getColspan();
		}
		return null;
	}
	
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public ReportDefinition getSource() {
		return mappedJasperReport.getReportDefinition();
	}
	public JRPrintElement getJrPrintElement() {
		return jrPrintElement;
	}
	public void setJrPrintElement(JRPrintElement printElement) {
		this.jrPrintElement = printElement;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}

	public String getUniqueId() {
		return uniqueId;
	}
	
	public void setUniqueId(String i) {
		this.uniqueId = i;
	}
	
	@Override
	public String toString() {
		if(jrPrintElement == null){
			return "jrPrintElement=null";
		}
		if(jrPrintElement instanceof JRPrintText){
			String reportName = jrPrintElement.getOrigin().getReportName();
			if(reportName == null){
				return ((JRPrintText) jrPrintElement).getText();
			}
			return ((JRPrintText) jrPrintElement).getText() +" ("+reportName+")";
		}
		if(jrPrintElement instanceof JRPrintFrame){
			List<JRPrintElement> elements = ((JRPrintFrame)jrPrintElement).getElements();
			if(elements.size() == 0){
				return "[]";
			}
			StringBuilder sb = new StringBuilder("\n\t[");
			for (final JRPrintElement jrpe : elements) {
				sb.append(new PrintElement(){{setJrPrintElement(jrpe);}});
				sb.append(", ");
			}
			sb.setLength(sb.length()-2);
			sb.append("]");
			return sb.toString();
		}
		return jrPrintElement.toString();
	}
}
