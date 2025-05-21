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

	private static final String PTS = "&nbsp;...&nbsp;";

	protected Integer currentPage;
	protected Integer totalNumberOfPages;
	protected String parameters;

	protected String panelClass;
	protected String itemClass;
	protected String selectedClass;
	protected String unselectedClass;

	@Override
	protected void doComponent() throws Exception {

		int start = Math.max(1, currentPage + 1 - 4) - 1;
		boolean start3pontos = start != 0;
		int fim = Math.min(9 - (currentPage - start) + currentPage, totalNumberOfPages);
		boolean fim3pontos = fim < totalNumberOfPages;//fim nao é incluido

		getOut().println("<ul class=\"" + panelClass + "\">");

		if (start3pontos) {
			getOut().print("<li>" + getLink(false, 0, PTS + 1, unselectedClass) + "&nbsp;...&nbsp;</li>");
		}

		for (int i = start; i < fim; i++) {
			String ics = itemClass != null ? " class=\"" + itemClass + "\"" : "";
			String acs = i == currentPage ? selectedClass : unselectedClass;
			getOut().print("<li" + ics + ">" + getLink(i == currentPage, i, String.valueOf(i + 1), acs) + "</li>");
		}

		if (fim3pontos) {
			getOut().print("<li>" + getLink(false, totalNumberOfPages, PTS + (totalNumberOfPages + 1), unselectedClass) + "</li>");
		}

		getOut().println("</ul>");

	}

	private String getLink(boolean current, int page, String label, String styleClass) {

		String link = null;

		if (current) {

			link = "#";

		} else {

			//Link basico
			link = getRequest().getContextPath() + NextWeb.getRequestContext().getRequestQuery() + "?currentPage=" + page + getParameters();

			//Verifica ultima acao
			String acao = (String) getRequest().getAttribute("lastAction");
			if (Util.strings.isNotEmpty(acao)) {
				link += "&ACTION=" + acao;
			}

			//Verifica URL Sufix
			link = WebUtils.rewriteUrl(link);

		}

		String cs = styleClass != null ? " class=\"" + styleClass + "\"" : "";

		return "<a href=\"" + link + "\"" + cs + " >" + label + "</a>";
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

	public String getItemClass() {
		return itemClass;
	}

	public void setItemClass(String itemClass) {
		this.itemClass = itemClass;
	}

	public String getSelectedClass() {
		return selectedClass;
	}

	public void setSelectedClass(String selectedClass) {
		this.selectedClass = selectedClass;
	}

	public String getUnselectedClass() {
		return unselectedClass;
	}

	public void setUnselectedClass(String unselectedClass) {
		this.unselectedClass = unselectedClass;
	}

}
