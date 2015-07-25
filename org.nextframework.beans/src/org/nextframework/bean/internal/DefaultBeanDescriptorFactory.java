package org.nextframework.bean.internal;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.IBeanDescriptorFactory;

public class DefaultBeanDescriptorFactory implements IBeanDescriptorFactory {

	@Override
	public BeanDescriptor forBean(Object bean) {
		return new org.nextframework.bean.internal.BeanDescriptorImpl(bean);
	}

	@Override
	public BeanDescriptor forClass(Class<?> clazz) {
		return new org.nextframework.bean.internal.BeanDescriptorImpl(clazz);
	}
	
}
