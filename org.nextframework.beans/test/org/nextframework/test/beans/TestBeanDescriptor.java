package org.nextframework.test.beans;

import java.lang.reflect.ParameterizedType;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.nextframework.bean.BeanDescriptor;
import org.nextframework.bean.BeanDescriptorFactory;
import org.springframework.beans.InvalidPropertyException;

public class TestBeanDescriptor {
	
	@Test
	public void testNotReadablePropertyForClass(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(SuperTestBeanA.class);
		try {
			bd.getPropertyDescriptor("parentField").getType();
			Assert.fail();
		} catch(InvalidPropertyException e){
			
		}
	}
	
	@Test
	public void testListItemForClass(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(BaseTestBeanA.class);
		Assert.assertEquals(BaseTestBeanC.class, bd.getPropertyDescriptor("b.listC[0]").getType());
	}
	
	@Test
	public void testMapPropertyFromClass(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(InterfaceAImpl.class);
		Assert.assertEquals(Integer.class, bd.getPropertyDescriptor("map[key]").getType());
	}
	
	@Test
	public void testMapPropertyFromObject(){
		BeanDescriptor bd = BeanDescriptorFactory.forBean(new InterfaceAImpl());
		Assert.assertEquals(Integer.class, bd.getPropertyDescriptor("map[key]").getType());
	}
	
	@Test
	public void testMapKeyFromClass(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(InterfaceAImpl.class);
		Assert.assertEquals(String.class, ((ParameterizedType)bd.getPropertyDescriptor("map").getType()).getActualTypeArguments()[0]);
	}
	@Test
	public void testMapValueFromClass(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(InterfaceAImpl.class);
		Assert.assertEquals(Integer.class, ((ParameterizedType)bd.getPropertyDescriptor("map").getType()).getActualTypeArguments()[1]);
	}
	
	@Test
	public void testPropertyForInterface(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(InterfaceA.class);
		Assert.assertEquals(String.class, bd.getPropertyDescriptor("id").getType());
	}
	
	@Test
	public void testPropertyForInterfaceInheritance(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(InterfaceB.class);
		Assert.assertEquals(String.class, bd.getPropertyDescriptor("id").getType());
	}
	
	@Test
	public void testPropertyForInterfaceImpl(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(InterfaceAImpl.class);
		Assert.assertEquals(String.class, bd.getPropertyDescriptor("id").getType());
	}
	
	@Test
	public void testPropertyForInterfaceImplInheritance(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(InterfaceAImplSub.class);
		Assert.assertEquals(String.class, bd.getPropertyDescriptor("id").getType());
	}
	
	@Test
	public void testPropertyForInterfaceImplInheritanceValue(){
		BeanDescriptor bd = BeanDescriptorFactory.forBean(new InterfaceAImplSub());
		Assert.assertEquals(String.class, bd.getPropertyDescriptor("id").getType());
		Assert.assertEquals(new InterfaceAImplSub().getId(), bd.getPropertyDescriptor("id").getValue());
	}
	
	@Test
	public void testNotReadablePropertyForBean(){
		BeanDescriptor bd = BeanDescriptorFactory.forBean(new SuperTestBeanA());
		try {
			bd.getPropertyDescriptor("parentField").getType();
			Assert.fail();
		} catch(InvalidPropertyException e){
			
		}
	}
	
	@Test
	public void testNotReadablePropertyForBean2(){
		BeanDescriptor bd = BeanDescriptorFactory.forBean(new SuperTestBeanA());
		try {
			bd.getPropertyDescriptor("detail[5].property").getType();
			Assert.fail();
		} catch(InvalidPropertyException e){
			
		}
	}
	
	
	@Test
	public void testInvalidPropertyForClass(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(BaseTestNoDefConstructor.class);
		try {
			bd.getPropertyDescriptor("invalid").getType();
			Assert.fail();
		} catch(InvalidPropertyException e){
			
		}
	}
	
	@Test
	public void testInvalidNestedPropertyForClass(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(BaseTestNoDefConstructor.class);
		try {
			bd.getPropertyDescriptor("invalid.nested").getType();
			Assert.fail();
		} catch(InvalidPropertyException e){
			
		}
	}
	
	@Test
	public void testAccessNoDefConstructorForClass(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(BaseTestNoDefConstructor.class);
		Assert.assertEquals(String.class, bd.getPropertyDescriptor("value").getType());
	}
	
	@Test
	public void testAccessNoDefConstructorForClassValue(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(BaseTestNoDefConstructor.class);
		Assert.assertNull(bd.getPropertyDescriptor("value").getValue());
	}
	
	@Test
	public void testAccessBForClass(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(BaseTestBeanA.class);
		Assert.assertNull(bd.getPropertyDescriptor("b").getValue());
	}
	
	@Test
	public void testTypeBForClass(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(BaseTestBeanA.class);
		Assert.assertEquals(bd.getPropertyDescriptor("b").getType(), BaseTestBeanB.class);
	}
	
	@Test
	public void testTypeCForClass(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(BaseTestBeanA.class);
		Assert.assertEquals(bd.getPropertyDescriptor("b.c").getType(), BaseTestBeanC.class);
	}
	
	@Test
	public void testTypeCListForClass(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(BaseTestBeanA.class);
		Assert.assertEquals(List.class, bd.getPropertyDescriptor("b.listC").getRawType());
	}
	
	@Test
	public void testTypeCListItemForClass(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(BaseTestBeanA.class);
		Assert.assertEquals(String.class, bd.getPropertyDescriptor("b.listC[0].stringValue").getRawType());
	}
	
	@Test
	public void testTypeCListEmptyItemForClass(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(BaseTestBeanA.class);
		Assert.assertEquals(String.class, bd.getPropertyDescriptor("b.listC[].stringValue").getRawType());
	}
	
	@Test
	public void testTypeCListEmptyItem(){
		BeanDescriptor bd = BeanDescriptorFactory.forBean(new BaseTestBeanA());
		Assert.assertEquals(String.class, bd.getPropertyDescriptor("b.instantiatedListC[].stringValue").getRawType());
	}
	
	@Test
	public void testTypeCListEmptyItem2(){
		BeanDescriptor bd = BeanDescriptorFactory.forBean(new BaseTestBeanA());
		Assert.assertEquals(String.class, bd.getPropertyDescriptor("defaultB.instantiatedListC[].stringValue").getRawType());
	}
	
	@Test
	public void testTypeCGenericListForClass(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(BaseTestBeanA.class);
		Assert.assertEquals(BaseTestBeanC.class, ((ParameterizedType)bd.getPropertyDescriptor("b.listC").getType()).getActualTypeArguments()[0]);
	}
	
	@Test
	public void testAccessBForClass2(){
		BeanDescriptor bd = BeanDescriptorFactory.forClass(BaseTestBeanA.class);
		Assert.assertNull(bd.getPropertyDescriptor("defaultB").getValue());
	}
	
	@Test
	public void testAccessB(){
		BeanDescriptor bd = BeanDescriptorFactory.forBean(new BaseTestBeanA());
		Assert.assertNull(bd.getPropertyDescriptor("b").getValue());
	}
	
	@Test
	public void testAccessB2(){
		BaseTestBeanB b = new BaseTestBeanB();
		BeanDescriptor bd = BeanDescriptorFactory.forBean(new BaseTestBeanA(b));
		Assert.assertSame(b, bd.getPropertyDescriptor("b").getValue());
	}
	
	@Test
	public void testAccessC(){
		BeanDescriptor bd = BeanDescriptorFactory.forBean(new BaseTestBeanA());
		Assert.assertNull(bd.getPropertyDescriptor("b.c").getValue());
	}
	
	@Test
	public void testAccessC2(){
		BaseTestBeanC c = new BaseTestBeanC();
		BaseTestBeanB b = new BaseTestBeanB(c);
		BeanDescriptor bd = BeanDescriptorFactory.forBean(new BaseTestBeanA(b));
		Assert.assertSame(c, bd.getPropertyDescriptor("b.c").getValue());
	}
	
	@Test
	public void testAccessId(){
		BeanDescriptor bd = BeanDescriptorFactory.forBean(new BaseTestBeanA());
		Assert.assertEquals("id", bd.getIdPropertyName());
	}
	
	@Test
	public void testAccessIdGenerated(){
		BeanDescriptor bd = BeanDescriptorFactory.forBean(new BaseTestBeanA_$$_());
		Assert.assertEquals("id", bd.getIdPropertyName());
	}

}
