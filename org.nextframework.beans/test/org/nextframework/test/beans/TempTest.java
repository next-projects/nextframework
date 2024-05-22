package org.nextframework.test.beans;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

public class TempTest {

	public static void main(String[] args) {
		TestABean a = new TestABean();
		BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(a);
		wrapper.setAutoGrowNestedPaths(true);
		System.out.println(wrapper.getPropertyValue("b.c"));
		System.out.println(a.getB());
	}

}
