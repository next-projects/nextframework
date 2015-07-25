package org.nextframework.summary.compilation;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.nextframework.bean.BeanDescriptorUtils;
import org.nextframework.summary.Summary;
import org.nextframework.summary.SummaryRow;
import org.nextframework.summary.SummaryUtils;
import org.nextframework.summary.annotations.Scope;
import org.nextframework.summary.definition.SummaryDefinition;
import org.nextframework.summary.definition.SummaryGroupDefinition;
import org.nextframework.summary.definition.SummaryVariableDefinition;

public abstract class AbstractSumarizedData<SUMARY extends Summary<?>, LISTTYPE, SELF extends AbstractSumarizedData<SUMARY, LISTTYPE, SELF>> {

	public abstract List<LISTTYPE> getItems();
	protected abstract SELF createNew(List<LISTTYPE> result);
	protected abstract Summary<?> getSummaryFor(LISTTYPE e);	
	protected abstract Object getRowFor(LISTTYPE e);	
	protected abstract SummaryDefinition<SUMARY> getSummaryDefinition();

	public SELF filterByGroup(String group, SummaryRow<?, SUMARY> example){
		return filterByGroup(group, example.getSummary());
	}
	public SELF filterByGroup(String group, SUMARY example){
		return filterByGroup(group, getProperty(SummaryUtils.convertCompositeGroupToMethodFormat(group), example));
	}

	public SELF filterByGroup(String group, Object propertyValue) {
		SortedSet<SummaryGroupDefinition> groups = getSummaryDefinition().getGroups();
		boolean exist = false;
		for (SummaryGroupDefinition groupDef : groups) {
			if(groupDef.getGroup().name().equals(group) || groupDef.getName().equals(group)){
				exist = true;
			}
		}
		if(!exist){
			Set<String> availableGroups = new LinkedHashSet<String>();
			for (SummaryGroupDefinition groupDef : groups) {
				availableGroups.add(groupDef.getName());
			}
			throw new IllegalArgumentException("No group named '"+group+"' found in summary "+this.getSummaryDefinition().getSummaryClass().getName()+". Available groups are "+availableGroups);
		}
		List<LISTTYPE> result = new ArrayList<LISTTYPE>();
		for (LISTTYPE e : getItems()) {
			Object groupProperty = getProperty(SummaryUtils.convertCompositeGroupToMethodFormat(group), getSummaryFor(e));
			if(groupProperty == null && propertyValue == null){
				result.add(e);
			} else if(groupProperty != null && groupProperty.equals(propertyValue)){
				result.add(e);
			}
		}
		return createNew(result);
	}
	
	public SELF filterRows(String property, Object value) {
		List<LISTTYPE> result = new ArrayList<LISTTYPE>();
		for (LISTTYPE e : getItems()) {
			Object propertyValue = getProperty(property, getRowFor(e));
			if(propertyValue == null && property == null){
				result.add(e);
			} else if(propertyValue != null && propertyValue.equals(value)){
				result.add(e);
			}
		}
		return createNew(result);
	}
	
	private Object getProperty(String propertyName, Object example) {
		try {
			Method getterMethod = BeanDescriptorUtils.getGetterMethod(example.getClass(), propertyName);
			getterMethod.setAccessible(true);
			return getterMethod.invoke(example);
		} catch (Exception e) {
			throw new RuntimeException("Invalid property '"+propertyName+"' of class "+example.getClass(), e);
		}
	}
	

	/**
	 * Reorder the items according to the order parameter.
	 * 
	 * @param order list of comma separated attributes that the list must be ordered by.
	 * @return the current object (this object will be actually modified, so it is not mandatory to create a reference to the returned object)
	 */
	@SuppressWarnings("unchecked")
	public SELF orderBy(String order){
		List<OrderItem> orderItems = OrderItem.toList(order);
		String[] avaiableGroups = getAvaiableGroups();
		Map<String, List<OrderItem>> mapGroupOrderItems = reorganizeOrderItems(orderItems);
		for (String currentGroup : avaiableGroups) {
			reorderGroup(currentGroup, mapGroupOrderItems.get(currentGroup));
		}
		return (SELF) this;
	}
	
	protected abstract void reorderGroup(String currentGroup, List<org.nextframework.summary.compilation.AbstractSumarizedData.OrderItem> list);
	
	protected abstract String[] getAvaiableGroups();
	
	
	private Map<String, List<OrderItem>> reorganizeOrderItems(List<OrderItem> orderItems) {
		Map<String, List<OrderItem>> result = new HashMap<String, List<OrderItem>>();
		for (OrderItem orderItem : orderItems) {
			String groupForOrderItem = getGroupForOrderItem(orderItem);
			List<OrderItem> list = result.get(groupForOrderItem);
			if(list == null){
				list = new ArrayList<OrderItem>();
				result.put(groupForOrderItem, list);
			}
			list.add(orderItem);
		}
		return result;
	}
	
	private String getGroupForOrderItem(OrderItem orderItem) {
		if(orderItem.property.startsWith("row")){
			return "ROW";// special group row
		}
		String property = orderItem.property; 
		if(property.startsWith("summary.")){
			property = property.substring("summary.".length());
		}
		//verify the group that the property is part of
		if(property.contains(".")){
			property = property.substring(0, property.indexOf('.'));
		} 
		Set<SummaryGroupDefinition> groups = getSummaryDefinition().getGroups();
		for (SummaryGroupDefinition group : groups) {
			if(group.getName().equals(property) || group.getMethod().getName().equals(BeanDescriptorUtils.getGetterFromProperty(property))){
				return group.getName();
			}
		}
		List<SummaryVariableDefinition> variables = getSummaryDefinition().getVariables();
		for (SummaryVariableDefinition variable : variables) {
			if(variable.getName().equals(property) || variable.getMethod().getName().equals(BeanDescriptorUtils.getGetterFromProperty(property))){
				if(variable.getScope() == Scope.GROUP){
					return variable.getScopeGroup();
				} else if(variable.getScope() == Scope.ROW){
					return "ROW";
				} else {
					throw new IllegalArgumentException("cannot order by "+orderItem.property);
				}
			}
		}
		//if there's no group it's row
		return "ROW";
	}
	

	protected static class OrderItem {
		String property;
		boolean asc = true;
		public static List<OrderItem> toList(String order){
			ArrayList<OrderItem> list = new ArrayList<OrderItem>();
			String[] elements = order.split("\\s*,\\s*");
			for (String el : elements) {
				String[] split = el.split("\\s+");
				if(split.length == 1){
					OrderItem e = new OrderItem();
					e.property = split[0];
					list.add(e);
				} else if(split.length == 2){
					OrderItem e = new OrderItem();
					e.property = split[0];
					if(split[1].equals("asc")) {
						e.asc = true;
					} else if(split[1].equals("desc")) {
						e.asc = false;
					} else {
						throw new IllegalArgumentException("invalid order: "+order);
					}
					list.add(e);
				} else {
					throw new IllegalArgumentException("invalid order: "+order);
				}
			}
			return list;
		}
		@Override
		public String toString() {
			return property + (asc?" asc":" desc");
		}
	}
}
