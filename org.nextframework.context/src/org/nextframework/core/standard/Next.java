/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2009 the original author or authors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * You may obtain a copy of the license at
 * 
 *     http://www.gnu.org/copyleft/lesser.html
 * 
 */
package org.nextframework.core.standard;

import org.nextframework.context.NotInNextContextException;
import org.nextframework.service.ServiceFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.MessageSource;
import org.springframework.util.StringUtils;

/**
 * @author rogelgarcia
 * @since 21/01/2006
 * @version 1.1
 */
public class Next {

	protected static final InheritableThreadLocal<RequestContext> requestContext;
	protected static final InheritableThreadLocal<ApplicationContext> applicationContext;

	static {
		requestContext = new InheritableThreadLocal<RequestContext>();
		applicationContext = new InheritableThreadLocal<ApplicationContext>();
	}

	public static boolean isInApplicationContext() {
		return applicationContext.get() != null;
	}

	public static void setRequestContext(RequestContext context) {
		requestContext.set(context);
		applicationContext.set(context.getApplicationContext());
	}

	public static void setApplicationContext(ApplicationContext context) {
		applicationContext.set(context);
	}

	/**
	 * Retorna um objeto registrado no Spring de determinada classe 
	 * <B>IMPORTANTE:</B>
	 * Inicialmente, o objeto será obtido a partir do nome simples da classe com a primeira letra minúscula.
	 * Se não for encontrado, será procurado pelo tipo da classe.
	 */
	@SuppressWarnings("unchecked")
	public static <E> E getObject(Class<E> clazz) {
		E bean = null;
		String beanName = StringUtils.uncapitalize(clazz.getSimpleName());
		DefaultListableBeanFactory beanFactory = getBeanFactory();
		try {
			bean = (E) beanFactory.getBean(beanName);
		} catch (NoSuchBeanDefinitionException ex) {
			try {
				bean = beanFactory.getBean(clazz);
			} catch (NoSuchBeanDefinitionException ex2) {
				throw new NoSuchBeanDefinitionException(beanName, "And, no unique bean of type [" + clazz.getName() + "] is defined: Cannot find bean " + clazz.getName() + " by class neither by name.");
			}
		}
		return bean;
	}

	/**
	 * Retorna determinado objeto registrado no Spring	
	 * @param string
	 * @return
	 */
	public static Object getObject(String beanName) {
		return getBeanFactory().getBean(beanName);
	}

	public static DefaultListableBeanFactory getBeanFactory() {
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) ServiceFactory.getService(org.springframework.context.ApplicationContext.class).getAutowireCapableBeanFactory();
		return beanFactory;
	}

	public static MessageSource getMessageSource() {
		return ServiceFactory.getService(MessageSource.class);
	}

//	public static User getUser(){
//		return Authorization.getUserLocator().getUser();
////		return Authorization.getUserLocator().getUser();
//	}

//	public static Config getConfig(){
//		return Next.getApplicationContext().getConfig();
//	}

	/**
	 * Retorna o contexto NEXT
	 * @return
	 * @throws NotInNextContextException se não existir um contexto Next nessa Thread
	 */
	public static RequestContext getRequestContext() throws NotInNextContextException {
		RequestContext nextContext = requestContext.get();
		if (nextContext == null) {
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			throw new NotInNextContextException("The code is not running in a NEXT context! " +
					"\nClass: " + stackTrace[3].getClassName() + " " +
					"\nMethod: " + stackTrace[3].getMethodName() + " " +
					"\nLine: " + stackTrace[3].getLineNumber());
		}
		return nextContext;
	}

	public static ApplicationContext getApplicationContext() throws NotInNextContextException {
		ApplicationContext nextApplicationContext = applicationContext.get();
		if (nextApplicationContext == null) {
			StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
			throw new NotInNextContextException("The code is not running in a NEXT context! " +
					"\nClass: " + stackTrace[3].getClassName() + " " +
					"\nMethod: " + stackTrace[3].getMethodName() + " " +
					"\nLine: " + stackTrace[3].getLineNumber());
		}
		return nextApplicationContext;
	}

	public static String getApplicationName() {
		return Next.getApplicationContext().getApplicationName();
	}

}
