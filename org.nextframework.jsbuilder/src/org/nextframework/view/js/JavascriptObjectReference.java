package org.nextframework.view.js;

import java.util.ArrayList;
import java.util.List;

public class JavascriptObjectReference implements JavascriptReferenciable {

	protected String variable;
	protected JavascriptInstance instance;

	public JavascriptObjectReference(String variable) {
		this(variable, null);
	}

	public JavascriptObjectReference(String variable, String className, Object... parameters) {
		this.variable = variable;
		this.instance = new JavascriptObject(className, parameters);
	}

	public JavascriptObjectReference(String variable, JavascriptInstance instance) {
		this.variable = variable;
		this.instance = instance;
	}

	@Override
	public String toString() {
		return "var " + variable + " = " + instance + ";";
	}

	public String call(String function, Object... parameters) {
		return call(function, true, parameters);
	}

	public String call(String function, boolean endSemicolon, Object... parameters) {
		List<Object> params = new ArrayList<Object>();
		for (Object object : parameters) {
			Object javascriptValue = JSBuilderUtils.convertToJavascriptValue(object);
			if (javascriptValue.toString().length() == 0) {
				javascriptValue = "null";
			}
			params.add(javascriptValue);
		}
		return this.variable + "." + function + "(" + JSBuilderUtils.join(params, ", ") + ")" + (endSemicolon ? ";" : "");
	}

	public String getReference() {
		return variable;
	}

}
