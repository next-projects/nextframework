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

import java.util.ArrayList;
import java.util.List;

import org.nextframework.service.ServiceFactory;
import org.nextframework.util.Util;
import org.nextframework.validation.JavascriptValidationItem;
import org.nextframework.validation.ValidatorRegistry;
import org.nextframework.validation.validators.JavascriptValidationFunctionBuilder;

/**
 * @author rogelgarcia
 * @since 31/01/2006
 * @version 1.1
 */
public class ValidationTag extends BaseTag {

	protected String functionName;

	protected JavascriptValidationFunctionBuilder functionBuilder;

	List<JavascriptValidationItem> validationItens = new ArrayList<JavascriptValidationItem>();

	public boolean register(JavascriptValidationItem o) {
		return validationItens.add(o);
	}

	@Override
	protected void doComponent() throws Exception {
		doBody();
		FormTag form = findParent(FormTag.class, true);
		String formName = form.getName();
		ValidatorRegistry validatorRegistry = ServiceFactory.getService(ValidatorRegistry.class);
		if (functionName == null) {
			functionName = "validate" + Util.strings.captalize(formName);
		}
		functionBuilder = new JavascriptValidationFunctionBuilder(validationItens, formName, functionName, validatorRegistry, getServletContext());
		String validationString = functionBuilder.buildValidation();
		getOut().println("<script language=\"javascript\">");
		getOut().println(validationString);
		getOut().println("</script>");
	}

	public String getFunctionName() {
		return functionName;
	}

	public List<JavascriptValidationItem> getValidationItens() {
		return validationItens;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}

}
