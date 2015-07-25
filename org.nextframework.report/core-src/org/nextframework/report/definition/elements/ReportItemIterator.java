package org.nextframework.report.definition.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportParent;

public class ReportItemIterator implements Iterator<ReportItem> {
	
	Iterator<ReportItem> iterator;
	
	public ReportItemIterator(ReportDefinition definition){
		readItens(definition);
	}
	
	public ReportItemIterator(ReportItem item){
		iterator = readItens(Arrays.asList(item)).iterator();
	}

	private void readItens(ReportDefinition definition) {
		iterator = readItens(definition.getChildren()).iterator();
	}

	private List<ReportItem> readItens(List<ReportItem> children) {
		List<ReportItem> result = new ArrayList<ReportItem>();
		for (ReportItem reportItem : children) {
			result.add(reportItem);
		}
		for (ReportItem reportItem : children) {
			if(reportItem instanceof ReportParent){
				result.addAll(readItens(((ReportParent) reportItem).getChildren()));
			}
		}
		return result;
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public ReportItem next() {
		return iterator.next();
	}

	public void remove() {
		iterator.remove();
	}


}
