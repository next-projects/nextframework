package org.nextframework.test.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class TestEntityChild {

	Integer id;

	String name;

	Integer value;

	TestEntityParent parent;

	TestEntityParent parent2;

	TestEntitySuper parentSuper;

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Integer getValue() {
		return value;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public TestEntityParent getParent() {
		return parent;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public TestEntityParent getParent2() {
		return parent2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public TestEntitySuper getParentSuper() {
		return parentSuper;
	}

	public void setParentSuper(TestEntitySuper parentSuper) {
		this.parentSuper = parentSuper;
	}

	public void setParent2(TestEntityParent parent2) {
		this.parent2 = parent2;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setValue(Integer value) {
		this.value = value;
	}

	public void setParent(TestEntityParent parent) {
		this.parent = parent;
	}

}
