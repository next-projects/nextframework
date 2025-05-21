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
package org.nextframework.view;

import org.nextframework.controller.MultiActionController;
import org.nextframework.core.web.NextWeb;
import org.nextframework.util.Util;
import org.nextframework.web.WebUtils;

/**
 * @author rogelgarcia
 * @since 25/01/2006
 * @version 1.1
 */
public class FormTag extends BaseTag {

	//atributos
	protected String action;
	protected String url;
	protected String name = "form";
	protected Boolean validate = true;
	protected String validateFunction;

	protected String forBean = null;

	protected String propertyMode = "input";

	protected String submitFunction;

	protected String method = "POST";

	protected String enctype = "multipart/form-data";

	@Override
	protected void doComponent() throws Exception {

		if (Util.strings.isEmpty(action)) {
			action = NextWeb.getRequestContext().getLastAction();
		}

		url = url != null ? WebUtils.getFullUrl(getRequest(), url) : getFirstFullUrl();

		if (validateFunction == null) {
			validateFunction = "validate" + Util.strings.captalize(getName());
		}

		submitFunction = "submit" + Util.strings.captalize(getName());

		includeJspTemplate();

	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (Util.strings.isNotEmpty(name)) {
			this.name = name;
		}
	}

	public Boolean getValidate() {
		return validate;
	}

	public void setValidate(Boolean validate) {
		this.validate = validate;
	}

	public String getValidateFunction() {
		return validateFunction;
	}

	public void setValidateFunction(String validateFunction) {
		this.validateFunction = validateFunction;
	}

	public String getForBean() {
		return forBean;
	}

	public void setForBean(String forBean) {
		this.forBean = forBean;
	}

	public String getPropertyMode() {
		return propertyMode;
	}

	public void setPropertyMode(String propertyMode) {
		this.propertyMode = propertyMode;
	}

	public String getSubmitFunction() {
		return submitFunction;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getEnctype() {
		return enctype;
	}

	public void setEnctype(String enctype) {
		this.enctype = enctype;
	}

	private String getFirstFullUrl() {
		return WebUtils.getFirstFullUrl();
	}

	public String getActionParameter() {
		return MultiActionController.ACTION_PARAMETER;
	}

}
