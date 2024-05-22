package org.nextframework.chart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.nextframework.view.chart.aggregate.ChartAggregateFunction;
import org.nextframework.view.chart.aggregate.ChartSumAggregateFunction;

public class ChartDataTimeSeries extends ChartData {

	private static final long serialVersionUID = 4186924060414301304L;

	private boolean removeNullValues = true;

	public void setRemoveNullValues(boolean removeNullValues) {
		this.removeNullValues = removeNullValues;
	}

	public boolean isRemoveNullValues() {
		return removeNullValues;
	}

	public ChartDataTimeSeries(ChartData data) {
		this.data = data.data;
		this.series = data.series;
		this.groupTitle = data.groupTitle;
		this.seriesTitle = data.seriesTitle;
	}

	public ChartDataTimeSeries() {
		super();
	}

	public ChartDataTimeSeries(Object groupTitle, Object seriesTitle) {
		super(groupTitle, seriesTitle);
	}

	private int[] calendarGroupingProperties = new int[] {
			Calendar.YEAR, Calendar.MONTH, Calendar.DATE,
			Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND,
			Calendar.MILLISECOND };

	public int[] getCalendarGroupingProperties() {
		return calendarGroupingProperties;
	}

	public void setCalendarGroupingProperties(int[] calendarGroupingProperties) {
		this.calendarGroupingProperties = calendarGroupingProperties;
	}

	/**
	 * Agrupa os dados por uma unidade de tempo.
	 * Utilize as constantes da classe Calendar definidas no atributo calendarGroupingProperties.
	 * @param period
	 */
	public void groupBy(int period) {

		groupBy(period, new ChartSumAggregateFunction());
		//depois de agrupar.. ordenar pela data

		Set<ChartRow> rows = new TreeSet<ChartRow>(new Comparator<ChartRow>() {

			@Override
			@SuppressWarnings("all")
			public int compare(ChartRow o1, ChartRow o2) {
				if (o1.getGroup() == null) {
					if (o2 == null) {
						return 0;
					}
					return -1;
				}
				if (o2.getGroup() == null) {
					return 1;
				}
				return ((Comparable) o1.getGroup()).compareTo(o2.getGroup());
			}

		});

		rows.addAll(getData());
		this.data = new ArrayList<ChartRow>(rows);

	}

	public void groupBy(int period, ChartAggregateFunction chartAggregateFunction) {
		groupBy(period, new ChartAggregateFunction[] { chartAggregateFunction });
	}

	/**
	 * Agrupa os dados por uma unidade de tempo.
	 * Utilize as constantes da classe Calendar definidas no atributo calendarGroupingProperties.
	 * @param period
	 * @param aggregate
	 */
	@SuppressWarnings("unchecked")
	public void groupBy(int period, ChartAggregateFunction[] aggregate) {

		if (!getData().isEmpty()) {
			int seriesLength = getData().get(0).getValues().length;
			if (aggregate.length == 1 && seriesLength > 1) {
				ChartAggregateFunction[] aggregateFunctions = new ChartAggregateFunction[seriesLength];
				for (int i = 0; i < aggregateFunctions.length; i++) {
					aggregateFunctions[i] = aggregate[0];
				}
				aggregate = aggregateFunctions;
			}
			if (seriesLength != aggregate.length) {
				throw new IllegalArgumentException("The number of aggregate functions differ from the series length. There must be one aggregate function for each serie.");
			}
		}

		//converter todos os dates para Calendar para facilitar o cálculo
		for (ChartRow row : getData()) {
			if (row.getGroup() instanceof Date) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime((Date) row.getGroup());
				row.setGroup(calendar);
			}
		}

		List<ChartRow> rows = getData();
		for (int i = 0; i < rows.size(); i++) {
			ChartRow chartRow1 = rows.get(i);
			if (isRemoveNullValues() && chartRow1.getGroup() == null) {
				rows.remove(i--);
				continue;
			}
			for (int j = rows.size() - 1; j > i; j--) {
				ChartRow chartRow2 = rows.get(j);
				if (!isSameGrouping((Calendar) chartRow1.getGroup(), (Calendar) chartRow2.getGroup(), period)) {
					continue;
				}
				List<Number>[] seriesValues = new List[chartRow1.getValues().length];
				for (int k = 0; k < seriesValues.length; k++) {
					seriesValues[k] = new ArrayList<Number>();
				}
				for (int ri = i; ri <= j; ri++) {
					for (int k = 0; k < chartRow1.getValues().length; k++) {
						seriesValues[k].add(rows.get(ri).getValues()[k]);
					}
				}
				for (int ri = j; ri > i; ri--) {
					rows.remove(ri);
				}
				j = i;
				for (int k = 0; k < chartRow1.getValues().length; k++) {
					ChartAggregateFunction aggregateSeries = aggregate[k];
					chartRow1.getValues()[k] = aggregateSeries.aggregate(seriesValues[k]);
				}
			}
		}

	}

	private boolean isSameGrouping(Calendar c1, Calendar c2, int period) {
		if (c1 == null && c2 == null) {
			return true;
		}
		if (c1 == null && c2 != null || c1 != null && c2 == null) {
			return false;
		}
		int groupingIndex = Arrays.binarySearch(calendarGroupingProperties, period);
		for (int i = 0; i < groupingIndex + 1; i++) {
			int calendarPart = calendarGroupingProperties[i];
			if (c1.get(calendarPart) != c2.get(calendarPart)) {
				return false;
			}
		}
		return true;
	}

}
