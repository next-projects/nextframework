package org.nextframework.test.summary;

import org.nextframework.summary.Summary;
import org.nextframework.summary.annotations.Group;

public class TestBeanSummary extends Summary<TestBean> {

	@Group(1)
	public String getGrupo1(){
		return getCurrent().getNome();
	}
	
	@Group(2)
	public Integer getGrupo2(){
		return getCurrent().getValue();
	}
}
