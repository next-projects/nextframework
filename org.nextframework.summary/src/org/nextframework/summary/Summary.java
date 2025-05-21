package org.nextframework.summary;

import org.nextframework.summary.compilation.SummaryResult;

public abstract class Summary<E> {

	private E current;
	private SummaryResult<E, ? extends Summary<E>> summaryResult;

	void setSummaryResult(SummaryResult<E, ? extends Summary<E>> summaryResult) {
		this.summaryResult = summaryResult;
	}

	/**
	 * Can only be called by non @Variable and non @Group methods
	 * @return
	 */
	public SummaryResult<E, ? extends Summary<E>> getSummaryResult() {
		return summaryResult;
	}

	public E getCurrent() {
		return current;
	}

}
