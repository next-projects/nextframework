package org.nextframework.bean.internal;

import java.beans.PropertyDescriptor;

import org.springframework.beans.AbstractPropertyAccessor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.Property;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

class BeanWrapperForDirectClassAccess extends AbstractPropertyAccessor implements BeanWrapper {
	
	Class<?> targetClass;
	
	BeanWrapperForDirectClassAccess(Class<?> targetClass) {
		this.targetClass = targetClass;
	}

	@Override
	public PropertyDescriptor getPropertyDescriptor(String propertyName) throws InvalidPropertyException {
		PropertyDescriptor pd = getProperty(propertyName);
		return pd;
	}

	private PropertyDescriptor getProperty(String propertyName) {
		PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(getWrappedClass(), propertyName);
		if(pd == null){
			//try super interfaces if it is an interface 
			if(getWrappedClass().isInterface()){
				Class<?>[] interfaces = getWrappedClass().getInterfaces();
				for (Class<?> class1 : interfaces) {
					pd = BeanUtils.getPropertyDescriptor(class1, propertyName);
					if(pd != null){
						break;
					}
				}
			}
			if(pd == null){
				//FIXME TODO use root object and nested path
				throw new InvalidPropertyException(getWrappedClass(), propertyName, "No property '" + propertyName + "' found");
			}
		}
		return pd;
	}

	@Override
	public PropertyDescriptor[] getPropertyDescriptors() {
		return BeanUtils.getPropertyDescriptors(getWrappedClass());
	}

	@Override
	public TypeDescriptor getPropertyTypeDescriptor(String propertyName) throws BeansException {
		int pos = PropertyAccessorUtils.getFirstNestedPropertySeparatorIndex(propertyName);
		Assert.isTrue(pos <= -1, "The "+BeanWrapperForDirectClassAccess.class+" does not handle nested properties");
		
		PropertyTokenHolder tokens = getPropertyNameTokens(propertyName);
		PropertyDescriptor pd = getProperty(tokens.actualName);
		if (tokens.keysLength > 0) {
			return TypeDescriptor.nested(property(pd), tokens.keysLength);
		} else {
			return new TypeDescriptor(property(pd));
		}
	}


	private Property property(PropertyDescriptor pd) {
		return new Property(getWrappedClass(), pd.getReadMethod(), pd.getWriteMethod());
	}
	

	@Override
	public Object getPropertyValue(String propertyName) throws BeansException {
		return null;
	}
	
	@Override
	public Class<?> getWrappedClass() {
		return targetClass;
	}

	@Override
	public Object getWrappedInstance() {
		return null;
	}

	@Override
	public boolean isReadableProperty(String propertyName) {
		throw new RuntimeException("Operation not supported");
	}

	@Override
	public boolean isWritableProperty(String propertyName) {
		throw new RuntimeException("Operation not supported");
	}

	@Override
	public void setPropertyValue(String propertyName, Object value) throws BeansException {
		throw new RuntimeException("Operation not supported");
	}

	@Override
	public int getAutoGrowCollectionLimit() {
		throw new RuntimeException("Operation not supported");
	}

	@Override
	public boolean isAutoGrowNestedPaths() {
		throw new RuntimeException("Operation not supported");
	}

	@Override
	public void setAutoGrowCollectionLimit(int autoGrowCollectionLimit) {
		throw new RuntimeException("Operation not supported");
	}

	@Override
	public void setAutoGrowNestedPaths(boolean autoGrowNestedPaths) {
		throw new RuntimeException("Operation not supported");
	}

	@Override
	public <T> T convertIfNecessary(Object value, Class<T> requiredType, MethodParameter methodParam) throws TypeMismatchException {
		throw new RuntimeException("Operation not supported");
	}

	

	//---------------------------------------------------------------------
	// Inner class for internal use (copied from BeanWrapperImpl)
	//---------------------------------------------------------------------

	private static class PropertyTokenHolder {

		public String actualName;

		public int keysLength = 0;
	}
	
	private PropertyTokenHolder getPropertyNameTokens(String propertyName) {
		PropertyTokenHolder tokens = new PropertyTokenHolder();
		tokens.keysLength = StringUtils.countOccurrencesOf(propertyName, PROPERTY_KEY_PREFIX);
		if(tokens.keysLength > 0){
			int keyStart = propertyName.indexOf(PROPERTY_KEY_PREFIX);
			tokens.actualName = propertyName.substring(0, keyStart);
		} else {
			tokens.actualName = propertyName;
		}
		return tokens;
	}
}
