package org.nextframework.web;

import javax.servlet.http.HttpServletRequest;

import org.nextframework.core.web.DefaultWebRequestContext;
import org.nextframework.core.web.NextWeb;
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
		return NextWeb.getRequestContext().getServletRequest().getContextPath() + getFirstUrl();
	}

	public static String getFirstUrl() {
		return NextWeb.getRequestContext().getFirstRequestUrl();
	}

	public static String getRequestModule() {
		return NextWeb.getRequestContext().getServletPath().substring(1);
	}

	public static String getRequestController() {
		String pathInfo = NextWeb.getRequestContext().getPathInfo();
		if (pathInfo.length() > 0) {
			return pathInfo.substring(1);
		}
		return null;
	}

	public static String getRequestModuleAndControllerURL() {
		String controller = getRequestController();
		return NextWeb.getRequestContext().getServletPath() + "/" + (controller != null ? controller : "");
	}

	public static String getModelAndViewName() {
		String view = (String) NextWeb.getRequestContext().getAttribute("bodyPage");
		if (view != null) {
			view = view.substring(view.lastIndexOf("/") + 1);
			int dot = view.lastIndexOf(".");
			if (dot > -1) {
				view = view.substring(0, dot);
			}
		}
		return view;
	}

	public static String getMessageCodeViewPrefix() {
		String view = getModelAndViewName();
		return getRequestModule() + "." + getRequestController() + "." + (view != null ? view : "view");
	}

	public static String getRequestAction() {
		HttpServletRequest httpServletRequest = WebContext.getRequest();
		return httpServletRequest.getParameter(DefaultWebRequestContext.ACTION_PARAMETER);
	}

}