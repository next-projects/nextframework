package org.nextframework.test.report.builder;

import java.util.ArrayList;
import java.util.List;

public class TestBean {

	String name;
	Integer mod;

	public String getName() {
		return name;
	}

	public void setName(String nome) {
		this.name = nome;
	}

	public Integer getMod() {
		return mod;
	}

	public void setMod(Integer mod) {
		this.mod = mod;
	}

	public static List<TestBean> createDataset(int quantity) {
		ArrayList<TestBean> result = new ArrayList<TestBean>();
		for (int i = 0; i < quantity; i++) {
			TestBean bean = new TestBean();
			bean.setName("NAME " + i);
			bean.setMod(i % 10);
			result.add(bean);
		}
		return result;
	}

}
