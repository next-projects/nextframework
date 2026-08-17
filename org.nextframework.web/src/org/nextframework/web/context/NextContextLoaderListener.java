package org.nextframework.web.context;

import org.nextframework.service.ServiceFactory;
import org.nextframework.web.NextScheduleService;
import org.nextframework.web.NextScheduledTaskRegistrar;
import org.springframework.context.ApplicationContextException;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.WebApplicationContext;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;

public class NextContextLoaderListener extends org.springframework.web.context.ContextLoaderListener {

	private NextScheduleService nextScheduleService;

	public NextContextLoaderListener() {
		super();
	}

	public NextContextLoaderListener(WebApplicationContext context) {
		super(context);
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {
		super.contextInitialized(event);
		nextScheduleService = new NextScheduleService();
		NextScheduledTaskRegistrar[] registrars = ServiceFactory.loadServices(NextScheduledTaskRegistrar.class);
		for (NextScheduledTaskRegistrar registrar : registrars) {
			registrar.registerScheduledTasks(nextScheduleService);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if (nextScheduleService != null) {
			nextScheduleService.shutdown();
			nextScheduleService = null;
		}
		super.contextDestroyed(event);
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
