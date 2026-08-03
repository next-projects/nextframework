package org.erplite.entity;

import java.util.Date;

import org.nextframework.types.Password;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "users")
public class User implements org.nextframework.authorization.User {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String username;

	@Password
	private String password;

	private String name;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;

	public Long getId() {
		return id;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

}
