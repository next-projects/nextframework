package org.nextframework.controller;

public class RequestLocaleAwareCommandListener extends CommandEventAdapter {

	@Override
	public void onInstantiateNewCommand(MultiActionController controller, Object command, String name, boolean session) {
		if (command instanceof RequestLocaleAware) {
			((RequestLocaleAware) command).setLocale(controller.getRequest().getLocale());
		}
	}

}