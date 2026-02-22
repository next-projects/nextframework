/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2009 the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * You may obtain a copy of the license at
 * 
 *     http://www.gnu.org/copyleft/lesser.html
 * 
 */
package org.nextframework.controller.crud;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.nextframework.controller.crud.CrudContext.ListModel;
import org.nextframework.core.web.DefaultWebRequestContext;
import org.nextframework.core.web.WebRequestContext;
import org.nextframework.exception.NextException;
import org.nextframework.persistence.ResultList;
import org.nextframework.service.GenericService;
import org.nextframework.util.Util;
import org.springframework.core.GenericTypeResolver;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * @author rogelgarcia
 * @since 01/02/2006
 * @version 1.1
 */
public class CrudController<FILTER extends ListViewFilter, FORMBEAN, BEAN> extends AbstractCrudController<FILTER, FORMBEAN> {

	protected Class<? extends FILTER> listCommandClass;
	protected Class<FORMBEAN> formCommandClass;
	protected Class<BEAN> beanClass;

	protected GenericService<BEAN> genericService;

	public CrudController() {
		initGenericClasses();
	}

	@SuppressWarnings("unchecked")
	protected void initGenericClasses() {
		@SuppressWarnings("rawtypes")
		Class[] classes = GenericTypeResolver.resolveTypeArguments(this.getClass(), CrudController.class);
		setListCommandClass(classes[0]);
		setFormCommandClass(classes[1]);
		setBeanClass(classes[2]);
	}

	public CrudController(Class<FILTER> listCommandClass, Class<FORMBEAN> formCommandClass, Class<BEAN> beanClass) {
		setListCommandClass(listCommandClass);
		setFormCommandClass(formCommandClass);
		setBeanClass(beanClass);
	}

	public void setGenericService(GenericService<BEAN> genericService) {
		this.genericService = genericService;
	}

	public void setBeanClass(Class<BEAN> beanClass) {
		this.beanClass = beanClass;
	}

	public void setFormCommandClass(Class<FORMBEAN> formCommandClass) {
		this.formCommandClass = formCommandClass;
	}

	public void setListCommandClass(Class<FILTER> listCommandClass) {
		this.listCommandClass = listCommandClass;
	}

	public GenericService<BEAN> getGenericService() {
		return genericService;
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		CrudContext.setCurrentInstance(createCrudContext());
		return super.handleRequest(request, response);
	}

	protected CrudContext createCrudContext() {
		return new CrudContext(this.beanClass);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void validate(Object obj, BindException errors, String action) {
		if (obj != null) {
			if (listCommandClass.isAssignableFrom(obj.getClass())) {
				validateFilter((FILTER) obj, errors);
			}
			if (formCommandClass.isAssignableFrom(obj.getClass())) {
				validateBean((FORMBEAN) obj, errors);
			}
		}
	}

	/**
	 * Override to validate bean
	 * @param bean
	 * @param errors
	 */
	protected void validateBean(FORMBEAN bean, BindException errors) {

	}

	/**
	 * Override to validade form
	 * @param filter
	 * @param errors
	 */
	protected void validateFilter(FILTER filter, BindException errors) {

	}

	@Override
	public ModelAndView doList(WebRequestContext request, FILTER filter) throws CrudException {
		try {
			List<BEAN> list = configureListModel(request, filter);
			list(request, filter, list);
		} catch (Exception e) {
			throw new CrudException(LIST, e);
		}
		return getListModelAndView(request, filter);
	}

	protected void list(WebRequestContext request, FILTER filter, List<BEAN> list) throws Exception {
		list(request, filter);
	}

	@Deprecated
	protected void list(WebRequestContext request, FILTER filter) throws Exception {
	}

	protected List<BEAN> configureListModel(WebRequestContext request, FILTER filter) throws Exception {
		List<BEAN> list;
		if (!listEmptyOnFirstRequest() || filter.isNotFirstTime()) {
			ResultList<BEAN> resultList = getList(request, filter);
			list = resultList.list();
		} else {
			list = new ArrayList<BEAN>();
			filter.setCurrentPage(0);
			filter.setNumberOfPages(0);
		}
		CrudContext context = CrudContext.getCurrentInstance();
		ListModel listModel = context.getListModel();
		listModel.setList(list);
		listModel.setFilter(filter);
		return list;
	}

	protected ResultList<BEAN> getList(WebRequestContext request, FILTER filter) {
		return genericService.loadListModel(filter);
	}

	protected boolean listEmptyOnFirstRequest() {
		return false;
	}

	@Override
	public ModelAndView doForm(WebRequestContext request, FORMBEAN form) throws CrudException {
		try {
			setFormDefaultInfo(request, form);
			form(request, form);
		} catch (Exception e) {
			throw new CrudException(FORM, e);
		}
		return getFormModelAndView(request, form);
	}

	protected void setFormDefaultInfo(WebRequestContext request, FORMBEAN form) throws Exception {
		request.setAttribute(CrudContext.getCurrentInstance().getBeanName(), form);
	}

	protected void form(WebRequestContext request, FORMBEAN form) throws Exception {
	}

	@Override
	public ModelAndView doCreate(WebRequestContext request, FORMBEAN form) throws CrudException {
		try {
			BEAN bean = formToBean(form);
			bean = create(request, bean);
			form = beanToForm(bean);
		} catch (Exception e) {
			throw new CrudException(CREATE, e);
		}
		return getCreateModelAndView(request, form);
	}

	protected BEAN create(WebRequestContext request, BEAN form) throws Exception {
		try {
			return beanClass.newInstance();
		} catch (InstantiationException e) {
			throw new NextException("cannot instantiate " + formCommandClass, e);
		} catch (IllegalAccessException e) {
			throw new NextException("cannot instantiate " + formCommandClass, e);
		}
	}

	@Override
	public ModelAndView doUpdate(WebRequestContext request, FORMBEAN form) throws CrudException {
		try {
			BEAN bean = formToBean(form);
			bean = load(request, bean);
			checkIfNull(bean);
			form = beanToForm(bean);
		} catch (Exception e) {
			throw new CrudException(UPDATE, e);
		}
		return getUpdateModelAndView(request, form);
	}

	@Deprecated
	private void checkIfNull(BEAN bean) {
		if (bean == null) {
			throw new NextException("Registro não encontrado no banco de dados!");
		}
	}

	protected BEAN load(WebRequestContext request, BEAN bean) throws Exception {
		return genericService.loadFormModel(bean);
	}

	@Override
	public ModelAndView doSave(WebRequestContext request, FORMBEAN form) throws CrudException {
		BEAN bean = null;
		try {
			bean = formToBean(form);
			save(request, bean);
		} catch (Exception e) {
			throw new CrudException(SAVE, e);
		}
		return getSaveModelAndView(request, bean);
	}

	protected void save(WebRequestContext request, BEAN bean) throws Exception {
		genericService.saveOrUpdate(bean);
	}

	@Override
	public ModelAndView doDelete(WebRequestContext request, FORMBEAN form) throws CrudException {
		BEAN bean = null;
		try {
			bean = formToBean(form);
			delete(request, bean);
		} catch (Exception e) {
			throw new CrudException(DELETE, e);
		}
		return getDeleteModelAndView(request, bean);
	}

	protected void delete(WebRequestContext request, BEAN bean) throws Exception {
		genericService.delete(bean);
	}

	@SuppressWarnings("unchecked")
	public FORMBEAN beanToForm(BEAN bean) {
		if (formCommandClass.equals(beanClass)) {
			return (FORMBEAN) bean;
		}
		throw new NextException("cannot convert bean to form, override beanToForm method");
	}

	@SuppressWarnings("unchecked")
	public BEAN formToBean(FORMBEAN form) {
		if (beanClass.equals(formCommandClass)) {
			return (BEAN) form;
		}
		throw new NextException("cannot convert form to bean, override formToBean method");
	}

	protected ModelAndView getListModelAndView(WebRequestContext request, FILTER filter) {
		return new ModelAndView("crud/" + CrudContext.getCurrentInstance().getBeanName() + "List");
	}

	protected ModelAndView getFormModelAndView(WebRequestContext request, FORMBEAN form) {
		if (Boolean.TRUE.equals(request.getAttribute(VIEW))) {
			return new ModelAndView("crud/" + CrudContext.getCurrentInstance().getBeanName() + "View");
		} else {
			return new ModelAndView("crud/" + CrudContext.getCurrentInstance().getBeanName() + "Form");
		}
	}

	protected ModelAndView getCreateModelAndView(WebRequestContext request, FORMBEAN form) throws CrudException {
		//return continueOnAction(FORM, form);
		//TODO FAZER O CONTINUETOACTION
		((DefaultWebRequestContext) request).setLastAction(FORM);
		return doForm(request, form);
	}

	protected ModelAndView getUpdateModelAndView(WebRequestContext request, FORMBEAN form) throws CrudException {
		//TODO FAZER O CONTINUETOACTION
		((DefaultWebRequestContext) request).setLastAction(FORM);
		return doForm(request, form);
	}

	protected ModelAndView getSaveModelAndView(WebRequestContext request, BEAN bean) {
		if ("true".equals(request.getParameter("fromInsertOne"))) {
			Object id = Util.beans.getId(bean);
			String description = Util.strings.toStringDescription(bean);
			PrintWriter out;
			try {
				out = request.getServletResponse().getWriter();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			request.getServletResponse().setCharacterEncoding(StandardCharsets.UTF_8.name());
			out
					.println("<html>" +
							"<script language=\"JavaScript\" src=\"" + request.getServletRequest().getContextPath() + "/resource/js/util.js\"></script>" +
							"<script language=\"JavaScript\">selecionar('" + id + "', '" + description + "', true);</script>" +
							"</html>");
			return null;
		} else {
			return sendRedirectToAction(LIST);
		}
	}

	protected ModelAndView getDeleteModelAndView(WebRequestContext request, BEAN bean) {
		return sendRedirectToAction(LIST);
	}

}
