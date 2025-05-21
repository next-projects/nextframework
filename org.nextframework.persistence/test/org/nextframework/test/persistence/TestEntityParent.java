package org.nextframework.test.persistence;

import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class TestEntityParent {

	Integer id;

	String name;

	List<TestEntityChild> children;

	Set<TestEntityChild> childrenSet;

	public TestEntityParent() {

	}

	public TestEntityParent(String name) {
		this.name = name;
	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	@OneToMany(mappedBy = "parent")
	public List<TestEntityChild> getChildren() {
		return children;
	}

	@OneToMany(mappedBy = "parent2")
	public Set<TestEntityChild> getChildrenSet() {
		return childrenSet;
	}

	public void setChildrenSet(Set<TestEntityChild> childrenSet) {
		this.childrenSet = childrenSet;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setChildren(List<TestEntityChild> children) {
		this.children = children;
	}

}
