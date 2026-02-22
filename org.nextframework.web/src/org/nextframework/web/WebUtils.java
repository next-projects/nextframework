package org.nextframework.web;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.nextframework.core.standard.Next;
import org.nextframework.core.web.DefaultWebRequestContext;
import org.nextframework.core.web.NextWeb;
import org.nextframework.core.web.WebApplicationContext;
import org.nextframework.exception.ApplicationException;
import org.nextframework.service.ServiceFactory;
import org.nextframework.util.Util;
import org.nextframework.web.service.UrlRewriter;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author rogelgarcia | marcusabreu
 * @since 25/01/2006
 * @version 1.1
 */
public class WebUtils {

	private static final String[] IP_HEADER_CANDIDATES = {
			"X-Forwarded-For",
			"X-Real-IP",
			"Proxy-Client-IP",
			"WL-Proxy-Client-IP",
			"HTTP_X_FORWARDED_FOR",
			"HTTP_X_FORWARDED",
			"HTTP_X_CLUSTER_CLIENT_IP",
			"HTTP_CLIENT_IP",
			"HTTP_FORWARDED_FOR",
			"HTTP_FORWARDED",
			"HTTP_VIA",
			"REMOTE_ADDR" };

	public static String getClientIpAddress() {
		return getClientIpAddress(NextWeb.getRequestContext().getServletRequest());
	}

	public static String getClientIpAddress(HttpServletRequest request) {
		for (String header : IP_HEADER_CANDIDATES) {
			String value = request.getHeader(header);
			if (Util.strings.isNotEmpty(value) && !"unknown".equalsIgnoreCase(value)) {
				String[] parts = value.split("(\\s*)?,(\\s*)?");
				return parts[0];
			}
		}
		return request.getRemoteAddr();
	}

	public static String getServerRealPath() {
		WebApplicationContext appContext = (WebApplicationContext) Next.getApplicationContext();
		return appContext.getServletContext().getRealPath("/");
	}

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

	public static String getRequestAction() {
		HttpServletRequest httpServletRequest = WebContext.getRequest();
		return httpServletRequest.getParameter(DefaultWebRequestContext.ACTION_PARAMETER);
	}

	public static String getModelAndViewName() {
		String view = (String) NextWeb.getRequestContext().getAttribute("viewName");
		if (view == null) {
			view = (String) NextWeb.getRequestContext().getAttribute("bodyPage");
		}
		if (view != null) {
			int slash = view.lastIndexOf("/");
			if (slash > -1) {
				view = view.substring(slash + 1);
			}
			int dot = view.lastIndexOf(".");
			if (dot > -1) {
				view = view.substring(0, dot);
			}
		}
		return view;
	}

	private static Pattern scriptPattern1 = Pattern.compile("<(\"[^\"]*\"|'[^']*'|[^'\">])*>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	private static Pattern scriptPattern2 = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern scriptPattern3 = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
	private static Pattern scriptPattern4 = Pattern.compile("script:", Pattern.CASE_INSENSITIVE);

	public static boolean containsTagsOrCodes(String source) {
		if (source == null || "<null>".equalsIgnoreCase(source)) {
			return false;
		}
		return scriptPattern1.matcher(source).find() ||
				scriptPattern2.matcher(source).find() ||
				scriptPattern3.matcher(source).find() ||
				scriptPattern4.matcher(source).find();
	}

	public static String removeTagsAndCodes(String source) {
		if (source == null) {
			return null;
		}
		String cleanValue = Normalizer.normalize(source, Normalizer.Form.NFD);
		cleanValue = cleanValue.replaceAll("\0", "");
		cleanValue = scriptPattern1.matcher(cleanValue).replaceAll("");
		cleanValue = scriptPattern2.matcher(cleanValue).replaceAll("");
		cleanValue = scriptPattern3.matcher(cleanValue).replaceAll("");
		cleanValue = scriptPattern4.matcher(cleanValue).replaceAll("");
		return cleanValue;
	}

	public static void verificaMapComHTML(Map<String, ?> parameters, String propertiesToIgnore) {
		List<String> propertiesToIgnoreList = Util.strings.splitFieldsAsList(propertiesToIgnore);
		for (String parametro : parameters.keySet()) {
			if (propertiesToIgnoreList != null && propertiesToIgnoreList.contains(parametro)) {
				continue;
			}
			Object valor = parameters.get(parametro);
			verificaValorComHTML(valor, parametro);
		}
	}

	public static void verificaAtributosComHTML(Object bean, String propertiesToIgnore) {
		List<String> propertiesToIgnoreList = Util.strings.splitFieldsAsList(propertiesToIgnore);
		Field[] fields = bean.getClass().getDeclaredFields();
		for (Field field : fields) {
			if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
				continue;
			}
			if (propertiesToIgnoreList != null && propertiesToIgnoreList.contains(field.getName())) {
				continue;
			}
			Method getterMethod = Util.beans.getGetterMethod(bean.getClass(), field.getName());
			if (getterMethod != null) {
				Object valor = Util.beans.getPropertyValue(bean, field.getName());
				verificaValorComHTML(valor, field.getName());
			}
		}
	}

	private static void verificaValorComHTML(Object valor, String parametro) {
		if (valor instanceof String) {
			String valorStr = (String) valor;
			if (containsTagsOrCodes(valorStr)) {
				throw new ApplicationException("O valor do parâmetro " + parametro + " contém marcações HTML não permitidas!");
			}
		} else if (valor instanceof String[]) {
			String[] valoresArray = (String[]) valor;
			for (String valorStr : valoresArray) {
				if (containsTagsOrCodes(valorStr)) {
					throw new ApplicationException("O valor do parâmetro " + parametro + " contém marcações HTML não permitidas!");
				}
			}
		}
	}

}
