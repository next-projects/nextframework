package org.nextframework.test.persistence;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;

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
