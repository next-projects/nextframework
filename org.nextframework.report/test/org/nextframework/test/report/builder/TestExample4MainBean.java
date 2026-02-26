package org.nextframework.test.report.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nextframework.bean.annotation.DescriptionProperty;

public class TestExample4MainBean {

	private String name;
	private List<TestExample4ItemBean> items = new ArrayList<TestExample4ItemBean>();

	public TestExample4MainBean(String name, List<TestExample4ItemBean> items) {
		super();
		this.name = name;
		this.items = items;
	}

	public TestExample4MainBean(String name) {
		super();
		this.name = name;
	}

	@DescriptionProperty
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<TestExample4ItemBean> getItems() {
		return items;
	}

	public void setItems(List<TestExample4ItemBean> items) {
		this.items = items;
	}

	public Map<String/*Phase*/, String/*Info*/> getMapPhase() {
		Map<String, String> map = new HashMap<String, String>();
		for (TestExample4ItemBean item : items) {
			map.put(item.getPhase().getName(), item.getInfo());
		}
		return map;
	}

}
