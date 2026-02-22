package org.springframework.orm.hibernate4;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.hibernate.Session;

public class CloseSuppressingInvocationHandler implements InvocationHandler {

	public CloseSuppressingInvocationHandler(Session session) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return null;
	}

}
