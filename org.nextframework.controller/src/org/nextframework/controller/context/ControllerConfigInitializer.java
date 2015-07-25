package org.nextframework.controller.context;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;
import javax.servlet.annotation.HandlesTypes;

import org.nextframework.controller.DispatcherServlet;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.web.servlet.mvc.Controller;

@HandlesTypes(Controller.class)
public class ControllerConfigInitializer implements ServletContainerInitializer {
	
	public static final String MODULES_ATTRIBUTE = ControllerConfigInitializer.class.getName()+".modulesFound";

	@SuppressWarnings("unchecked")
	@Override
	public void onStartup(Set<Class<?>> controllerClasses, ServletContext servletContext) throws ServletException {
		List<Class<? extends Controller>> controllerList = new LinkedList<Class<? extends Controller>>();

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
		
		Map<String, List<Class<? extends Controller>>> moduleControllers = new HashMap<String, List<Class<? extends Controller>>>();
		
		for (Class<? extends Controller> controllerClass : controllerList) {
			org.nextframework.controller.Controller annotation = controllerClass.getAnnotation(org.nextframework.controller.Controller.class);
			if(annotation == null && !Modifier.isAbstract(controllerClass.getModifiers())){
				//servletContext.log("WARN: Controller "+controllerClass+" is not annotated with @Controller");
			} else {
				String[] paths = annotation.path();
				for (String path : paths) {
					if(!path.startsWith("/")){
						servletContext.log("WARN: Controller "+controllerClass+" has a wrong path "+path+". Paths must be started with '/'.");
					} else {
						addController(moduleControllers, getModule(path), controllerClass);
					}
				}
			}
		}
		Properties authenticationProperties = new Properties();
		try {
			authenticationProperties = PropertiesLoaderUtils.loadAllProperties("authentication.properties");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int i = 0;
		Set<String> modules = new TreeSet<String>(moduleControllers.keySet());
		for (String module : modules) {
			Dynamic servlet = servletContext.addServlet(module, DispatcherServlet.class);
			if(servlet != null){
				servlet.addMapping("/"+module+"/*");
				servlet.setLoadOnStartup(i++);
				String authentication = (String) authenticationProperties.get(module);
				if(authentication == null){
					authentication = (String) authenticationProperties.get("/"+module);
				}
				if("true".equalsIgnoreCase(authentication)){
					servlet.setInitParameter("secured", "true");
				}
			}
		}
		
		servletContext.setAttribute(MODULES_ATTRIBUTE, modules);
	}

	private void addController(Map<String, List<Class<? extends Controller>>> moduleControllers, String module, Class<? extends Controller> controllerClass) {
		List<Class<? extends Controller>> list = moduleControllers.get(module);
		if(list == null){
			list = new ArrayList<Class<? extends Controller>>();
			moduleControllers.put(module, list);
		}
		list.add(controllerClass);
	}

	private String getModule(String path) {
		return path.substring(1, path.indexOf('/', 1));
	}

}
