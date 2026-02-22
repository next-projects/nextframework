package org.nextframework.test.persistence;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class TestEntitySuper {

	Long id;
	String name;

	List<TestEntityChild> children;

	@Id
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(mappedBy = "parentSuper")
	public List<TestEntityChild> getChildren() {
		return children;
	}

	public void setChildren(List<TestEntityChild> children) {
		this.children = children;
	}

}
