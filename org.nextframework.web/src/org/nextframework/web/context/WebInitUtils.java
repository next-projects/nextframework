package org.nextframework.web.context;

import java.util.Set;
import java.util.TreeSet;

import javax.servlet.ServletContext;

class WebInitUtils {

	private static final String BASE_PATH = "/WEB-INF/classes/";

	static String[] findScanPaths(ServletContext servletContext) {
		Set<String> searchContexts = new TreeSet<String>();
		searchContexts(servletContext, BASE_PATH, searchContexts);
		return searchContexts.toArray(new String[searchContexts.size()]);
	}

	private static void searchContexts(ServletContext servletContext, String searchPath, Set<String> searchContexts) {
		Set<String> resourcePaths = servletContext.getResourcePaths(searchPath);
		if (resourcePaths == null) {
			return;
		}
		for (String path : resourcePaths) {
			if (path.endsWith("/")) {
				String packagePath = path.substring(BASE_PATH.length());
				if (packagePath.startsWith("META-INF")) {
					continue;
				}
				String formatedPackage = packagePath.substring(0, packagePath.length() - 1).replace('/', '.');
				//(avoid adding org, com or net packages)
				if (formatedPackage.equals("org") || formatedPackage.equals("com") || formatedPackage.equals("net")) {
					searchContexts(servletContext, path, searchContexts);
				} else if (formatedPackage.equals("org.nextframework")) { // ignore org.nextframework
					continue;
				} else {
					Set<String> packageResources = servletContext.getResourcePaths(path);
					boolean hasFiles = false;
					if (packageResources != null) {
						for (String pckResource : packageResources) {
							if (!pckResource.endsWith("/")) {
								hasFiles = true;
							}
						}
					}
					if (hasFiles) {
						searchContexts.add(formatedPackage);
					} else {
						searchContexts(servletContext, path, searchContexts);
					}
				}
			}
		}
	}

}
