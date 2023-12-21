package org.nextframework.report.definition.builder;

import java.beans.PropertyEditor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.BeanDescriptorUtils;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.chart.Chart;
import org.nextframework.chart.ChartData;
import org.nextframework.chart.ChartDataBuilder;
import org.nextframework.chart.ChartRow;
import org.nextframework.chart.ChartStyle.LegendPosition;
import org.nextframework.chart.ChartType;
import org.nextframework.report.definition.ReportGroup;
import org.nextframework.report.definition.ReportSection;
import org.nextframework.report.definition.elements.ReportChart;
import org.nextframework.report.definition.elements.ReportConstants;
import org.nextframework.report.definition.elements.ReportGrid;
import org.nextframework.report.definition.elements.ReportItem;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.definition.elements.ReportTextField;
import org.nextframework.report.definition.elements.Subreport;
import org.nextframework.report.definition.elements.style.Border;
import org.nextframework.report.definition.elements.style.ReportAlignment;
import org.nextframework.report.definition.elements.style.ReportItemStyle;
import org.nextframework.report.renderer.ReportBuilderValueConverter;
import org.nextframework.report.renderer.ValueConverter;
import org.nextframework.report.renderer.jasper.builder.JasperRenderParameters;
import org.nextframework.summary.Summary;
import org.nextframework.summary.SummaryRow;
import org.nextframework.summary.compilation.SummaryResult;
import org.nextframework.summary.definition.SummaryDefinition;
import org.nextframework.summary.definition.SummaryGroupDefinition;
import org.nextframework.view.chart.aggregate.ChartSumAggregateFunction;

import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.VerticalAlignEnum;

public abstract class BaseReportBuilder extends AbstractReportBuilder {

	public static final String LOCALE = "LOCALE";
	public static final String CONVERTER = "CONVERTER";

	protected Locale locale;
	protected ValueConverter valueConverter = new ReportBuilderValueConverter();
	protected SummaryResult<?, ? extends Summary<?>> summaryResult;
	protected boolean sumarizedData = false;
	protected List<?> data;

	protected Map<String, GroupSetup> groupSetups = new HashMap<String, GroupSetup>();

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public void setValueConverter(ValueConverter valueConverter) {
		this.valueConverter = valueConverter;
	}

	public <E> void setData(SummaryResult<E, ? extends Summary<E>> summaryResult) {
		this.summaryResult = summaryResult;
		setData(summaryResult.getItems());
	}

	@SuppressWarnings("rawtypes")
	public void setData(List<?> items) {
		if (items.size() > 0) {
			Object item0 = items.get(0);
			if (item0 instanceof SummaryRow) {
				sumarizedData = true;
				if (getSummaryClass() != null && !getSummaryClass().isAssignableFrom(((SummaryRow) item0).getSummary().getClass())) {
					throw new RuntimeException("The data Summary class is not of the same type defined by the method getSummaryClass(). Found " + ((SummaryRow) items.get(0)).getSummary().getClass().getName() + " expected " + getSummaryClass().getName());
				}
			}
		}
		this.data = items;
	}

	@SuppressWarnings("unchecked")
	protected <E, X extends Summary<E>> SummaryResult<E, X> recalculateData(Class<X> summaryClass) {
		return SummaryResult.createFrom((List<E>) summaryResult.getRowItens(), summaryClass);
	}

	protected Class<? extends Summary<?>> getSummaryClass() {
		if (summaryResult == null) {
			return null;
		}
		return summaryResult.getSummaryClass();
	}

	protected Class<?> getRowClass() {
		Class<? extends Summary<?>> summaryClass = getSummaryClass();
		if (summaryClass == null) {
			throw new NullPointerException(this.getClass().getName() + " row class or summary class not set. Override method getRowClass or getSummaryClass");
		}
		return getSummaryClassType(summaryClass);
	}

	private Class<?> getSummaryClassType(Class<? extends Summary<?>> summaryClass) {
		try {
			Class<?> superClass = summaryClass;
			while (superClass.getSuperclass() != null && !superClass.getSuperclass().equals(Summary.class)) {
				superClass = superClass.getSuperclass();
			}
			ParameterizedType genericSuperclass = (ParameterizedType) superClass.getGenericSuperclass();
			return (Class<?>) genericSuperclass.getActualTypeArguments()[0];
		} catch (Exception e) {
			throw new ReportBuilderException("Cannot determine generic type for summary class " + summaryClass.getName() + "", e);
		}
	}

	private BeanDescriptor rowClassBeanDescriptorCache;

	protected BeanDescriptor getRowClassBeanDescriptorCache() {
		if (rowClassBeanDescriptorCache == null) {
			rowClassBeanDescriptorCache = BeanDescriptorFactory.forClass(getRowClass());
		}
		return rowClassBeanDescriptorCache;
	}

	private BeanDescriptor summaryClassBeanDescriptorCache;

	protected BeanDescriptor getSummaryClassBeanDescriptorCache() {
		if (summaryClassBeanDescriptorCache == null) {
			summaryClassBeanDescriptorCache = BeanDescriptorFactory.forClass(getSummaryClass());
		}
		return summaryClassBeanDescriptorCache;
	}

	@Override
	protected void configureDefinition() {

		configureBasicParameters();

		getDefinition().setData(data);

		if (isSetupGroups()) {
			setupGroups();
		}

	}

	protected void configureBasicParameters() {
		getDefinition().setParameter(LOCALE, locale);
		getDefinition().setParameter(CONVERTER, valueConverter);
	}

	protected boolean isSetupGroups() {
		return true;
	}

	@SuppressWarnings("unchecked")
	protected void setupGroups() {
		Class<? extends Summary<?>> summaryClass = getSummaryClass();
		if (summaryClass != null) {
			SummaryDefinition<? extends Summary<?>> def = new SummaryDefinition<Summary<?>>((Class<Summary<?>>) summaryClass);
			Set<SummaryGroupDefinition> groups = def.getGroups();
			int index = 0;
			for (SummaryGroupDefinition summaryGroupDefinition : groups) {
				setupGroup(summaryGroupDefinition, index++);
			}
		}
	}

	protected void setupGroup(SummaryGroupDefinition summaryGroupDefinition, int index) {
		String groupName = summaryGroupDefinition.getName();
		Method groupDefinitionMethod = summaryGroupDefinition.getMethod();
		String expression = "summary." + BeanDescriptorUtils.getPropertyFromGetter(groupDefinitionMethod.getName());
		expression = configureGroupDetailedProperty(groupName, groupDefinitionMethod, expression);
		setupGroup(groupName, expression, index);
	}

	public String configureGroupDetailedProperty(String groupName, Method groupDefinitionMethod, String expression) {
		Class<?> returnType = groupDefinitionMethod.getReturnType();
		if (!returnType.getName().startsWith("java.") && isAutoConfigureDescriptionPropertyForGroup(groupName)) {
			String descriptionPropertyName = BeanDescriptorFactory.forClass(returnType).getDescriptionPropertyName();
			if (descriptionPropertyName != null) {
				expression += "." + descriptionPropertyName;
			}
		}
		return expression;
	}

	protected boolean isAutoConfigureDescriptionPropertyForGroup(String groupName) {
		return true;
	}

	protected void setupGroup(String groupName, String expression, int index) {

		ReportGroup reportGroup = getDefinition().createGroup(expression);

		GroupSetup groupSetup = new GroupSetup();
		groupSetup.setGroupExpression(expression);
		groupSetup.setGroupName(groupName);
		groupSetup.setReportGroup(reportGroup);

		groupSetups.put(groupName, groupSetup);

		onSetupGroup(groupName, groupSetup, index);

	}

	protected void onSetupGroup(String groupName, GroupSetup groupSetup, int index) {
	}

	protected Map<String, GroupSetup> getGroupSetups() {
		return groupSetups;
	}

	protected GroupSetup getGroupSetup(String group) {
		return groupSetups.get(group);
	}

	protected void setTitle(String title, String subtitle) {
		setTitle(title);
		setSubtitle(subtitle);
	}

	protected void setTitle(String title) {
		getDefinition().setTitle(title);
	}

	protected void setSubtitle(String subtitle) {
		getDefinition().setSubtitle(subtitle);
	}

	protected ReportItemStyle style() {
		return new ReportItemStyle();
	}

	@SuppressWarnings("deprecation")
	protected ReportLabel separator(String text, int colspan) {
		ReportLabel reportLabel = new ReportLabel(text);
		reportLabel.setColspan(colspan);
		reportLabel.setHeight(2);
		reportLabel.getStyle().setBorderBottom(new Border(1));
		reportLabel.setRenderParameter(JasperRenderParameters.POSITION_TYPE, PositionTypeEnum.FLOAT);
		reportLabel.setRenderParameter("jasper-vertical-alignment", VerticalAlignEnum.BOTTOM);
		return reportLabel;
	}

	protected ReportLabel label(String text) {
		return label(text, style());
	}

	protected ReportLabel label(String text, ReportItemStyle style) {
		ReportLabel reportLabel = new ReportLabel(text);
		reportLabel.setStyle(style);
		return reportLabel;
	}

	protected ReportLabel label(String text, boolean bold) {
		return label(text, style().setBold(bold));
	}

	protected ReportLabel label(String text, ReportAlignment alignment) {
		return new ReportLabel(text, alignment);
	}

	protected ReportLabel label(String text, ReportAlignment alignment, ReportItemStyle style) {
		ReportLabel reportLabel = new ReportLabel(text);
		style.setAlignment(alignment);
		reportLabel.setStyle(style);
		return reportLabel;
	}

	protected ReportLabel label(String text, int colspan) {
		return new ReportLabel(text).setColspan(colspan);
	}

	protected ReportLabel label(String text, int colspan, ReportAlignment alignment) {
		return new ReportLabel(text, alignment).setColspan(colspan);
	}

	protected ReportTextField field(String fieldExpression) {
		return field(fieldExpression, style());
	}

	protected ReportTextField field(String fieldExpression, ReportAlignment alignment) {
		return field(fieldExpression, style().setAlignment(alignment));
	}

	protected ReportTextField field(String fieldExpression, boolean forceRow) {
		return field(fieldExpression, style(), forceRow);
	}

	protected ReportTextField field(String fieldExpression, ReportItemStyle style) {
		return field(fieldExpression, style, sumarizedData);
	}

	protected ReportTextField field(String fieldExpression, ReportItemStyle style, boolean forceRow) {
		if (!fieldExpression.startsWith("row.") && !fieldExpression.startsWith("summary.")) {
			if (forceRow) {
				fieldExpression = "row." + fieldExpression;
			}
		}
		ReportTextField reportTextField = new ReportTextField(fieldExpression);
		if (style == null) {
			throw new IllegalArgumentException("style cannot be null");
		}
		reportTextField.setStyle(style);
		FieldConfig config = null;
		if (!reportTextField.isLiteral()) {
			if (fieldExpression.startsWith("param.")) {
				//parameters does not have config
			} else if (fieldExpression.startsWith("summary.")) {
				config = getConfigForSummaryField(fieldExpression.substring("summary.".length()));
			} else if (fieldExpression.startsWith("row.")) {
				config = getConfigForRowField(fieldExpression.substring("row.".length()));
			} else {
				config = getConfigForRowField(fieldExpression);
			}
		}
		if (config != null) {
			if (style.getAlignment() == null) {
				reportTextField.getStyle().setAlignment(config.alignment);
			}
			reportTextField.setPattern(config.pattern);
		}
		return reportTextField;
	}

	public static class FieldConfig {

		public String label;
		public String originalExpression;
		public String reportExpression;
		public String preffix;
		public String pattern;
		public ReportAlignment alignment;

		public FieldConfig(String label, String originalExpression, String preffix, String reportExpression, String pattern, ReportAlignment alignment) {
			this.label = label;
			this.originalExpression = originalExpression;
			this.preffix = preffix;
			this.reportExpression = reportExpression;
			this.pattern = pattern;
			this.alignment = alignment;
		}

	}

	protected FieldConfig getConfigForSummaryField(String fieldName) {
		if (fieldName.startsWith("summary.")) {
			fieldName = fieldName.substring("summary.".length());
		}
		String fieldPreffix = "summary.";
		return getConfigForBeanDescriptor(fieldName, fieldPreffix, getSummaryClassBeanDescriptorCache());
	}

	protected FieldConfig getConfigForRowField(String fieldName) {
		if (fieldName.startsWith("row.")) {
			fieldName = fieldName.substring("row.".length());
		}
		String fieldPreffix = sumarizedData ? "row." : null;
		return getConfigForBeanDescriptor(fieldName, fieldPreffix, getRowClassBeanDescriptorCache());
	}

	protected FieldConfig getConfigForBeanDescriptor(String fieldName, String fieldPreffix, BeanDescriptor beanDescriptor) {

		PropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(fieldName);
		String label = propertyDescriptor.getDisplayName();
		String pattern = null;
		ReportAlignment alignment = null;

		Type type = propertyDescriptor.getType();
		if (type instanceof Class<?>) {

			Class<?> clazz = (Class<?>) type;

			if (Number.class.isAssignableFrom(clazz)) {

				alignment = ReportAlignment.RIGHT;
				if (clazz.getName().startsWith("java")) {
					pattern = "#,##0";
					if (Float.class.isAssignableFrom(clazz) || Double.class.isAssignableFrom(clazz) || BigDecimal.class.isAssignableFrom(clazz)) {
						pattern = "#,##0.00";
					}
				}

			} else if (Date.class.isAssignableFrom(clazz)) {

				pattern = "dd/MM/yyyy";

			} else if (Calendar.class.isAssignableFrom(clazz)) {

				pattern = "dd/MM/yyyy";

			}

		}

		String reportExpression = fieldName;
		if (fieldPreffix != null) {
			reportExpression = fieldPreffix + fieldName;
		}

		return createFieldConfig(this, beanDescriptor, propertyDescriptor,
				label, fieldName, fieldPreffix, reportExpression,
				pattern, alignment);
	}

	protected FieldConfig createFieldConfig(BaseReportBuilder builder, BeanDescriptor beanDescriptor, PropertyDescriptor propertyDescriptor,
			String label, String fieldName, String fieldPreffix, String reportExpression,
			String pattern, ReportAlignment alignment) {
		return new FieldConfig(label, fieldName, fieldPreffix, reportExpression, pattern, alignment);
	}

	protected ReportGrid grid() {
		return grid(1);
	}

	protected ReportGrid grid(int columns) {
		return new ReportGrid(columns);
	}

	protected ReportGrid grid(Object... elements) {
		List<Integer> widths = new ArrayList<Integer>();
		int sizeElements = 0;
		for (Object object : elements) {
			if (object instanceof Integer) {
				sizeElements++;
				widths.add((Integer) object);
			}
		}
		if (widths.size() > 0) {
			Object[] newElements = new Object[elements.length - sizeElements];
			System.arraycopy(elements, widths.size(), newElements, 0, newElements.length);
			Integer[] widthConfig = widths.toArray(new Integer[widths.size()]);
			ReportGrid grid = new ReportGrid(widthConfig);
			for (Object newElement : newElements) {
				grid.addItem((ReportItem) newElement);
			}
			return grid;
		} else if (elements.length == 0) {
			ReportGrid grid = new ReportGrid(1);
			for (Object newElement : elements) {
				grid.addItem((ReportItem) newElement);
			}
			return grid;
		} else {
			ReportGrid grid = new ReportGrid(elements.length);
			grid.addRow(elements);
			return grid;
		}
	}

	protected ReportChart chart(String groupProperty) {
		return chart(ChartType.PIE, null, data, groupProperty, null);
	}

	protected ReportChart chart(String chartTitle, String groupProperty) {
		return chart(ChartType.PIE, chartTitle, data, groupProperty, null);
	}

	protected ReportChart chart(ChartType chartType, String chartTitle, String groupProperty) {
		return chart(chartType, chartTitle, data, groupProperty, null);
	}

	protected ReportChart chart(ChartType chartType, String chartTitle, String groupProperty, String seriesProperty) {
		return chart(chartType, chartTitle, data, groupProperty, seriesProperty);
	}

	protected ReportChart chart(ChartType chartType, String chartTitle, String groupProperty, String seriesProperty, String valuesProperty) {
		return chart(chartType, chartTitle, data, groupProperty, seriesProperty, valuesProperty);
	}

	protected ReportChart chart(ChartType chartType, String chartTitle, List<?> chartData, String groupProperty) {
		return chart(chartType, chartTitle, chartData, groupProperty, null);
	}

	protected ReportChart chart(ChartType chartType, String chartTitle, List<?> chartData, String groupProperty, String seriesProperty) {
		return chart(chartType, chartTitle, chartData, groupProperty, seriesProperty, null);
	}

	protected ReportChart chart(ChartType chartType, String chartTitle, List<?> chartData, String groupProperty, String seriesProperty, String valueProperty) {

		Chart chart = createChart(chartType, chartTitle);

		ChartData data = null;
		if (ChartType.PIE.equals(chartType) && seriesProperty != null) {
			if (isAutoAggregateGroups(chartTitle)) {
				data = ChartDataBuilder.buildSum(chartData, groupProperty, null, seriesProperty);
			} else {
				data = ChartDataBuilder.build(chartData, groupProperty, null, seriesProperty);
			}
		} else {
			if (isAutoAggregateGroups(chartTitle)) {
				data = ChartDataBuilder.buildSum(chartData, groupProperty, seriesProperty, valueProperty);
			} else {
				data = ChartDataBuilder.build(chartData, groupProperty, seriesProperty, valueProperty);
			}
		}
		chart.setData(data);

		if (seriesProperty == null && valueProperty != null) {
			chart.getStyle().setLegendPosition(LegendPosition.NONE);
			if (chartData.size() > 0) {
				Object i0 = chartData.get(0);
				String seriesDisplay = BeanDescriptorFactory.forBean(i0).getPropertyDescriptor(valueProperty).getDisplayName();
				data.setSeries(seriesDisplay);
			}
			chart.setData(data);
		}

		ReportChart reportChart = new ReportChart(chart);
		reportChart.getStyle().setAlignment(ReportAlignment.CENTER);
		reportChart.setHeight(125);
		reportChart.getStyle().setPaddingTop(2);
		reportChart.getStyle().setPaddingBottom(2);

		return reportChart;
	}

	public Chart createChart(ChartType chartType, String chartTitle) {
		Chart chart = new Chart(chartType, chartTitle, "500", "300");
		if (valueConverter instanceof PropertyEditor) {
			PropertyEditor valueConverterPe = (PropertyEditor) valueConverter;
			chart.setFormatters(valueConverterPe);
		}
		return chart;
	}

	protected ReportChart chartPropertiesAsSeries(ChartType chartType, String w, String h, String chartTitle, String groupProperty, String... seriesProperties) {
		return chartPropertiesAsSeries(chartType, w, h, chartTitle, data, groupProperty, seriesProperties);
	}

	protected ReportChart chartPropertiesAsSeries(ChartType chartType, String chartTitle, String groupProperty, String... seriesProperties) {
		return chartPropertiesAsSeries(chartType, chartTitle, data, groupProperty, seriesProperties);
	}

	protected ReportChart chartPropertiesAsSeries(ChartType chartType, String chartTitle, List<?> chartData, String groupProperty, String... seriesProperties) {
		return chartPropertiesAsSeries(chartType, null, null, chartTitle, chartData, groupProperty, seriesProperties);
	}

	protected ReportChart chartPropertiesAsSeries(ChartType chartType, String w, String h, String chartTitle, List<?> chartData, String groupProperty, String... seriesProperties) {

		Chart chart = createChart(chartType, chartTitle);

		ChartData data = ChartDataBuilder.buildPropertiesAsSeries(chartData, groupProperty, seriesProperties);
		if (isAutoAggregateGroups(chartTitle)) {
			aggregateGroups(data);
		}
		chart.setData(data);

		if (w != null && h != null) {
			chart.setDimension(w, h);
		}

		ReportChart reportChart = new ReportChart(chart);
		reportChart.getStyle().setAlignment(ReportAlignment.CENTER);
		reportChart.setHeight(125);
		reportChart.getStyle().setPaddingTop(2);
		reportChart.getStyle().setPaddingBottom(2);

		return reportChart;
	}

	public void aggregateGroups(ChartData data) {
		List<ChartRow> rows = data.getData();
		for (int i = 0; i < rows.size() - 1; i++) {
			ChartRow chartRow1 = rows.get(i);
			ChartRow chartRow2 = rows.get(i + 1);
			if (chartRow1.getGroup().equals(chartRow2.getGroup())) {
				Number[] values1 = chartRow1.getValues();
				Number[] values2 = chartRow2.getValues();
				for (int j = 0; j < values1.length; j++) {
					values1[j] = new ChartSumAggregateFunction().aggregate(Arrays.asList(values1[j], values2[j]));
				}
				rows.remove(i + 1);
				i--;
			}
		}
	}

	protected boolean isAutoAggregateGroups(String chartTitle) {
		return true;
	}

	protected Subreport subreport(BaseReportBuilder subreport) {
		return subreport(subreport, summaryResult);
	}

	protected Subreport subreport(BaseReportBuilder subreport, String dataExpression) {
		Subreport subreportItem = new Subreport(subreport.getDefinition());
		subreportItem.setExpression(dataExpression);
		return subreportItem;
	}

	protected Subreport subreport(BaseReportBuilder subreport, SummaryResult<?, ?> data) {
		return subreport(subreport, data, null);
	}

	protected Subreport subreport(BaseReportBuilder subreport, List<?> data) {
		subreport.setData(data);
		Subreport subreportItem = new Subreport(subreport.getDefinition());
		subreportItem.setWidth(100 | ReportConstants.PERCENT_WIDTH);
		return subreportItem;
	}

	protected Subreport subreport(BaseReportBuilder subreport, SummaryResult<?, ?> data, String order) {
		if (order != null) {
			data.orderBy(order);
		}
		subreport.setData(data);
		Subreport subreportItem = new Subreport(subreport.getDefinition());
		subreportItem.setWidth(100 | ReportConstants.PERCENT_WIDTH);
		return subreportItem;
	}

	protected void configure(int column, int colspan, ReportAlignment alignment, ReportSection section, Object[] elements, String defaultFieldPrefix) {
		for (int i = 0; i < elements.length; i++) {
			Object object = elements[i];
			if (object instanceof String) {
				String string = (String) object;
				if (string.startsWith("$")) {
					string = string.substring(1);
					elements[i] = field(string);
				} else {
					elements[i] = label(string);
				}
			} else if (object instanceof Chart) {
				ReportChart reportChart = new ReportChart((Chart) object);
				reportChart.getStyle().setAlignment(ReportAlignment.CENTER);
				reportChart.setHeight(getChartDefaultHeight());
				reportChart.getStyle().setPaddingTop(2);
				reportChart.getStyle().setPaddingBottom(2);
				elements[i] = reportChart;
			} else if (!(object instanceof ReportItem)) {
				throw new IllegalArgumentException("column elements must be ReportItem or String");
			}
		}
		for (int i = 0; i < elements.length; i++) {
			ReportItem object = (ReportItem) elements[i];
			if (alignment != null && object.getStyle().getAlignment() == null) {
				object.getStyle().setAlignment(alignment);
			}
			if (object instanceof ReportTextField) {
				ReportTextField reportTextField = (ReportTextField) object;
				if (sumarizedData && !startsWithRowOrSummary(reportTextField)) {
					reportTextField.setExpression(defaultFieldPrefix + reportTextField.getExpression());
				}
			}
		}
		if (elements.length > 1) {
			ReportGrid grid = new ReportGrid(elements.length);
			grid.addRow(elements);
			grid.setColspan(colspan);
			getDefinition().addItem(grid, section, column);
		} else {
			ReportItem reportItem = (ReportItem) elements[0];
			if (reportItem.getStyle().getAlignment() == null) {
				reportItem.getStyle().setAlignment(alignment);
			}
			if (reportItem.getColspan() == 1) {
				reportItem.setColspan(colspan);
			}
			addItemToDefinition(column, section, reportItem);
		}
	}

	protected int getChartDefaultHeight() {
		return 100;
	}

	boolean startsWithRowOrSummary(ReportTextField reportTextField) {
		String expression = reportTextField.getExpression();
		return (expression.startsWith("row.") || expression.startsWith("summary."));
	}

	protected ReportItem addItemToDefinition(int column, ReportSection section, ReportItem reportItem) {
		return getDefinition().addItem(reportItem, section, column);
	}

}