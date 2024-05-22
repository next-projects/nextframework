package org.nextframework.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DeprecatedLogger {

	private static Log logger = LogFactory.getLog("org.nextframework.deprecated");

	public static void warn(String message) {
		logger.warn(message);
	}

}
