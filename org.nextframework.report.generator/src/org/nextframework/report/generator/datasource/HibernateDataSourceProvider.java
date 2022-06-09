package org.nextframework.report.generator.datasource;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.bean.annotation.DescriptionProperty;
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

	String fromClass;

	public void setFromClass(String fromClass) {
		this.fromClass = fromClass;
	}

	@Override
	@SuppressWarnings("rawtypes")
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

	@SuppressWarnings("rawtypes")
	public QueryBuilder createQueryBuilder(ReportElement element, Map<String, Object> filterMap, Map<String, Object> fixedCriteriaMap) {

		Class mainType = getMainType();

		QueryBuilder query = new QueryBuilder().from(ClassUtils.getUserClass(mainType));
		List<String> properties = element.getProperties();
		BeanDescriptor bd = BeanDescriptorFactory.forClass(mainType);
		JoinManager joinManager = new JoinManager(query.getAlias());

		//String select = "";

		for (String property : properties) {

			if (element.getData().isCalculated(property)) {
				continue;
			}

			String currentPath = "";

			String[] parts = property.split("\\.");
			for (int i = 0; i < parts.length; i++) {

				String basePath = currentPath;
				currentPath += (i > 0 ? "." : "") + parts[i];
				PropertyDescriptor pd = bd.getPropertyDescriptor(currentPath);

				boolean isRelationship = isRelationship(pd);
				if (isRelationship) {
					joinManager.addJoin(currentPath);
				}

				boolean ultParte = i == parts.length - 1;
				if (ultParte || pd.getAnnotation(Transient.class) != null) {

					List<String> usingFields = getUsingFields(pd, basePath);
					for (String field : usingFields) {

						PropertyDescriptor pdField = bd.getPropertyDescriptor(field);
						if (isRelationship(pdField)) {
							joinManager.addJoin(field);
						}

					}

					if (isRelationship) {

						BeanDescriptor currentPathBD = BeanDescriptorFactory.forClass(pd.getRawType());
						if (currentPathBD.getDescriptionPropertyName() != null) {

							PropertyDescriptor currentPathDP = currentPathBD.getPropertyDescriptor(currentPathBD.getDescriptionPropertyName());

							List<String> usingFieldsDP = getUsingFields(currentPathDP, currentPath);
							for (String field : usingFieldsDP) {

								PropertyDescriptor pdField = bd.getPropertyDescriptor(field);
								if (isRelationship(pdField)) {
									joinManager.addJoin(field);
								}

							}

						}

					}

					break;

				}

			}

		}

		Map<String, String> joinMap = joinManager.getJoinMap();
		for (String join : joinMap.keySet()) {
			query.leftOuterJoinFetch(join + " " + joinMap.get(join));
		}

		final Set<String> filteredFields = new HashSet<String>();
		Map<String, Object> transients = new HashMap<String, Object>() {
			private static final long serialVersionUID = 1L;

			public Object get(Object key) {
				filteredFields.add((String) key);
				return super.get(key);
			}
		};

		for (String filter : filterMap.keySet()) {
			String filterNoSuffix = removeSuffix(filter);
			PropertyDescriptor propertyDescriptor = bd.getPropertyDescriptor(filterNoSuffix);
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
			dao = DAOUtils.getDAOForClass(mainType);
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
		query.orderBy(query.getAlias() + "." + bd.getIdPropertyName());

		updateQuery(query, element, filterMap);

		return query;
	}

	private void checkProperty() {

	}

	private List<String> getUsingFields(PropertyDescriptor pd, String prefix) {
		List<String> fields = new ArrayList<String>();
		ReportField rf = pd.getAnnotation(ReportField.class);
		if (rf != null && rf.usingFields().length > 0) {
			for (String field : rf.usingFields()) {
				String path = prefix + (prefix.length() > 0 ? "." : "") + field;
				fields.add(path);
			}
		}
		DescriptionProperty dp = pd.getAnnotation(DescriptionProperty.class);
		if (dp != null && dp.usingFields().length > 0) {
			for (String field : dp.usingFields()) {
				String path = prefix + (prefix.length() > 0 ? "." : "") + field;
				fields.add(path);
			}
		}
		return fields;
	}

	private boolean isRelationship(PropertyDescriptor pd) {
		return pd.getAnnotation(ManyToOne.class) != null || pd.getAnnotation(OneToOne.class) != null;
	}

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
