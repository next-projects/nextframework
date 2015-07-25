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

	protected static final String RESOURCE_LASTMOD = "RESOURCE_LASTMOD";

	public static final String RESOURCE = "resource";

	protected Map<String, String> map = new HashMap<String, String>();
	
	public void init(ServletConfig config) throws ServletException {
		map.put("js/validation", "org/nextframework/resource");
		map.put("js/inputs", 	 "org/nextframework/resource");
		map.put("js/ajax",       "org/nextframework/resource");
		map.put("js/util",       "org/nextframework/resource");
		map.put("js",            "org/nextframework/resource");
		map.put("calendar",      "org/nextframework/resource/calendar");
		map.put("menu",       	 "org/nextframework/resource/menu");
		map.put("htmlarea",      "org/nextframework/resource/htmlarea");
		map.put("css",           "org/nextframework/resource");
		map.put("img",           "org/nextframework/resource/imgs");
		map.put("report",        "org/nextframework/report/renderer/html/resource");
		
		//layouts
		map.put("css/layout/lightblue",          "org/nextframework/resource/layout/lightblue");
		map.put("css/layout/lightgreen",         "org/nextframework/resource/layout/lightgreen");
		map.put("css/layout/alternate",          "org/nextframework/resource/layout/alternate");
		map.put("css/layout/simpleposts",        "org/nextframework/resource/layout/simpleposts");
		
		map.put("theme",				         "org/nextframework/resource/theme");
		map.put("theme/fonts",				     "org/nextframework/resource/theme/fonts");
		map.put("theme/images",				     "org/nextframework/resource/theme/images");
		
		//adicionar layouts customizados
		Enumeration<String> initParameterNames = config.getInitParameterNames();
		while(initParameterNames.hasMoreElements()){
			String parameter = initParameterNames.nextElement();
			if(parameter.startsWith("layout:")){
				map.put(parameter.substring("layout:".length()), config.getInitParameter(parameter));
			}
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
		String requestResource = getRequestedResource(request);
		int indexOfFile = requestResource.lastIndexOf('/');
		String tipo;
		String file = null;
		if(indexOfFile < 0){
			tipo = requestResource;
		} else {
			tipo = requestResource.substring(0, indexOfFile);
			file = requestResource.substring(indexOfFile);
		}

		if(tipo.equals(RESOURCE)){
			HttpSession session = request.getSession();
			Integer id = new Integer(request.getParameter("id"));
			Resource recurso = ResourceUtil.get(session, id);
			if (recurso != null) {
				response.setContentType(recurso.getContentType());
				response.setHeader("Content-Disposition", "attachment; filename=\"" + recurso.getFileName()	+ "\";");
				response.getOutputStream().write(recurso.getContents());
			}
		} else {
	        String basePath = map.get(tipo);
	        InputStream classpathInputStream = null;
			if(basePath == null){
				if(tipo.endsWith(RESOURCE)){
					classpathInputStream = getClasspathResource(request, tipo, file);
				}
				if(classpathInputStream == null){
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
			}
			String resourcePath = basePath+file;
			//request.getSession().getServletContext().getResourceAsStream("/WEB-INF/classes/"+
			//this.getClass().getClassLoader().getResourceAsStream(
			
			//verificar se o recurso existe na aplicaçao (NEXT aberto)
			boolean useLastModified = true;
	        InputStream inputStream = classpathInputStream != null? classpathInputStream: request.getSession().getServletContext().getResourceAsStream("/WEB-INF/classes/"+resourcePath);
	        if(inputStream == null){
	        	//procurar no classpath (utiliza JAR)
	        	useLastModified = true; // usamos o lastModified apenas para o JAR porque no outro modo alterações tem que ser enviadas para o cliente
	        	inputStream = this.getClass().getClassLoader().getResourceAsStream(resourcePath);
	        }
			
			if (inputStream != null) {
				if(useLastModified && request.getSession().getServletContext().getAttribute(RESOURCE_LASTMOD+resourcePath) == null){
					request.getSession().getServletContext().setAttribute(RESOURCE_LASTMOD+resourcePath, System.currentTimeMillis());					
				}
				if(file.endsWith(".css")){
					response.setContentType("text/css");
				}
	            
				InputStream in = new BufferedInputStream(inputStream);
				OutputStream out = response.getOutputStream();
				
				String ae = request.getHeader("accept-encoding");
				if (ae != null && ae.indexOf("gzip") != -1 && (requestResource.endsWith("js") || requestResource.endsWith("css"))) {
					response.addHeader("Content-Encoding", "gzip");
					out = new GZIPOutputStream(out);
				}
				
				int b = 0;
				while ((b = in.read()) != -1) {
					out.write(b);
				}
				 
				/*
				byte[] buffer = new byte[8192];
				int read;
				while((read = in.read(buffer)) != -1){
					out.write(buffer, 0, read);
				}
				*/
				if(out instanceof GZIPOutputStream){
					((GZIPOutputStream)out).finish();
				}
				
				out.flush();
			} else {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}			
		}

	}

	protected InputStream getClasspathResource(HttpServletRequest request, String tipo, String file) {
		return getClass().getClassLoader().getResourceAsStream(tipo + file);
	}

	protected String getRequestedResource(HttpServletRequest request) {
		String requestURI = request.getRequestURI();
		requestURI = requestURI.replace((CharSequence)"//", "/");
		String servletPath = request.getServletPath();
		int s = requestURI.indexOf(servletPath)+servletPath.length()+1;
		String requestResource = requestURI.substring(s);
		return requestResource;
	}

    public long getLastModified(HttpServletRequest request) {
    	String requestResource = getRequestedResource(request);
		int indexOfFile = requestResource.lastIndexOf('/');
		String tipo;
		String file = null;
		if(indexOfFile < 0){
			tipo = requestResource;
		} else {
			tipo = requestResource.substring(0, indexOfFile);
			file = requestResource.substring(indexOfFile);
		}
		if(!tipo.equals(RESOURCE)){
			String basePath = map.get(tipo);
			if(basePath != null){
				String resourcePath = basePath+file;
				Object last = request.getSession().getServletContext().getAttribute(RESOURCE_LASTMOD+resourcePath);
				if(last != null){
					return (Long)last;
				}
			}
			
		}
    	return -1;
    }
}
