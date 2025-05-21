package org.nextframework.test.summary;

import java.util.Map;

public class TestBean {

	String nome;
	Integer value;
	Map<String, Double> map;

	public TestBean() {

	}

	public TestBean(String nome, Integer value) {
		super();
		this.nome = nome;
		this.value = value;
	}

	public Integer getValue() {
		return value;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Map<String, Double> getMap() {
		return map;
	}

	public void setMap(Map<String, Double> map) {
		this.map = map;
	}

}
