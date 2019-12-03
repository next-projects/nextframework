package org.nextframework.message;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

public class NextMessageSourceResolvable extends DefaultMessageSourceResolvable {

	private static final long serialVersionUID = 1L;

	public NextMessageSourceResolvable(String code) {
		super(new String[] { code }, null, null);
	}

	public NextMessageSourceResolvable(String code, String defaultMessage) {
		super(new String[] { code }, null, defaultMessage);
	}

	public NextMessageSourceResolvable(String code, Object[] arguments) {
		super(new String[] { code }, arguments, null);
	}

	public NextMessageSourceResolvable(String code, Object[] arguments, String defaultMessage) {
		super(new String[] { code }, arguments, defaultMessage);
	}

	public NextMessageSourceResolvable(MessageSourceResolvable resolvable) {
		super(resolvable.getCodes(), resolvable.getArguments(), resolvable.getDefaultMessage());
	}

	@Override
	public String toString() {
		return resolvableToString();
	}

}