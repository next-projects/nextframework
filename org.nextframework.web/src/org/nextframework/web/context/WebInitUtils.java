package org.nextframework.web.context;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import jakarta.servlet.ServletContext;

class WebInitUtils {

	private static final String BASE_PATH = "/WEB-INF/classes/";
	private static final Set<String> IGNORED_PACKAGES = new HashSet<String>();

	static {
		IGNORED_PACKAGES.add("org.nextframework");
		IGNORED_PACKAGES.add("org.stjs");
		IGNORED_PACKAGES.add("org.eclipse");
		IGNORED_PACKAGES.add("org.springframework.orm.hibernate4");
		IGNORED_PACKAGES.add("org.hibernate.community.dialect");
		IGNORED_PACKAGES.add("google.maps");
		IGNORED_PACKAGES.add("i18n");
	}

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
				if (isIgnoredPackage(formatedPackage)) {
					continue;
				}
				//(avoid adding org, com or net packages)
				if (formatedPackage.equals("org") || formatedPackage.equals("com") || formatedPackage.equals("net")) {
					searchContexts(servletContext, path, searchContexts);
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

	private static boolean isIgnoredPackage(String packageName) {
		if (IGNORED_PACKAGES.contains(packageName)) {
			return true;
		}
		return packageName.endsWith(".test") || packageName.contains(".test.");
	}

}
