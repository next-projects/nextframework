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

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.nextframework.exception.ApplicationException;
import org.nextframework.exception.BusinessException;
import org.nextframework.message.MessageResolver;
import org.nextframework.message.NextMessageSourceResolvable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

/**
 * @author rogelgarcia
 * @since 26/01/2006
 * @version 1.1
 */
public class ObjectUtils {

	public boolean isEmpty(Object o) {
		if (o == null) {
			return true;
		}
		if (o instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) o;
			return map.isEmpty();
		} else if (o instanceof Collection<?>) {
			Collection<?> c = (Collection<?>) o;
			return c.isEmpty();
		} else if (o.getClass().isArray()) {
			return Array.getLength(o) == 0;
		} else if (o instanceof String) {
			return ((String) o).length() == 0;
		} else {
			return false;
		}
	}

	public boolean isNotEmpty(Object type) {
		return !isEmpty(type);
	}

	@SuppressWarnings("all")
	public Method findMethod(Object object, String methodName, Class... arguments) {
		if (object == null) {
			throw new NullPointerException("N�o foi poss�vel encontrar m�todo " + methodName + ": objeto nulo ");
		}
		try {
			Method[] methods = object.getClass().getMethods();
			for (Method method : methods) {
				Class<?>[] parameterTypes = method.getParameterTypes();
				if (method.getName().equals(methodName)) {
					if (Arrays.deepEquals(parameterTypes, arguments)) {
						return method;
					}
				}

			}
			for (Method method : methods) {
				if (method.getName().equals(methodName)) {
					Class<?>[] parameterTypes = method.getParameterTypes();
					if (parameterTypes.length == arguments.length) {
						boolean match = true;
						for (int i = 0; i < parameterTypes.length; i++) {
							Class class1 = parameterTypes[i];
							Class class2 = arguments[i];
							if (class2.equals(Void.class)) {//void as parameter represents an unknown type
								continue;
							} else if (isBoolean(class1) && isBoolean(class2)) {
								continue;
							} else if (!class1.isAssignableFrom(class2)) {
								match = false;
								break;
							}
						}
						if (match) {
							return method;
						}
					}
				}
			}
			throw new NoSuchMethodException();
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("M�todo " + methodName + " n�o encontrado na classe " + object.getClass().getSimpleName(), e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		}
	}

	private boolean isBoolean(Class<?> class2) {
		return class2.equals(boolean.class) || class2.equals(Boolean.class);
	}

	public boolean equals(Object o1, Object o2) {
		if (o1 == null && o2 == null) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}
		return o1.equals(o2);
	}

	public Object findAndInvokeMethod(Object object, String methodName, Object... params) {
		Class<?>[] argumentTypes = new Class[params.length];
		for (int i = 0; i < argumentTypes.length; i++) {
			if (params[i] != null) {
				argumentTypes[i] = params[i].getClass();
			}
		}
		Method findMethod = findMethod(object, methodName, argumentTypes);
		try {
			return findMethod.invoke(object, params);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Argumentos inv�lidos ao chamar m�todo " + methodName + " na classe " + object.getClass().getSimpleName(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Acesso ilegal ao chamar m�todo " + methodName + " da classe " + object.getClass().getSimpleName(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("M�todo " + methodName + " lan�ou exe��o " + e.getClass().getSimpleName(), e);
		}
	}

	public Object findAndInvokeMethod(Object object, String methodName, Object[] params, Class<?>[] classes) {
		Method findMethod = findMethod(object, methodName, classes);
		try {
			return findMethod.invoke(object, params);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Argumentos inv�lidos ao chamar m�todo " + methodName + " na classe " + object.getClass().getSimpleName(), e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Acesso ilegal ao chamar m�todo " + methodName + " na classe " + object.getClass().getSimpleName(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("M�todo " + methodName + " lan�ou exe��o " + e.getTargetException().getClass().getSimpleName(), e.getTargetException());
		}
	}

	public MessageSourceResolvable newMessage(String code) {
		return new NextMessageSourceResolvable(code);
	}

	public MessageSourceResolvable newMessage(String[] codes) {
		return new NextMessageSourceResolvable(codes);
	}

	public MessageSourceResolvable newMessage(String code, Object[] args) {
		return new NextMessageSourceResolvable(code, args);
	}

	public MessageSourceResolvable newMessage(String[] codes, Object[] args) {
		return new NextMessageSourceResolvable(codes, args);
	}

	public MessageSourceResolvable newMessage(String code, Object[] args, String defaultMessage) {
		return new NextMessageSourceResolvable(code, args, defaultMessage);
	}

	public MessageSourceResolvable newMessage(String[] codes, Object[] args, String defaultMessage) {
		return new NextMessageSourceResolvable(codes, args, defaultMessage);
	}

	public ApplicationException getApplicationException(MessageResolver resolver, BusinessException exception) {
		String txt = getExceptionDescription(resolver, exception, false);
		throw new ApplicationException(txt, exception.getCause());
	}

	public String getExceptionDescription(MessageResolver resolver, Throwable exception) {
		return getExceptionDescription(resolver, exception, true);
	}

	public String getExceptionDescription(MessageResolver resolver, Throwable exception, boolean includeCauses) {

		String allDesc = "";

		Set<Throwable> allCauses = new HashSet<Throwable>();
		Throwable cause = exception;
		while (cause != null && !allCauses.contains(cause)) {

			String exTipo = null;

			Class<?> clazz = cause.getClass();
			do {
				try {
					exTipo = resolver.message(clazz.getName());
				} catch (NoSuchMessageException e) {
					//N�o encontrado...
				}
				clazz = clazz.getSuperclass();
			} while (exTipo == null && clazz != Object.class);

			if (exTipo == null && !(cause instanceof RuntimeException) && !(cause instanceof ApplicationException) && !(cause instanceof BusinessException)) {
				exTipo = cause.getClass().getSimpleName();
			}

			String exMsg = null;

			if (cause instanceof MessageSourceResolvable) {
				exMsg = resolver.message((MessageSourceResolvable) cause);
			} else {
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

			allDesc += (allDesc.length() == 0 ? "" : " -> ") +
					(exTipo != null ? exTipo : "") +
					(exTipo != null && exMsg != null ? ": " : "") +
					(exMsg != null ? exMsg : "");

			if (!includeCauses) {
				break;
			}

			allCauses.add(cause);
			cause = cause.getCause();
		}

		return allDesc;
	}

	static ThreadLocal<Long> timestamp = new ThreadLocal<Long>();
	static {
		timestamp.set(System.currentTimeMillis());
	}

	public void beginTimestamp() {
		timestamp.set(System.currentTimeMillis());
	}

	public long endTimestamp() {
		return System.currentTimeMillis() - timestamp.get();
	}
}
