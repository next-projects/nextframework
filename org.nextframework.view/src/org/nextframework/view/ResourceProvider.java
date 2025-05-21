package org.nextframework.view;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.nextframework.controller.resource.Resource;

public class ResourceProvider {

	protected static final String RESOURCE_LASTMOD = "RESOURCE_LASTMOD_";

	public static final String RESOURCE = "resource";

	protected Map<String, String> map = new HashMap<String, String>();

	public void init(ServletConfig config) throws ServletException {

		map.put("js/validation", "org/nextframework/resource");
		map.put("js/inputs", "org/nextframework/resource");
		map.put("js/ajax", "org/nextframework/resource");
		map.put("js/util", "org/nextframework/resource");
		map.put("js", "org/nextframework/resource");
		map.put("calendar", "org/nextframework/resource/calendar");
		map.put("menu", "org/nextframework/resource/menu");
		map.put("htmlarea", "org/nextframework/resource/htmlarea");
		map.put("css", "org/nextframework/resource");
		map.put("img", "org/nextframework/resource/imgs");
		map.put("report", "org/nextframework/report/renderer/html/resource");

		map.put("bootstrap/css", "org/nextframework/resource/bootstrap/css");
		map.put("bootstrap/js", "org/nextframework/resource/bootstrap/js");
		map.put("bootstrap/menu", "org/nextframework/resource/bootstrap/menu");
		map.put("bootstrap/fonts", "org/nextframework/resource/bootstrap/fonts");

		//layouts
		map.put("layout/lightblue", "org/nextframework/resource/layout/lightblue");
		map.put("layout/lightbluebs", "org/nextframework/resource/layout/lightbluebs");

		//adicionar layouts customizados
		Enumeration<String> initParameterNames = config.getInitParameterNames();
		while (initParameterNames.hasMoreElements()) {
			String parameter = initParameterNames.nextElement();
			if (parameter.startsWith("layout:")) {
				map.put(parameter.substring("layout:".length()), config.getInitParameter(parameter));
			}
		}

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String[] references = getReferences(request);
		if (references == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		String tipo = references[0];
		String file = references[1];

		if (tipo.equals(RESOURCE)) {
			HttpSession session = request.getSession();
			Integer id = new Integer(request.getParameter("id"));
			Resource recurso = ResourceUtil.get(session, id);
			if (recurso != null) {
				response.setContentType(recurso.getContentType());
				response.setHeader("Content-Disposition", "attachment; filename=\"" + recurso.getFileName() + "\";");
				response.getOutputStream().write(recurso.getContents());
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		} else {

			InputStream inputStream = getResource(request, response, tipo, file);
			if (inputStream != null) {

				String chave = RESOURCE_LASTMOD + tipo + file;
				if (request.getSession().getServletContext().getAttribute(chave) == null) {
					request.getSession().getServletContext().setAttribute(chave, System.currentTimeMillis());
				}

				InputStream in = new BufferedInputStream(inputStream);
				OutputStream out = response.getOutputStream();

				String contentType = getContentType(file);
				if (contentType != null) {
					response.setContentType(contentType);
				}

				String ae = request.getHeader("accept-encoding");
				if (ae != null && ae.indexOf("gzip") != -1 && (file.endsWith("js") || file.endsWith("css"))) {
					response.addHeader("Content-Encoding", "gzip");
					out = new GZIPOutputStream(out);
				}

				int b = 0;
				while ((b = in.read()) != -1) {
					out.write(b);
				}

				if (out instanceof GZIPOutputStream) {
					((GZIPOutputStream) out).finish();
				}

				out.flush();
				out.close();
				in.close();
				inputStream.close();

			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}
		}

	}

	protected String[] getReferences(HttpServletRequest request) {

		String requestResource = getRequestedResource(request);
		if (requestResource == null) {
			return null;
		}

		String tipo = null;
		String file = null;
		int indexOfFile = requestResource.lastIndexOf('/');
		if (indexOfFile < 0) {
			if (requestResource.indexOf(".") > -1) {
				tipo = "file";
				file = requestResource;
			} else {
				tipo = requestResource;
			}
		} else {
			tipo = requestResource.substring(0, indexOfFile);
			file = requestResource.substring(indexOfFile);
		}

		return new String[] { tipo, file };
	}

	protected String getRequestedResource(HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		requestURI = requestURI.replace((CharSequence) "//", "/");
		String servletPath = request.getServletPath();
		int resourceIndex = requestURI.indexOf(servletPath) + servletPath.length() + 1;
		if (requestURI.length() < resourceIndex) {
			return null;
		}
		String requestResource = requestURI.substring(resourceIndex);
		return requestResource;
	}

	protected InputStream getResource(HttpServletRequest request, HttpServletResponse response, String tipo, String file) throws IOException {
		String basePath = map.get(tipo);
		if (basePath == null) {
			//solicitações de js dentro de pacotes
			if (tipo.endsWith(RESOURCE)) {
				InputStream inputStream = getInputStream(request, tipo, file);
				if (inputStream != null) {
					return inputStream;
				}
			}
			//Se não encontrou...
			return null;
		}
		return getInputStream(request, basePath, file);
	}

	protected InputStream getInputStream(HttpServletRequest request, String tipo, String file) {
		//Tenta encontrar o arquivo explodido
		InputStream stream = request.getSession().getServletContext().getResourceAsStream("/WEB-INF/classes/" + tipo + file);
		if (stream == null) {
			//Se não encontrar, tenta no classpath
			stream = getClass().getClassLoader().getResourceAsStream(tipo + file);
		}
		return stream;
	}

	protected String getContentType(String file) {
		if (file.endsWith(".css")) {
			return "text/css";
		}
		return null;
	}

	public long getLastModified(HttpServletRequest request) {

		String[] references = getReferences(request);
		if (references == null) {
			return -1;
		}

		String tipo = references[0];
		String file = references[1];

		if (!RESOURCE.equals(tipo)) {
			String chave = RESOURCE_LASTMOD + tipo + file;
			Long last = (Long) request.getSession().getServletContext().getAttribute(chave);
			if (last != null && last != -1) {
				return last / 1000 * 1000;
			}
		}

		return -1;
	}

}
