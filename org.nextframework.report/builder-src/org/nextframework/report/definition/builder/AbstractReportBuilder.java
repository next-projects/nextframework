package org.nextframework.report.definition.builder;

import org.nextframework.report.definition.ReportDefinition;

public abstract class AbstractReportBuilder implements IReportBuilder {
	
	protected ReportDefinition definition;

	protected boolean rendering = false;

	@Override
	public ReportDefinition getDefinition() {
		if(rendering){
			return definition;
		}
		if(definition == null){
			rendering = true;
			definition = new ReportDefinition();
			definition.setReportName(getReportName());
			configureDefinition();
		}
		return definition;
	}

	
	protected abstract void configureDefinition();
	
	protected String getReportName() {
		String reportName = this.getClass().getSimpleName();
		if(reportName.equals("")){
			if(this.getClass().getEnclosingClass() != null){
				reportName = this.getClass().getEnclosingClass().getSimpleName() + "_" + (int)(Math.random()*100000000);
				Class<?>[] declaredClasses = this.getClass().getEnclosingClass().getDeclaredClasses();
				for (int i = 0; i < declaredClasses.length; i++) {
					if(declaredClasses[i].equals(this.getClass())){
						reportName = this.getClass().getEnclosingClass().getSimpleName() + "_" + i + "_" + (int)(Math.random()*100000000);
					}
				}
			}
		}
		if(reportName.endsWith("Builder")){
			reportName = reportName.substring(0, reportName.indexOf("Builder"));
		}
		return reportName;
	}
	
}
