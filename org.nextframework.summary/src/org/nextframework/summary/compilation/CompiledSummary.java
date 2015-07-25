package org.nextframework.summary.compilation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;

import org.nextframework.summary.Summary;
import org.nextframework.summary.SummaryRow;

public abstract class CompiledSummary<SUMARY extends Summary<ROW>, ROW> {

	public SummaryResult<ROW, SUMARY> createSummaryResult(Collection<ROW> rows) {
		if(rows instanceof SortedSet<?>){
			return createSummaryResult(rows, false);
		}
		return createSummaryResult(rows, true);
	}
	
	public SummaryResult<ROW, SUMARY> createSummaryResult(Collection<ROW> rows, boolean reorderGroups) {
		if(rows == null){
			throw new IllegalArgumentException("rows cannot be null");
		}
		if(reorderGroups){
			List<RowHolder<ROW>> holdersCollection = getHolders(rows);
			Collections.sort(holdersCollection, new Comparator<RowHolder<ROW>>() {

				@SuppressWarnings("all")
				@Override
				public int compare(RowHolder<ROW> o1, RowHolder<ROW> o2) {
					Comparable<?>[] groupValues1 = getGroupValuesForRow(o1.row);
					Comparable<?>[] groupValues2 = getGroupValuesForRow(o2.row);
					for (int i = 0; i < groupValues1.length; i++) {
						Comparable comparable1 = groupValues1[i];
						Comparable comparable2 = groupValues2[i];
						if((comparable1 == null && comparable2 == null)
							 || (comparable1 != null && comparable1.equals(comparable2))){
							continue;
						}
						if(comparable2 == null && comparable1 != null){
							return 1;
						}
						if(comparable1 == null && comparable2 != null){
							return -1;
						}
						int diference = comparable1.compareTo(comparable2);
						if(diference != 0){
							return diference;
						}
					}
					return o1.rowIndex - o2.rowIndex; //keep original order
				}

			});
			rows = new ArrayList<ROW>();
			for (RowHolder<ROW> rowHolder : holdersCollection) {
				rows.add(rowHolder.row);
			}
			
		}
		List<SummaryRow<ROW, SUMARY>> result = new ArrayList<SummaryRow<ROW,SUMARY>>();
		SummaryResult<ROW, SUMARY> summaryResult = new SummaryResult<ROW, SUMARY>(result, getSummaryClass());
		int i = 0;
		for (ROW row : rows) {
			SummaryRow<ROW, SUMARY> summaryRow = onNewRow(row, i++);
			summaryRow.setResult(summaryResult);
			result.add(summaryRow);
		}
		return summaryResult;
	}

	private List<RowHolder<ROW>> getHolders(Collection<ROW> rows) {
		List<RowHolder<ROW>> result = new ArrayList<CompiledSummary.RowHolder<ROW>>();
		int index = 0;
		for (ROW row : rows) {
			result.add(new RowHolder<ROW>(row, index++));
		}
		return result;
	}
	
	protected abstract Comparable<?>[] getGroupValuesForRow(ROW o1);
	
	protected abstract SummaryRow<ROW, SUMARY> onNewRow(ROW row, int rowIndex);
	
	protected abstract Class<SUMARY> getSummaryClass();

	private static class RowHolder<ROW> {
		ROW row;
		int rowIndex;
		public RowHolder(ROW row, int rowIndex) {
			this.row = row;
			this.rowIndex = rowIndex;
		}
	}
	
}
