package org.nextframework.chart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class ChartDataRegrouper {

	private int maximumTopBySerie;

	public ChartDataRegrouper(int maximumTopBySerie) {
		this.maximumTopBySerie = maximumTopBySerie;
	}

	public void regroup(ChartData data) {

		if (maximumTopBySerie >= data.getSeries().length + 1) {
			return;
		}

		SortedSet<Integer> topSeries = new TreeSet<Integer>();
		List<ChartRow> rows = data.getData();
		for (ChartRow chartRow : rows) {
			Set<SerieValue> maximumValues = new TreeSet<SerieValue>();
			Number[] values = chartRow.getValues();
			for (int i = 0; i < values.length; i++) {
				if (values[i] != null && values[i].doubleValue() > 0) {
					maximumValues.add(new SerieValue(i, values[i]));
				}
			}
			Iterator<SerieValue> iterator = maximumValues.iterator();
			for (int i = 0; i < maximumTopBySerie && iterator.hasNext(); i++) {
				topSeries.add(iterator.next().serieIndex);
			}
		}

		data.setSeries(keepPositions(data.getSeries(), new Comparable<?>[topSeries.size() + 1], topSeries, new Grouper<Comparable<?>>() {

			@Override
			public void add(Comparable<?> e) {
			}

			@Override
			public Comparable<?> getComposite() {
				return "Outros";
			}

		}));

		for (ChartRow chartRow : rows) {
			Number[] newArray = new Number[topSeries.size() + 1];
			newArray[newArray.length - 1] = 5;
			chartRow.setValues(keepPositions(chartRow.getValues(), newArray, topSeries, new Grouper<Number>() {

				List<Number> v = new ArrayList<Number>();

				@Override
				public void add(Number e) {
					v.add(e);
				}

				@Override
				public Number getComposite() {
					Number sum = new Long(0);
					for (Number number : v) {
						if (number instanceof Double) {
							sum = new Double(sum.doubleValue());
						}
						if (sum instanceof Double) {
							sum = number.doubleValue() + sum.doubleValue();
						} else {
							sum = number.longValue() + sum.longValue();
						}
					}
					return sum;
				}

			}));
		}

	}

	private <E> E[] keepPositions(E[] original, E[] newArray, SortedSet<Integer> topSeries, Grouper<E> grouper) {
		Integer[] keep = topSeries.toArray(new Integer[topSeries.size()]);
		for (int i = 0; i < keep.length; i++) {
			newArray[i] = original[keep[i]];
		}
		for (int i = 0; i < original.length; i++) {
			if (topSeries.contains(i)) {
				continue;
			}
			grouper.add(original[i]);
		}
		newArray[newArray.length - 1] = grouper.getComposite();
		return newArray;
	}

	private interface Grouper<E> {

		public void add(E e);

		public E getComposite();

	}

	private static class SerieValue implements Comparable<SerieValue> {

		int serieIndex;
		Number value;

		public SerieValue(int serieIndex, Number value) {
			this.serieIndex = serieIndex;
			this.value = value;
		}

		@Override
		public int compareTo(SerieValue o) {
			if (this.value.doubleValue() < o.value.doubleValue()) {
				return 1;
			}
			if (this.value.doubleValue() > o.value.doubleValue()) {
				return -1;
			}
			return o.serieIndex - this.serieIndex;
		}

		@Override
		public String toString() {
			return String.format("[serieIndex=%s, value=%s]", serieIndex, value);
		}

	}

}
