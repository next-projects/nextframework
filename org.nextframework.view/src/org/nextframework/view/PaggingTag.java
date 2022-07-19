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
import org.nextframework.util.Util;
import org.nextframework.web.WebUtils;

/**
 * @author rogelgarcia
 * @since 08/02/2006
 * @version 1.1
 */
public class PaggingTag extends BaseTag {

	protected Integer currentPage;
	protected Integer totalNumberOfPages;
	protected String parameters;

	protected String panelClass;
	protected String selectedClass;

	@Override
	protected void doComponent() throws Exception {

		int start = Math.max(1, currentPage + 1 - 4) - 1;
		boolean start3pontos = start != 0;
		int fim = Math.min(9 - (currentPage - start) + currentPage, totalNumberOfPages);
		boolean fim3pontos = fim < totalNumberOfPages;//fim nao é incluido

		getOut().println("<ul class=\"" + panelClass + "\">");

		if (start3pontos) {
			getOut().print("<li>" + getLink(0) + "&nbsp;...&nbsp;</li>");
		}

		for (int i = start; i < fim; i++) {
			if (i == currentPage) {
				String cs = selectedClass != null ? " class=\"" + selectedClass + "\"" : "";
				getOut().print("<li><a " + cs + " href=\"#\">" + (i + 1) + "</a></li> ");
			} else {
				getOut().print("<li>" + getLink(i) + "</li>");
			}
		}

		if (fim3pontos) {
			getOut().print("<li>&nbsp;...&nbsp;" + getLink(totalNumberOfPages) + "</li>");
		}

		getOut().println("</ul>");

	}

	private String getLink(int page) {

		//Link basico
		String link = getRequest().getContextPath() + NextWeb.getRequestContext().getRequestQuery() + "?currentPage=" + page + getParameters();

		//Verifica ultima acao
		String acao = (String) getRequest().getAttribute("lastAction");
		if (Util.strings.isNotEmpty(acao)) {
			link += "&ACTION=" + acao;
		}

		//Verifica URL Sufix
		link = WebUtils.rewriteUrl(link);

		return "<a href=\"" + link + "\" >" + (page + 1) + "</a>";
	}

	public Integer getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getTotalNumberOfPages() {
		return totalNumberOfPages;
	}

	public void setTotalNumberOfPages(Integer totalNumberOfPages) {
		this.totalNumberOfPages = totalNumberOfPages;
	}

	public String getParameters() {
		if (parameters == null) {
			return "";
		}
		return "&" + parameters.replaceAll(";", "&");
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public String getPanelClass() {
		return panelClass;
	}

	public void setPanelClass(String panelClass) {
		this.panelClass = panelClass;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

}
