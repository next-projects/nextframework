package org.nextframework.view;

import org.nextframework.core.web.WebRequestContext;

public interface BaseTagManagerConfigurer {

	void configure(BaseTagManager baseTagManager, WebRequestContext requestContext);

}
