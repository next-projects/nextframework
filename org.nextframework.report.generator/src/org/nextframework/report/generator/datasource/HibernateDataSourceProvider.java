package org.nextframework.report.generator.datasource;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.controller.crud.ListViewFilter;
import org.nextframework.core.standard.MessageType;
import org.nextframework.core.standard.Next;
import org.nextframework.exception.NextException;
import org.nextframework.persistence.DAOUtils;
import org.nextframework.persistence.GenericDAO;
import org.nextframework.persistence.QueryBuilder;
import org.nextframework.persistence.ResultListImpl;
import org.nextframework.persistence.TransientsFilter;
import org.nextframework.report.generator.ReportElement;
import org.nextframework.report.generator.annotation.ExtendBean;
import org.nextframework.report.generator.annotation.ReportField;
import org.nextframework.report.generator.chart.ChartElement;
import org.nextframework.report.generator.data.GroupElement;
import org.nextframework.report.generator.layout.FieldDetailElement;
import org.nextframework.report.generator.layout.LayoutItem;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

@SuppressWarnings("unchecked")
public class HibernateDataSourceProvider implements DataSourceProvider<Object> {

//	private static BeanFacotryUtils beanFacotries = new BeanFacotryUtils();

	String fromClass;

	public void setFromClass(String fromClass) {
		this.fromClass = fromClass;
	}

	@Override
	public Class getMainType() {
		try {
			return Class.forName(fromClass);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("all")
	@Override
	public List getResult(ReportElement element, Map<String, Object> filterMap, Map<String, Object> fixedCriteriaMap, int limitResults) {

		ListViewFilter filter = new ListViewFilter();
		filter.setPageSize(500);

		List fullResult = new ArrayList();
		QueryBuilder query = createQueryBuilder(element, filterMap, fixedCriteriaMap);
		ResultListImpl rs = new ResultListImpl(query, filter);

		while (true) {
			List subResult = rs.list();
			fullResult.addAll(subResult);
			if (rs.hasNextPage() && fullResult.size() < limitResults) {
				rs.nextPage();
			} else {
				break;
			}
		}

		return fullResult;
	}

	public QueryBuilder createQueryBuilder(ReportElement element, Map<String, Object> filterMap, Map<String, Object> fixedCriteriaMap) {

		QueryBuilder query = new QueryBuilder().from(ClassUtils.getUserClass(getMainType()));
		List<String> properties = element.getProperties();
		//Set<String> pathsSet = new HashSet<String>();
		BeanDescriptor beanDescriptor = BeanDescriptorFactory.forClass(getMainType());
		//properties.addAll(beans.getPropertiesWithAnnotation(getMainType(), ManyToOne.class));
		JoinManager joinManager = new JoinManager(query.getAlias());

		//LinkedHashSet<String> orderByProperties = new LinkedHashSet<String>();

		for (String property : properties) {

			if (element.getData().isCalculated(property)) {
				//this is a calculated field
				continue;
			}
			PropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(property);
//			if(propertyDescriptor.getAnnotation(ManyToOne.class) != null){
//				//query.leftOuterJoinFetch(query.getAlias()+"."+property + " ");
////				joinMap.put(query.getAlias()+"."+property, query.getAlias()+"_"+property);
//			}
			//TODO IMPROVE ALIAS CONCATENATION (REUSE ALIAS)

			if (property.contains(".")) {
//				String[] path = property.substring(0, property.lastIndexOf(".")).split("\\.");
//				String currentPath = "";
////				String lastPath = "";
//				for (String partialPath : path) {
//					currentPath += partialPath;
//					if(pathsSet.add(currentPath)){
//						propertyDescriptor = beanDescriptor.getPropertyDescriptor(currentPath);
//						ManyToOne manyToOne = propertyDescriptor.getAnnotation(ManyToOne.class);
//						if(manyToOne != null){
//							query.leftOuterJoinFetch(query.getAlias()+"."+currentPath + " ");
//						}
//					}
//				}
				boolean lastTransient = false;
				String[] parts = property.split("\\.");
//				String currentAlias = "";
				String currentPath = "";
				String basePath = "";
				for (int i = 0; i < parts.length; i++) {
					if (lastTransient) {
						break;
					}
					basePath = currentPath;
					if (i > 0) {
						currentPath += ".";
					}
					currentPath += parts[i];
//					String newAlias = currentAlias+"_"+parts[i];
//					query.leftOuterJoinFetch(currentAlias+"."+parts[i] + " "+newAlias);
					propertyDescriptor = beanDescriptor.getPropertyDescriptor(currentPath);
					if (propertyDescriptor.getAnnotation(Transient.class) != null) {
						lastTransient = true;
					}
					ManyToOne mto = propertyDescriptor.getAnnotation(ManyToOne.class);
					OneToOne oto = propertyDescriptor.getAnnotation(OneToOne.class);
					if (mto != null || oto != null) {
//						joinManager.put(currentAlias+"."+parts[i], newAlias);
						joinManager.addJoin(currentPath);
//						currentAlias = newAlias;
						//if (i == parts.length - 1) {
							//orderByProperties.add(currentPath);
						//}
					} else {
						if (propertyDescriptor.getAnnotation(ReportField.class) != null && propertyDescriptor.getAnnotation(ReportField.class).usingFields().length > 0) {
							for (String field : propertyDescriptor.getAnnotation(ReportField.class).usingFields()) {
//								treatReportField(currentPath, query, joinManager, field);
								String path = basePath;
								if (path.length() > 0) {
									path += ".";
								}
								joinManager.addJoin(path + field);
							}
						//} else {
							//if (i == parts.length - 1 && !lastTransient) {
							//	orderByProperties.add(currentPath);
							//}
						}
						break;
					}
				}
			} else {
//				if(propertyDescriptor.getAnnotation(ReportField.class) != null && propertyDescriptor.getAnnotation(ReportField.class).usingFields().length > 0){
//					for(String field: propertyDescriptor.getAnnotation(ReportField.class).usingFields()){
////						treatReportField("", query, joinManager, field);
//						joinManager.addJoin(field);
//					}
//				}
				if (propertyDescriptor.getAnnotation(ReportField.class) != null && propertyDescriptor.getAnnotation(ReportField.class).usingFields().length > 0) {
					for (String field : propertyDescriptor.getAnnotation(ReportField.class).usingFields()) {
//						treatReportField(currentPath, query, joinManager, field);
						joinManager.addJoin(field);
					}
				} else if (propertyDescriptor.getAnnotation(ManyToOne.class) != null || propertyDescriptor.getAnnotation(OneToOne.class) != null) {
					joinManager.addJoin(property);
					//orderByProperties.add(property);
//					joinManager.put(query.getAlias()+"."+property, query.getAlias()+"_"+property);
				//} else {
					//if (propertyDescriptor.getAnnotation(ExtendBean.class) == null && propertyDescriptor.getAnnotation(Transient.class) == null) {
					//	orderByProperties.add(property);
					//}
				}
			}
		}
		Map<String, String> joinMap = joinManager.getJoinMap();
		Set<String> joins = joinMap.keySet();
		for (String join : joins) {
			query.leftOuterJoinFetch(join + " " + joinMap.get(join));
		}
		final Set<String> filteredFields = new HashSet<String>();
		Map<String, Object> transients = new HashMap<String, Object>() {
			public Object get(Object key) {
				filteredFields.add((String) key);
				return super.get(key);
			}
		};
		for (String filter : filterMap.keySet()) {
			String filterNoSuffix = removeSuffix(filter);
			PropertyDescriptor propertyDescriptor = beanDescriptor.getPropertyDescriptor(filterNoSuffix);
			Object parameterValue = filterMap.get(filter);
			if (propertyDescriptor.getAnnotation(Transient.class) != null) {
				transients.put(filterNoSuffix, parameterValue);
				continue;
			}
			Type type = propertyDescriptor.getType();
			if (filter.contains("_begin")) {
				query.where(query.getAlias() + "." + filterNoSuffix + " >= ?", parameterValue);
			} else if (filter.contains("_end")) {
				query.where(query.getAlias() + "." + filterNoSuffix + " < ?", max(parameterValue));
			} else if (String.class.equals(type)) {
				query.whereLike(query.getAlias() + "." + filter, (String) parameterValue);
			} else if (parameterValue != null && parameterValue.getClass().isArray()) {
				query.whereIn(query.getAlias() + "." + filter, Arrays.asList((Object[]) parameterValue));
			} else {
				query.where(query.getAlias() + "." + filter + " = ?", parameterValue);
			}
		}
		for (String filter : fixedCriteriaMap.keySet()) {
			Object criteriaValue = fixedCriteriaMap.get(filter);
			if ("ISNULL".equals(criteriaValue)) {
				query.where(query.getAlias() + "." + filter + " is null");
			} else if ("NOTNULL".equals(criteriaValue)) {
				query.where(query.getAlias() + "." + filter + " is not null");
			}
		}
		GenericDAO dao = null;
		try {
			dao = DAOUtils.getDAOForClass(getMainType());
		} catch (NoSuchBeanDefinitionException e) {
			//if no DAO no problem...
		}
		if (dao instanceof TransientsFilter) {
			((TransientsFilter) dao).filterQueryForTransients(query, transients);
			Set<String> keySet = transients.keySet();
			keySet.removeAll(filteredFields);
			if (keySet.size() > 0) {
				Next.getRequestContext().addMessage("Not all the fields where filtered. Non filtered fields are: " + keySet, MessageType.WARN);
			}
		} else if (transients.size() > 0) {
			throw new NextException("There are transient fields " + transients + " for filtering, but the " + dao.getClass().getSimpleName() + " does not implement " + TransientsFilter.class.getSimpleName() + ".");
		}

		//Melhor reaordenar fora do BD para ordenar pelos atributos calculados também.
		//Porém, é necessário que haja pelo menos 1 critério de ordenação, para garantir a paginação.
		query.orderBy(query.getAlias() + "." + beanDescriptor.getIdPropertyName());

		/*
		StringBuilder orderByBuffer = new StringBuilder("  ");
		for (String property : orderByProperties) {
			if (element.getData().isCalculated(property)) {
				continue;
			}
			String orderColumn = query.getAlias() + "." + property;

			int pIndex = -1;
			do {
				pIndex = orderColumn.indexOf(".", pIndex + 1);
				if (pIndex > -1) {
					String base = orderColumn.substring(0, pIndex);
					String alias = joinMap.get(base);
					if (alias != null) {
						orderColumn = alias + orderColumn.substring(pIndex);
						pIndex = 0;
					}
				} else {
					String alias = joinMap.get(orderColumn);
					if (alias != null) {
						orderColumn = alias;
						pIndex = -1;
					}
				}
			} while (pIndex != -1);

			orderByBuffer.append(orderColumn + ", ");
		}
		orderByBuffer.setLength(orderByBuffer.length() - 2);
		query.orderBy(orderByBuffer.toString());
		*/

		updateQuery(query, element, filterMap);

		return query;
	}

//	private void treatReportField(String currentPath, QueryBuilder query, JoinManager joinMap, String field) {
//		if(field.contains(".")){
//			String[] parts = field.split("\\.");
//			String currentAlias = query.getAlias();
//			for (int i = 0; i < parts.length; i++) {
//				String newAlias = currentAlias+"_"+parts[i];
////							query.leftOuterJoinFetch(currentAlias+"."+parts[i] + " "+newAlias);
////				joinMap.put(currentAlias+"."+parts[i], newAlias);
//				joinMap.addJoin(currentPath+"."+parts[i]);
//				currentAlias = newAlias;
//			}
//		} else {
//			//query.leftOuterJoinFetch(query.getAlias()+"."+field + " "+query.getAlias()+"_"+field);
//			joinMap.put(query.getAlias()+"."+field, query.getAlias()+"_"+field);
//		}
//	}

	protected void updateQuery(QueryBuilder<?> query, ReportElement element, Map<String, Object> filterMap) {

	}

	private Object max(Object object) {
		if (object instanceof Calendar) {
			Calendar c = (Calendar) object;
			Calendar instance = Calendar.getInstance();
			instance.setTime(c.getTime());
			instance.add(Calendar.DATE, 1);
			return instance;
		}
		return object;
	}

	protected String removeSuffix(String filter) {
		if (filter.endsWith("_begin")) {
			return filter.substring(0, filter.length() - "_begin".length());
		}
		if (filter.endsWith("_end")) {
			return filter.substring(0, filter.length() - "_end".length());
		}
		return filter;
	}

	public List<String> getProperties(ReportElement element) {
		List<String> properties = new ArrayList<String>();
		for (LayoutItem layoutItem : element.getLayout().getItems()) {
			if (layoutItem instanceof FieldDetailElement) {
				properties.add(((FieldDetailElement) layoutItem).getName());
			}
		}
		List<GroupElement> groups = element.getData().getGroups();
		for (GroupElement groupElement : groups) {
			properties.add(groupElement.getName());
		}
		List<ChartElement> charts = element.getCharts().getItems();
		for (ChartElement chartElement : charts) {
			String groupProperty = chartElement.getGroupProperty();
			String seriesProperty = chartElement.getSeriesProperty();
			String valueProperty = chartElement.getValueProperty();
			if (groupProperty != null) {
				properties.add(groupProperty);
			}
			if (StringUtils.hasText(seriesProperty)) {
				properties.add(seriesProperty);
			}
			if (valueProperty != null && !valueProperty.equals("count")) {
				properties.add(valueProperty);
			}
		}
		return properties;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fromClass == null) ? 0 : fromClass.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HibernateDataSourceProvider other = (HibernateDataSourceProvider) obj;
		if (fromClass == null) {
			if (other.fromClass != null)
				return false;
		} else if (!fromClass.equals(other.fromClass))
			return false;
		return true;
	}

}
