package org.nextframework.test.beans;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TestBeans {
}

interface InterfaceA {
	String getId();
}

interface InterfaceB extends InterfaceA {
}


class InterfaceAImpl implements InterfaceA {
	
	Map<String, Integer> map = new HashMap<String, Integer>();

	@Override
	public String getId() {
		return "value";
	}
	
	public Map<String, Integer> getMap() {
		return map;
	}
	
}

class InterfaceAImplSub extends InterfaceAImpl {
	
}

class BaseTestNoDefConstructor {
	
	String value;
	
	public BaseTestNoDefConstructor(String value){
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}

class SuperTestBeanA {
	
	String parentField;
	
	String parentProperty;
	
	String parentReadOnlyProperty;
	
	public String getParentProperty() {
		return parentProperty;
	}
	
	public void setParentProperty(String parentProperty) {
		this.parentProperty = parentProperty;
	}
	
	public String getParentReadOnlyProperty() {
		return parentReadOnlyProperty;
	}
}

class BaseTestBeanA extends SuperTestBeanA {
	
	Integer id;
	
	@Id
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	BaseTestBeanB b;

	BaseTestBeanB defaultB = new BaseTestBeanB();

	public BaseTestBeanA() {
	}

	public BaseTestBeanA(BaseTestBeanB b) {
		this.b = b;
	}

	public void setDefaultB(BaseTestBeanB defaultB) {
		this.defaultB = defaultB;
	}

	public BaseTestBeanB getDefaultB() {
		return defaultB;
	}

	public void setB(BaseTestBeanB b) {
		this.b = b;
	}

	public BaseTestBeanB getB() {
		return b;
	}
	
	@Override
	public String toString() {
		return "BaseTestBeanA[b="+b+"]";
	}
}

class BaseTestBeanA_$$_ extends BaseTestBeanA {
	
	@Override
	public Integer getId() {
		return super.getId();
	}
}

class BaseTestBeanB {
	BaseTestBeanC c;
	List<BaseTestBeanC> listC;
	List<BaseTestBeanC> instantiatedListC = new ArrayList<BaseTestBeanC>();

	public BaseTestBeanB() {
	}

	public BaseTestBeanB(BaseTestBeanC c) {
		this.c = c;
	}

	public BaseTestBeanC getC() {
		return c;
	}

	public void setC(BaseTestBeanC c) {
		this.c = c;
	}

	public void setListC(List<BaseTestBeanC> listC) {
		this.listC = listC;
	}

	public List<BaseTestBeanC> getListC() {
		return listC;
	}

	public List<BaseTestBeanC> getInstantiatedListC() {
		return instantiatedListC;
	}

	public void setInstantiatedListC(List<BaseTestBeanC> instantiatedListC) {
		this.instantiatedListC = instantiatedListC;
	}
}

class BaseTestBeanC {

	String stringValue;
	Date dateValue;
	Calendar calendarValue;

	public String getStringValue() {
		return stringValue;
	}

	public Date getDateValue() {
		return dateValue;
	}

	public Calendar getCalendarValue() {
		return calendarValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public void setDateValue(Date dateValue) {
		this.dateValue = dateValue;
	}

	public void setCalendarValue(Calendar calendarValue) {
		this.calendarValue = calendarValue;
	}
}

@Retention(RetentionPolicy.RUNTIME)
@interface Id{
	
}


