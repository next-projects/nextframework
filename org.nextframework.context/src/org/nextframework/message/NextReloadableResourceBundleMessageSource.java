package org.nextframework.message;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;

public class NextReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {

	private String[] optionalPrefixes = new String[0];

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

	public String[] getOptionalPrefixes() {
		return optionalPrefixes;
	}

	public void setOptionalPrefixes(String[] optionalPrefixes) {
		this.optionalPrefixes = optionalPrefixes;
	}

}