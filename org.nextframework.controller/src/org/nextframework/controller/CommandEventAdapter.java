package org.nextframework.controller;

import org.springframework.web.bind.ServletRequestDataBinder;

/**
 * Helper to implement the CommandEventListener interface.
 * 
 * Note that when registering the class as a service, it must be registered with CommandEventListener interface (not CommandEventAdapter).
 * 
 * @author rogelgarcia
 *
 */
public abstract class CommandEventAdapter implements CommandEventListener {

	@Override
	public void onInstantiateNewCommand(MultiActionController controller, Object command, String name, boolean session) {
	}

	@Override
	public void onCreateBinderForCommand(MultiActionController controller, Object command, ServletRequestDataBinder binder) {
	}

	@Override
	public void onCommandBind(MultiActionController controller, Object command, ServletRequestDataBinder binder) {
	}

	@Override
	public void onCommandValidation(MultiActionController controller, Object command, ServletRequestDataBinder binder) {
	}

}