package org.nextframework.controller;

import javax.servlet.ServletRequest;

import org.springframework.web.bind.ServletRequestDataBinder;

/**
 * Implemente uma classe BinderConfigurer para configurar os binders dos requests. 
 * Como adicionar novos PropertyEditors
 * @author rogel
 *
 */
public interface BinderConfigurer {

	/**
	 * Configura determinado Binder
	 * @param request
	 * @param command
	 * @param binder
	 */
	void configureBinder(ServletRequestDataBinder binder, ServletRequest request, Object command);

}
