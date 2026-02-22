package org.nextframework.report.generator.datasource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.bean.annotation.DescriptionProperty;
import org.nextframework.controller.crud.ListViewFilter;
import org.nextframework.core.standard.Next;
import org.nextframework.exception.NextException;
import org.nextframework.persistence.DAOUtils;
import org.nextframework.persistence.GeneratedReportDAOFilter;
import org.nextframework.persistence.GenericDAO;
import org.nextframework.persistence.QueryBuilder;
import org.nextframework.persistence.QueryBuilder.JoinMode;
import org.nextframework.persistence.ResultListImpl;
import org.nextframework.report.generator.ReportElement;
import org.nextframework.report.generator.annotation.ExtendBean;
import org.nextframework.report.generator.annotation.ReportField;
import org.nextframework.report.generator.datasource.extension.BeanExtender;
import org.nextframework.util.Util;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.util.ClassUtils;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;

public class HibernateDataSourceProvider implements DataSourceProvider {

	private BeanExtender beanExtender;

	@Override
	public String getName() {
		return "hibernateDataProvider";
	}

	@Override
	public Class<?> getMainType(String fromClass) {
		try {
			Class<?> mainType = Class.forName(fromClass);
			return getExtendedClass(mainType);
		} catch (ClassNotFoundException e) {
			throw new NextException(e);
		}
	}

	protected Class<?> getExtendedClass(Class<?> mainType) {
		mainType = Util.objects.getRealClass(mainType);
		Class<?> extendedMainType = getBeanExtender().createExtendedClassForBeanClass(mainType);
		return extendedMainType;
	}

	protected BeanExtender getBeanExtender() {
		if (beanExtender == null) {
			List<Object> allBeans = new ArrayList<Object>();
			String[] beanDefinitionNames = Next.getBeanFactory().getBeanDefinitionNames();
			for (String beanName : beanDefinitionNames) {
				Object bean = Next.getBeanFactory().getBean(beanName);
				if (bean != null) {
					allBeans.add(bean);
				}
			}
			beanExtender = new BeanExtender(allBeans);
		}
		return beanExtender;
	}

	@Override
	public <OBJ> List<OBJ> getResult(Class<OBJ> mainType, ReportElement element, Map<String, Object> filterMap, Map<String, Object> fixedCriteriaMap, int limitResults) {

		BeanExtender beanExtender = getBeanExtender();
		boolean extend = beanExtender.isSubClass(mainType);
		Class<OBJ> mainType2 = Util.objects.getRealClass(mainType);

		GenericDAO<OBJ> dao = null;
		try {
			dao = DAOUtils.getDAOForClass(mainType);
		} catch (NoSuchBeanDefinitionException e) {
			//if no DAO no problem...
		}

		QueryBuilder<OBJ> query = createQueryBuilder(mainType, dao, element, filterMap, fixedCriteriaMap);

		ListViewFilter filter = new ListViewFilter();
		filter.setPageSize(500);

		ResultListImpl<OBJ> rs = new ResultListImpl<OBJ>(query, filter);
		List<OBJ> fullResult = new ArrayList<OBJ>();
		while (true) {

			List<OBJ> subResult = rs.list();
			if (extend) {
				for (OBJ object : subResult) {
					OBJ extendedBean = (OBJ) beanExtender.extendBean(object, mainType2);
					boolean filterOk = validateExtendedFilter(dao, filterMap, extendedBean);
					if (filterOk) {
						fullResult.add(extendedBean);
					}
				}
			} else {
				fullResult.addAll(subResult);
			}

			if (rs.hasNextPage() && fullResult.size() < limitResults) {
				rs.nextPage();
			} else {
				break;
			}

		}

		return fullResult;
	}

	@SuppressWarnings("unchecked")
	protected <OBJ> QueryBuilder<OBJ> createQueryBuilder(Class<OBJ> mainType, GenericDAO<OBJ> dao, ReportElement element, Map<String, Object> filterMap, Map<String, Object> fixedCriteriaMap) {

		BeanDescriptor bd = BeanDescriptorFactory.forClass(mainType);

		QueryBuilder<OBJ> query = new QueryBuilder<OBJ>().from(ClassUtils.getUserClass(mainType));

		Set<String> selectProperties = new LinkedHashSet<String>();
		JoinManager joinManager = new JoinManager(query.getAlias());
		Set<String> fetchCollections = new LinkedHashSet<String>();
		Set<String> checkedProperties = new LinkedHashSet<String>();

		List<String> properties = element.getProperties();
		for (String property : properties) {
			if (!element.getData().isCalculated(property)) {
				checkProperty(bd, property, selectProperties, joinManager, fetchCollections, true, checkedProperties);
			}
		}

		String prefixo = query.getFrom().getAlias() + ".";

		Set<String> aliasRemovidos = new HashSet<String>();
		Map<String, String> joinMap = joinManager.getJoinMap();
		for (String join : joinMap.keySet()) {
			String joinAlias = joinMap.get(join);
			if (join.startsWith(prefixo)) {
				String property = join.substring(prefixo.length());
				PropertyDescriptor pd = bd.getPropertyDescriptor(property);
				if (pd.getAnnotation(ExtendBean.class) != null) {
					aliasRemovidos.add(joinAlias);
					continue;
				}
			} else if (join.contains(".")) {
				String alias = join.substring(0, join.indexOf("."));
				if (aliasRemovidos.contains(alias)) {
					aliasRemovidos.add(joinAlias);
					continue;
				}
			}
			query.join(JoinMode.LEFT_OUTER, false, join + " " + joinAlias);
		}

		String selectStr = "";
		for (String selectProperty1 : selectProperties) {
			String selectProperty2 = joinManager.applyJoin(selectProperty1);
			String alias = selectProperty2.substring(0, selectProperty2.indexOf("."));
			if (selectProperty2.startsWith(prefixo)) {
				String property = selectProperty2.substring(prefixo.length());
				PropertyDescriptor pd = bd.getPropertyDescriptor(property);
				if (pd.getAnnotation(ExtendBean.class) != null) {
					continue;
				}
			} else if (aliasRemovidos.contains(alias)) {
				continue;
			}
			selectStr += (selectStr.length() > 0 ? "," : "") + selectProperty2;
		}
		query.select(selectStr);

		for (String fetchCollection : fetchCollections) {
			query.fetchCollection(fetchCollection, true);
		}

		for (String filter : filterMap.keySet()) {

			String filterNoSuffix = removeSuffix(filter);
			PropertyDescriptor pd = bd.getPropertyDescriptor(filterNoSuffix);
			Object parameterValue = filterMap.get(filter);

			if (dao instanceof GeneratedReportDAOFilter) {
				GeneratedReportDAOFilter<OBJ> dao2 = (GeneratedReportDAOFilter<OBJ>) dao;
				if (dao2.isFilterableForPropertyForGeneratedReport(filter)) {
					dao2.filterQueryForGeneratedReport(query, filter, filterNoSuffix, parameterValue);
					continue;
				}
			}

			if (pd.getAnnotation(Transient.class) != null) {
				throw new NextException("The class " + dao.getClass().getSimpleName() + " must implement " + GeneratedReportDAOFilter.class.getSimpleName() + " and filter the transient field " + filter + "!");
			}

			if (isFilterExtended(bd, filter)) {
				continue;
			}

			if (parameterValue != null) {
				if (filter.contains("_begin")) {
					query.where(query.getAlias() + "." + filterNoSuffix + " >= ?", parameterValue);
				} else if (filter.contains("_end")) {
					query.where(query.getAlias() + "." + filterNoSuffix + " < ?", max(parameterValue));
				} else if (parameterValue instanceof String) {
					query.whereLike(query.getAlias() + "." + filter, (String) parameterValue);
				} else if (parameterValue.getClass().isArray()) {
					query.whereIn(query.getAlias() + "." + filter, Arrays.asList((Object[]) parameterValue));
				} else {
					query.where(query.getAlias() + "." + filter + " = ?", parameterValue);
				}
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

		//Melhor reaordenar fora do BD para ordenar pelos atributos calculados também.
		//Porém, é necessário que haja pelo menos 1 critério de ordenação, para garantir a paginação.
		query.orderBy(query.getAlias() + "." + bd.getIdPropertyName());

		if (dao instanceof GeneratedReportDAOFilter) {
			GeneratedReportDAOFilter<OBJ> dao2 = (GeneratedReportDAOFilter<OBJ>) dao;
			dao2.updateQueryForGeneratedReport(query);
		}

		return query;
	}

	protected void checkProperty(BeanDescriptor bd, String property, Set<String> selectProperties, JoinManager joinManager, Set<String> fetchCollections, boolean descOnly, Set<String> checkedProperties) {

		if (checkedProperties.contains(property)) {
			return;
		}
		checkedProperties.add(property);

		String currentPath = "";
		boolean isRelationship = true;
		String[] parts = property.split("\\.");
		for (int i = 0; i < parts.length; i++) {

			String basePath = currentPath;
			currentPath += (i > 0 ? "." : "") + parts[i];
			PropertyDescriptor pd = bd.getPropertyDescriptor(currentPath);
			boolean lastPart = i == parts.length - 1;

			ReportField rf = pd.getAnnotation(ReportField.class);
			if (rf != null && rf.usingFields().length > 0) {
				String[] fields = getFields(rf.usingFields(), basePath);
				for (String field : fields) {
					checkProperty(bd, field, selectProperties, joinManager, fetchCollections, false, checkedProperties);
				}
			}

			boolean isInsideRelationship = isRelationship;
			isRelationship = pd.getAnnotation(ManyToOne.class) != null || pd.getAnnotation(OneToOne.class) != null;
			if (isRelationship) {

				joinManager.addJoin(currentPath);

				if (lastPart) {

					if (descOnly) {

						BeanDescriptor currentPathBD = BeanDescriptorFactory.forClass(pd.getRawType());
						if (currentPathBD.getDescriptionPropertyName() != null) {

							String dpPath = currentPath + (currentPath.length() > 0 ? "." : "") + currentPathBD.getDescriptionPropertyName();
							checkProperty(bd, dpPath, selectProperties, joinManager, fetchCollections, true, checkedProperties);

							PropertyDescriptor currentPathDP = currentPathBD.getPropertyDescriptor(currentPathBD.getDescriptionPropertyName());
							DescriptionProperty dp = currentPathDP.getAnnotation(DescriptionProperty.class);
							if (dp != null && dp.usingFields().length > 0) {
								String[] fields = getFields(dp.usingFields(), currentPath);
								for (String field : fields) {
									checkProperty(bd, field, selectProperties, joinManager, fetchCollections, true, checkedProperties);
								}
							}

						}

					} else {

						selectProperties.add(currentPath);

					}

				}

			} else if (pd.getAnnotation(OneToMany.class) != null) {

				fetchCollections.add(currentPath);
				break;

			} else if (pd.getAnnotation(Transient.class) != null) {

				break;

			} else if (isInsideRelationship && lastPart) {

				selectProperties.add(currentPath);

			}

		}

	}

	private String[] getFields(String[] usingFields, String prefix) {
		List<String> fields = new ArrayList<String>();
		for (String usingField : usingFields) {
			for (String field : usingField.split("(\\s*)?[,;](\\s*)?")) {
				String fieldPath = prefix + (prefix.length() > 0 ? "." : "") + field;
				fields.add(fieldPath);
			}
		}
		return fields.toArray(new String[fields.size()]);
	}

	protected boolean isFilterExtended(BeanDescriptor bd, String filter) {
		String filterBase = filter.contains(".") ? filter.split("\\.")[0] : filter;
		filterBase = removeSuffix(filterBase);
		PropertyDescriptor pdBase = bd.getPropertyDescriptor(filterBase);
		return pdBase.getAnnotation(ExtendBean.class) != null;
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

	@SuppressWarnings("unchecked")
	protected <OBJ> boolean validateExtendedFilter(GenericDAO<OBJ> dao, Map<String, Object> filterMap, Object extendedBean) {

		BeanDescriptor bd = BeanDescriptorFactory.forBean(extendedBean);

		for (String filter : filterMap.keySet()) {

			if (!isFilterExtended(bd, filter)) {
				continue;
			}

			if (dao instanceof GeneratedReportDAOFilter) {
				GeneratedReportDAOFilter<OBJ> dao2 = (GeneratedReportDAOFilter<OBJ>) dao;
				if (dao2.isFilterableForPropertyForGeneratedReport(filter)) {
					continue;
				}
			}

			String filterNoSuffix = removeSuffix(filter);
			PropertyDescriptor pd = bd.getPropertyDescriptor(filterNoSuffix);
			Object beanValue = convertType(pd.getValue());
			Object parameterValue = convertType(filterMap.get(filter));

			if (parameterValue != null) {
				if (filter.contains("_begin")) {
					Calendar beanValueCal = (Calendar) beanValue;
					Calendar parameterValueCal = (Calendar) parameterValue;
					if (beanValueCal == null || beanValueCal.before(parameterValueCal)) {
						return false;
					}
				} else if (filter.contains("_end")) {
					Calendar beanValueCal = (Calendar) beanValue;
					Calendar parameterValueCal = (Calendar) max((Calendar) parameterValue);
					if (beanValueCal == null || !beanValueCal.before(parameterValueCal)) {
						return false;
					}
				} else if (parameterValue instanceof String) {
					String parameterValueStr = (String) parameterValue;
					parameterValueStr = Util.strings.isEmpty(parameterValueStr) ? null : parameterValueStr;
					if (parameterValueStr != null) {
						String beanValueStr = (String) beanValue;
						beanValueStr = Util.strings.isEmpty(beanValueStr) ? null : beanValueStr;
						if (beanValueStr == null || !beanValueStr.contains(parameterValueStr)) {
							return false;
						}
					}
				} else if (parameterValue.getClass().isArray()) {
					boolean ok = false;
					for (Object pv : (Object[]) parameterValue) {
						if (Util.objects.equals(beanValue, pv)) {
							ok = true;
							break;
						}
					}
					if (!ok) {
						return false;
					}
				} else {
					if (!Util.objects.equals(beanValue, parameterValue)) {
						return false;
					}
				}
			}

		}

		return true;
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

	private Object convertType(Object value) {
		if (value instanceof Date) {
			Calendar c = Calendar.getInstance();
			c.setTime((Date) value);
			return c;
		}
		return value;
	}

	/*
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
	*/

}
