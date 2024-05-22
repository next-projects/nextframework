package org.nextframework.classmanager;

import java.util.HashMap;
import java.util.Map;

import org.nextframework.context.ApplicationScanPathsProvider;
import org.nextframework.service.ServiceFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;

public class ClassManagerFactory {

	public static Map<ResourceLoader, ClassManager> instance = new HashMap<ResourceLoader, ClassManager>();

	public static ClassManager getClassManager() {
//		ClassManager classManager = instance.get(context);
//		if(classManager == null){
//			throw new NextException("Nenhum class manager registrado para o contexto "+context);
//		}
//		return classManager;
		ResourceLoader resourceLoader = ServiceFactory.getService(ResourceLoader.class);
		ApplicationScanPathsProvider applicationScanPathsProvider = ServiceFactory.getService(ApplicationScanPathsProvider.class);
		ClassManager classManager = instance.get(resourceLoader);
		if (classManager == null) {
			classManager = new ClassPathScannerClassManager((ResourcePatternResolver) resourceLoader, applicationScanPathsProvider);
			instance.put(resourceLoader, classManager);
		}
		return classManager;
	}

//	public static void setClassManager(Object context, ClassManager cm){
//		instance.put(context, cm);
//	}

}
