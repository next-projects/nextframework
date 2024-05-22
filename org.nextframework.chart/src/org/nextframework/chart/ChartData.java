package org.nextframework.chart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ChartData implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Object groupTitle;
	protected Object seriesTitle;
	protected List<ChartRow> data = new ArrayList<ChartRow>();
	protected Comparable<?>[] series;

	public ChartData() {
		series = new String[0];
	}

	public ChartData(Object groupTitle, Object seriesTitle) {
		this.groupTitle = groupTitle;
		this.seriesTitle = seriesTitle;
		series = new String[0];
	}

	public void addRow(Object group, Number... values) {
		addRow(new ChartRow(group, values));
	}

	public void addRow(ChartRow row) {
		if (series == null || row.getValues().length != series.length) {
			throw new RuntimeException("O número de series da linha \"" + row + "\" é diferente do configurado no ChartData \"" + Arrays.deepToString(series) + "\". Chame o método setSeries do ChartData para configurar as series corretamente. Deveria configurar " + row.getValues().length + " serie(s).");
		}
		data.add(row);
	}

	public ChartData invert() {
		List<Comparable<?>> newSeries = new ArrayList<Comparable<?>>();
		for (ChartRow row : getData()) {
			newSeries.add((Comparable<?>) row.getGroup());
		}
		ChartData other = new ChartData(this.seriesTitle, this.groupTitle);
		other.setSeries(newSeries.toArray(new Comparable<?>[newSeries.size()]));
		int i = 0;
		for (Object toRowGroup : this.getSeries()) {
			List<Number> newValues = new ArrayList<Number>();
			for (ChartRow row : getData()) {
				newValues.add(row.getValues()[i]);
			}
			ChartRow row = new ChartRow(toRowGroup, newValues.toArray(new Number[newValues.size()]));
			other.addRow(row);
			i++;
		}
		return other;
	}

	public void regroup(int maximumNumberOfSeries) {
		new ChartDataRegrouper(maximumNumberOfSeries).regroup(this);
	}

	@SuppressWarnings("rawtypes")
	public void limit(int maximumNumberOfSeries) {
		new ChartDataRegrouper(maximumNumberOfSeries).regroup(this);
		for (ChartRow row : data) {
			Number[] values = row.getValues();
			Number[] newValues = new Number[values.length - 1];
			System.arraycopy(values, 0, newValues, 0, newValues.length);
			row.setValues(newValues);
		}
		Comparable[] newSeries = new Comparable[series.length - 1];
		System.arraycopy(series, 0, newSeries, 0, newSeries.length);
		this.series = newSeries;
	}

	public void removeEmptyGroups() {
		for (Iterator<ChartRow> iterator = data.iterator(); iterator.hasNext();) {
			ChartRow row = iterator.next();
			if (row.getGroup() == null || row.getGroup().toString().equals("")) {
				iterator.remove();
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public void removeEmptySeries() {
		int emptySerieIndex = -1;
		int index = 0;
		for (Comparable<?> serie : series) {
			if (serie == null || serie.toString().equals("")) {
				emptySerieIndex = index;
			}
			index++;
		}
		if (emptySerieIndex >= 0) {
			for (ChartRow row : data) {
				Number[] values = row.getValues();
				Number[] newValues = new Number[values.length - 1];
				System.arraycopy(values, 0, newValues, 0, emptySerieIndex);
				System.arraycopy(values, emptySerieIndex + 1, newValues, emptySerieIndex, newValues.length - emptySerieIndex);
				row.setValues(newValues);
			}
			Comparable[] newSeries = new Comparable[series.length - 1];
			System.arraycopy(series, 0, newSeries, 0, emptySerieIndex);
			System.arraycopy(series, emptySerieIndex + 1, newSeries, emptySerieIndex, newSeries.length - emptySerieIndex);
			series = newSeries;
		}
	}

	public Object getGroupTitle() {
		return groupTitle;
	}

	public void setGroupTitle(Object groupTitle) {
		this.groupTitle = groupTitle;
	}

	public Object getSeriesTitle() {
		return seriesTitle;
	}

	public void setSeriesTitle(Object seriesTitle) {
		this.seriesTitle = seriesTitle;
	}

	public Comparable<?>[] getSeries() {
		return series;
	}

	public void setSeries(Comparable<?>... series) {
		this.series = series;
	}

	public List<ChartRow> getData() {
		return data;
	}

}
