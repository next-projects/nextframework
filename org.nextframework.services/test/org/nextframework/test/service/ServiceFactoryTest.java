package org.nextframework.test.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.nextframework.service.ServiceFactory;
import org.nextframework.service.ServiceProvider;

public class ServiceFactoryTest {

	public interface TestService {
	}

	private static class TestServiceImpl implements TestService {
	}

	private static class RecordingProvider implements ServiceProvider {

		private final int priority;
		private final TestService service;
		private final List<String> calls;
		private final String name;

		RecordingProvider(String name, int priority, TestService service, List<String> calls) {
			this.name = name;
			this.priority = priority;
			this.service = service;
			this.calls = calls;
		}

		@Override
		public <E> E getService(Class<E> serviceInterface) {
			if (serviceInterface != TestService.class) {
				return null;
			}
			calls.add(name);
			return serviceInterface.cast(service);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <E> E[] loadServices(Class<E> serviceInterface) {
			E[] services = (E[]) Array.newInstance(serviceInterface, service == null ? 0 : 1);
			if (service != null && serviceInterface == TestService.class) {
				services[0] = serviceInterface.cast(service);
			}
			return services;
		}

		@Override
		public int priority() {
			return priority;
		}

		@Override
		public void release() {
		}
	}

	private static class AlphaProvider extends RecordingProvider {

		AlphaProvider(String name, int priority, TestService service, List<String> calls) {
			super(name, priority, service, calls);
		}
	}

	private static class ZuluProvider extends RecordingProvider {

		ZuluProvider(String name, int priority, TestService service, List<String> calls) {
			super(name, priority, service, calls);
		}
	}

	@Before
	public void setUp() {
		ServiceFactory.refresh();
	}

	@After
	public void tearDown() {
		ServiceFactory.refresh();
	}

	@Test
	public void retainsAndConsultsProvidersWithTheSamePriority() {

		List<String> calls = new ArrayList<String>();
		RecordingProvider first = new AlphaProvider("first", 30, null, calls);
		TestService expected = new TestServiceImpl();
		RecordingProvider second = new ZuluProvider("second", 30, expected, calls);

		ServiceFactory.registerProvider(second);
		ServiceFactory.registerProvider(first);

		Set<ServiceProvider> providers = ServiceFactory.getProviders();
		assertTrue(providers.contains(first));
		assertTrue(providers.contains(second));
		assertSame(expected, ServiceFactory.getService(TestService.class));
		assertEquals(List.of("first", "second"), calls);

	}

	@Test
	public void loadsServicesFromEveryProviderWithTheSamePriority() {

		List<String> calls = new ArrayList<String>();
		TestService firstService = new TestServiceImpl();
		TestService secondService = new TestServiceImpl();
		ServiceFactory.registerProvider(new ZuluProvider("second", 30, secondService, calls));
		ServiceFactory.registerProvider(new AlphaProvider("first", 30, firstService, calls));

		assertArrayEquals(new TestService[] { firstService, secondService },
				ServiceFactory.loadServices(TestService.class));

	}

	@Test
	public void ordersByPriorityBeforeClassName() {

		List<String> calls = new ArrayList<String>();
		TestService expected = new TestServiceImpl();
		RecordingProvider alphabeticallyFirst = new AlphaProvider("alphabeticallyFirst", 40, null, calls);
		RecordingProvider higherPriority = new ZuluProvider("higherPriority", 20, expected, calls);

		ServiceFactory.registerProvider(alphabeticallyFirst);
		ServiceFactory.registerProvider(higherPriority);

		assertSame(expected, ServiceFactory.getService(TestService.class));
		assertEquals(List.of("higherPriority"), calls);

	}

	@Test
	public void providerViewsAreImmutableSnapshots() {

		List<String> calls = new ArrayList<String>();
		Set<ServiceProvider> snapshot = ServiceFactory.getProviders();
		RecordingProvider later = new AlphaProvider("later", 30, null, calls);

		ServiceFactory.registerProvider(later);

		assertFalse(snapshot.contains(later));
		assertTrue(ServiceFactory.getProviders().contains(later));
		try {
			snapshot.add(later);
		} catch (UnsupportedOperationException expected) {
			return;
		}

		throw new AssertionError("Provider snapshots must be immutable");

	}
}
