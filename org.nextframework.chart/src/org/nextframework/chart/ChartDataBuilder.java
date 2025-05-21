package org.nextframework.chart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.summary.SummaryRow;
import org.nextframework.view.chart.aggregate.ChartAggregateFunction;
import org.nextframework.view.chart.aggregate.ChartExceptionAggregateFunction;
import org.nextframework.view.chart.aggregate.ChartSumAggregateFunction;

public class ChartDataBuilder {

	public static final String UNIQUE_SERIES = "UNIQUE";
	ChartAggregateFunction aggregateFunction = new ChartExceptionAggregateFunction();

	public static ChartData buildPropertiesAsSeries(List<?> objects, String groupProperty, String... serieProperties) {
		return new ChartDataBuilder().buildFromListPropertiesAsSeries(objects, groupProperty, serieProperties);
	}

	public static ChartData build(List<?> objects, String groupProperty) {
		return new ChartDataBuilder().buildFromList(objects, groupProperty, null, null);
	}

	/**
	 * Constrói um ChartData utilizando propriedades dos beans da lista para formar o grupo e as séries.<BR>
	 * Será feito a contagem dos itens da lista para cada conjunto de grupo e série para formar os valores.
	 * @param objects
	 * @param groupProperty
	 * @param seriesProperty
	 * @return
	 */
	public static ChartData build(List<?> objects, String groupProperty, String seriesProperty) {
		return new ChartDataBuilder().buildFromList(objects, groupProperty, seriesProperty, null);
	}

	/**
	 * Constrói um ChartData utilizando propriedades dos beans da lista para formar o grupo, as séries e os valores.<BR>
	 * Se houver conjuntos de grupos e séries repetidos, uma exceção será lançada.
	 * @param objects
	 * @param groupProperty
	 * @param seriesProperty
	 * @param valueProperty
	 * @return
	 */
	public static ChartData build(List<?> objects, String groupProperty, String seriesProperty, String valueProperty) {
		return new ChartDataBuilder().buildFromList(objects, groupProperty, seriesProperty, valueProperty);
	}

	/**
	 * Constrói um chart data builder somando valores duplicados
	 * @param objects
	 * @param groupProperty
	 * @param seriesProperty
	 * @param valueProperty
	 * @param aggregateFunction
	 * @return
	 */
	public static ChartData buildSum(List<?> objects, String groupProperty, String seriesProperty, String valueProperty) {
		ChartDataBuilder chartDataBuilder = new ChartDataBuilder();
		chartDataBuilder.setAggregateFunction(new ChartSumAggregateFunction());
		return chartDataBuilder.buildFromList(objects, groupProperty, seriesProperty, valueProperty);
	}

	public static ChartData build(List<?> objects, String groupProperty, String seriesProperty, String valueProperty, ChartAggregateFunction aggregateFunction) {
		ChartDataBuilder chartDataBuilder = new ChartDataBuilder();
		chartDataBuilder.setAggregateFunction(aggregateFunction);
		return chartDataBuilder.buildFromList(objects, groupProperty, seriesProperty, valueProperty);
	}

	@SuppressWarnings("all")
	public ChartData buildFromListPropertiesAsSeries(List<?> objects, String groupProperty, String... serieProperties) {

		if (objects.size() == 0) {
			return new ChartData();
		}

		ChartData data = new ChartData();
		List<String> serieTitles = new ArrayList<String>();

		BeanDescriptor bd = BeanDescriptorFactory.forBean(objects.get(0));
		for (String serieProperty : serieProperties) {
			//TODO TRY TO REMOVE THIS DEPENDENCY 2012-08-07
			if (objects.get(0).getClass().isAssignableFrom(SummaryRow.class) && serieProperty.startsWith("summary.")) {
				SummaryRow<?, ?> summaryRow = (SummaryRow<?, ?>) objects.get(0);
				Class<?> superclass = summaryRow.getSummary().getClass().getSuperclass();
				BeanDescriptor bdSummary = BeanDescriptorFactory.forClass(superclass);//get SummaryClass not the compiled one
				PropertyDescriptor propertyDescriptor = bdSummary.getPropertyDescriptor(serieProperty.substring("summary.".length()));
				serieTitles.add(propertyDescriptor.getDisplayName());
			} else {
				serieTitles.add(bd.getPropertyDescriptor(serieProperty).getDisplayName());
			}
		}

		data.setSeries(serieTitles.toArray(new String[serieTitles.size()]));

		for (Object object : objects) {
			BeanDescriptor beanDescriptor = BeanDescriptorFactory.forBean(object);
			Number[] values = new Number[serieProperties.length];
			ChartRow row = new ChartRow(beanDescriptor.getPropertyDescriptor(groupProperty).getValue(), values);
			int i = 0;
			for (String serie : serieProperties) {
				Number v = (Number) beanDescriptor.getPropertyDescriptor(serie).getValue();
				values[i++] = v;
			}
			data.addRow(row);
		}

		return data;
	}

	public ChartData buildFromList(List<?> objects, String groupProperty, String seriesProperty, String valueProperty) {

		ChartData data = new ChartData();
		Set<Comparable<?>> series;
		if (seriesProperty != null) {
			//procurar as series
			series = new TreeSet<Comparable<?>>(getUniversalComparator());

			for (Object bean : objects) {
				BeanDescriptor beanDescriptor = BeanDescriptorFactory.forBean(bean);
				Object serie = getSerieValue(beanDescriptor, seriesProperty);
				if (serie == null) {
					serie = "";
				}
				if (!series.contains(serie)) { //TODO THE SERIES COMPARATOR MUST UNDERSTAND DIFFERENT TYPES
					series.add((Comparable<?>) serie);
				}
			}

		} else {
			series = new TreeSet<Comparable<?>>(Arrays.asList(UNIQUE_SERIES));
		}
		data.setSeries(series.toArray(new Comparable[series.size()]));

		Map<Object, Map<Comparable<?>, List<Number>>> valuesMap = new LinkedHashMap<Object, Map<Comparable<?>, List<Number>>>();
		for (Object bean : objects) {
			BeanDescriptor beanDescriptor = BeanDescriptorFactory.forBean(bean);
			Object group = getGroupNullSafe(groupProperty, beanDescriptor);
			//String groupString = convertToString(group);
			HashMap<Comparable<?>, List<Number>> seriesMapForGroup = new HashMap<Comparable<?>, List<Number>>();
			valuesMap.put(group, seriesMapForGroup);
		}

		Set<?> groups = valuesMap.keySet();
		for (Object group : groups) {
			Map<Comparable<?>, List<Number>> serieValueMap = valuesMap.get(group);
			for (Object bean : objects) {
				BeanDescriptor beanDescriptor = BeanDescriptorFactory.forBean(bean);
				Object beanGroup = getGroupNullSafe(groupProperty, beanDescriptor);
				if ((group == null && beanGroup != null) || (group != null && !group.equals(beanGroup))) {
					continue;
				}
				Comparable<?> serie = getSerieValue(beanDescriptor, seriesProperty);
//				Comparable<?> serie = (Comparable<?>) (seriesProperty != null? beanDescriptor.getPropertyDescriptor(seriesProperty).getValue() : UNIQUE_SERIES);
				try {
					List<Number> numbers = serieValueMap.get(serie);
					if (numbers == null) {
						numbers = new ArrayList<Number>();
						serieValueMap.put(serie, numbers);
					}
					if (valueProperty == null) {
						numbers.add(1);
					} else {
						numbers.add((Number) beanDescriptor.getPropertyDescriptor(valueProperty).getValue());
					}
				} catch (Exception e) {
					throw new RuntimeException("Erro ao configurar grupo " + group + " e série " + serie, e);
				}
			}
		}

		for (Object group : groups) {
			Map<Comparable<?>, List<Number>> serieValueMap = valuesMap.get(group);
			Number[] values = new Number[series.size()];
			ChartRow row = new ChartRow(group, values);
			int i = 0;
			for (Object serie : series) {
				List<Number> valuesList = serieValueMap.get(serie);
				Number serieValue = aggregate(valuesList);
				if (serieValue == null) {
					serieValue = 0;
				}
				values[i++] = serieValue;
			}
			data.addRow(row);
		}

		return data;
	}

	private Number aggregate(List<Number> list) {
		return aggregateFunction.aggregate(list);
	}

	public Comparator<Object> getUniversalComparator() {
		return new Comparator<Object>() {

			@SuppressWarnings("all")
			@Override
			public int compare(Object o1, Object o2) {
				if (o1 == null && o2 == null) {
					return 0;
				} else if (o1 == null) {
					return 1;
				} else if (o2 == null) {
					return -1;
				} else if (o1.getClass().equals(o2.getClass())) {
					return ((Comparable) o1).compareTo(o2);
				}
				return o1.getClass().getName().compareTo(o2.getClass().getName());
			}

		};
	}

	public Comparable<?> getSerieValue(BeanDescriptor beanDescriptor, String seriesProperty) {
		if (seriesProperty == null) {
			return UNIQUE_SERIES;
		}
		Object serie = beanDescriptor.getPropertyDescriptor(seriesProperty).getValue();
		if (!(serie instanceof Comparable<?>)) {
			if (serie == null) {
				return null; //may crash if the other series is not of the same type
			}
			serie = BeanDescriptorFactory.forBean(serie).getDescription();
			if (!(serie instanceof Comparable<?>)) {
				throw new RuntimeException("O valor da série " + serie + " da propriedade" + seriesProperty + " não implementa Comparable");
			}
		}
		return (Comparable<?>) serie;
	}

	private Object getGroupNullSafe(String groupProperty, BeanDescriptor beanDescriptor) {
		return groupProperty != null ? beanDescriptor.getPropertyDescriptor(groupProperty).getValue() : "UNIQUE";
	}

	public ChartAggregateFunction getAggregateFunction() {
		return aggregateFunction;
	}

	public void setAggregateFunction(ChartAggregateFunction aggregateFunction) {
		this.aggregateFunction = aggregateFunction;
	}

}
