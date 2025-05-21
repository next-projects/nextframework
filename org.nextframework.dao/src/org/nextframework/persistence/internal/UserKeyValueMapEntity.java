package org.nextframework.persistence.internal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "_next_keyvalue", uniqueConstraints = @UniqueConstraint(name = "_nx_uk_un_mp", columnNames = { "username", "map_key" }), indexes = @Index(name = "_nx_idx_un", columnList = "username"))
@TableGenerator(name = "_userkey_gen", table = "_next_hilo_gen", allocationSize = 10)
public class UserKeyValueMapEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "_userkey_gen")
	Long id;

	@Column(updatable = false)
	String username;

	@Column(name = "map_key", nullable = false, updatable = false, length = 256)
	String key;

	@Column(name = "map_value", nullable = true, length = 1024)
	String value;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserKeyValueMapEntity other = (UserKeyValueMapEntity) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
