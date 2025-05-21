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

import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.servlet.jsp.JspFactory;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.el.ELException;

import org.nextframework.core.web.NextWeb;
import org.nextframework.exception.NextException;
import org.nextframework.web.WebUtils;

/**
 * @author rogelgarcia | marcusabreu
 * @since 25/01/2006
 * @version 1.1
 */
@SuppressWarnings("deprecation")
public class ViewUtils {

	@SuppressWarnings("unchecked")
	public static <E> E evaluate(String expression, PageContext pageContext, Class<E> clazz) {
		if (pageContext.getExpressionEvaluator() != null && pageContext.getVariableResolver() != null) {
			//tentar a forma servlet 2
			try {
				return (E) pageContext.getExpressionEvaluator().evaluate(expression, clazz, pageContext.getVariableResolver(), null);
			} catch (ELException e) {
				throw new NextException(e);
			}
		} else {
			//tentar servlet 3 (se esse código não compilar e estiver utilizando servlet 2, pode excluir)
			ExpressionFactory expressionFactory = JspFactory.getDefaultFactory().getJspApplicationContext(pageContext.getServletContext()).getExpressionFactory();
			ValueExpression ve = expressionFactory.createValueExpression(pageContext.getELContext(), expression, clazz);
			E evaluate = (E) ve.getValue(pageContext.getELContext());
			return evaluate;
		}
	}

	public static String getMessageCodeViewPrefix() {
		String messageCodeViewPrefix = (String) NextWeb.getRequestContext().getAttribute("messageCodeViewPrefix");
		if (messageCodeViewPrefix != null) {
			return messageCodeViewPrefix;
		}
		String view = WebUtils.getModelAndViewName();
		return WebUtils.getRequestModule() + "." + WebUtils.getRequestController() + "." + (view != null ? view : "view");
	}

}
