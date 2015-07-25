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
public abstract class ReportController<FILTRO> extends ResourceSenderController<FILTRO> {
	
	protected String name;
	
	public void setName(String name) {
		this.name = name;
	}
	
	protected Class<FILTRO> filtroClass;

	@SuppressWarnings("unchecked")
	public ReportController(){
		Class[] genericTypes = GenericTypeResolver.resolveTypeArguments(this.getClass(), ReportController.class);
//		if(genericTypes.length < 1){
//			boolean invalido = false;
//			//tentar a outra forma de Generics
//			{
//				try {
//					genericTypes = Util.generics.getGenericTypes2(this.getClass());
//					if(genericTypes.length != 1){
//						invalido = true;
//					}
//				} catch (Exception e) {
//					genericTypes = new Class[]{Object.class};
//				}
//				
//			}
//			if(invalido){
//				throw new RuntimeException("A classe "+this.getClass().getName()+" deve declarar um tipo genérico que indique o command que será usado");
//			}
//		}
		Class<?> clazz = genericTypes[0];
		filtroClass = (Class<FILTRO>) clazz;
	}
	
//	@Override
//	// isso é necessário quando utilizar generics e o método nao estiver sobrescrito
//	protected Class<?> getCommandClass(Method method) {
//		//TODO FAZER A DETECCAO MESMO QUANDO UTILIZAR GENERICS
//		Class<?> class1 = super.getCommandClass(method);
//		if(!class1.equals(Object.class)){
//			return class1;
//		}
//		return filtroClass;
//	}
	
	@Override
	public ModelAndView doFiltro(WebRequestContext request, FILTRO filtro) throws ResourceGenerationException {
		try {
			filtro(request, filtro);
		} catch (Exception e) {
			throw new ResourceGenerationException(FILTRO, e);
		}
		return getFiltroModelAndView(request, filtro);
	}
	
	protected ModelAndView getFiltroModelAndView(WebRequestContext request, FILTRO filtro) {
		if (name == null) {
			if(!this.getClass().getSimpleName().endsWith("Report")){
				throw new NextException("Um controller de relatórios deve ter o sufixo Report ou então setar a variável name");
			}
			String className = org.springframework.util.StringUtils.uncapitalize(this.getClass()
					.getSimpleName());
			name = className.substring(0, className.length()- "Report".length());
		}
		return new ModelAndView("relatorio/"+name,"filtro", filtro);
	}

	protected void filtro(WebRequestContext request, FILTRO filtro) throws Exception {
		request.setAttribute("filtro", filtro);
	}

	@Override
	public Resource generateResource(WebRequestContext request, FILTRO filtro) throws Exception {
		IReport report = createReport(request, filtro);
		
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

	public IReport createReport(WebRequestContext request, FILTRO filtro) throws Exception {
		throw new RuntimeException("Implement the method createReport or overwrite the method generateResource");
	}


}
