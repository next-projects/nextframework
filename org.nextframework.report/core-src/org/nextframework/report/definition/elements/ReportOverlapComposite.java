package org.nextframework.report.definition.elements;

import java.util.List;

public class ReportOverlapComposite extends ReportComposite {
	
	@Override
	public int getWidth() {
		if(!isFieldWidthAuto()){
			return super.getWidth();
		}
		int maxWidth = -1;
		List<ReportItem> children = getChildren();
		for (ReportItem reportItem : children) {
			if(!reportItem.isWidthAuto() && ! reportItem.isWidthPercent()){
				maxWidth = Math.max(maxWidth, reportItem.getWidth());
			}
		}
		if(maxWidth == -1){
			return super.getWidth();
		}
		return maxWidth;
	}

	@Override
	public String getDescriptionName() {
		return "Overlap";
	}
}
