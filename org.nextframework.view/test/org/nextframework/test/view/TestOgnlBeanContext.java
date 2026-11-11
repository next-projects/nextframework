package org.nextframework.test.view;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Array;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.nextframework.bean.BeanDescriptorFactory;
import org.nextframework.service.ServiceFactory;
import org.nextframework.service.ServiceProvider;
import org.nextframework.view.BaseTag;
import org.nextframework.view.BeanTag;
import org.nextframework.view.WebContextMap;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class TestOgnlBeanContext {

	private static StaticApplicationContext context;
	private final Map<String, Object> requestAttributes = new HashMap<>();
	private final Map<String, Object> sessionAttributes = new HashMap<>();
	private final Map<String, Object> applicationAttributes = new HashMap<>();
	private HttpServletRequest request;

	@BeforeClass
	public static void registerContext() {
		context = new StaticApplicationContext();
		context.getBeanFactory().registerSingleton("colisao", "spring");
		ServiceFactory.registerProvider(new ServiceProvider() {
			@Override
			public <E> E getService(Class<E> serviceInterface) {
				return serviceInterface == ApplicationContext.class ? serviceInterface.cast(context) : null;
			}

			@Override
			public int priority() {
				return Integer.MIN_VALUE;
			}

			@Override
			public void release() {
			}

			@SuppressWarnings("unchecked")
			@Override
			public <E> E[] loadServices(Class<E> serviceInterface) {
				E service = getService(serviceInterface);
				E[] services = (E[]) Array.newInstance(serviceInterface, service != null ? 1 : 0);
				if (service != null) {
					services[0] = service;
				}
				return services;
			}
		});
	}

	@AfterClass
	public static void closeContext() {
		context.close();
		context = null;
	}

	@Before
	public void prepareRequest() {
		ServletContext application = surface(ServletContext.class, applicationAttributes, Map.of());
		HttpSession session = surface(HttpSession.class, sessionAttributes, Map.of("getServletContext", application));
		request = surface(HttpServletRequest.class, requestAttributes, Map.of("getSession", session));
	}

	@Test
	public void resolvesBeanPropertiesAndExplicitDtoPath() {
		Filtro dto = new Filtro("local");
		requestAttributes.put("dto", dto);
		TestTag tag = tag(dto);
		assertEquals("local", tag.getOgnlValue("localEmbarque"));
		assertEquals("local", tag.getOgnlValue("dto.localEmbarque"));
	}

	@Test
	public void preservesRequestSessionApplicationAndSpringPrecedence() {
		TestTag tag = tag(new Filtro("bean"));
		requestAttributes.put("colisao", "request");
		sessionAttributes.put("colisao", "session");
		applicationAttributes.put("colisao", "application");
		assertEquals("request", tag.getOgnlValue("colisao"));
		requestAttributes.remove("colisao");
		assertEquals("session", tag.getOgnlValue("colisao"));
		sessionAttributes.remove("colisao");
		assertEquals("application", tag.getOgnlValue("colisao"));
		applicationAttributes.remove("colisao");
		assertEquals("spring", tag.getOgnlValue("colisao"));
	}

	@Test
	public void resolvesServiceArgumentsWithoutDtoPrefix() {
		Filtro dto = new Filtro("local");
		requestAttributes.put("dto", dto);
		requestAttributes.put("servico", new Servico());
		TestTag tag = tag(dto);
		assertEquals("local", tag.getOgnlValue("servico.recebe(localEmbarque)"));
		assertEquals("local", tag.getOgnlValue("servico.recebe(dto.localEmbarque)"));
	}

	@Test
	public void usesNearestBean() {
		assertEquals("linha", tag(new Filtro("formulario"), new Filtro("linha")).getOgnlValue("localEmbarque"));
	}

	@Test
	public void nullPropertyDoesNotFallBackToOuterBean() {
		assertNull(tag(new Filtro("formulario"), new Filtro(null)).getOgnlValue("localEmbarque"));
	}

	@Test
	public void missingPropertyDoesNotFallBackToOuterBean() {
		assertNull(tag(new Filtro("formulario"), new Object()).getOgnlValue("localEmbarque"));
	}

	@Test
	public void missingPropertyAndMissingBeanPreserveNullResult() {
		assertNull(tag(new Filtro("local")).getOgnlValue("inexistente"));
		assertNull(tag().getOgnlValue("localEmbarque"));
	}

	@Test
	public void contextWithoutBeanKeepsExistingBehavior() {
		requestAttributes.put("dto", new Filtro("local"));
		WebContextMap map = new WebContextMap(request);
		assertNull(map.get("localEmbarque"));
		assertSame(requestAttributes.get("dto"), map.get("dto"));
	}

	@Test
	public void dynamicAttributesResolveBeanProperties() {
		TestTag tag = tag(new Filtro("local"));
		assertTrue(tag.getDynamicAttributesToString(Map.of("title", "ognl:localEmbarque")).contains("title='local'"));
	}

	@Test
	public void typedEvaluationResolvesCollections() {
		Filtro dto = new Filtro("local");
		assertSame(dto.getLocais(), tag(dto).collection("locais"));
	}

	@Test
	public void writeOnlyPropertyIsNotRead() {
		assertNull(tag(new Filtro("local")).getOgnlValue("senha"));
	}

	@Test
	public void getterFailureIsNotHidden() {
		try {
			tag(new Filtro("local")).getOgnlValue("falha");
			fail("A falha do getter deve ser propagada");
		} catch (RuntimeException e) {
			Throwable cause = e;
			while (cause.getCause() != null) {
				cause = cause.getCause();
			}
			assertEquals("falha no getter", cause.getMessage());
		}
	}

	private TestTag tag(Object... beans) {
		Stack<BaseTag> stack = new Stack<>();
		for (Object bean : beans) {
			stack.push(new TestBeanTag(bean));
		}
		TestTag tag = new TestTag(request);
		stack.push(tag);
		requestAttributes.put("TagStack", stack);
		return tag;
	}

	private static <T> T surface(Class<T> type, Map<String, Object> attributes, Map<String, Object> methods) {
		return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[] { type }, (proxy, method, args) -> {
			switch (method.getName()) {
				case "getAttribute":
					return attributes.get(args[0]);
				case "setAttribute":
					if (args[1] == null) {
						attributes.remove(args[0]);
					} else {
						attributes.put((String) args[0], args[1]);
					}
					return null;
				case "removeAttribute":
					attributes.remove(args[0]);
					return null;
				default:
					if (methods.containsKey(method.getName())) {
						return methods.get(method.getName());
					}
					throw new UnsupportedOperationException(method.toString());
			}
		}));
	}

	public static class Filtro {
		private final String empresa;
		private final List<String> locais = List.of("local");

		public Filtro(String empresa) {
			this.empresa = empresa;
		}

		public String getLocalEmbarque() {
			return empresa;
		}

		public String getColisao() {
			throw new AssertionError("O bean Spring deve ter prioridade");
		}

		public List<String> getLocais() {
			return locais;
		}

		public void setSenha(String senha) {
		}

		public String getFalha() {
			throw new IllegalStateException("falha no getter");
		}
	}

	public static class Servico {
		public Object recebe(Object value) {
			return value;
		}
	}

	private static class TestBeanTag extends BeanTag {
		TestBeanTag(Object bean) {
			beanDescriptor = BeanDescriptorFactory.forBean(bean);
		}
	}

	private static class TestTag extends BaseTag {
		private final HttpServletRequest request;

		TestTag(HttpServletRequest request) {
			this.request = request;
		}

		@Override
		public HttpServletRequest getRequest() {
			return request;
		}

		Collection<?> collection(String expression) {
			return getOgnlValue(expression, Collection.class);
		}
	}

}
