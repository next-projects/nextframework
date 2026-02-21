package org.nextframework.test.context;

import org.junit.Before;
import org.junit.Test;
import org.nextframework.context.StaticBeanDefinitionLoader;
import org.nextframework.context.factory.support.QualifiedListableBeanFactory;
import org.nextframework.core.standard.DefaultApplicationContext;
import org.nextframework.core.standard.Next;
import org.nextframework.core.standard.NextStandard;
import org.nextframework.service.ServiceFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.QualifierAnnotationAutowireCandidateResolver;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.util.Assert;

public class TestContextUnit {

	@Before
	public void setUp() {
		DefaultApplicationContext.setApplicationName("testApp");
	}

	@Test
	public void testSimpleContextInit() {
		StaticBeanDefinitionLoader loader = StaticBeanDefinitionLoader.getInstance();
		loader.addBeanForClass(TestServiceConsumer.class);
		loader.addBeanForClass(TestService1.class);
		NextStandard.start();
		Assert.notNull(Next.getObject(TestService1.class));
	}

	@Test
	public void testSimpleContextInitQualified() {
		StaticBeanDefinitionLoader loader = StaticBeanDefinitionLoader.getInstance();
		loader.addBeanForClass(TestServiceConsumer.class);
		loader.addBeanForClass(TestService1.class);
		loader.addBeanForClass(TestService2.class);
		NextStandard.start();
		Assert.notNull(Next.getObject(TestService1.class));
		BeanDefinition beanDefinition = ((QualifiedListableBeanFactory) ServiceFactory.getService(ListableBeanFactory.class)).getBeanDefinition("testService1");
		System.out.println(((AbstractBeanDefinition) beanDefinition).getQualifiers());
	}

	// ================= Tests that don't require NextStandard.start() =================

	@Test
	public void testQualifiedListableBeanFactoryCreation() {
		QualifiedListableBeanFactory factory = new QualifiedListableBeanFactory(null);
		Assert.notNull(factory, "QualifiedListableBeanFactory should be instantiable");
		Assert.isInstanceOf(QualifiedListableBeanFactory.class, factory);
	}

	@Test
	public void testQualifiedListableBeanFactoryWithParent() {
		QualifiedListableBeanFactory parent = new QualifiedListableBeanFactory(null);
		QualifiedListableBeanFactory child = new QualifiedListableBeanFactory(parent);
		Assert.notNull(child, "QualifiedListableBeanFactory should be created with parent");
	}

	@Test
	public void testQualifiedListableBeanFactoryRegisterAndRetrieveBean() {
		QualifiedListableBeanFactory factory = new QualifiedListableBeanFactory(null);
		factory.setAutowireCandidateResolver(new QualifierAnnotationAutowireCandidateResolver());

		AnnotatedBeanDefinitionReader reader = new AnnotatedBeanDefinitionReader(factory);
		reader.register(TestService1.class);
		reader.register(TestService2.class);

		// Both bean definitions should be retrievable
		BeanDefinition bd1 = factory.getBeanDefinition("testService1");
		BeanDefinition bd2 = factory.getBeanDefinition("testService2");
		Assert.notNull(bd1, "testService1 bean definition should exist");
		Assert.notNull(bd2, "testService2 bean definition should exist");
	}

	@Test
	public void testStaticBeanDefinitionLoaderSingleton() {
		StaticBeanDefinitionLoader instance1 = StaticBeanDefinitionLoader.getInstance();
		StaticBeanDefinitionLoader instance2 = StaticBeanDefinitionLoader.getInstance();
		Assert.isTrue(instance1 == instance2, "StaticBeanDefinitionLoader should be singleton");
	}

	@Test
	public void testStaticBeanDefinitionLoaderAddBeans() {
		StaticBeanDefinitionLoader loader = StaticBeanDefinitionLoader.getInstance();
		// Should not throw when adding bean classes
		loader.addBeanForClass(TestService1.class);
		loader.addBeanForClass(TestService2.class);
		Assert.notNull(loader, "Loader should handle multiple addBeanForClass calls");
	}

}
