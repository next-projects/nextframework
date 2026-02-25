
package org.nextframework.test.persistence;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class TestEntityChildDAO {

	Integer id;
	TestEntitySuperDao parentSuper;

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	public TestEntitySuperDao getParentSuper() {
		return parentSuper;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setParentSuper(TestEntitySuperDao parentSuper) {
		this.parentSuper = parentSuper;
	}

}
