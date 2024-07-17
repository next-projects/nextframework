package org.nextframework.controller;

import org.springframework.web.bind.ServletRequestDataBinder;

public class RequestLocaleAwareCommandListener extends CommandEventAdapter {

	@Override
	public void onCommandBind(MultiActionController controller, Object command, ServletRequestDataBinder binder) {
		if (command instanceof RequestLocaleAware) {
			((RequestLocaleAware) command).setLocale(controller.getRequest().getLocale());
		}
	}

}