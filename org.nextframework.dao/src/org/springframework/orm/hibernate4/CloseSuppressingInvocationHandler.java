package org.springframework.orm.hibernate4;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.hibernate.Session;

/**
 * Invocation handler that suppresses close calls on Hibernate Sessions.
 * Also prepares returned Query and Criteria objects.
 *
 * @see HibernateTemplate#createSessionProxy(Session)
 */
public class CloseSuppressingInvocationHandler implements InvocationHandler {

	private final Session target;

	public CloseSuppressingInvocationHandler(Session target) {
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// Handle close() by suppressing it
		if ("close".equals(method.getName())) {
			return null;
		}

		// Handle equals()
		if ("equals".equals(method.getName()) && args != null && args.length == 1) {
			return proxy == args[0];
		}

		// Handle hashCode()
		if ("hashCode".equals(method.getName()) && (args == null || args.length == 0)) {
			return System.identityHashCode(proxy);
		}

		// Handle toString()
		if ("toString".equals(method.getName()) && (args == null || args.length == 0)) {
			return "CloseSuppressingProxy for " + target.toString();
		}

		// Delegate to the real session
		try {
			return method.invoke(target, args);
		} catch (InvocationTargetException ex) {
			throw ex.getTargetException();
		}
	}

}
