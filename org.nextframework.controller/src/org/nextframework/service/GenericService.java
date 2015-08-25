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
package org.nextframework.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.controller.crud.ListViewFilter;
import org.nextframework.persistence.DAO;
import org.nextframework.persistence.ResultList;



public class GenericService<BEAN extends Object> {

	protected Log log = LogFactory.getLog(this.getClass());
	
	protected DAO<BEAN> genericDAO;

	public List<BEAN> findForCombo(String...extraFields) {
		return genericDAO.findForCombo(extraFields);
	}

	public ResultList<BEAN> loadListModel(ListViewFilter filter) {
		return genericDAO.loadListModel(filter);
	}

	public BEAN loadFormModel(BEAN bean) {
		return genericDAO.loadFormModel(bean);
	}
	
	public List<BEAN> findAll() {
		return genericDAO.findAll();
	}

	public BEAN load(BEAN bean) {
		return genericDAO.load(bean);
	}
	
	protected List<BEAN> empty() {
		return new ArrayList<BEAN>();
	}
	
	public BEAN loadById(Serializable id){
		return genericDAO.loadById(id);
	}

	public void delete(BEAN bean) {
		genericDAO.delete(bean);
	}

	public void saveOrUpdate(BEAN bean) {
		genericDAO.saveOrUpdate(bean);
	}
	
	public void bulkSaveOrUpdate(Collection<BEAN> beans){
		genericDAO.bulkSaveOrUpdate(beans);
	}

	public void setGenericDAO(DAO<BEAN> genericDAO) {
		this.genericDAO = genericDAO;
	}

	public DAO<BEAN> getGenericDAO() {
		return genericDAO;
	}

	public List<BEAN> findBy(Object o, boolean forCombo, String...extraFields) {
		return genericDAO.findBy(o, forCombo, extraFields);
	}	
	
	public List<BEAN> findBy(Object o, String...extraFields) {
		return genericDAO.findBy(o, true, extraFields);
	}	
	
}
