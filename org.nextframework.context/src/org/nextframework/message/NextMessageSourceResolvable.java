package org.nextframework.message;

import java.io.Serializable;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class NextMessageSourceResolvable implements MessageSourceResolvable, Serializable {

	private static final long serialVersionUID = 1L;

	private final String[] codes;
	private final Object[] arguments;
	private final String defaultMessage;

	public NextMessageSourceResolvable(String code) {
		this(new String[] { code }, null, null);
	}

	public NextMessageSourceResolvable(String[] codes) {
		this(codes, null, null);
	}

	public NextMessageSourceResolvable(String code, String defaultMessage) {
		this(new String[] { code }, null, defaultMessage);
	}

	public NextMessageSourceResolvable(String[] codes, String defaultMessage) {
		this(codes, null, defaultMessage);
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
		this.codes = codes;
		this.arguments = arguments;
		this.defaultMessage = defaultMessage;
	}

	public NextMessageSourceResolvable(MessageSourceResolvable resolvable) {
		this(resolvable.getCodes(), resolvable.getArguments(), resolvable.getDefaultMessage());
	}

	@Override
	public String[] getCodes() {
		return this.codes;
	}

	@Override
	public Object[] getArguments() {
		return this.arguments;
	}

	@Override
	public String getDefaultMessage() {
		return this.defaultMessage;
	}

	protected final String resolvableToString() {
		StringBuilder result = new StringBuilder();
		result.append("codes [").append(StringUtils.arrayToDelimitedString(this.codes, ","));
		result.append("]; arguments [" + StringUtils.arrayToDelimitedString(this.arguments, ","));
		result.append("]; default message [").append(this.defaultMessage).append(']');
		return result.toString();
	}

	@Override
	public String toString() {
		return resolvableToString();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MessageSourceResolvable)) {
			return false;
		}
		MessageSourceResolvable otherResolvable = (MessageSourceResolvable) other;
		return ObjectUtils.nullSafeEquals(getCodes(), otherResolvable.getCodes()) &&
				ObjectUtils.nullSafeEquals(getArguments(), otherResolvable.getArguments()) &&
				ObjectUtils.nullSafeEquals(getDefaultMessage(), otherResolvable.getDefaultMessage());
	}

	@Override
	public int hashCode() {
		int hashCode = ObjectUtils.nullSafeHashCode(getCodes());
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(getArguments());
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(getDefaultMessage());
		return hashCode;
	}

}