package org.nextframework.controller.crud;

import java.util.List;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.core.web.NextWeb;
import org.nextframework.persistence.PageAndOrder;
import org.nextframework.util.Util;

/**
 * Crud request data
 * 
 * @author rogelgarcia
 * @since 2015
 */
public class CrudContext {

	private Class<?> beanClass;
	private String beanName;
	private String idPropertyName;

	private String displayName;
	private boolean customDisplayName;

	private ListModel listModel = new ListModel();

	public CrudContext(Class<?> beanClass) {
		this.beanClass = beanClass;
		BeanDescriptor bd = BeanDescriptorFactory.forClass(beanClass);
		this.displayName = Util.beans.getDisplayName(bd, NextWeb.getRequestContext().getLocale());
		this.beanName = Util.strings.uncaptalize(beanClass.getSimpleName());
		this.idPropertyName = bd.getIdPropertyName();
	}

	public static CrudContext getCurrentInstance() {
		return (CrudContext) NextWeb.getRequestContext().getAttribute(CrudContext.class.getName());
	}

	public static void setCurrentInstance(CrudContext context) {
		NextWeb.getRequestContext().setAttribute(CrudContext.class.getName(), context);
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getIdPropertyName() {
		return idPropertyName;
	}

	public void setIdPropertyName(String idPropertyName) {
		this.idPropertyName = idPropertyName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
		this.customDisplayName = true;
	}

	public boolean hasCustomDisplayName() {
		return customDisplayName;
	}

	public ListModel getListModel() {
		return listModel;
	}

	public void setListModel(ListModel crudList) {
		this.listModel = crudList;
	}

	public static class ListModel {

		PageAndOrder filter;
		List<?> list;

		public ListModel() {
			super();
		}

		public PageAndOrder getFilter() {
			return filter;
		}

		public void setFilter(PageAndOrder filter) {
			this.filter = filter;
		}

		public List<?> getList() {
			return list;
		}

		public void setList(List<?> list) {
			this.list = list;
		}

	}

}
