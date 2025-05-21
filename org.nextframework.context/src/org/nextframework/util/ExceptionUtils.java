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
package org.nextframework.util;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.nextframework.core.standard.Next;
import org.nextframework.exception.ApplicationException;
import org.nextframework.exception.BusinessException;
import org.nextframework.service.ServiceException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

public class ExceptionUtils {

	public String getExceptionDescription(Throwable exception, Locale locale) {
		return getExceptionDescription(exception, true, locale);
	}

	public String getExceptionDescription(Throwable exception, boolean includeCauses, Locale locale) {

		String allDesc = "";

		Set<Throwable> allCauses = new HashSet<Throwable>();
		Throwable cause = exception;
		while (cause != null && !allCauses.contains(cause)) {

			String exTipo = null;
			Class<?> clazz = cause.getClass();
			do {
				try {
					exTipo = Next.getMessageSource().getMessage(clazz.getName(), null, locale);
				} catch (NoSuchMessageException e) {
					//Não encontrado...
				} catch (ServiceException e) {
					//Serviço não encontrado...
				}
				clazz = clazz.getSuperclass();
			} while (exTipo == null && clazz != Object.class);
			if (exTipo == null && !(cause instanceof RuntimeException) && !(cause instanceof ApplicationException) && !(cause instanceof BusinessException)) {
				exTipo = cause.getClass().getSimpleName();
			}
			exTipo = Util.strings.isNotEmpty(exTipo) ? exTipo : null;

			String exMsg = null;

			if (cause instanceof MessageSourceResolvable) {
				try {
					exMsg = Next.getMessageSource().getMessage((MessageSourceResolvable) cause, locale);
				} catch (NoSuchMessageException e) {
					//Não encontrado...
				} catch (ServiceException e) {
					//Serviço não encontrado...
				}
			}
			if (exMsg == null) {
				exMsg = cause.getMessage();
				if (exMsg == null) {
					exMsg = cause.toString();
				}
				if (exMsg != null) {
					int nestedIndex = exMsg.indexOf("; nested exception is");
					if (nestedIndex > -1) {
						exMsg = exMsg.substring(0, nestedIndex);
					}
				}
			}

			if (Util.strings.isNotEmpty(exTipo) || Util.strings.isNotEmpty(exMsg)) {
				allDesc += (allDesc.length() == 0 ? "" : " -> ") +
						(exTipo != null ? exTipo : "") +
						(exTipo != null && exMsg != null ? ": " : "") +
						(exMsg != null ? exMsg : "");
			}

			if (!includeCauses) {
				break;
			}

			allCauses.add(cause);
			cause = cause.getCause();
		}

		return allDesc;
	}

}
