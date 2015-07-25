package org.nextframework.report;

import org.nextframework.web.WebContext;


public class LegacyReportUtils {

	private static ReportGenerator reportGenerator;
	
	public static ReportGenerator getReportGenerator() {
		if (reportGenerator == null) {
			try {
				// se nao estiver em um contexto Web dá pau aqui
				ReportNameResolverImpl nameResolverImpl = new ReportNameResolverImpl("/WEB-INF/relatorio/", ".jasper", WebContext.getServletContext());
				ReportTranslatorImpl translatorImpl = new ReportTranslatorImpl(nameResolverImpl);
				ReportGeneratorImpl generatorImpl = new ReportGeneratorImpl(translatorImpl);
				reportGenerator = generatorImpl;
			} catch (ClassCastException e) {
				throw new ReportException("A geração de relatórios só pode ser feita em um contexto WEB");
			}
		}
		return reportGenerator;
	}
}
