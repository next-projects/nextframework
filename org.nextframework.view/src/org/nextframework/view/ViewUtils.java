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

import org.nextframework.core.web.NextWeb;
import org.nextframework.exception.NextException;
import org.nextframework.web.WebUtils;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.ValueExpression;
import jakarta.servlet.jsp.JspFactory;
import jakarta.servlet.jsp.PageContext;

/**
 * @author rogelgarcia | marcusabreu
 * @since 25/01/2006
 * @version 1.1
 */
@SuppressWarnings("deprecation")
public class ViewUtils {

	@SuppressWarnings("unchecked")
	public static <E> E evaluate(String expression, PageContext pageContext, Class<E> clazz) {
		try {
			ExpressionFactory expressionFactory = JspFactory.getDefaultFactory()
					.getJspApplicationContext(pageContext.getServletContext())
					.getExpressionFactory();

			ELContext elContext = pageContext.getELContext();
			ValueExpression ve = expressionFactory.createValueExpression(elContext, expression, clazz);
			E result = (E) ve.getValue(elContext);
			return result;
		} catch (Exception e) {
			throw new NextException("Erro ao avaliar expressão EL: " + expression, e);
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
