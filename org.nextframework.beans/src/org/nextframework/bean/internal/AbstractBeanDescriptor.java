package org.nextframework.bean.internal;

import java.lang.reflect.InvocationTargetException;

import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.bean.PropertyDescriptor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.NotReadablePropertyException;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;

public abstract class AbstractBeanDescriptor implements BeanDescriptor {
	
	BeanWrapper wrapper;
	//wrapper for accessing class property descriptors, used when the bean type is generated with BGLIB or Javaassist
	BeanWrapper typeWrapper; 
	
	Object bean;
	Class<?> targetClass;
	
	String nestedPath;
	Object parentObject;
	
	public AbstractBeanDescriptor(Object bean) {
		Assert.notNull(bean, "Cannot instantiate "+this.getClass().getName()+" with null bean");
		this.bean = bean;
		this.targetClass = bean.getClass();
		this.wrapper = new BeanWrapperImpl(bean);
		this.typeWrapper = new BeanWrapperForDirectClassAccess(getUserClass(this.targetClass)); 
	}
	
	public AbstractBeanDescriptor(Class<?> targetClass) {
		Assert.notNull(targetClass, "Cannot instantiate "+this.getClass().getName()+" with null class");
		this.targetClass = targetClass;
		this.wrapper = new BeanWrapperForDirectClassAccess(targetClass);
		this.typeWrapper = this.wrapper;
	}

	public void setNestedPath(String nestedPath) {
		this.nestedPath = nestedPath;
	}
	
	public void setParentObject(Object parentObject) {
		this.parentObject = parentObject;
	}
	
	public String getNestedPath() {
		return nestedPath;
	}
	
	public Object getParentObject() {
		return parentObject;
	}

	@Override
	public String getDisplayName() {
		return DisplayNameUtils.getDisplayName(getTargetClass());
	}

	@Override
	public Object getTargetBean() {
		return bean;
	}

	@Override
	public Class<?> getTargetClass() {
		return targetClass;
	}

	@Override
	public PropertyDescriptor getPropertyDescriptor(String propertyPath) {
		int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(propertyPath);
		// Handle nested properties recursively.
		if (pos > -1) {
			String nestedProperty = propertyPath.substring(0, pos);
			String nestedPath = propertyPath.substring(pos + 1);
			BeanDescriptor nestedBeanDescriptor = createNestedBeanDescriptor(nestedProperty);
			if(nestedBeanDescriptor == null){
				throw new InvalidPropertyException(getTargetClass(), propertyPath, "Property not found by "+this.getClass().getName());
			}
			return nestedBeanDescriptor.getPropertyDescriptor(nestedPath);
		}
		else {
			if(propertyPath.indexOf('[') > 0){
				//it it is an indexed property there's no java.beans.PropertyDescriptor
				TypeDescriptor propertyTypeDescriptor = typeWrapper.getPropertyTypeDescriptor(propertyPath);
				if(propertyTypeDescriptor != null){
					return new PropertyDescriptorIndexedImpl(propertyPath, propertyTypeDescriptor.getType(), bean != null ? wrapper.getPropertyValue(propertyPath): null);
				}
			} 
			java.beans.PropertyDescriptor propertyDescriptor = typeWrapper.getPropertyDescriptor(propertyPath);
			return new PropertyDescriptorImpl(propertyDescriptor, bean);
		}
	}

	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		java.beans.PropertyDescriptor[] propertyDescriptors = typeWrapper.getPropertyDescriptors();
		PropertyDescriptor[] result = new PropertyDescriptor[propertyDescriptors.length];
		for (int i = 0; i < propertyDescriptors.length; i++) {
			result[i] = new PropertyDescriptorImpl(propertyDescriptors[i], bean);
		}
		return result;
	}

	private BeanDescriptor createNestedBeanDescriptor(String nestedProperty) {
		AbstractBeanDescriptor nestedBeanDescriptor;
		TypeDescriptor propertyTypeDescriptor = wrapper.getPropertyTypeDescriptor(nestedProperty);
		if(bean == null){
			nestedBeanDescriptor = (AbstractBeanDescriptor) BeanDescriptorFactory.forClass(propertyTypeDescriptor.getType());
		} else {
			Object propertyValue;
			try {
				propertyValue = wrapper.getPropertyValue(nestedProperty);
				if(propertyValue != null){
					nestedBeanDescriptor = (AbstractBeanDescriptor) BeanDescriptorFactory.forBean(propertyValue);
				} else {
					nestedBeanDescriptor = (AbstractBeanDescriptor) BeanDescriptorFactory.forClass(wrapper.getPropertyType(nestedProperty));
				}
			} catch (NotReadablePropertyException e){
				throw e;
			} catch (InvalidPropertyException e){
				if(e.getCause() instanceof InvocationTargetException){
					Throwable originalException = e.getCause().getCause();
					throw new RuntimeException(originalException);
				} else {
					nestedBeanDescriptor = (AbstractBeanDescriptor) BeanDescriptorFactory.forClass(propertyTypeDescriptor.getType());	
				}
			} catch (RuntimeException e) {
				//try only by type (can be a empty array like [])
				nestedBeanDescriptor = (AbstractBeanDescriptor) BeanDescriptorFactory.forClass(propertyTypeDescriptor.getType());
			}
		}
		nestedBeanDescriptor.setNestedPath(nestedProperty);
		nestedBeanDescriptor.setParentObject(bean);
		return nestedBeanDescriptor;
	}

	private static final String CGLIB_CLASS_SEPARATOR = "$$";
	private static final String BEAN_EXTENDER_SEPARATOR = "ExtendedByBeanExtender";

	//code from SpringFramework - Apache Licence http://www.apache.org/licenses/LICENSE-2.0
	private static Class<?> getUserClass(Class<?> clazz) {
		if (clazz != null && clazz.getName().contains(CGLIB_CLASS_SEPARATOR )
				&& !clazz.getName().contains(BEAN_EXTENDER_SEPARATOR)) {
			Class<?> superClass = clazz.getSuperclass();
			if (superClass != null && !Object.class.equals(superClass)) {
				return superClass;
			}
		}
		return clazz;
	}
}
