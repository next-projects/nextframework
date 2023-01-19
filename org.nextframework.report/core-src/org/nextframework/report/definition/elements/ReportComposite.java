package org.nextframework.report.definition.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.nextframework.report.definition.ReportParent;

/**
 * <P>Elements will be organized in line. 
 * <P>Their widths will be adjusted when the width is auto. When there is a fixed width it will be respected.
 * <P>If the elements do not fit in one row, other rows can be created inside this composite.
 * 
 * @author rogelgarcia
 *
 */
public class ReportComposite extends ReportItem implements ReportParent {

	private List<ReportItem> items = new ArrayList<ReportItem>();

	public ReportComposite() {
	}

	public ReportComposite(int width) {
		this.width = width;
	}

	public ReportComposite(ReportItem... itens) {
		this.items = Arrays.asList(itens);
	}

	public ReportComposite(List<ReportItem> itens) {
		super();
		this.items = itens;
	}

	public ReportComposite addItem(ReportItem e) {
		e.setParent(this);
		items.add(e);
		return this;
	}

	@Override
	public List<ReportItem> getChildren() {
		return items;
	}

	@Override
	public String getDescriptionName() {
		return "Composite";
	}

}
