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
package org.nextframework.view.code;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;

import org.nextframework.exception.NextException;
import org.nextframework.view.BaseTag;
import org.nextframework.view.LogicalTag;

public class ClassTag extends BaseTag implements CodeTag, LogicalTag {

	public static final String RUN_METHOD_ATTRIBUTE = "RUN_METHOD_ATTRIBUTE";
	public static final String CALL_BODY = "CALL_BODY";

	public Map<String, JspFragment> metodos = new HashMap<String, JspFragment>();

	@Override
	protected void doComponent() throws Exception {
		PrintWriter out = new PrintWriter(new ByteArrayOutputStream());
		getJspBody().invoke(out);
		out.close();
		String runmethod = (String) getRequest().getAttribute(RUN_METHOD_ATTRIBUTE);
		if (runmethod == null) {
			runmethod = MainTag.NAME;
		}
		executeMethod(runmethod, getDynamicAttributesMap(), null);
	}

	public void executeMethod(String name, Map<String, Object> parameters, String callBody) throws JspException, IOException {
		JspFragment jspFragment = metodos.get(name);
		if (jspFragment == null) {
			throw new NextException("Método não encontrado: " + name);
		} else {
			Set<String> keySet = parameters.keySet();
			for (String parameter : keySet) {
				pushAttribute(parameter, parameters.get(parameter));
			}
			pushAttribute(CALL_BODY, callBody);
			jspFragment.invoke(null);
			popAttribute(CALL_BODY);
			for (String parameter : keySet) {
				popAttribute(parameter);
			}
		}
	}

	public void registerMethod(String method, JspFragment jspFragment) {
		if (metodos.put(method, jspFragment) != null) {
			throw new NextException("Método JSP duplicado: " + method);
		}
	}

}
