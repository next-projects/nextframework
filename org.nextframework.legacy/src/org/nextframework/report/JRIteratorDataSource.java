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
package org.nextframework.report;

import java.util.Iterator;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class JRIteratorDataSource implements JRDataSource {

	private Iterator<?> iterator;
	private Object currentBean;

	protected PropertyNameProvider propertyNameProvider = null;
	protected boolean isUseFieldDescription = true;

	public JRIteratorDataSource(Iterator<?> iterator) {

		this.iterator = iterator;

		if (isUseFieldDescription) {
			propertyNameProvider = new PropertyNameProvider() {

				public String getPropertyName(JRField field) {
					if (field.getDescription() == null) {
						return field.getName();
					} else {
						return field.getDescription();
					}
				}

			};
		} else {
			propertyNameProvider = new PropertyNameProvider() {

				public String getPropertyName(JRField field) {
					return field.getName();
				}

			};
		}
	}

	public boolean next() throws JRException {

		boolean hasNext = false;

		if (this.iterator != null) {
			hasNext = this.iterator.hasNext();

			if (hasNext) {
				this.currentBean = this.iterator.next();
			}
		}

		return hasNext;
	}

	public Object getFieldValue(JRField jrField) throws JRException {

		Object value = null;

		if (currentBean != null) {
			@SuppressWarnings("unused")
			String propertyName = propertyNameProvider.getPropertyName(jrField);
			/*
			try
			{
				//TODO REDO
				//value = PropertyUtils.getProperty(currentBean, propertyName);
			}
			catch (java.lang.IllegalAccessException e)
			{
				throw new JRException("Error retrieving field value from bean : " + propertyName, e);
			}
			catch (java.lang.reflect.InvocationTargetException e)
			{
				throw new JRException("Error retrieving field value from bean : " + propertyName, e);
			}
			catch (java.lang.NoSuchMethodException e)
			{
				throw new JRException("Error retrieving field value from bean : " + propertyName, e);
			}*/
		}

		return value;
	}

	/**
	 *
	 */
	interface PropertyNameProvider {

		public String getPropertyName(JRField field);

	}

}
