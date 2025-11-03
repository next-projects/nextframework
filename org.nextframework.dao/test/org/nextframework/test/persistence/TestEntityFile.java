package org.nextframework.test.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.nextframework.types.File;

@Entity
public class TestEntityFile implements File {

	private static final long serialVersionUID = 1L;

	Long cdfile;
	String name;
	String contenttype;
	byte[] content;
	long size;

	@Override
	public long getSize() {
		return size;
	}

	@Override
	public void setSize(long size) {
		this.size = size;
	}

	@Id
	@GeneratedValue
	public Long getCdfile() {
		return cdfile;
	}

	public String getName() {
		return name;
	}

	public String getContenttype() {
		return contenttype;
	}

	public byte[] getContent() {
		return content;
	}

	public void setCdfile(Long cdfile) {
		this.cdfile = cdfile;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setContenttype(String contenttype) {
		this.contenttype = contenttype;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

}
