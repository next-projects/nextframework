package org.nextframework.context;

import org.springframework.context.ConfigurableApplicationContext;

public interface SpringApplicationContextProvider {

	ConfigurableApplicationContext getApplicationContext();

}
