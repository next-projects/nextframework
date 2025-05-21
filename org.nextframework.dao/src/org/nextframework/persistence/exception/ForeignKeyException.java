/**
 * 
 */
package org.nextframework.persistence.exception;

import org.springframework.dao.DataAccessException;

public class ForeignKeyException extends DataAccessException {

	private static final long serialVersionUID = 1L;

	public ForeignKeyException(String defaultMensagem) {
		super(defaultMensagem);
	}

	public ForeignKeyException(String defaultMensagem, Throwable cause) {
		super(defaultMensagem, cause);
	}

}
