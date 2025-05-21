package org.nextframework.persistence;

public interface PageAndOrder {

	String getOrderBy();

	boolean isAsc();

	int getPageSize();

	int getCurrentPage();

	int getNumberOfPages();

	int getNumberOfResults();

	void setCurrentPage(int page);

	void setNumberOfPages(int num);

	void setNumberOfResults(int results);

	boolean resetPage();

}
