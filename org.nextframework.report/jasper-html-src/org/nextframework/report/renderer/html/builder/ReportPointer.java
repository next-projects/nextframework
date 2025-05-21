package org.nextframework.report.renderer.html.builder;

import java.util.ArrayList;
import java.util.List;

import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.elements.ReportItem;
import org.nextframework.report.definition.elements.Subreport;

public class ReportPointer {

	ReportPointer parent;

	List<ReportPointer> children = new ArrayList<ReportPointer>();

	ReportDefinition definition;

	Subreport subreport;

	public ReportPointer(ReportDefinition definition) {
		this.definition = definition;
		config();
	}

	public ReportPointer(Subreport reportItem) {
		this(reportItem.getReport());
		this.subreport = reportItem;
	}

	private void config() {
		List<ReportItem> reportChildren = definition.getChildren();
		for (ReportItem reportItem : reportChildren) {
			if (reportItem instanceof Subreport) {
				if (!reportItem.getRow().getSection().isRender()) {
					//2015-07-03 solves bug of hidden section
					//if the section of the subreport is not rendered, 
					//this subreport will not appear
					continue;
				}
				ReportPointer child = new ReportPointer((Subreport) reportItem);
				child.parent = this;
				children.add(child);
			}
		}
	}

	@Override
	public String toString() {
		return toString("");
	}

	private String toString(String padding) {
		String reportName = definition.getReportName();
		StringBuilder sb = new StringBuilder(padding + reportName + "\n");
		for (ReportPointer c : children) {
			sb.append(padding + "   |" + c.toString(padding + "  "));
		}
		return sb.toString();
	}

	int childIndex = 0;

	public ReportPointer next() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		if (children.size() > 0 && children.size() > childIndex) {
			return children.get(childIndex++);
		} else {
			childIndex = 0;
		}
		if (parent == null) {
			//has returned to the begining
			return children.get(childIndex++);
		}
		return parent;
	}

}
