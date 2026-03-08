package org.nextframework.controller.context;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.nextframework.controller.NextDispatcherServlet;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.servlet.mvc.Controller;

import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration.Dynamic;
import jakarta.servlet.annotation.HandlesTypes;

@HandlesTypes(Controller.class)
public class ControllerConfigInitializer implements ServletContainerInitializer {

	public static final String MODULES_ATTRIBUTE = ControllerConfigInitializer.class.getName() + ".modulesFound";

	@Override
	public void onStartup(Set<Class<?>> controllerClasses, ServletContext servletContext) throws ServletException {

		Set<String> modules = getModules(controllerClasses, servletContext);
		Properties authenticationProperties = getAuthenticationProperties();

		int i = 0;
		for (String module : modules) {
			Dynamic servlet = servletContext.addServlet(module, NextDispatcherServlet.class);
			if (servlet != null) {
				servlet.addMapping("/" + module + "/*");
				servlet.setLoadOnStartup(i++);
				servlet.setMultipartConfig(new MultipartConfigElement(""));
				String authentication = (String) authenticationProperties.get(module);
				if (authentication == null) {
					authentication = (String) authenticationProperties.get("/" + module);
				}
				if ("true".equalsIgnoreCase(authentication)) {
					servlet.setInitParameter("secured", "true");
				}
			}
		}

		servletContext.setAttribute(MODULES_ATTRIBUTE, modules);

	}

	private Set<String> getModules(Set<Class<?>> controllerClasses, ServletContext servletContext) {

		Set<String> modules = new TreeSet<>();

		if (controllerClasses != null) {
			for (Class<?> controllerClass : controllerClasses) {
				if (!controllerClass.isInterface() && !Modifier.isAbstract(controllerClass.getModifiers()) && Controller.class.isAssignableFrom(controllerClass)) {
					org.nextframework.controller.Controller annotation = controllerClass.getAnnotation(org.nextframework.controller.Controller.class);
					if (annotation != null) {
						String[] paths = annotation.path();
						if (paths != null) {
							for (String path : paths) {
								if (!path.startsWith("/")) {
									servletContext.log("WARN: Controller " + controllerClass + " has a wrong path " + path + ". Paths must be started with '/'.");
								} else {
									String module = path.substring(1, path.indexOf('/', 1));
									modules.add(module);
								}
							}
						}
					}
				}
			}
		}

		return modules;
	}

	private Properties getAuthenticationProperties() {
		Properties authenticationProperties = new Properties();
		try {
			authenticationProperties = PropertiesLoaderUtils.loadAllProperties("authentication.properties");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return authenticationProperties;
	}

}
