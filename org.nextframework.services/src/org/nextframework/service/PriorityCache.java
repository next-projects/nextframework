package org.nextframework.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceConfigurationError;

/*
 * Used by JavaLoaderServiceProvider
 */
class PriorityCache {

	private static final String PRIORITY_FILE = "META-INF/services/org.nextframework.service.priority";

	private static PriorityCache instance = new PriorityCache();

	private PriorityCache() {
	}

	Map<String, Integer> priorityCache;

	public static int getPriority(Object o) {
		try {
			return instance.readPriority(o);
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}

	private int readPriority(Object o) throws IOException {
		if (priorityCache == null) {
			HashMap<String, Integer> names = new HashMap<String, Integer>();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Enumeration<URL> configs;
			if (loader == null)
				configs = ClassLoader.getSystemResources(PRIORITY_FILE);
			else
				configs = loader.getResources(PRIORITY_FILE);

			while (configs.hasMoreElements()) {
				read(configs.nextElement(), names);
			}
			priorityCache = names;
		}
		Integer result = priorityCache.get(o.getClass().getName());
		if (result == null) {
			return 1;
		}
		return result;
	}

	private void read(URL u, HashMap<String, Integer> names) {
		InputStream in = null;
		BufferedReader r = null;
		try {
			in = u.openStream();
			r = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
			int lc = 1;
			while ((lc = parseLine(u, r, lc, names)) >= 0);
		} catch (IOException x) {
			fail("Error reading configuration file", x);
		} finally {
			try {
				if (r != null)
					r.close();
				if (in != null)
					in.close();
			} catch (IOException y) {
				fail("Error closing configuration file", y);
			}
		}
	}

	private int parseLine(URL u, BufferedReader r, int lc, Map<String, Integer> names) throws IOException {
		//copied and adapted from ServiceLoader class
		String ln = r.readLine();
		if (ln == null) {
			return -1;
		}
		int ci = ln.indexOf('#');
		if (ci >= 0)
			ln = ln.substring(0, ci);
		ln = ln.trim();
		int n = ln.length();
		if (n != 0) {
			if ((ln.indexOf(' ') >= 0) || (ln.indexOf('\t') >= 0))
				fail(u, lc, "Illegal configuration-file syntax");
			int cp = ln.codePointAt(0);
			if (!Character.isJavaIdentifierStart(cp))
				fail(u, lc, "Illegal provider-class name: " + ln);
			int i;
			for (i = Character.charCount(cp); i < n; i += Character.charCount(cp)) {
				cp = ln.codePointAt(i);
				if (cp == '=') {
					break;
				}
				if (!Character.isJavaIdentifierPart(cp) && (cp != '.'))
					fail(u, lc, "Illegal provider-class name: " + ln);
			}
			int sep = i;
			i++;
			for (; i < ln.length(); i += 1) {
				char dig = ln.charAt(i);
				if (!Character.isDigit(dig))
					fail(u, lc, "Illegal priority number: " + ln);
			}
			String className = ln.substring(0, sep);
			int priority = Integer.parseInt(ln.substring(sep + 1));
			names.put(className, priority);
		}
		return lc + 1;
	}

	private static void fail(String msg, IOException e) throws ServiceConfigurationError {
		throw new ServiceConfigurationError(msg, e);
	}

	private static void fail(URL u, int line, String msg) throws ServiceConfigurationError {
		throw new ServiceConfigurationError(u + ":" + line + ": " + msg);
	}

}
