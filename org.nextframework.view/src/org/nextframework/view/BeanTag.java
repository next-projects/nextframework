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

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.core.web.NextWeb;
import org.nextframework.exception.BeanDescriptorCreationException;
import org.nextframework.exception.NextException;
import org.nextframework.util.Util;
import org.nextframework.view.DataGridTag.Status;

/**
 * @author rogelgarcia
 * @since 26/01/2006
 * @version 1.1
 */
public class BeanTag extends BaseTag implements LogicalTag {

	protected String name;
	protected String propertyPrefix;
	protected String propertyIndex;
	protected Class<?> valueType;
	protected String varLabel = "label";

	//extras
	protected Object bean;
	protected BeanDescriptor beanDescriptor;

	@Override
	@SuppressWarnings("unchecked")
	protected void doComponent() throws Exception {

		if (Util.strings.isEmpty(name)) {
			throw new IllegalArgumentException("Attribute name of tag BeanTag is empty. ");
		}

		bean = ViewUtils.evaluate("${" + name + "}", getPageContext(), (valueType == null ? Object.class : valueType));
		if (bean == null && getParent() instanceof DataGridTag) {
			DataGridTag dataGridTag = (DataGridTag) getParent();
			if (dataGridTag.currentStatus == Status.DYNALINE) {
				//if dynaline and there is no bean on scope.. instantiate one
				bean = valueType.newInstance();
			}
		}

		try {
			if (bean == null && valueType == null) {
				throw new NextException("bean and valueType cannot be both null for BeanTag. Object with name=" + name + ", not found in scope. ");
			}
			beanDescriptor = BeanDescriptorFactory.forBeanOrClass(bean, (Class<Object>) valueType);
		} catch (BeanDescriptorCreationException e) {
			String msg = "Error in tag bean (name='" + name + "'). ";
			if (valueType == null) {
				msg += "You may need to set attribute valueType. ";
			}
			throw new NextException(msg + e.getMessage(), e);
		}

		pushAttribute(varLabel, Util.beans.getDisplayName(beanDescriptor, NextWeb.getRequestContext().getLocale()));
		doBody();
		popAttribute(varLabel);

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPropertyPrefix() {
		return propertyPrefix;
	}

	public void setPropertyPrefix(String propertyPrefix) {
		this.propertyPrefix = propertyPrefix;
	}

	public String getPropertyIndex() {
		return propertyIndex;
	}

	public void setPropertyIndex(String propertyIndex) {
		this.propertyIndex = propertyIndex;
	}

	public Class<?> getValueType() {
		return valueType;
	}

	public void setValueType(Class<?> valueType) {
		this.valueType = valueType;
	}

	public String getVarLabel() {
		return varLabel;
	}

	public void setVarLabel(String varLabel) {
		this.varLabel = varLabel;
	}

	public BeanDescriptor getBeanDescriptor() {
		return beanDescriptor;
	}

}
