package org.nextframework.controller.context;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.nextframework.controller.NextDispatcherServlet;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.servlet.mvc.Controller;

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

	@SuppressWarnings("unchecked")
	private Set<String> getModules(Set<Class<?>> controllerClasses, ServletContext servletContext) {

		List<Class<? extends Controller>> controllerList = new LinkedList<>();

		if (controllerClasses != null) {
			for (Class<?> controllerClass : controllerClasses) {
				// Be defensive: Some servlet containers provide us with invalid classes,
				// no matter what @HandlesTypes says... (copied from spring)
				if (!controllerClass.isInterface() && !Modifier.isAbstract(controllerClass.getModifiers()) &&
						Controller.class.isAssignableFrom(controllerClass)) {
					controllerList.add((Class<? extends Controller>) controllerClass);
				}
			}
		}

		Set<String> modules = new TreeSet<>();

		for (Class<? extends Controller> controllerClass : controllerList) {
			org.nextframework.controller.Controller annotation = controllerClass.getAnnotation(org.nextframework.controller.Controller.class);
			if (annotation == null) {
				//if (!Modifier.isAbstract(controllerClass.getModifiers())) {
				//servletContext.log("WARN: Controller "+controllerClass+" is not annotated with @Controller");
				//}
			} else {
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
