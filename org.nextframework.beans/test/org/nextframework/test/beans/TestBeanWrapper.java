package org.nextframework.test.beans;

import java.beans.PropertyDescriptor;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

public class TestBeanWrapper {

	@Test
	public void testBeanProperties() {
		BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(new BaseTestBeanA());
		PropertyDescriptor[] propertyDescriptors = bw.getPropertyDescriptors();
		String[] actuals = new String[propertyDescriptors.length];
		int i = 0;
		for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
			actuals[i++] = propertyDescriptor.getName();
		}
		String[] expected = new String[] {
				"b", "class", "defaultB", "id", "parentProperty", "parentReadOnlyProperty"
		};
		Assert.assertArrayEquals(expected, actuals);
	}

}
