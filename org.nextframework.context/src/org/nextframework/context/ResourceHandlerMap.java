package org.nextframework.context;

import java.util.Set;

public interface ResourceHandlerMap {

	Object getHandler(String resource);

	Set<String> getHandlerNames();

	boolean isAuthenticationRequired(String resource);

}
