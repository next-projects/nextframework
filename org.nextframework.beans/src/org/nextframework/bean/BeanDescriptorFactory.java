/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2012 the original author or authors.
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
package org.nextframework.bean;

import org.nextframework.service.ServiceFactory;

/**
 * Starting point for creating BeanDescriptor objects.<BR>
 * It uses ServiceFactory API to get a IBeanDescriptorFactory object.<BR>
 * 
 * @see ServiceFactory
 * @see BeanDescriptor
 * @author rogelgarcia
 *
 */
public class BeanDescriptorFactory {
	
	public static IBeanDescriptorFactory getFactory(){
		return ServiceFactory.getService(IBeanDescriptorFactory.class);
	}
	
	/**
	 * Creates a BeanDescriptor for the class.<BR>
	 * It uses a factory configured with ServiceFactory API.
	 * @param clazz
	 * @return
	 */
	public static BeanDescriptor forClass(Class<?> clazz){
		return getFactory().forClass(clazz);
	}
	
	/**
	 * Creates a BeanDescriptor for the object.<BR>
	 * It uses a factory configured with ServiceFactory API.
	 * @param bean
	 * @return
	 */
	public static BeanDescriptor forBean(Object bean){
		return getFactory().forBean(bean);
	}
	
	/**
	 * Creates a BeanDescriptor for the object or the class (if the bean is null).<BR>
	 * It uses a factory configured with ServiceFactory API.
	 * @param bean
	 * @return
	 */
	public static BeanDescriptor forBeanOrClass(Object bean, Class<?> clazz){
		if(bean != null){
			return forBean(bean);
		} else {
			return forClass(clazz);
		}
	}
	
}
