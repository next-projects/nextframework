package org.nextframework.js;

import org.nextframework.js.ajax.AjaxRequest;
import org.stjs.javascript.Map;

public abstract class NextAjax {

	/**
	 * Envia uma requisição AJAX
	 * @param options 
	 *   appendContext <BR>
	 *   url<BR>
	 *   params<BR>
	 *   async<BR>
	 *   charset<BR>
	 *   method<BR>
	 *   callbackParrameters<BR>
	 *   onComplete<BR>
	 *   afterComplete<BR>
	 *   evalResponse<BR>
	 *   evalScripts<BR>
	 *   noCache<BR>
	 *   onError<BR>
	 *   on404<BR>
	 */
	public abstract void send(Map<String, ?> options);

	public abstract AjaxRequest newRequest();

	public abstract AjaxRequest newFormRequest();

	public abstract AjaxRequest newFormRequest(String name);

}
