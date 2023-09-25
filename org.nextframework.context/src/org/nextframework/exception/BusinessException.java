/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2009 the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * You may obtain a copy of the license at
 * 
 *     http://www.gnu.org/copyleft/lesser.html
 * 
 */
package org.nextframework.exception;

import org.nextframework.util.Util;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.util.ObjectUtils;

public class BusinessException extends ApplicationException implements MessageSourceResolvable {

	private static final long serialVersionUID = 1L;

	private MessageSourceResolvable resolvable;
	private String mensagem;

	public BusinessException(Throwable cause) {
		super(cause);
	}

	public BusinessException(String code) {
		this(code, null, null);
	}

	public BusinessException(String code, Object[] arguments) {
		this(code, arguments, null);
	}

	public BusinessException(String code, Object[] arguments, String defaultMessage) {
		this(Util.objects.newMessage(code, arguments, defaultMessage));
	}

	public BusinessException(MessageSourceResolvable msr) {
		super();
		this.resolvable = msr;
		this.mensagem = msr.toString();
	}

	public BusinessException(Throwable cause, String code) {
		this(cause, code, null, null);
	}

	public BusinessException(Throwable cause, String code, Object[] arguments) {
		this(cause, code, arguments, null);
	}

	public BusinessException(Throwable cause, String code, Object[] arguments, String defaultMessage) {
		this(cause, Util.objects.newMessage(code, arguments, defaultMessage));
	}

	public BusinessException(Throwable cause, MessageSourceResolvable msr) {
		super(cause);
		this.resolvable = msr;
		this.mensagem = msr.toString();
	}

	@Override
	public String getMessage() {
		return this.mensagem;
	}

	@Override
	public String[] getCodes() {
		return this.resolvable.getCodes();
	}

	@Override
	public Object[] getArguments() {
		return this.resolvable.getArguments();
	}

	@Override
	public String getDefaultMessage() {
		return this.resolvable.getDefaultMessage();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BusinessException)) {
			return false;
		}
		BusinessException otherResolvable = (BusinessException) other;
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