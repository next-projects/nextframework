package org.nextframework.web.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.springframework.context.ApplicationContextException;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;

public class ContextLoaderListener extends org.springframework.web.context.ContextLoaderListener {

	public ContextLoaderListener() {
		super();
	}

	public ContextLoaderListener(WebApplicationContext context) {
		super(context);
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
	}

	@Override
	protected Class<?> determineContextClass(ServletContext servletContext) {
		//copied from Spring source code
		String contextClassName = servletContext.getInitParameter(CONTEXT_CLASS_PARAM);
		if (contextClassName != null) {
			try {
				return ClassUtils.forName(contextClassName, ClassUtils.getDefaultClassLoader());
			} catch (ClassNotFoundException ex) {
				throw new ApplicationContextException(
						"Failed to load custom context class [" + contextClassName + "]", ex);
			}
		} else {
			//next specific implementation
			return NextWebApplicationContext.class;
		}
	}

}
