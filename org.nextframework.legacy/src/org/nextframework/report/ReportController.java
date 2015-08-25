/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2009 the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * You may obtain a copy of the license at
 * 
 *     http://www.gnu.org/copyleft/lesser.html
 * 
 */
package org.nextframework.report;

import org.nextframework.controller.resource.Resource;
import org.nextframework.controller.resource.ResourceGenerationException;
import org.nextframework.controller.resource.ResourceSenderController;
import org.nextframework.core.web.WebRequestContext;
import org.nextframework.exception.NextException;
import org.springframework.core.GenericTypeResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author rogelgarcia
 * @since 02/02/2006
 * @version 1.1
 */
public abstract class ReportController<FILTER> extends ResourceSenderController<FILTER> {
	
	protected String name;
	
	public void setName(String name) {
		this.name = name;
	}
	
	protected Class<FILTER> filterClass;

	@SuppressWarnings("unchecked")
	public ReportController(){
		Class<?>[] genericTypes = GenericTypeResolver.resolveTypeArguments(this.getClass(), ReportController.class);
		Class<?> clazz = genericTypes[0];
		filterClass = (Class<FILTER>) clazz;
	}
	
	
	@Override
	public ModelAndView doFilter(WebRequestContext request, FILTER filter) throws ResourceGenerationException {
		try {
			filter(request, filter);
		} catch (Exception e) {
			throw new ResourceGenerationException(FILTER, e);
		}
		return getFilterModelAndView(request, filter);
	}
	
	protected ModelAndView getFilterModelAndView(WebRequestContext request, FILTER filter) {
		if (name == null) {
			if(!this.getClass().getSimpleName().endsWith("Report")){
				throw new NextException("Um controller de relatórios deve ter o sufixo Report ou então setar a variável name");
			}
			String className = org.springframework.util.StringUtils.uncapitalize(this.getClass()
					.getSimpleName());
			name = className.substring(0, className.length()- "Report".length());
		}
		return new ModelAndView("relatorio/"+name, "filter", filter);
	}

	protected void filter(WebRequestContext request, FILTER filter) throws Exception {
		request.setAttribute("filter", filter);
	}

	@Override
	public Resource generateResource(WebRequestContext request, FILTER filter) throws Exception {
		IReport report = createReport(request, filter);
		
        String name = getReportName(report);
        byte[] bytes = getReportBytes(report);
        return getPdfResource(name, bytes);
	}

	protected Resource getPdfResource(String name, byte[] bytes) {
		Resource resource = new Resource();
        resource.setContentType("application/pdf");
        resource.setFileName(name);
        resource.setContents(bytes);
		return resource;
	}

	protected byte[] getReportBytes(IReport report) {
		return getReportGenerator().toPdf(report);
	}

	protected String getReportName(IReport report) {
		String name = report.getFileName();
        if(name == null){
        	name = report.getName();
        	if (name.indexOf('/') != -1) {
        		name = name.substring(report.getName().lastIndexOf('/') + 1);
			}
        }
        if(!name.endsWith(".pdf")){
        	name+=".pdf";
        }
		return name;
	}

	
	protected ReportGenerator getReportGenerator() {
		return LegacyReportUtils.getReportGenerator();
	}

	public IReport createReport(WebRequestContext request, FILTER filter) throws Exception {
		throw new RuntimeException("Implement the method createReport or overwrite the method generateResource");
	}


}
