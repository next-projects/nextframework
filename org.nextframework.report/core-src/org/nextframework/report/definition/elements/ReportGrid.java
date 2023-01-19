package org.nextframework.report.definition.elements;

import org.nextframework.report.definition.ReportDefinition;

/**
 * <P> The elements will be organized in a grid with a configurable amount of columns.
 * <P> When one row is filled a new one is created.
 * <P> The elements inside this component always have auto width.
 * 
 * @author rogelgarcia
 *
 */
public class ReportGrid extends ReportComposite {

	private int[] columnWidths;

	public ReportGrid(int width, int[] columnWidths) {
		this(columnWidths);
		setWidth(width);
	}

	public ReportGrid(int[] columnWidths) {
		setColumnWidths(columnWidths);
	}

	public ReportGrid(Integer[] columnWidths) {
		setColumnWidths(columnWidths);
	}

	public ReportGrid(int columns, int gridWidth) {
		this(columns);
		setWidth(gridWidth);
	}

	public ReportGrid(int columns) {
		setColumns(columns);
	}

	public void setColumnWidths(int[] columnWidths) {
		this.columnWidths = columnWidths;
	}

	public void setColumnWidths(Integer[] columnWidths) {
		this.columnWidths = new int[columnWidths.length];
		int i = 0;
		for (Integer integer : columnWidths) {
			this.columnWidths[i++] = integer;
		}
	}

	public void setColumns(int columns) {
		int c1 = 100 / columns;
		int c2 = 100 - (columns - 1) * c1;
		columnWidths = new int[columns];
		for (int i = 0; i < columnWidths.length; i++) {
			if (i < columnWidths.length - 1) {
				columnWidths[i] = c1 | ReportConstants.PERCENT_WIDTH;
			} else {
				columnWidths[i] = c2 | ReportConstants.PERCENT_WIDTH;
			}
		}
	}

	public ReportGrid addItems(Object... elements) {
		for (int i = 0; i < elements.length; i++) {
			Object object = elements[i];
			if (object instanceof String) {
				elements[i] = ReportDefinition.convertStringToReportItem((String) object);
			}
		}
		for (Object object : elements) {
			addItem((ReportItem) object);
		}
		return this;
	}

	public ReportGrid addRow(Object... elements) {
		for (int i = 0; i < elements.length; i++) {
			Object object = elements[i];
			if (object instanceof String) {
				elements[i] = ReportDefinition.convertStringToReportItem((String) object);
			}
		}
		for (Object object : elements) {
			addItem((ReportItem) object);
		}
		if (columnWidths != null && columnWidths.length != elements.length) {
			throw new IllegalArgumentException("invalid number of columns " + elements.length + ", should be " + columnWidths.length);
		}
		if (columnWidths == null) {
			setColumns(elements.length);
		}
		return this;
	}

	@Override
	public int getWidth() {
		int sum = 0;
		if (columnWidths != null) {
			for (int cwidth : columnWidths) {
				if ((cwidth & ReportConstants.AUTO_WIDTH) == ReportConstants.AUTO_WIDTH
						|| (cwidth & ReportConstants.PERCENT_WIDTH) == ReportConstants.PERCENT_WIDTH) {
					return super.getWidth();
				}
				sum += cwidth;
			}
		} else {
			return super.getWidth();
		}
		return sum;
	}

	public int[] getColumnWidths() {
		return columnWidths;
	}

	@Override
	public ReportGrid setColspan(int colspan) {
		super.setColspan(colspan);
		return this;
	}

}
