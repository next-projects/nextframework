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
package org.nextframework.view.template;

/**
 * @author rogelgarcia
 * @since 03/02/2006
 * @version 1.1
 */
public class ViewTag extends TemplateTag {
	
	protected boolean includeForm = true;
	protected boolean validateForm = true;
	protected String formAction;
	protected String formMethod = "POST";
	protected String formName;
	protected String propertyMode;


	protected String useBean;
	protected Class<?> beanType;
	
	protected String title;
	
	
	public String getPropertyMode() {
		return propertyMode;
	}

	public void setPropertyMode(String propertyMode) {
		this.propertyMode = propertyMode;
	}
	public String getUseBean() {
		return useBean;
	}

	public Class<?> getBeanType() {
		return beanType;
	}

	public void setUseBean(String beanName) {
		this.useBean = beanName;
	}

	public void setBeanType(Class<?> beanType) {
		this.beanType = beanType;
	}

	@Override
	protected void doComponent() throws Exception {
		if(formName == null && useBean != null){
			formName = useBean;
		}
		pushAttribute("Ttela", this);
		pushAttribute("viewTag", this);
		includeJspTemplate();
		popAttribute("Ttela");
		popAttribute("viewTag");
	}
	
	public String getFormName() {
		return formName;
	}

	public void setFormName(String formName) {
		this.formName = formName;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String name) {
		this.title = name;
	}
	@Deprecated
	public String getTitulo() {
		return title;
	}
	@Deprecated
	public void setTitulo(String titulo) {
		this.title = titulo;
	}

	public boolean isIncludeForm() {
		return includeForm;
	}

	public void setIncludeForm(boolean includeForm) {
		this.includeForm = includeForm;
	}

	public String getFormAction() {
		return formAction;
	}

	public void setFormAction(String formAction) {
		this.formAction = formAction;
	}

	public String getFormMethod() {
		return formMethod;
	}

	public void setFormMethod(String formMethod) {
		this.formMethod = formMethod;
	}

	public boolean isValidateForm() {
		return validateForm;
	}

	public void setValidateForm(boolean validateForm) {
		this.validateForm = validateForm;
	}

}
