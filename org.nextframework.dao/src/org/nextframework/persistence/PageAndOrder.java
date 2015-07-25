package org.nextframework.persistence;

public interface PageAndOrder {

	void setNumberOfResults(int numberOfResults);
	
	int getNumberOfResults();

	int getPageSize();

	void setNumberOfPages(int i);
	
	int getNumberOfPages();

	int getCurrentPage();

	void setCurrentPage(int page);

	String getOrderBy();

	boolean isAsc();
	
	boolean resetPage();

}
