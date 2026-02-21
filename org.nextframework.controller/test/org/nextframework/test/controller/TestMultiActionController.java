package org.nextframework.test.controller;

import org.junit.Assert;
import org.junit.Test;
import org.nextframework.controller.MultiActionController;
import org.springframework.web.servlet.mvc.AbstractController;
import org.springframework.web.servlet.mvc.multiaction.MethodNameResolver;

/**
 * Tests for MultiActionController integration points with Spring MVC.
 *
 * These tests verify the class structure and API contracts that are
 * critical for Spring MVC version upgrades (e.g., inheritance from
 * AbstractController, MethodNameResolver usage).
 */
public class TestMultiActionController {

	@Test
	public void testExtendsAbstractController() {
		Assert.assertTrue("MultiActionController should extend AbstractController",
				AbstractController.class.isAssignableFrom(MultiActionController.class));
	}

	@Test
	public void testMethodNameResolverInterface() {
		// Verify MethodNameResolver interface exists and is accessible
		// This is critical because Spring removed this in later versions
		Assert.assertNotNull(MethodNameResolver.class);
	}

	@Test
	public void testHasHandleRequestInternalMethod() throws NoSuchMethodException {
		// The key method that Spring calls to dispatch requests
		java.lang.reflect.Method method = MultiActionController.class.getDeclaredMethod(
				"handleRequestInternal",
				javax.servlet.http.HttpServletRequest.class,
				javax.servlet.http.HttpServletResponse.class);
		Assert.assertNotNull("handleRequestInternal should exist", method);
		Assert.assertEquals(org.springframework.web.servlet.ModelAndView.class, method.getReturnType());
	}

	@Test
	public void testServletRequestDataBinderAvailable() {
		// Verify that ServletRequestDataBinder is accessible - this API changed in Spring 5/6
		Assert.assertNotNull(org.springframework.web.bind.ServletRequestDataBinder.class);
	}

	@Test
	public void testBindExceptionAvailable() {
		// BindException moved packages in newer Spring versions
		Assert.assertNotNull(org.springframework.validation.BindException.class);
	}

	@Test
	public void testValidationUtilsAvailable() {
		// ValidationUtils is used for validation flow
		Assert.assertNotNull(org.springframework.validation.ValidationUtils.class);
	}

}
