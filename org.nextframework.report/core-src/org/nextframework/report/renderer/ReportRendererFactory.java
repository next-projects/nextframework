package org.nextframework.report.renderer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ReportRendererFactory {

	static List<ReportRenderer> renderers = new ArrayList<ReportRenderer>();
	
	public static void registerRenderer(ReportRenderer renderer){
		for (Iterator<ReportRenderer> iterator = renderers.iterator(); iterator.hasNext();) {
			ReportRenderer reportRenderer = iterator.next();
			if(reportRenderer.getOutputType().equals(renderer.getOutputType())){
				iterator.remove();
				break;
			}
		}
		renderers.add(renderer);
	}
	
	public static ReportRenderer getRendererForOutput(String outputType){
		for (ReportRenderer renderer : renderers) {
			if(renderer.getOutputType().equals(outputType)){
				return renderer;
			}
		}
		throw new RuntimeException("Nenhum ReportRenderer foi encontrado para o tipo de saída "+outputType);
	}
	
	static {
		try {
			Class.forName("org.nextframework.report.renderer.jasper.JasperReportsRenderer");
			Class.forName("org.nextframework.report.renderer.html.HtmlReportRenderer");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
