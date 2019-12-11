package org.nextframework.message;

import java.util.Locale;

import org.springframework.context.MessageSourceResolvable;

public interface MessageResolver {

	public Locale getLocale();

	public String message(String code);

	public String message(String code, Object[] argumentsArray);

	public String message(String code, Object[] argumentsArray, String defaultValue);

	public String message(MessageSourceResolvable resolvable);

}