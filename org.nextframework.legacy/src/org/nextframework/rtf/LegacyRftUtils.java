package org.nextframework.rtf;

import org.nextframework.web.WebContext;

public class LegacyRftUtils {

	private static RTFGeneratorImpl rtfGenerator;

	public static RTFGenerator getRTFGenerator() {
		if (rtfGenerator == null) {
			try {
				// se nao estiver em um contexto Web d� pau aqui
				RTFNameResolverImpl nameResolverImpl = new RTFNameResolverImpl("/WEB-INF/rtf/", ".rtf", WebContext.getServletContext());
				rtfGenerator = new RTFGeneratorImpl(nameResolverImpl);
			} catch (ClassCastException e) {
				throw new RuntimeException("A gera��o de RTFs s� pode ser feita em um contexto WEB");
			}
		}
		return rtfGenerator;
	}

}
