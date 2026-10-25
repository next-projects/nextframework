package org.nextframework.test.context;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

import java.lang.reflect.Proxy;
import org.junit.After;
import org.junit.Test;
import org.nextframework.context.NotInNextContextException;
import org.nextframework.core.standard.ApplicationContext;
import org.nextframework.core.standard.Next;
import org.nextframework.core.standard.RequestContext;

public class NextThreadContextTest {

	@After
	public void removeContext() {
		Next.remove();
	}

	@Test
	public void requestContextCanBeRemoved() {

		ApplicationContext applicationContext = new ApplicationContext() {

			@Override
			public String getApplicationName() {
				return "test";
			}

			@Override
			public String getApplicationDir() {
				return ".";
			}
		};
		RequestContext requestContext = (RequestContext) Proxy.newProxyInstance(RequestContext.class.getClassLoader(),
				new Class<?>[] { RequestContext.class },
				(proxy, method, args) -> method.getName().equals("getApplicationContext") ? applicationContext : null);

		Next.setRequestContext(requestContext);
		assertSame(requestContext, Next.getRequestContext());
		assertSame(applicationContext, Next.getApplicationContext());

		Next.remove();
		assertThrows(NotInNextContextException.class, Next::getRequestContext);
		assertThrows(NotInNextContextException.class, Next::getApplicationContext);

	}

}
