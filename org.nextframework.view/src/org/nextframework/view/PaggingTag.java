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
package org.nextframework.view;

import org.nextframework.core.web.NextWeb;

/**
 * @author rogelgarcia
 * @since 08/02/2006
 * @version 1.1
 */
public class PaggingTag extends BaseTag {

	protected Integer currentPage;
	protected Integer totalNumberOfPages;
	protected String parameters;
	
	protected String selectedClass;
	protected String unselectedClass;
		
	@Override
	protected void doComponent() throws Exception {
		int start = Math.max(1, currentPage+1 - 4) - 1;
		boolean start3pontos = start != 0;
		int fim = Math.min( 9 - (currentPage - start) + currentPage , totalNumberOfPages);
		boolean fim3pontos = fim < totalNumberOfPages;//fim nao é incluido
		if(start3pontos)getOut().print("...&nbsp;");
		for (int i = start; i < fim; i++) {
			if(i == currentPage){
				String cs = selectedClass != null? " class=\""+selectedClass +"\"": "";
				getOut().print("<span"+cs+">"+(i+1)+"</span> ");
			} else {
				String cs = unselectedClass != null? " class=\""+unselectedClass +"\"": "";
				getOut().print("<a href=\""+getRequest().getContextPath()+NextWeb.getRequestContext().getRequestQuery()+"?currentPage="+i+getParameters()+"\" "+cs+">"+(i+1)+"</a> ");
			}
			//codigo de teste
//			if(i==currentPage){
//				System.out.println(">"+(i+1));
//			} else {
//				System.out.println((i+1));
//			}
		}
		if(fim3pontos)getOut().print("...&nbsp;");
	}
	
	public Integer getCurrentPage() {
		return currentPage;
	}
	public String getSelectedClass() {
		return selectedClass;
	}
	public Integer getTotalNumberOfPages() {
		return totalNumberOfPages;
	}
	public String getUnselectedClass() {
		return unselectedClass;
	}
	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}
	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}
	public void setTotalNumberOfPages(Integer totalNumberOfPages) {
		this.totalNumberOfPages = totalNumberOfPages;
	}
	public void setUnselectedClass(String unselectedClass) {
		this.unselectedClass = unselectedClass;
	}

	public String getParameters() {
		if(parameters == null){
			return "";
		}
		return "&"+parameters.replaceAll(";", "&");
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
}
