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

import java.util.Arrays;
import java.util.Collection;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.PropertyDescriptor;
import org.nextframework.exception.NextException;

public class ForEachBeanTag extends PropertyTag {

	protected String property;
	protected String var = "bean";
	protected String varIndex = "index";

	public String getVarIndex() {
		return varIndex;
	}

	public void setVarIndex(String varIndex) {
		this.varIndex = varIndex;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	String fullName;
	String fullNestedName;

	@Override
	public String getFullName() {
		return fullName;
	}

	@Override
	public String getFullNestedName() {
		return fullNestedName;
	}

	@Override
	protected void doComponent() throws Exception {

		setName(getProperty());//simula colocar um <n:property> para configurar os fullnestedname, etc

		this.fullName = montarFullPropertyName();
		this.fullNestedName = montarFullNestedName(this, name);

		final BeanDescriptor beanDescriptor = findParent(BeanTag.class, true).getBeanDescriptor();
		//removed in 2012-08-08
//		beanDescriptor.setIndexValueResolver(new PageContextIndexResolver(getPageContext()));
		PropertyDescriptor propertyDescriptor = null;
		if (!"".equals(fullNestedName)) {
			propertyDescriptor = beanDescriptor.getPropertyDescriptor(fullNestedName);
		}

		if (propertyDescriptor == null) {
			throw new NextException("Erro na tag forEachBean. Não foi possível achar o property descriptor para a propriedade " + fullNestedName);
		}

		Object value = propertyDescriptor.getValue();
		if (value != null && value.getClass().isArray()) {
			value = Arrays.asList((Object[]) value);
		}

		if (!(value instanceof Collection<?>) && value != null) {
			throw new NextException("Erro na tag forEachBean. O property leva a um atributo que não é uma coleção. Valor encontrado" + value);
		}

		if (value != null) {
			Collection<?> collection = (Collection<?>) value;
			String fullName = this.fullName;  //nome do input
			String fullNestedName = this.fullNestedName; //nome da propriedade começando do bean
			int i = 0;
			for (Object object : collection) {
				pushAttribute(var, object);
				pushAttribute(varIndex, i);

				this.fullName = fullName + "[" + i + "]";
				this.fullNestedName = fullNestedName + "[" + i + "]";
				doBody();

				i++;
				popAttribute(varIndex);
				popAttribute(var);
			}
		}

	}

}
