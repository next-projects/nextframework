package org.nextframework.report.generator;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.compilation.SourceCodeBuilder;
import org.nextframework.exception.NextException;
import org.nextframework.report.definition.builder.IReportBuilder;
import org.nextframework.report.definition.builder.LayoutReportBuilder;
import org.nextframework.report.generator.data.CalculatedFieldElement;
import org.nextframework.report.generator.data.FilterElement;
import org.nextframework.report.generator.data.GroupElement;
import org.nextframework.report.generator.generated.ReportSpec;
import org.nextframework.report.generator.layout.FieldDetailElement;
import org.nextframework.report.generator.layout.LayoutItem;
import org.nextframework.report.generator.source.ReportBuilderSourceGenerator;
import org.nextframework.summary.Summary;
import org.nextframework.summary.SummaryRow;
import org.nextframework.summary.annotations.CalculationType;
import org.nextframework.summary.compilation.SummaryResult;
import org.nextframework.summary.dynamic.DynamicSummary;
import org.nextframework.summary.dynamic.DynamicVariable;
import org.nextframework.summary.dynamic.DynamicVariableDecorator;
import org.nextframework.view.progress.IProgressMonitor;
import org.springframework.util.StringUtils;

public class ReportGenerator {
	
	static WeakReference<ClassLoader> classLoaderReference = new WeakReference<ClassLoader>(new URLClassLoader(new URL[0], ReportGenerator.class.getClassLoader()));
	
	private ReportElement reportElement;
	private ReportBuilderSourceGenerator reportBuilderSourceGenerator;
	private IProgressMonitor progressMonitor;
	
	private BeanDescriptor beanDescriptorCache = null;
	
	Map<String, Object> context = new HashMap<String, Object>();
	
	public ReportGenerator(ReportElement reportElement) {
		this(reportElement, null);
	}
	
	public ReportGenerator(ReportElement reportElement, IProgressMonitor progressMonitor) {
		this.reportElement = reportElement;
		this.reportBuilderSourceGenerator = new ReportBuilderSourceGenerator(this);
		this.progressMonitor = progressMonitor;
	}
	
	public String getReportQualifiedClassName() {
		return reportBuilderSourceGenerator.getQualifiedClassName();
	}

	/**
	 * Returns the report generated source code
	 * @return
	 */
	public String getSourceCode() {
		return reportBuilderSourceGenerator.getSource();
	}
	
	public SourceCodeBuilder createSourceCodeBuilder() {
		return reportBuilderSourceGenerator.createSourceCodeBuilder();
	}

	@SuppressWarnings("unchecked")
	public ReportSpec generateReportSpec(Map<String, Object> filterMap, int limitResults){
		
		ReportSpec spec = new ReportSpec();
		IReportBuilder reportBuilder = createReportBuilder();
		Map<String, Object> fixedCriteriaMap = getFixedCriteriaMap(reportElement);
		
		if (progressMonitor != null) {
			progressMonitor.setTaskName("Obtendo registros");
		}
		
		List<?> result = reportElement.getData().getDataSourceProvider().getResult(reportElement, filterMap, fixedCriteriaMap, limitResults);
		
		if (progressMonitor != null) {
			progressMonitor.worked(60);
			progressMonitor.setTaskName("Sumarizando resultados");
		}
		
		List<GroupElement> groups = reportElement.getData().getGroups();
		DynamicSummary summary = createSummary();
		LayoutReportBuilder layoutBuilder = (LayoutReportBuilder)reportBuilder;
		layoutBuilder.setFilter(createFilter(filterMap, layoutBuilder));
		SummaryResult summaryResult = summary.getSummaryResult(result);
		for (final GroupElement groupElement : groups) {
			if(isDateType(getTypeForProperty(groupElement.getName()))){
				String pattern = groupElement.getPattern() != null? groupElement.getPattern() : "MM/yyyy";
				final SimpleDateFormat sdf = new SimpleDateFormat(pattern);
				
				final String groupProperty = getCompositeGroupName(groupElement.getName());
				summaryResult.reorderGroup(groupProperty, new Comparator<SummaryRow<?, Summary<?>>>() {

					@Override
					public int compare(SummaryRow<?, Summary<?>> o1, SummaryRow<?, Summary<?>> o2) {
						Summary<?> summary1 = o1.getSummary();
						Summary<?> summary2 = o2.getSummary();
						BeanDescriptor bd1 = BeanDescriptorFactory.forBean(summary1);
						BeanDescriptor bd2 = BeanDescriptorFactory.forBean(summary2);
						String ds1 = (String) bd1.getPropertyDescriptor(groupProperty).getValue();
						String ds2 = (String) bd2.getPropertyDescriptor(groupProperty).getValue();
						if(ds1.equals("")){
							if(ds2.equals("")){
								return 0; 
							}
							return -1;
						}
						if(ds2.equals("")){
							return 1;
						}
						try {
							int result = sdf.parse(ds1).compareTo(sdf.parse(ds2));
							return result;
						} catch (ParseException e) {
							throw new RuntimeException("Error reordering date group", e);
						}
					}
				});
			}
		}
		
		layoutBuilder.setData(summaryResult);
		
		if (progressMonitor != null) {
			progressMonitor.worked(40);
		}
		
		spec.setReportBuilder(reportBuilder);
		spec.setSummary(summary);
		return spec;
	}
	
	private Map<String, Object> getFixedCriteriaMap(ReportElement reportElement) {
		List<FilterElement> filters = reportElement.getData().getFilters();
		Map<String, Object> parametersMap = new HashMap<String, Object>();
		for (FilterElement filterElement : filters) {
			if (filterElement.getFixedCriteria() != null) {
				parametersMap.put(filterElement.getName(), filterElement.getFixedCriteria());
			}
		}
		return parametersMap;
	}

	@SuppressWarnings("rawtypes")
	public DynamicSummary createSummary() {
		DynamicSummary summary = DynamicSummary.getInstance(getMainType());
		configureGroups(reportElement.getData().getGroups(), summary);
		configureVariables(reportElement, reportElement.getLayout().getItems(), summary);
		configureExpressions(reportElement, reportElement.getData().getCalculatedFields(), summary);
		return summary;
	}

	//copyied from DynamicSummaryImpl
	private String getCompositeGroupName(String name) {
		String[] names = name.split("\\.");
		StringBuilder builder = new StringBuilder();
		boolean includeUnderscore = false;
		for (String var : names) {
			if(includeUnderscore){
				builder.append("_");
				var = StringUtils.capitalize(var);
			}
			builder.append(var);
			includeUnderscore = true;
		}
		return builder.toString();
	}

	private Object createFilter(Map<String, Object> filterMap, LayoutReportBuilder layoutBuilder) {
		ReportGeneratorUtils filterUtils = new ReportGeneratorUtils();
		Object filterObject = filterUtils.transformToObject(layoutBuilder.getClass().getClassLoader(), reportElement, layoutBuilder.getClass().getName(), filterMap);
		return filterObject;
	}
	

	private void configureExpressions(ReportElement reportElement, List<CalculatedFieldElement> calculatedFields, DynamicSummary summary) {
		for (CalculatedFieldElement calculatedField : calculatedFields) {
			String name = calculatedField.getName();
			String expression = calculatedField.getExpression();
			String processors = calculatedField.getProcessors();
			String displayName = calculatedField.getDisplayName();
			boolean formatAsNumber = CalculatedFieldElement.FORMAT_AS_NUMBER.equals(calculatedField.getFormatAs());
			String formatTimeDetail = calculatedField.getFormatTimeDetail();
			LayoutItem itemWithName = this.reportElement.getLayout().getItemWithName(name);
			CalculationType calculation = CalculationType.SUM;
			if(itemWithName instanceof FieldDetailElement){
				FieldDetailElement fieldElement = (FieldDetailElement) itemWithName;
				String aggregateType = fieldElement.getAggregateType();
				if(aggregateType != null){
					calculation = CalculationType.valueOf(aggregateType);
				}
			}
			configureExpression(reportElement, summary, name, expression, displayName, formatAsNumber, formatTimeDetail, calculation, processors);
		}
	}

	public static void configureExpression(ReportElement reportElement, DynamicSummary summary, String name, String expression, String displayName, boolean formatAsNumber, String formatTimeDetail, CalculationType calculation, String processors) {
		String b = ReportGeneratorUtils.reorganizeExpression(reportElement, summary.getReferenceClass(), expression, processors);
		//TODO TYPE IS FORCED... TRY TO DETECT TYPE
		String timeDiv = "";
		if(!formatAsNumber){
			timeDiv = convertToTimeFormula(formatTimeDetail);
		}
		summary.addVariable(new DynamicVariable(name, displayName, 
					calculation, "(double)(("+b+")"+timeDiv+")", Double.class));
	}

	@SuppressWarnings("serial")
	private static Map<String, String> formatTimeMap = new HashMap<String, String>(){{
		put("minutes", 	" / (1000.0 * 60)");
		put("hours", 	" / (1000.0 * 60 * 60)");
		put("days", 	" / (1000.0 * 60 * 60 * 24)");
	}};
	public static String convertToTimeFormula(String formatTimeDetail) {
		String result = formatTimeMap.get(formatTimeDetail);
		if(result == null){
			return formatTimeMap.get("hours");//TODO this default value is in a lot of places
		}
		return result;
	}

	private void configureVariables(ReportElement reportElement, List<LayoutItem> items, DynamicSummary<?> summary) {
		for (LayoutItem layoutItem : items) {
			if(layoutItem instanceof FieldDetailElement){
				FieldDetailElement fieldDetailElement = (FieldDetailElement) layoutItem;
				String name = fieldDetailElement.getName();
				String displayName;
				if(!reportElement.getData().isCalculated(fieldDetailElement.getName())){
					displayName = getBeanDescriptorForMainType().getPropertyDescriptor(name).getDisplayName();
					if(fieldDetailElement.isAggregateField() || fieldDetailElement.isCustomPattern()){
						if(fieldDetailElement.getAggregateType() == null){
							summary.addVariable(name, displayName, CalculationType.SUM);
						} else {
							String aggregateType = fieldDetailElement.getAggregateType();
							CalculationType calculation = CalculationType.valueOf(aggregateType);
							summary.addVariable(name, displayName, calculation);
						}
					}
				} else {
					CalculatedFieldElement calc = reportElement.getData().getCalculatedFieldWithName(name);
					displayName = calc.getDisplayName();
					if(fieldDetailElement.isCustomPattern() && !fieldDetailElement.isAggregateField()){
						summary.addVariable(name, displayName, CalculationType.NONE);
					}
				}
				if(fieldDetailElement.isCustomPattern()){
					String cpe = fieldDetailElement.getCustomPatternExpression();
					summary.addVariable(new DynamicVariableDecorator(name+"Formatted", displayName, name, 
							cpe, String.class));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void configureGroups(List<GroupElement> groups, DynamicSummary summary) {
		for (GroupElement groupElement : groups) {
			Type type = getTypeForProperty(groupElement.getName());
			if(isEntityType(type)){
				BeanDescriptor bdp = BeanDescriptorFactory.forClass((Class)type);
				String property = "class";
				if(bdp.getDescriptionPropertyName() != null){
					property = bdp.getDescriptionPropertyName();
				} else if(bdp.getIdPropertyName() != null){
					property = bdp.getIdPropertyName();
				}
				summary.addGroup(groupElement.getName()+"."+property);
				continue;
			}
			
			if(isDateType(type)	&& groupElement.getPattern() == null){
				summary.addGroup(groupElement.getName(), "MM/yyyy");
				continue;
			}
			summary.addGroup(groupElement.getName(), groupElement.getPattern());
		}
	}

	private boolean isEntityType(Type type) {
		if(type instanceof Class<?>){
			return ((Class<?>)type).isAnnotationPresent(Entity.class);
		} else {
			return false;
		}
	}

	private boolean isDateType(Type type) {
		if(type instanceof Class<?>){
			return (Calendar.class.isAssignableFrom((Class<?>) type) || Date.class.isAssignableFrom((Class<?>) type));
		} else {
			return false;
		}
	}
	
	private Class<?> getMainType() {
		return reportElement.getData().getMainType();
	}

	private Type getTypeForProperty(String name) {
		PropertyDescriptor propertyDescriptor = getBeanDescriptorForMainType().getPropertyDescriptor(name);
		Type type = propertyDescriptor.getType();
		return type;
	}

	private BeanDescriptor getBeanDescriptorForMainType() {
		if(beanDescriptorCache == null){
			beanDescriptorCache = BeanDescriptorFactory.forClass(getMainType()); 
		}
		return beanDescriptorCache; 
	}

	private IReportBuilder createReportBuilder() {
		try {
			return ReportGeneratorContext.getClassFor(this).newInstance();
		} catch (InstantiationException e) {
			throw new NextException(e);
		} catch (IllegalAccessException e) {
			throw new NextException(e);
		}
	}
	
	public Map<String, Object> getContext() {
		return context;
	}

	public void setContext(Map<String, Object> context) {
		this.context = context;
	}

	public ReportElement getReportElement() {
		return reportElement;
	}
	
}
