package org.nextframework.authorization.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.nextframework.authorization.Role;
import org.nextframework.authorization.User;

public class AuthorizationManagerImplTest {

	@Test
	public void staticRolesAreTrimmedAndComparedCaseInsensitively() {
		TestAuthorizationManager manager = managerWithRule("/restricted/**", " ADMIN, manager , ");

		assertTrue(manager.check(user("admin"), "/restricted/one"));
		assertTrue(manager.check(user("MaNaGeR"), "/restricted/two"));
		assertFalse(manager.check(user("user"), "/restricted/three"));
	}

	@Test
	public void emptyStaticRoleRuleIsPublic() {
		TestAuthorizationManager manager = managerWithRule("/public/**", "  , \t, ");

		assertTrue(manager.check(null, "/public/page"));
	}

	@Test
	public void cachedRolesAreImmutableAndSafeForConcurrentPopulation() throws Exception {
		TestAuthorizationManager manager = managerWithRule("/restricted/**", "ADMIN");
		ExecutorService executor = Executors.newFixedThreadPool(8);
		try {
			List<Callable<Boolean>> checks = new ArrayList<>();
			for (int i = 0; i < 100; i++) {
				String resource = "/restricted/" + (i % 20);
				checks.add(() -> manager.check(user("ADMIN"), resource));
			}

			for (Future<Boolean> result : executor.invokeAll(checks)) {
				assertTrue(result.get());
			}
		} finally {
			executor.shutdownNow();
		}

		assertEquals(20, manager.cacheResourceRoles.size());
		try {
			manager.cacheResourceRoles.get("/restricted/0").add("user");
			fail("Cached role lists must be immutable");
		} catch (UnsupportedOperationException expected) {
			// Expected.
		}
	}

	private TestAuthorizationManager managerWithRule(String resourcePattern, String roles) {
		Properties properties = new Properties();
		properties.setProperty(resourcePattern, roles);
		return new TestAuthorizationManager(properties);
	}

	private User user(String role) {
		return new TestUser(role);
	}

	private static class TestAuthorizationManager extends AuthorizationManagerImpl {

		TestAuthorizationManager(Properties properties) {
			authorizationProperties = properties;
			authorizationDAO = new AbstractAuthorizationDAO() {
				@Override
				public User findUserByUsername(String username) {
					return null;
				}

				@Override
				public Role[] findUserRoles(User user) {
					return new Role[] { new TestRole(user.getUsername()) };
				}
			};
		}

		boolean check(User user, String resource) {
			return checkStaticAuthorization(user, resource, null, null);
		}
	}

	private static class TestUser implements User {

		private static final long serialVersionUID = 1L;

		private final String role;

		TestUser(String role) {
			this.role = role;
		}

		@Override
		public String getUsername() {
			return role;
		}

		@Override
		public String getPassword() {
			return null;
		}
	}

	private static class TestRole implements Role {

		private static final long serialVersionUID = 1L;

		private final String name;

		TestRole(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			return name;
		}
	}
}
