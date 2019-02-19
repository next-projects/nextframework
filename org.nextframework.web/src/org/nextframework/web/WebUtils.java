package org.nextframework.web;

import javax.servlet.http.HttpServletRequest;

import org.nextframework.core.web.NextWeb;
import org.nextframework.core.web.WebRequestContext;
import org.nextframework.service.ServiceFactory;
import org.nextframework.web.service.UrlRewriter;

/**
 * @author rogelgarcia | marcusabreu
 * @since 25/01/2006
 * @version 1.1
 */
public class WebUtils {

	/**
	 * Usa o URL Rewriter para reescrever a URL
	 * O urlRewriter default apenas retorna a url passada como parâmetro
	 * @param url
	 * @return
	 */
	public static String rewriteUrl(String url) {
		return ServiceFactory.getService(UrlRewriter.class).rewriteUrl(url);
	}

	@Deprecated
	public static String getFullUrl(HttpServletRequest request) {
		String path = request.getServletPath() + request.getPathInfo();
		return getFullUrl(request, path);
	}

	public static String getFullUrl(HttpServletRequest request, String path) {
		//nao utilizar o nome do módulo igual ao nome da aplicacao
		String contextPath = request.getContextPath();
		if (!path.startsWith(contextPath)) {
			return contextPath + path;
		}
		return path;
	}

	public static String getFirstFullUrl() {
		return ((WebRequestContext) NextWeb.getRequestContext()).getServletRequest().getContextPath() + getFirstUrl();
	}

	public static String getFirstUrl() {
		return ((WebRequestContext) NextWeb.getRequestContext()).getFirstRequestUrl();
	}

}
