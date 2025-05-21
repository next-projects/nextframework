package org.nextframework.report.definition;

import java.util.List;

import org.nextframework.report.definition.elements.ReportItem;

public interface ReportParent {

	List<ReportItem> getChildren();

}
