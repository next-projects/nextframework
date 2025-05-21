package org.nextframework.test.context;

import org.junit.Test;
import org.nextframework.context.StaticBeanDefinitionLoader;
import org.nextframework.context.factory.support.QualifiedListableBeanFactory;
import org.nextframework.core.standard.Next;
import org.nextframework.core.standard.NextStandard;
import org.nextframework.service.ServiceFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.util.Assert;

public class TestContextUnit {

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

}
