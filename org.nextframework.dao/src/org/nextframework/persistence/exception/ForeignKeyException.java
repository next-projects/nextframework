/**
 * 
 */
package org.nextframework.persistence.exception;

import org.springframework.dao.DataAccessException;

public class ForeignKeyException extends DataAccessException {

	private static final long serialVersionUID = 1L;
	
	private String msg;

	public ForeignKeyException(String msg) {
		super(msg);
		this.msg = msg;
	}
	
	public ForeignKeyException(String msg, Throwable cause) {
		super(msg, cause);
		this.msg = msg;
	}

	public String getOriginalMessage() {
		return msg;
	}
}