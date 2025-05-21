package org.nextframework.view.js;

public class JavascriptFunctionReference implements JavascriptReferenciable {

	private String name;
	private JavascriptCode code;

	public JavascriptFunctionReference(String name, JavascriptCode code) {
		this.name = name;
		this.code = code;
		this.code.setIdentation(1);
	}

	public void append(Object o) {
		code.append(o);
	}

	@Override
	public String toString() {
		return "function " + name + "(){\n" + code + "\n}";
	}

	public String getReference() {
		return name;
	}

}
