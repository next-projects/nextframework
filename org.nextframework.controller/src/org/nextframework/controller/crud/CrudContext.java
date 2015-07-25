package org.nextframework.controller.crud;

import java.util.List;

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

	private String displayName;
	private String beanName;
	private Class<?> beanClass;
	private String idPropertyName;

	private ListModel listModel = new ListModel();

	public CrudContext(Class<?> beanClass) {
		this.beanClass = beanClass;
		this.displayName = BeanDescriptorFactory.forClass(beanClass).getDisplayName();
		this.beanName = Util.strings.uncaptalize(beanClass.getSimpleName());
		this.idPropertyName = BeanDescriptorFactory.forClass(beanClass).getIdPropertyName();
	}

	public static CrudContext getCurrentInstance() {
		return (CrudContext) NextWeb.getRequestContext().getAttribute(CrudContext.class.getName());
	}

	public static void setCurrentInstance(CrudContext context) {
		NextWeb.getRequestContext().setAttribute(CrudContext.class.getName(), context);
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public Class<?> getBeanClass() {
		return beanClass;
	}

	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	public String getIdPropertyName() {
		return idPropertyName;
	}

	public void setIdPropertyName(String idPropertyName) {
		this.idPropertyName = idPropertyName;
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
