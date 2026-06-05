/*
 * Next Framework http://www.nextframework.org
 * Copyright (C) 2012 the original author or authors.
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
 * 
 * Spring Framework 
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nextframework.compilation;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author rogelgarcia | marcusabreu
 */
public class SourceCodeUtils {

	public static String packageDeclaration(Class<?> type, String suffix) {
		return "package " + type.getName().substring(0, type.getName().lastIndexOf(type.getSimpleName())) + suffix + ";\n";
	}

	public static String importDeclaration(Class<?> class1) {
		return "import " + class1.getName() + ";\n";
	}

	/**
	 * Code from spring framework
	 * 
	 * Capitalize a <code>String</code>, changing the first letter to
	 * upper case as per {@link Character#toUpperCase(char)}.
	 * No other letters are changed.
	 * @param str the String to capitalize, may be <code>null</code>
	 * @return the capitalized String, <code>null</code> if null
	 */
	public static String capitalize(String str) {
		return changeFirstCharacterCase(str, true);
	}

	/**
	 * Code from spring framework
	 * 
	 * Uncapitalize a <code>String</code>, changing the first letter to
	 * lower case as per {@link Character#toLowerCase(char)}.
	 * No other letters are changed.
	 * @param str the String to uncapitalize, may be <code>null</code>
	 * @return the uncapitalized String, <code>null</code> if null
	 */
	public static String uncapitalize(String str) {
		return changeFirstCharacterCase(str, false);
	}

	/**
	 * Code from spring framework
	 * 
	 * @param str
	 * @param capitalize
	 * @return
	 */
	private static String changeFirstCharacterCase(String str, boolean capitalize) {
		if (str == null || str.length() == 0) {
			return str;
		}
		StringBuilder sb = new StringBuilder(str.length());
		if (capitalize) {
			sb.append(Character.toUpperCase(str.charAt(0)));
		} else {
			sb.append(Character.toLowerCase(str.charAt(0)));
		}
		sb.append(str.substring(1));
		return sb.toString();
	}

	public static String buildCompilerClassPath(boolean defaultClassPath, String customClassPath, boolean threadClassLoader, ClassLoader classLoader) {
		LinkedHashSet<String> classPathEntries = new LinkedHashSet<String>();
		if (defaultClassPath) {
			addClassPathEntries(classPathEntries, System.getProperty("java.class.path"));
		}
		if (customClassPath != null) {
			addClassPathEntries(classPathEntries, customClassPath);
		}
		if (threadClassLoader) {
			addClassLoaderUrls(classPathEntries, Thread.currentThread().getContextClassLoader());
		}
		if (classLoader != null) {
			addClassLoaderUrls(classPathEntries, classLoader);
		}
		return String.join(File.pathSeparator, classPathEntries);
	}

	private static void addClassPathEntries(LinkedHashSet<String> classPathEntries, String classPath) {
		if (classPath == null || classPath.trim().isEmpty()) {
			return;
		}
		for (String entry : classPath.split(File.pathSeparator)) {
			if (!entry.trim().isEmpty()) {
				classPathEntries.add(entry);
			}
		}
	}

	private static void addClassLoaderUrls(LinkedHashSet<String> classPathEntries, ClassLoader classLoader) {
		Set<ClassLoader> visited = Collections.newSetFromMap(new IdentityHashMap<ClassLoader, Boolean>());
		ClassLoader current = classLoader;
		while (current != null && visited.add(current)) {
			if (current instanceof URLClassLoader) {
				URL[] urls = ((URLClassLoader) current).getURLs();
				for (URL url : urls) {
					String path = toClassPathEntry(url);
					if (path != null) {
						classPathEntries.add(path);
					}
				}
			}
			current = current.getParent();
		}
	}

	private static String toClassPathEntry(URL url) {
		if (url == null || !"file".equalsIgnoreCase(url.getProtocol())) {
			return null;
		}
		try {
			return new File(url.toURI()).getPath();
		} catch (URISyntaxException e) {
			return new File(url.getPath()).getPath();
		}
	}

}
