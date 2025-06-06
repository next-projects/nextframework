package org.nextframework.report.generator.mvc;

import java.awt.Color;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.persistence.Embedded;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.servlet.http.HttpServletRequest;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.classmanager.ClassManagerFactory;
import org.nextframework.controller.ServletRequestDataBinderNext;
import org.nextframework.exception.BusinessException;
import org.nextframework.persistence.PersistenceUtils;
import org.nextframework.report.definition.ReportDefinition;
import org.nextframework.report.definition.ReportSectionRow;
import org.nextframework.report.definition.elements.ReportLabel;
import org.nextframework.report.generator.ReportElement;
import org.nextframework.report.generator.annotation.ExtendBean;
import org.nextframework.report.generator.annotation.ReportEntity;
import org.nextframework.report.generator.annotation.ReportField;
import org.nextframework.report.generator.data.CalculatedFieldElement;
import org.nextframework.report.generator.data.FilterElement;
import org.nextframework.report.generator.datasource.extension.GeneratedReportListener;
import org.nextframework.report.generator.layout.DynamicBaseReportDefinition;
import org.nextframework.service.ServiceFactory;
import org.nextframework.util.Util;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;

import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings("rawtypes")
public class ReportDesignControllerUtil {

	Map<Class, String> getDisplayNameForEntities(Class<?>[] entities, Locale locale) {
		Map<Class, String> displayNames = new HashMap<Class, String>();
		for (Class<?> class1 : entities) {
			displayNames.put(class1, Util.beans.getDisplayName(class1, locale));
		}
		return displayNames;
	}

	Class<?>[] getAvaiableEntities() {
		Class<?>[] classesWithAnnotation = ClassManagerFactory.getClassManager().getClassesWithAnnotation(ReportEntity.class);
		Arrays.sort(classesWithAnnotation, new Comparator<Class>() {

			public int compare(Class o1, Class o2) {
				return o1.getSimpleName().compareTo(o2.getSimpleName());
			}

		});
		return classesWithAnnotation;
	}

	public Set<String> getAvailablePropertiesForClass(Class<?> selectedType, String parentProperty, int deepLevel) {

		if (deepLevel < 0) {
			return new LinkedHashSet<String>();
		}

		Set<String> avaiableProperties = new LinkedHashSet<String>();

		List<Method> propertyGetters = getOrderedProperties(selectedType);
		for (Method method : propertyGetters) {
			String property = Util.beans.getPropertyFromGetter(method.getName());
			if (parentProperty != null && parentProperty.equals(property)) {
				continue;
			}
			ReportField reportField = method.getAnnotation(ReportField.class);
			if (reportField != null || method.getAnnotation(Id.class) != null) {
				if (!"class".equals(property) && !Collection.class.isAssignableFrom(method.getReturnType()) && !method.getReturnType().isArray()) {
					avaiableProperties.add(property);
					ManyToOne manyToOne = method.getAnnotation(ManyToOne.class);
					OneToOne oneToOne = method.getAnnotation(OneToOne.class);
					Embedded embedded = method.getAnnotation(Embedded.class);
					ExtendBean extendBean = method.getAnnotation(ExtendBean.class);
					ReportEntity refereceReportEntity = method.getReturnType().getAnnotation(ReportEntity.class);
					if (manyToOne != null || oneToOne != null || extendBean != null || embedded != null || refereceReportEntity != null) {
						Class<?> subPropertyClass = method.getReturnType();
						Set<String> subProperties = getAvailablePropertiesForClass(subPropertyClass, property, deepLevel - 1);
						for (String subProperty : subProperties) {
							if (!subProperty.equals("id")) {
								avaiableProperties.add(property + "." + subProperty);
							}
						}
					}
				}
			}
		}

		return avaiableProperties;
	}

	private List<Method> getOrderedProperties(final Class<?> selectedType) {
		final BeanDescriptor bd = BeanDescriptorFactory.forClass(selectedType);
		Set<Method> propertyGettersSet = Util.beans.getPropertyGetters(selectedType);
		List<Method> propertyGetters = new ArrayList<Method>(propertyGettersSet);
		propertyGetters.sort(new Comparator<Method>() {

			@Override
			public int compare(Method m1, Method m2) {
				//Para subir o ID
				if (m1.getAnnotation(Id.class) != null) {
					return -1;
				}
				if (m2.getAnnotation(Id.class) != null) {
					return 1;
				}
				//Restante ordena pela descrição
				String o1Desc = toFullDescription(m1);
				String o2Desc = toFullDescription(m2);
				return o1Desc.compareTo(o2Desc);
			}

			private String toFullDescription(Method m) {
				String property = Util.beans.getPropertyFromGetter(m.getName());
				return Util.beans.getDisplayName(bd.getPropertyDescriptor(property), null, null);
			}

		});
		return propertyGetters;
	}

	HashMap<String, Object> getPropertiesMapForCalculatedField(CalculatedFieldElement calculatedFieldElement) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("displayName", calculatedFieldElement.getDisplayName());
		map.put("expression", calculatedFieldElement.getExpression());
		map.put("processors", calculatedFieldElement.getProcessors());
		map.put("filterable", false);
		map.put("numberType", true);
		map.put("calculated", true);
		map.put("formatAs", calculatedFieldElement.getFormatAs());
		map.put("formatTimeDetail", calculatedFieldElement.getFormatTimeDetail());
		setJsonMetadata(map);
		return map;
	}

	HashMap<String, Object> getMetadataForProperty(ReportElement report, Locale locale, BeanDescriptor beanDescriptor, String property) {

		PropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(property);
		HashMap<String, Object> map = new LinkedHashMap<>();

		if (report != null) {
			FilterElement filter = report.getData().getFilterByName(property);
			if (filter != null) {
				if (filter.getFilterDisplayName() != null) {
					map.put("filterDisplayName", filter.getFilterDisplayName());
				}
				if (filter.getPreSelectDate() != null) {
					map.put("preSelectDate", filter.getPreSelectDate().toString());
				}
				if (filter.getPreSelectEntity() != null) {
					map.put("preSelectEntity", filter.getPreSelectEntity());
				}
				if (filter.getFixedCriteria() != null) {
					map.put("fixedCriteria", filter.getFixedCriteria());
				}
				if (filter.isFilterSelectMultiple()) {
					map.put("filterSelectMultiple", filter.isFilterSelectMultiple());
				}
				map.put("requiredFilter", filter.isFilterRequired());
			}
		}

		boolean extended = isExtendedProperty(beanDescriptor, property);
		map.put("extended", extended);

		map.put("type", propertyDescriptor.getType());
		map.put("displayName", getCompleteDisplayName(beanDescriptor, propertyDescriptor, property, locale));
		map.put("displayNameSimple", Util.beans.getDisplayName(propertyDescriptor, locale));

		map.put("transient", propertyDescriptor.getAnnotation(Transient.class) != null);

		if (propertyDescriptor.getType() instanceof Class) {

			Class propertyClass = (Class) propertyDescriptor.getType();
			boolean isEntity = PersistenceUtils.isEntity(propertyClass);

			map.put("entity", isEntity);
			if (isEntity) {
				String descriptionPropertyName = BeanDescriptorFactory.forClass(propertyClass).getDescriptionPropertyName();
				map.put("descriptionProperty", descriptionPropertyName);
			}
			map.put("comparable", Comparable.class.isAssignableFrom(propertyClass));

			map.put("dateType", isDateType(propertyClass));
			map.put("numberType", isNumberType(propertyClass));
			map.put("money", propertyClass.getName().contains("Money"));
			map.put("enumType", propertyClass.isEnum());
			if (propertyClass.isEnum()) {
				List<Object> enumsList = Arrays.asList(propertyClass.getEnumConstants());
				String enumExample = Util.collections.join(enumsList, ", ", locale);
				enumExample = enumExample.length() > 100 ? enumExample.substring(0, 50) + "..." : enumExample;
				map.put("enumExample", enumExample);
			}

		}

		map.put("filterable", isFilterable(beanDescriptor, property));

		if (!map.containsKey("requiredFilter")) {
			ReportField reportField = propertyDescriptor.getAnnotation(ReportField.class);
			map.put("requiredFilter", reportField != null && reportField.requiredFilter() && !extended);
		}

		map.put("columnable", isColumnable(beanDescriptor, property));

		map.put("propertyDepth", countSubProperties(property));

		return map;
	}

	public String getCompleteDisplayName(BeanDescriptor beanDescriptor, PropertyDescriptor propertyDescriptor, String property, Locale locale) {
		String[] parts = property.split("\\.");
		if (parts.length > 1) {
			StringBuilder buffer = new StringBuilder();
			String currentPart = "";
			for (int i = 0; i < parts.length; i++) {
				String part = parts[i];
				currentPart += part;
				PropertyDescriptor pd2 = beanDescriptor.getPropertyDescriptor(currentPart);
				String displayName = Util.beans.getDisplayName(pd2, locale);
				currentPart += ".";
				buffer.append(displayName);
				if (i + 1 < parts.length) {
					buffer.append(" - ");
				}
			}
			return buffer.toString();
		} else {
			return Util.beans.getDisplayName(propertyDescriptor, locale);
		}
	}

	private boolean isExtendedProperty(BeanDescriptor beanDescriptor, String property) {
		String base = property.contains(".") ? property.substring(0, property.indexOf('.')) : property;
		PropertyDescriptor basePropertyDescriptor = beanDescriptor.getPropertyDescriptor(base);
		ExtendBean extendBean = basePropertyDescriptor.getAnnotation(ExtendBean.class);
		return extendBean != null;
	}

	public boolean isFilterable(BeanDescriptor beanDescriptor, String property) {

		PropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(property);
		String base = property.contains(".") ? property.substring(0, property.indexOf(".")) : property;
		PropertyDescriptor basePropertyDescriptor = beanDescriptor.getPropertyDescriptor(base);

		boolean trans = basePropertyDescriptor.getAnnotation(Transient.class) != null;

		ExtendBean extendBean = basePropertyDescriptor.getAnnotation(ExtendBean.class);
		boolean extended = extendBean != null;

		ReportField reportField = basePropertyDescriptor.getAnnotation(ReportField.class);
		boolean filter = reportField != null && (reportField.filter() || reportField.requiredFilter());

		boolean numberType = false;
		if (propertyDescriptor.getType() instanceof Class) {
			Class propertyClass = (Class) propertyDescriptor.getType();
			numberType = isNumberType(propertyClass);
		}

		//Campos normais da entidade e que não sejam extendidos, ou que sejam filtráveis. Nenhum número.
		boolean filterable = (!trans && !extended || filter) && !numberType;
		return filterable;
	}

	public boolean isColumnable(BeanDescriptor beanDescriptor, String property) {
		String base = property.contains(".") ? property.substring(0, property.indexOf('.')) : property;
		PropertyDescriptor basePropertyDescriptor = beanDescriptor.getPropertyDescriptor(base);
		ReportField reportField = basePropertyDescriptor.getAnnotation(ReportField.class);
		return reportField != null && reportField.column();
	}

	private int countSubProperties(String property) {
		int count = 0;
		for (int i = 0; i < property.length(); i++) {
			if (property.charAt(i) == '.') {
				count++;
			}
		}
		return count;
	}

	protected boolean isNumberType(Class<?> propertyClass) {
		return Number.class.isAssignableFrom(propertyClass);
	}

	protected boolean isDateType(Class<?> propertyClass) {
		return Date.class.isAssignableFrom(propertyClass) || Calendar.class.isAssignableFrom(propertyClass);
	}

	public Map<String, Object> getFilterMap(HttpServletRequest request, ReportElement reportElement) {

		BeanDescriptor bd = BeanDescriptorFactory.forClass(reportElement.getData().getMainType());

		ServletRequestDataBinderNext dataBinder = new ServletRequestDataBinderNext(new Object(), "");
		MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(request);
		dataBinder.bind(mpvs);

		List<FilterElement> filters = reportElement.getData().getFilters();
		Map<String, Object> parametersMap = new HashMap<String, Object>();
		for (FilterElement filterElement : filters) {
			if (filterElement.getFixedCriteria() != null) {
				continue;
			}
			String name = filterElement.getName();
			Class type = (Class) bd.getPropertyDescriptor(name).getType();
			if (isDateType(type)) {
				String keyBegin = name + "_begin";
				String keyEnd = name + "_end";
				Object valueBegin = getValue(request, filterElement, dataBinder, mpvs, keyBegin, type);
				Object valueEnd = getValue(request, filterElement, dataBinder, mpvs, keyEnd, type);
				if (filterElement.getPreSelectDate() != null) {
					if (valueBegin == null) {
						valueBegin = filterElement.getPreSelectDate().getBegin();
					}
					if (valueEnd == null) {
						valueEnd = filterElement.getPreSelectDate().getEnd();
					}
				}
				parametersMap.put(keyBegin, valueBegin);
				parametersMap.put(keyEnd, valueEnd);
			} else {
				Object value = getValue(request, filterElement, dataBinder, mpvs, name, type);
				String entityStr = filterElement.getPreSelectEntity();
				if (value == null && entityStr != null) {
					value = ServletRequestDataBinderNext.translateObjectValue(entityStr);
				}
				parametersMap.put(name, value);
			}
		}

		return parametersMap;
	}

	@SuppressWarnings("unchecked")
	private Object getValue(HttpServletRequest request, FilterElement filterElement, ServletRequestDataBinderNext dataBinder, MutablePropertyValues mpvs, String name, Class type) {
		Object value;
		try {
			if (filterElement.isFilterSelectMultiple()) {
				String[] parameterValues = request.getParameterValues(name);
				if (ServletRequestDataBinderNext.isObjectValue(parameterValues)) {
					return ServletRequestDataBinderNext.translateObjectValue(name, parameterValues, mpvs);
				}
				if (parameterValues != null && parameterValues.length > 0 && type.isEnum()) {
					Object[] parameterValues2 = new Object[parameterValues.length];
					for (int i = 0; i < parameterValues.length; i++) {
						parameterValues2[i] = dataBinder.convertIfNecessary(parameterValues[i], type);
					}
					return parameterValues2;
				}
				value = dataBinder.convertIfNecessary(parameterValues, type);
			} else {
				String parameterValue = request.getParameter(name);
				if (ServletRequestDataBinderNext.isObjectValue(parameterValue)) {
					return ServletRequestDataBinderNext.translateObjectValue(name, parameterValue, mpvs);
				}
				value = dataBinder.convertIfNecessary(parameterValue, type);
			}
		} catch (Exception e) {
			value = mpvs.getPropertyValue(name).getValue();
		}
		return value;
	}

	public void insertMaxResultsWarning(ReportDefinition definition, int maxResults) {
		int total = getResultCount(definition);
		if (total == maxResults) {
			ReportLabel label = new ReportLabel("Obs: Apenas os " + maxResults + " primeiros registros estão sendo mostrados.");
			label.getStyle().setForegroundColor(Color.RED);
			label.getStyle().setFontSize(6);
			label.getStyle().setItalic(true);
			label.setColspan(definition.getColumns().size());
			ReportSectionRow row = definition.getSectionFirstPageHeader().insertRow(0);
			definition.addItem(label, row, 0);
		}
	}

	public int getResultCount(ReportDefinition definition) {
		if (definition instanceof DynamicBaseReportDefinition) {
			DynamicBaseReportDefinition d2 = (DynamicBaseReportDefinition) definition;
			return d2.getSummarizedData().getItems().size();
		}
		return definition.getData().size();
	}

	public Map<String, Map<String, Object>> getPropertiesMetadata(ReportElement report, Locale locale, Class selectedType, Collection<String> properties) {
		Map<String, Map<String, Object>> propertyMetadata = new LinkedHashMap<String, Map<String, Object>>();
		for (String property : properties) {
			propertyMetadata.put(property, new HashMap<String, Object>());
		}
		return getPropertiesMetadata(report, locale, selectedType, propertyMetadata);
	}

	public Map<String, Map<String, Object>> getPropertiesMetadata(ReportElement report, Locale locale, Class selectedType, Map<String, Map<String, Object>> properties) {
		BeanDescriptor beanDescriptor = BeanDescriptorFactory.forClass(selectedType);
		Map<String, Map<String, Object>> propertyMetadata = new LinkedHashMap<String, Map<String, Object>>();
		for (String property : properties.keySet()) {
			HashMap<String, Object> metadataForProperty = getMetadataForProperty(report, locale, beanDescriptor, property);
			metadataForProperty.putAll(properties.get(property));
			setJsonMetadata(metadataForProperty);
			propertyMetadata.put(property, metadataForProperty);
		}
		return propertyMetadata;
	}

	void setJsonMetadata(HashMap<String, Object> metadataForProperty) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			metadataForProperty.put("json", mapper.writeValueAsString(metadataForProperty));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Map<String, List<String>> getDependencies(Class<?> mainType, List<String> properties) {

		final BeanDescriptor beanDescriptor = BeanDescriptorFactory.forClass(mainType);

		Map<String, List<String>> dependencies = new HashMap<String, List<String>>();
		for (String o1 : properties) {
			ArrayList<String> deps = new ArrayList<String>();
			dependencies.put(o1, deps);
			for (String o2 : properties) {
				if (!o1.equals(o2)) {
					PropertyDescriptor p1 = beanDescriptor.getPropertyDescriptor(o1);
					PropertyDescriptor p2 = beanDescriptor.getPropertyDescriptor(o2);
					if (depends(p1, p2)) {
						deps.add(o2);
					}
				}
			}
		}

		return dependencies;
	}

	boolean depends(PropertyDescriptor p1, PropertyDescriptor p2) {
		if (!(p1.getType() instanceof Class<?> && p2.getType() instanceof Class<?>)) {
			return false;
		}
		Class<?> type1 = ((Class<?>) p1.getType());
		Class<?> type2 = ((Class<?>) p2.getType());
		Set<Method> methods = Util.beans.getPropertiesAsGettersWithAnnotation(type1, ManyToOne.class);
		Set<Method> methods2 = Util.beans.getPropertiesAsGettersWithAnnotation(type1, OneToOne.class);
		methods.addAll(methods2);
		for (Method method : methods) {
			if (method.getReturnType().isAssignableFrom(type2)) {
				return true;
			}
		}
		return false;
	}

	public List<String> reorganizeFilters(Class<?> mainType, Collection<String> propertiesSet) {
		List<String> properties = new ArrayList<String>(propertiesSet);
		Map<String, List<String>> dependencies = getDependencies(mainType, properties);
		int countLoop = 0;
		for (int i = 0; i < properties.size() - 1; i++) {
			countLoop++;
			if (countLoop == 200) {
				break;//possible a circular reference
			}
			List<String> propertyDependencies = dependencies.get(properties.get(i));
			for (int j = i + 1; j < properties.size(); j++) {
				if (propertyDependencies.contains(properties.get(j))) {
					properties.add(i, properties.remove(j));
					i--;
					break;
				}
			}
		}
		return properties;
	}

	public void checkFiltersMap(ReportDesignModel model, ReportElement reportElement, Map<String, Map<String, Object>> filtersMetadataMap) {
		GeneratedReportListener[] grListeners = ServiceFactory.loadServices(GeneratedReportListener.class);
		Class mainType = Util.objects.getRealClass(reportElement.getData().getMainType());
		for (String filter : filtersMetadataMap.keySet()) {
			for (GeneratedReportListener filterListener : grListeners) {
				if (filterListener.acceptMainClass(mainType)) {
					filterListener.checkFilters(model, reportElement, filter, filtersMetadataMap);
				}
			}
		}
	}

	public void validadeAllowedProperties(final ReportElement reportElement) {
		BeanDescriptor bd = BeanDescriptorFactory.forClass(reportElement.getData().getMainType());
		for (String property : reportElement.getProperties()) {
			if (!reportElement.getData().isCalculated(property)) {
				PropertyDescriptor propertyDescriptor = bd.getPropertyDescriptor(property);
				ReportField reportField = propertyDescriptor.getAnnotation(ReportField.class);
				Id idField = propertyDescriptor.getAnnotation(Id.class);
				if (reportField == null && idField == null) {
					throw new BusinessException("org.nextframework.report.generator.mvc.ReportDesignController.invalidProperty", new Object[] { property, reportElement.getData().getMainType().getSimpleName() }, "Não é possível executar o relatório, pois a propriedade {0} de {1} não é permitida!");
				}
			}
		}
	}

}
