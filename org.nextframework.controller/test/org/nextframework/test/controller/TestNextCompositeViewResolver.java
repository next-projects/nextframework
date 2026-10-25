package org.nextframework.test.controller;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;

import org.junit.Test;
import org.nextframework.controller.NextCompositeViewResolver;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

import jakarta.servlet.ServletContext;

public class TestNextCompositeViewResolver {

	@Test
	public void webResourceViewKeepsTheBasePageComposition() throws Exception {
		String packagedPage = "/WEB-INF/nextframework/authorization/login.jsp";
		String basePage = "/WEB-INF/jsp/base.jsp";
		ServletContext servletContext = (ServletContext) Proxy.newProxyInstance(
				ServletContext.class.getClassLoader(),
				new Class<?>[] { ServletContext.class },
				(proxy, method, args) -> {
					if (method.getName().equals("getResource") && packagedPage.equals(args[0])) {
						return new URL("jar:file:/application/WEB-INF/lib/dashboard.jar!/META-INF/resources" + packagedPage);
					}
					if (method.getName().equals("getResourceAsStream")
							&& (basePage.equals(args[0]) || packagedPage.equals(args[0]))) {
						return new ByteArrayInputStream(new byte[0]);
					}
					if (method.getReturnType() == boolean.class) {
						return false;
					}
					if (method.getReturnType() == int.class) {
						return 0;
					}
					if (Enumeration.class.isAssignableFrom(method.getReturnType())) {
						return Collections.emptyEnumeration();
					}
					return null;
				});

		TestableViewResolver resolver = new TestableViewResolver();
		StaticWebApplicationContext applicationContext = new StaticWebApplicationContext();
		applicationContext.setServletContext(servletContext);
		applicationContext.refresh();
		resolver.setServletContext(servletContext);
		resolver.setApplicationContext(applicationContext);
		resolver.setPrefix("/WEB-INF/jsp/app/");
		resolver.setSuffix(".jsp");
		resolver.setBaseViews(basePage);
		resolver.setParameterName("bodyPage");

		AbstractUrlBasedView view = resolver.load(NextCompositeViewResolver.WEB_RESOURCE_URL_PREFIX + packagedPage);

		assertEquals(basePage, view.getUrl());
		assertEquals(packagedPage, view.getStaticAttributes().get("bodyPage"));
	}

	private static class TestableViewResolver extends NextCompositeViewResolver {

		AbstractUrlBasedView load(String viewName) throws Exception {
			return (AbstractUrlBasedView) loadView(viewName, null);
		}
	}
}
