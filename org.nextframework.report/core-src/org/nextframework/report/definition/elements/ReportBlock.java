package org.nextframework.report.definition.elements;

import java.util.ArrayList;
import java.util.List;

import org.nextframework.report.definition.ReportParent;

public class ReportBlock extends ReportItem implements ReportParent {

	List<ReportItem> itens = new ArrayList<ReportItem>();
	
	String element;
	
	public ReportBlock(String element){
		this.element = element;
	}
	
	public ReportBlock(String element, int width){
		this(element);
		this.width = width;
	}

	public boolean addItem(ReportItem e) {
		e.setParent(this);
		return itens.add(e);
	}
	
	@Override
	public List<ReportItem> getChildren() {
		return itens;
	}
	
	public String getElement() {
		return element;
	}

	@Override
	public String getDescriptionName() {
		return "Block";
	}
	
}
