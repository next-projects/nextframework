package org.nextframework.controller;

import org.springframework.web.bind.ServletRequestDataBinder;

/**
 * Listener for command events.
 * Request parameters are bind to commands in the action methods of controllers.
 * 
 * Register services using next services to add a listener to the application.
 * 
 * @author rogelgarcia
 *
 */
public interface CommandEventListener {

	/**
	 * Called after instantiation of the command
	 * @param controller
	 * @param command
	 * @param name
	 * @param session
	 */
	void onInstantiateNewCommand(MultiActionController controller, Object command, String name, boolean session);

	/**
	 * Called after the creation of a binder for the command. At this stage the command has not been binded yet.
	 * @param controller
	 * @param command
	 * @param binder
	 */
	void onCreateBinderForCommand(MultiActionController controller, Object command, ServletRequestDataBinder binder);

	/**
	 * Called after the binding of a command.
	 * @param controller
	 * @param command
	 * @param binder
	 */
	void onCommandBind(MultiActionController controller, Object command, ServletRequestDataBinder binder);

	/**
	 * Called after the validation of a command.
	 * @param controller
	 * @param command
	 * @param binder
	 */
	void onCommandValidation(MultiActionController controller, Object command, ServletRequestDataBinder binder);

}
