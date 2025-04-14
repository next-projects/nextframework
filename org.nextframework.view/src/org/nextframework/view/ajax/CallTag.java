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
package org.nextframework.view.ajax;

import org.nextframework.controller.MultiActionController;
import org.nextframework.util.Util;
import org.nextframework.view.BaseTag;
import org.nextframework.web.WebUtils;

public class CallTag extends BaseTag {

	protected String url;
	protected String action;
	protected String parameters;
	protected String callback;
	protected String functionName;

	public String getAction() {
		return action;
	}

	public String getCallback() {
		return callback;
	}

	public String getParameters() {
		if (parameters == null) {
			parameters = "";
		}
		return parameters;
	}

	public String getUrl() {
		return url;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setCallback(String callback) {
		this.callback = callback;
	}

	public void setParameters(String parameters) {
		this.parameters = parameters;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	protected void doComponent() throws Exception {

		url = url == null ? WebUtils.getFirstFullUrl() : (url.startsWith("/") ? WebUtils.getFullUrl(getRequest(), url) : url);

		if (getParameters().startsWith("javascript:")) {
			parameters = getParameters().substring("javascript:".length());
		} else {
			parameters = "'" + Util.strings.escape(getParameters()) + "'";
		}

		if (!functionName.contains("(")) {
			functionName = functionName + "()";
		}

		includeJspTemplate();

	}

	public String getActionParameter() {
		return MultiActionController.ACTION_PARAMETER;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

}
