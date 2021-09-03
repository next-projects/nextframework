package org.nextframework.message;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

public class NextMessageSourceResolvable extends DefaultMessageSourceResolvable {

	private static final long serialVersionUID = 1L;

	public NextMessageSourceResolvable(String code) {
		this(new String[] { code }, null, null);
	}

	public NextMessageSourceResolvable(String[] codes) {
		this(codes, null, null);
	}

	public NextMessageSourceResolvable(String code, Object[] arguments) {
		this(new String[] { code }, arguments, null);
	}

	public NextMessageSourceResolvable(String[] codes, Object[] arguments) {
		this(codes, arguments, null);
	}

	public NextMessageSourceResolvable(String code, Object[] arguments, String defaultMessage) {
		this(new String[] { code }, arguments, defaultMessage);
	}

	public NextMessageSourceResolvable(String[] codes, Object[] arguments, String defaultMessage) {
		super(codes, arguments, defaultMessage);
	}

	public NextMessageSourceResolvable(MessageSourceResolvable resolvable) {
		this(resolvable.getCodes(), resolvable.getArguments(), resolvable.getDefaultMessage());
	}

	@Override
	public String toString() {
		return resolvableToString();
	}

}