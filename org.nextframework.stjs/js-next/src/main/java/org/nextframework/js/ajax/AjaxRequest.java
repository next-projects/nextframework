package org.nextframework.js.ajax;

import org.stjs.javascript.annotation.STJSBridge;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.functions.Callback1;
import org.stjs.javascript.functions.Callback2;

@STJSBridge
/**
 * To create an instance of this class use:
 * 
 * NextGlobalJs.next.ajax.newRequest();
 * 
 * OR
 * 
 * NextGlobalJs.next.ajax.newFormRequest();
 * 
 * If you use newFormRequest (any of the overloaded methods), the request will be baked by this form.
 * It means that the URL will be configured from the form tag, and the elements will be the ones from the inputs of the form.
 * 
 *  The system fields (like ACTION) will not be passed.
 * 
 * @author rogelgarcia
 *
 */
public abstract class AjaxRequest {

	public abstract void send();

	public abstract void send(String data);

	/**
	 * Sets the action in the controller the request should be directed to.<BR>
	 * Parameter ACTION.
	 * @param action
	 * @return
	 */
	public abstract AjaxRequest setAction(String action);

	public abstract AjaxRequest setParameterFromElement(Element el);

	public abstract AjaxRequest setParameterFromElement(Element el, String useName);

	public abstract AjaxRequest setParameter(Element el);

	public abstract AjaxRequest setParameter(String name, Object value);

	public abstract AjaxRequest setUrl(String url);

	public abstract AjaxRequest setAppendContext(boolean b);

	public abstract AjaxRequest setOnComplete(Callback1<String> callback);

	public abstract AjaxRequest setOnError(Callback2<String, String> callback);

	public abstract AjaxRequest setAfterError(Callback2<String, String> callback);

	public abstract AjaxRequest setCallback(Callback1<?> callback);

	public abstract AjaxRequest setCallback(Object o, String methodName);

	/**
	 * If this request is baked by a form, only send properties with name starting with 'prefix'.
	 * @param prefix
	 * @return
	 */
	public abstract AjaxRequest setFormPropertyPrefix(String... prefix);

}
