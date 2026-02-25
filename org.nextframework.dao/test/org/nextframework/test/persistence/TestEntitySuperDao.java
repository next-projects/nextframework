package org.nextframework.test.persistence;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class TestEntitySuperDao {

	private Long id;
	private String name;
	private List<TestEntityChildDAO> children;
	private TestEntityFile entityFile;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Column(length = 200)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@OneToMany(mappedBy = "parentSuper")
	public List<TestEntityChildDAO> getChildren() {
		return children;
	}

	public void setChildren(List<TestEntityChildDAO> children) {
		this.children = children;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public TestEntityFile getEntityFile() {
		return entityFile;
	}

	public void setEntityFile(TestEntityFile entityFile) {
		this.entityFile = entityFile;
	}

}
