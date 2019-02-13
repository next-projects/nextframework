package org.nextframework.view;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @since Aug 2013 
 * @author rogelgarcia
 *
 */
public class ImportJSTag extends SimpleTagSupport {

	String pack;
	
	String fileName;

	public String getPackage() {
		return pack;
	}

	public String getFileName() {
		return fileName;
	}

	public void setPackage(String pack) {
		this.pack = pack;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		ServletContext servletContext = ((PageContext)getJspContext()).getServletContext();
		Set<String> files = new HashSet<String>();
		
		//FIXME Se for necessário uma ordem de carregamento, o carregamento por pacote não está pronto pra isso... :-(
		if(pack != null){
			if(!pack.endsWith("resource")){
				getJspContext().getOut().println("<div style='color:red; background-color: white'>The package attribute of the importJS tag does not end with \"resource\". " +
						"Only packages ended with resource can be imported. Package: "+pack+"</div>");
				return;
			}
			String packageAsDir = "/"+pack.replace('.', '/');
			Set<String> resourcePaths = servletContext.getResourcePaths("/WEB-INF/classes"+packageAsDir);
			for (String file : resourcePaths) {
				if(file.endsWith(".js")){
					files.add(file);
				}
			}
		}
		
		for (String file : files) {
			file = servletContext.getContextPath()+ "/"+ResourceProvider.RESOURCE + file.substring("/WEB-INF/classes".length());
			getJspContext().getOut().println("<script language=\"JavaScript\" src=\""+file+"\"></script>");
		}
		
		if(fileName != null){
			if(!fileName.endsWith(".js")){
				fileName += ".js";
			}
			fileName = fileName.replace('.', '/');
			fileName = fileName.substring(0, fileName.length() - 3) + ".js";
			getJspContext().getOut().println("<script language=\"JavaScript\" src=\""+servletContext.getContextPath()+ "/"+ResourceProvider.RESOURCE+"/"+fileName+"\"></script>");
		}
	}
	
}
