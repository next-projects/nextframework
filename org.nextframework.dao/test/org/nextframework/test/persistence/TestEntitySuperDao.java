package org.nextframework.test.persistence;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class TestEntitySuperDao {
	
	Long id;
	String name;
	
	List<TestEntityChild> children;
	
	TestEntityFile entityFile;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	@Column(length=200)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@OneToMany(mappedBy="parentSuper")
	public List<TestEntityChild> getChildren() {
		return children;
	}
	
	public void setChildren(List<TestEntityChild> children) {
		this.children = children;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	public TestEntityFile getEntityFile() {
		return entityFile;
	}
	
	public void setEntityFile(TestEntityFile entityFile) {
		this.entityFile = entityFile;
	}
	
}
