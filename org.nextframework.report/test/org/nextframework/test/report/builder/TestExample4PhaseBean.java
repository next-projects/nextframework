package org.nextframework.test.report.builder;

import org.nextframework.bean.annotation.DescriptionProperty;

public class TestExample4PhaseBean {

	private Integer id;
	private String name;

	public TestExample4PhaseBean(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	@DescriptionProperty
	public String getName() {
		return name;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

}
