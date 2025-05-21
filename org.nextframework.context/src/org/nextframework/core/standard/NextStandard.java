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

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextframework.context.NextStandardApplicationContext;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * @author rogelgarcia
 * @since 21/01/2006
 * @version 1.1
 */
public class NextStandard extends Next {

	public static boolean AUTO_LOAD_APPLICATION_CONFIG_XML = true;
	public static boolean INIT_LOGGING = true;

	static Log logger = LogFactory.getLog(NextStandard.class);

	public static void start() {
		createNextContext();
	}

	public static RequestContext createNextContext(String... fileLocations) {

		ApplicationContext applicationContext = createApplicationContext(fileLocations);
		DefaultRequestContext defaultRequestContext = new DefaultRequestContext(applicationContext);

		Next.requestContext.set(defaultRequestContext);

		return defaultRequestContext;
	}

	public static ApplicationContext createApplicationContext(String... fileLocations) {

		if (INIT_LOGGING) {
			initLog4J();
		}

		NextStandardApplicationContext applicationContext = new NextStandardApplicationContext();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(applicationContext);

		Next.applicationContext.set(new DefaultApplicationContext());

		if (AUTO_LOAD_APPLICATION_CONFIG_XML) {
			PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver(applicationContext);
			try {
				Resource[] resources = pathMatchingResourcePatternResolver.getResources("**/applicationConfig.xml");
				for (Resource resource : resources) {
					System.err.println("Found applicationConfig.xml in " + resource);
					reader.loadBeanDefinitions(resource);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		for (String location : fileLocations) {
			reader.loadBeanDefinitions(new FileSystemResource(location));
		}

		applicationContext.refresh();

		return Next.applicationContext.get();
	}

	public static void initLog4J() {
		Properties properties = new Properties();
		properties.setProperty("log4j.defaultInitOverride", "false");
		properties.setProperty("log4j.rootCategory", "INFO, console");
		properties.setProperty("log4j.appender.console", "org.apache.log4j.ConsoleAppender");
		properties.setProperty("log4j.appender.console.layout", "org.apache.log4j.PatternLayout");
		properties.setProperty("log4j.appender.console.layout.ConversionPattern", "%-5p %c %x - %m%n");
	}

	public static RequestContext createNextContext() {
		return createNextContext(new String[0]);
	}

}
