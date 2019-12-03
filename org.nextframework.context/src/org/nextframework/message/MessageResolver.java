package org.nextframework.message;

import org.springframework.context.MessageSourceResolvable;

public interface MessageResolver {

	public String message(String code);

	public String message(String code, Object[] argumentsArray);

	public String message(String code, Object[] argumentsArray, String defaultValue);

	public String message(MessageSourceResolvable resolvable);

}