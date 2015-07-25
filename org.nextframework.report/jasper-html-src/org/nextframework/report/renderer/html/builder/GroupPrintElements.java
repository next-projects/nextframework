package org.nextframework.report.renderer.html.builder;

import java.util.ArrayList;
import java.util.List;

import net.sf.jasperreports.engine.JRPrintRectangle;
import net.sf.jasperreports.engine.JRPrintText;

import org.nextframework.report.definition.ReportDefinition;

public class GroupPrintElements extends PrintElement {
	
	ReportDefinition groupDefinition;
	
	boolean subreportChecked = true;
	
	public boolean isSubreportChecked() {
		return subreportChecked;
	}
	
	public void setSubreportChecked(boolean subreportChecked) {
		this.subreportChecked = subreportChecked;
	}

	public ReportDefinition getGroupDefinition() {
		return groupDefinition;
	}

	public void setGroupDefinition(ReportDefinition groupDefinition) {
		this.groupDefinition = groupDefinition;
	}

	List<PrintElement> printElements = new ArrayList<PrintElement>();
	
	public List<PrintElement> getPrintElements() {
		return printElements;
	}
	
	@Override
	public String toString() {
		if(printElements == null){
			return "printElements=null";
		}
		if(printElements.size() == 0){
			return "empty";
		}
		String result = "\n\t[";
		for (PrintElement printElement : printElements) {
			if(printElement.getJrPrintElement() instanceof JRPrintRectangle){
				result += "rect "+((JRPrintRectangle) printElement.getJrPrintElement()).getUUID()+", ";
			} if(printElement.getJrPrintElement() instanceof JRPrintText){
				result += ((JRPrintText) printElement.getJrPrintElement()).getText()+", ";
			} else {
				result += printElement+", ";
			}
		}
		return result.substring(0, result.length() - 2)+"]";
	}
}
