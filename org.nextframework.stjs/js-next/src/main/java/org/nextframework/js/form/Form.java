package org.nextframework.js.form;

import org.stjs.javascript.annotation.STJSBridge;

@STJSBridge
public abstract class Form {

	/**
	 * Returns a new Form object with only the properties that begins with 'prefix' 
	 * @param prefix
	 * @return
	 */
	public abstract Form subForm(String prefix);

	public abstract Form setProperty(String name, Object value);

	public abstract void removeSystemFields();

	public abstract String toQueryString();

}
