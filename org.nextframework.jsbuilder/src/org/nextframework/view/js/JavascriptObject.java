package org.nextframework.view.js;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class JavascriptObject implements JavascriptInstance {

	private String className;
	List<Object> parameters = new ArrayList<Object>();

	public JavascriptObject(String className, Object... parameters) {
		this.className = className;
		for (Object object : parameters) {
			this.parameters.add(JSBuilderUtils.convertToJavascriptValue(object));
		}
	}

	@Override
	public String toString() {
		return "new " + className + "(" + JSBuilderUtils.join(this.parameters, ", ") + ")";
	}

	public static void main(String[] args) {
		System.out.println(new JavascriptObject("google.visualization.AreaChart", new Date(), "bagaca"));
	}

}
