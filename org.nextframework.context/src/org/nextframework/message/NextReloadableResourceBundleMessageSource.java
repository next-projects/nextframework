package org.nextframework.message;

import java.io.IOException;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.nextframework.util.Util;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class NextReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource implements ResourceLoaderAware {

	private long cacheMillis2 = -1;

	//https://www.tabnine.com/web/assistant/code/rs/5c66e35d1095a50001e73891#L21
	private static final String PROPERTIES_SUFFIX = ".properties";
	private final ConcurrentMap<String, PropertiesHolder> cachedClasspathProperties = new ConcurrentHashMap<String, PropertiesHolder>();
	private PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

	private String[] optionalPrefixes = new String[0];

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		if (resourceLoader != null) {
			resolver = new PathMatchingResourcePatternResolver(resourceLoader);
		}
	}

	@Override
	protected PropertiesHolder refreshProperties(String filename, PropertiesHolder propHolder) {
		if (isPattern(filename)) {
			PropertiesHolder existingHolder = this.cachedClasspathProperties.get(filename);
			if (existingHolder != null && existingHolder.getRefreshTimestamp() > (System.currentTimeMillis() - cacheMillis2)) {
				return existingHolder;
			}
			return refreshClassPathProperties(filename, propHolder);
		} else {
			return super.refreshProperties(filename, propHolder);
		}
	}

	private boolean isPattern(String filename) {
		return filename.startsWith(PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX) ||
				resolver.getPathMatcher().isPattern(filename);
	}

	private PropertiesHolder refreshClassPathProperties(String filename, PropertiesHolder propHolder) {
		Properties properties = new Properties();
		long lastModified = -1;
		try {
			Resource[] resources = resolver.getResources(filename + PROPERTIES_SUFFIX);
			for (Resource resource : resources) {
				String sourcePath = resource.getURI().toString().replace(PROPERTIES_SUFFIX, "");
				PropertiesHolder holder = super.refreshProperties(sourcePath, propHolder);
				properties.putAll(holder.getProperties());
				if (lastModified < resource.lastModified()) {
					lastModified = resource.lastModified();
				}
			}
		} catch (IOException ignored) {
		}
		PropertiesHolder holder = new PropertiesHolder(properties, lastModified);
		holder.setRefreshTimestamp(cacheMillis2 < 0 ? -1 : System.currentTimeMillis());
		cachedClasspathProperties.put(filename, holder);
		return holder;
	}

	@Override
	protected Properties loadProperties(Resource resource, String filename) throws IOException {

		Properties properties = super.loadProperties(resource, filename);

		if (!properties.isEmpty() && optionalPrefixes != null && optionalPrefixes.length > 0) {

			Properties newProperties = new Properties();
			for (Entry<Object, Object> entry : properties.entrySet()) {
				String key = (String) entry.getKey();
				String prefix = getKeyPrefix(key);
				if (prefix != null) {
					String newKey = key.substring(prefix.length());
					if (!properties.containsKey(newKey)) {
						newProperties.put(newKey, entry.getValue());
					}
				}
			}

			if (!newProperties.isEmpty()) {
				properties.putAll(newProperties);
			}

		}

		return properties;
	}

	private String getKeyPrefix(String key) {
		for (String prefix : optionalPrefixes) {
			if (key.startsWith(prefix)) {
				return prefix;
			}
		}
		return null;
	}

	@Override
	public void clearCache() {
		super.clearCache();
		this.cachedClasspathProperties.clear();
	}

	@Override
	protected Object[] resolveArguments(Object[] args, Locale locale) {
		if (args == null || args.length == 0) {
			return args;
		}
		Object[] stringArgs = new Object[args.length];
		for (int i = 0; i < args.length; i++) {
			stringArgs[i] = Util.strings.toStringDescription(args[i], "dd/MM/yyyy HH:mm:ss", null, locale);
		}
		return stringArgs;
	}

	public String[] getOptionalPrefixes() {
		return optionalPrefixes;
	}

	public void setOptionalPrefixes(String[] optionalPrefixes) {
		this.optionalPrefixes = optionalPrefixes;
	}

	public void setCacheSeconds(int cacheSeconds) {
		super.setCacheSeconds(cacheSeconds);
		this.cacheMillis2 = (cacheSeconds * 1000);
	}

}