package org.nextframework.summary;

import org.nextframework.summary.compilation.SummaryResult;

public class SummaryRow<ROW, SUMMARY extends Summary<ROW>> {

	ROW row;
	SUMMARY summary;
	boolean first;
	private String[] changedGroups;
	private int rowIndex;

	public SummaryRow(int rowIndex, ROW row, SUMMARY summary, boolean first, String[] changedGroups) {
		this.rowIndex = rowIndex;
		this.row = row;
		this.summary = summary;
		this.first = first;
		this.changedGroups = changedGroups;
	}

	public void setResult(SummaryResult<ROW, SUMMARY> summaryResult) {
		summary.setSummaryResult(summaryResult);
	}

	public boolean isGroupChanged(String group) {
		for (String g : changedGroups) {
			if (g.equals(group)) {
				return true;
			}
		}
		return false;
	}

	public String[] getChangedGroups() {
		return changedGroups;
	}

	public boolean isFirst() {
		return first;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public ROW getRow() {
		return row;
	}

	public SUMMARY getSummary() {
		return summary;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("[").append(row).append(", ").append(summary).append("]");
		return builder.toString();
	}

}
