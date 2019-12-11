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
package org.nextframework.controller.crud;

import java.io.Serializable;

import org.nextframework.persistence.PageAndOrder;

public class ListViewFilter implements PageAndOrder, Serializable {

	private static final long serialVersionUID = 1L;

	public static final String FILTER = "FILTER";

	protected String EVENT = "";

	protected boolean notFirstTime = false;

	protected String orderBy = "";
	protected boolean asc = true;

	protected int pageSize = 30;
	protected int currentPage = 0;
	protected int numberOfPages = 1;
	protected int numberOfResults = 0;

	public String getEVENT() {
		return EVENT;
	}

	public boolean isNotFirstTime() {
		return notFirstTime;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public boolean isAsc() {
		return asc;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getNumberOfPages() {
		return numberOfPages;
	}

	public int getNumberOfResults() {
		return numberOfResults;
	}

	public void setEVENT(String list_event) {
		EVENT = list_event;
	}

	public void setNotFirstTime(boolean notFirstTime) {
		this.notFirstTime = notFirstTime;
	}

	public void setOrderBy(String orderBy) {
		if (this.orderBy != null && orderBy != null) {
			if (orderBy.equals(this.orderBy) && orderBy.length() > 0) {
				asc = !asc;
			}
		}
		currentPage = 0;
		this.orderBy = orderBy;
	}

	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public void setNumberOfPages(int numberOfPages) {
		this.numberOfPages = numberOfPages;
	}

	public void setNumberOfResults(int numberOfResults) {
		this.numberOfResults = numberOfResults;
	}

	@Override
	public boolean resetPage() {
		return getEVENT().equals(ListViewFilter.FILTER);
	}

}
