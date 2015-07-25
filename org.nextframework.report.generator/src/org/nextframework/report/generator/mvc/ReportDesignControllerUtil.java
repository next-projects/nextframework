package org.nextframework.report.generator.mvc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.codehaus.jackson.map.ObjectMapper;
import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.classmanager.ClassManagerFactory;
import org.nextframework.controller.ServletRequestDataBinderNext;
import org.nextframework.core.web.NextWeb;
import org.nextframework.persistence.QueryBuilder;
import org.nextframework.report.generator.ReportElement;
import org.nextframework.report.generator.annotation.ExtendBean;
import org.nextframework.report.generator.annotation.ReportEntity;
import org.nextframework.report.generator.annotation.ReportField;
import org.nextframework.report.generator.data.CalculatedFieldElement;
import org.nextframework.report.generator.data.FilterElement;
import org.nextframework.util.Util;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;

@SuppressWarnings("rawtypes")
public class ReportDesignControllerUtil {

	Map<Class, String> getDisplayNameForEntities(Class<?>[] entities) {
		Map<Class, String> displayNames = new HashMap<Class, String>();
		for (Class<?> class1 : entities) {
			displayNames.put(class1, BeanDescriptorFactory.forClass(class1).getDisplayName());
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

	List getPreviewData(ReportDesignModel model) {
		QueryBuilder queryBuilder = new QueryBuilder();
		List preview = queryBuilder.from(model.getSelectedType())
				.orderBy("id desc")
				.setMaxResults(30)
				.list();
		return preview;
	}

	Set<String> getAvailablePropertiesForClass(Class<?> selectedType, int deepLevel) {
		if(deepLevel < 0){
			return new HashSet<String>();
		}
		List<Method> propertyGetters = Util.beans.getPropertyGetters(selectedType);
		final BeanDescriptor bd = BeanDescriptorFactory.forClass(selectedType);
		final Set<String> avaiableProperties = createPropertyListTreeSet();
		for (Method method : propertyGetters) {
			String parentProperty = Util.beans.getPropertyFromGetter(method.getName());
			ReportField reportField = method.getAnnotation(ReportField.class);
			if(reportField != null || method.getAnnotation(Id.class) != null){
				if(!"class".equals(parentProperty) && !Collection.class.isAssignableFrom(method.getReturnType())){
					avaiableProperties.add(parentProperty);
					ManyToOne manyToOne = method.getAnnotation(ManyToOne.class);
					Embedded embedded = method.getAnnotation(Embedded.class);
					ExtendBean extendBean = method.getAnnotation(ExtendBean.class);
					ReportEntity refereceReportEntity = method.getReturnType().getAnnotation(ReportEntity.class);
					if(manyToOne != null || extendBean != null || embedded != null || refereceReportEntity != null){
						Class<?> subPropertyClass = method.getReturnType();
						Set<String> correctSubProperties = createPropertyListTreeSet();
						Set<String> subProperties = getAvailablePropertiesForClass(subPropertyClass, deepLevel - 1);
						for (String subProperty : subProperties) {
							if(!subProperty.equals("id")){
								correctSubProperties.add(parentProperty+"."+subProperty);
							}
						}
						if(correctSubProperties.size() > 1){
							avaiableProperties.addAll(correctSubProperties);
						}
					}
				}
			}
		}
		return avaiableProperties;
	}

	private TreeSet<String> createPropertyListTreeSet() {
		return new TreeSet<String>(new Comparator<String>() {
	
			@Override
			public int compare(String o1, String o2) {
				if(o1.toLowerCase().equals("id")){
					return -1;
				}
				if(o2.toLowerCase().equals("id")){
					return 1;
				}
				if(o1.toLowerCase().startsWith("id")){
					return -1;
				}
				if(o2.toLowerCase().startsWith("id")){
					return 1;
				}
//				PropertyDescriptor pd1 = bd.getPropertyDescriptor(o1);
//				PropertyDescriptor pd2 = bd.getPropertyDescriptor(o2);
//				int cp = pd1.getType().toString().compareTo(pd2.getType().toString());
//				if(cp == 0){
//					return pd1.getDisplayName().compareTo(pd2.getDisplayName());
//				}
				return o1.compareTo(o2);
			}
	
		});
	}

	HashMap<String, Object> getPropertiesMapForCalculatedField(CalculatedFieldElement calculatedFieldElement) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("displayName", 	calculatedFieldElement.getDisplayName());
		map.put("expression", 	calculatedFieldElement.getExpression());
		map.put("processors", 	calculatedFieldElement.getProcessors());
		map.put("filterable", 	false);
		map.put("numberType", 	true);
		map.put("calculated", 	true);
		map.put("formatAs", 	calculatedFieldElement.getFormatAs());
		map.put("formatTimeDetail", 	calculatedFieldElement.getFormatTimeDetail());
		
		setJsonMetadata(map);
		return map;
	}

	@SuppressWarnings("unchecked") HashMap<String, Object> getMetadataForProperty(ReportElement report, BeanDescriptor beanDescriptor, String property) {
		PropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(property);
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		if(report != null){
			FilterElement filter = report.getData().getFilterByName(property);
			if(filter != null){
				if(filter.isFilterSelectMultiple()){
					map.put("filterSelectMultiple", filter.isFilterSelectMultiple());
				}
				if(filter.getPreSelectDate() != null){
					map.put("preSelectDate", filter.getPreSelectDate().toString());
				}
				if(filter.getPreSelectEntity() != null){
					map.put("preSelectEntity", filter.getPreSelectEntity());
				}
				if(filter.getFilterDisplayName() != null){
					map.put("filterDisplayName", filter.getFilterDisplayName());
				}
				if(filter.isFilterRequired()){
					map.put("requiredFilter", filter.isFilterRequired());
				}
			}
		}
		
		if(propertyDescriptor.getType() instanceof Class){
			Class propertyClass = (Class) propertyDescriptor.getType();
			boolean isEntity = propertyClass.isAnnotationPresent(Entity.class);
			if(isEntity){
				String descriptionPropertyName = BeanDescriptorFactory.forClass(propertyClass).getDescriptionPropertyName();
				map.put("descriptionProperty", descriptionPropertyName);
			}
			map.put("comparable", Comparable.class.isAssignableFrom(propertyClass));
			map.put("entity", isEntity);
			map.put("dateType", isDateType(propertyClass));
			map.put("numberType", isNumberType(propertyClass));
	
			map.put("money", propertyClass.getName().contains("Money"));
		}
		
		map.put("type", propertyDescriptor.getType());
		map.put("extended", isExtendedProperty(beanDescriptor, property));
		map.put("displayName", getCompleteDisplayName(beanDescriptor, propertyDescriptor, property));
		map.put("displayNameSimple", propertyDescriptor.getDisplayName());
		map.put("transient", propertyDescriptor.getAnnotation(Transient.class) != null);
		ReportField reportField = propertyDescriptor.getAnnotation(ReportField.class);
		map.put("filterable", isFilterable(beanDescriptor, property, reportField) );
		if(!map.containsKey("requiredFilter")){
			map.put("requiredFilter", reportField != null && reportField.requiredFilter() && !isExtendedProperty(beanDescriptor, property));
		}
		map.put("propertyDepth", countSubProperties(property));
		return map;
	}

	private Object getCompleteDisplayName(BeanDescriptor beanDescriptor, PropertyDescriptor propertyDescriptor, String property) {
		String[] parts = property.split("\\.");
		if(parts.length > 1){
			StringBuilder buffer = new StringBuilder();
			String currentPart = "";
			for (int i = 0; i < parts.length; i++) {
				String part = parts[i];
				currentPart += part;
				String displayName = beanDescriptor.getPropertyDescriptor(currentPart).getDisplayName();
				currentPart += ".";
				buffer.append(displayName);
				if(i+1 < parts.length){
					buffer.append(" - ");
				}
			}
			return buffer.toString();
		} else {
			return propertyDescriptor.getDisplayName();
		}
	}

	private boolean isFilterable(BeanDescriptor beanDescriptor, String property, ReportField reportField) {
		if(isExtendedProperty(beanDescriptor, property)){
			return false;
		}
		return reportField != null && (reportField.filter() || reportField.requiredFilter());
	}

	private boolean isExtendedProperty(BeanDescriptor beanDescriptor, String property) {
		if(countSubProperties(property) > 0){
			String base = property.substring(0, property.indexOf('.'));
			PropertyDescriptor basePD = beanDescriptor.getPropertyDescriptor(base);
			if(basePD.getAnnotation(ExtendBean.class)!= null){
				return true;
			}
		}
		return false;
	}
	
	private int countSubProperties(String property) {
		int count = 0;
		for (int i = 0; i < property.length(); i++) {
			if(property.charAt(i) == '.'){
				count++;
			}
		}
		return count;
	}

	protected Object isNumberType(Class<?> propertyClass) {
		return Number.class.isAssignableFrom(propertyClass);
	}

	protected boolean isDateType(Class<?> propertyClass) {
		return Date.class.isAssignableFrom(propertyClass) || Calendar.class.isAssignableFrom(propertyClass);
	}


	Map<String, Object> getFilterMap(ReportElement reportElement) {
		BeanDescriptor bd = BeanDescriptorFactory.forClass(reportElement.getData().getMainType());
		
		ServletRequestDataBinderNext dataBinder = new ServletRequestDataBinderNext(new Object(), "");
		MutablePropertyValues mpvs = new ServletRequestParameterPropertyValues(NextWeb.getRequestContext().getServletRequest());
		dataBinder.bind(mpvs);
		
		List<FilterElement> filters = reportElement.getData().getFilters();
		Map<String, Object> parametersMap = new HashMap<String, Object>();
		for (FilterElement filterElement : filters) {
			String name = filterElement.getName();
			Class type = (Class)bd.getPropertyDescriptor(filterElement.getName()).getType();
			if(isDateType(type)){
				String keyBegin = name+"_begin";
				String keyEnd = name+"_end";
				Object valueBegin = getValue(filterElement, dataBinder, mpvs, keyBegin, type);
				Object valueEnd = getValue(filterElement, dataBinder, mpvs, keyEnd, type);
				if(filterElement.getPreSelectDate() != null){
					Date dateBegin = filterElement.getPreSelectDate().getBegin();
					Date dateEnd = filterElement.getPreSelectDate().getEnd();
					if(valueBegin == null){
						valueBegin = dateBegin;
					}
					if(valueEnd == null){
						valueEnd = dateEnd;
					}
				}
				parametersMap.put(keyBegin, valueBegin);
				parametersMap.put(keyEnd, valueEnd);
			} else {
				Object value = getValue(filterElement, dataBinder, mpvs, name, type);
				String entityText = filterElement.getPreSelectEntity();
				if(value == null && entityText != null){
					value = ServletRequestDataBinderNext.translateObjectValue(entityText);
				}
				parametersMap.put(name, value);
			}
		}
		return parametersMap;
	}	
	
	@SuppressWarnings("unchecked")
	Object getValue(FilterElement filterElement, ServletRequestDataBinderNext dataBinder, MutablePropertyValues mpvs, String name, Class type) {
		Object value;
		try {
			if(filterElement.isFilterSelectMultiple()){
				String[] parameterValues = NextWeb.getRequestContext().getServletRequest().getParameterValues(name);
				if(ServletRequestDataBinderNext.isObjectValue(parameterValues)){
					return ServletRequestDataBinderNext.translateObjectValue(name, parameterValues, mpvs);
				}
				value = dataBinder.convertIfNecessary(parameterValues, type);
			} else {
				String parameterValue = NextWeb.getRequestContext().getServletRequest().getParameter(name);
				if(ServletRequestDataBinderNext.isObjectValue(parameterValue)){
					return ServletRequestDataBinderNext.translateObjectValue(name, parameterValue, mpvs);
				}
				value = dataBinder.convertIfNecessary(parameterValue, type);
			}
		} catch (Exception e) {
			value = mpvs.getPropertyValue(name).getValue();
		}
		return value;
	}

	Map<String, Map<String, Object>> getPropertiesMetadata(ReportElement report, Class selectedType, Collection<String> properties) {
		Map<String, Map<String, Object>> propertyMetadata = new HashMap<String, Map<String,Object>>();
		for (String property : properties) {
			propertyMetadata.put(property, new HashMap<String, Object>());
		}
		return getPropertiesMetadata(report, selectedType, propertyMetadata);
	}
	Map<String, Map<String, Object>> getPropertiesMetadata(ReportElement report, Class selectedType, Map<String, Map<String, Object>> properties) {
		BeanDescriptor beanDescriptor = BeanDescriptorFactory.forClass(selectedType);
		Map<String, Map<String, Object>> propertyMetadata = new HashMap<String, Map<String,Object>>();
		for (String property : properties.keySet()) {
			HashMap<String, Object> metadataForProperty = getMetadataForProperty(report, beanDescriptor, property);
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
				if(!o1.equals(o2)){
					PropertyDescriptor p1 = beanDescriptor.getPropertyDescriptor(o1);
					PropertyDescriptor p2 = beanDescriptor.getPropertyDescriptor(o2);
					if(depends(p1, p2)){
						deps.add(o2);
					}
				}
			}
		}
		return dependencies;
	}	

	boolean depends(PropertyDescriptor p1, PropertyDescriptor p2) {
		if(!(p1.getType() instanceof Class<?> && p2.getType() instanceof Class<?>)){
			return false;
		}
		Class<?> type1 = ((Class<?>)p1.getType());
		Class<?> type2 = ((Class<?>)p2.getType());
		Set<Method> methods = Util.beans.getPropertiesAsGettersWithAnnotation(type1, ManyToOne.class);
		for (Method method : methods) {
			if(method.getReturnType().isAssignableFrom(type2)){
				return true;
			}
		}
		return false;
	}

	List<String> reorganizeFilters(Class<?> mainType, Collection<String> propertiesSet) {
		List<String> properties = new ArrayList<String>(propertiesSet);
		Map<String, List<String>> dependencies = getDependencies(mainType, properties);
		int countLoop = 0;
		for (int i = 0; i < properties.size()-1; i++) {
			countLoop++;
			if(countLoop == 200){
				break;//possible a circular reference
			}
			List<String> propertyDependencies = dependencies.get(properties.get(i));
			for (int j = i+1; j < properties.size(); j++) {
				if(propertyDependencies.contains(properties.get(j))){
					properties.add(i, properties.remove(j));
					i--;
					break;
				}
			}
		}
		return properties;
	}
}
