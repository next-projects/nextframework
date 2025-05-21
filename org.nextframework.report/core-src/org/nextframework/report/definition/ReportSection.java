package org.nextframework.report.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextframework.report.definition.elements.style.ReportItemStyle;

public class ReportSection {

	private ReportSectionType type;
	private ReportDefinition definition;
	private ReportItemStyle style = new ReportItemStyle();
	private boolean render = true;
	private Map<String, Object> renderParameters = new HashMap<String, Object>();

	List<ReportSectionRow> rows = new ArrayList<ReportSectionRow>() {

		private static final long serialVersionUID = 1L;

		@Override
		public ReportSectionRow get(int index) {
			while (index >= rows.size()) {
				add(new ReportSectionRow(ReportSection.this, rows.size()));
			}
			return super.get(index);
		}

	};

	public Map<String, Object> getRenderParameters() {
		return renderParameters;
	}

	public ReportSection(ReportDefinition definition, ReportSectionType type) {
		super();
		this.definition = definition;
		this.type = type;
		rows.add(new ReportSectionRow(this, rows.size()));
	}

	public ReportItemStyle getStyle() {
		return style;
	}

	public List<ReportSectionRow> getRowsWithElements() {
		ArrayList<ReportSectionRow> rowsWithElements = new ArrayList<ReportSectionRow>();
		for (ReportSectionRow reportSectionRow : rows) {
			if (reportSectionRow.hasElements()) {
				rowsWithElements.add(reportSectionRow);
			}
		}
		return rowsWithElements;
	}

	public List<ReportSectionRow> getRows() {
		return rows;
	}

	public ReportSectionRow getRow(int index) {
		while (rows.size() < index) {
			rows.add(new ReportSectionRow(this, rows.size()));
		}
		return rows.get(index);
	}

	public ReportSectionRow insertRow(int i) {
		ReportSectionRow newRow = new ReportSectionRow(this, i);
		rows.add(i, newRow);
		for (int j = i + 1; j < rows.size(); j++) {
			rows.get(j).setRowNumber(j);
		}
		return newRow;
	}

	public ReportSectionType getType() {
		return type;
	}

	/**
	 * Finish this report line, and start a new one
	 */
	public void breakLine() {
		getRows().get(getRows().size());//cria uma nova linha
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getSimpleName() + " [").append(type).append("]");
		return builder.toString();
	}

	public ReportDefinition getDefinition() {
		return definition;
	}

	public boolean isRender() {
		return render;
	}

	public void setRender(boolean render) {
		this.render = render;
	}

}
