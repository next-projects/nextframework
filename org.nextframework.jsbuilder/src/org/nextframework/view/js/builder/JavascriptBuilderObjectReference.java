package org.nextframework.view.js.builder;

import org.nextframework.view.js.JavascriptInstance;
import org.nextframework.view.js.JavascriptObjectReference;

public class JavascriptBuilderObjectReference extends JavascriptObjectReference {

	public JavascriptBuilderObjectReference(JavascriptInstance instance) {
		super(getScript().generateUniqueId("reference"), instance);
	}

	public JavascriptBuilderObjectReference(String className, Object... parameters) {
		super(getScript().generateUniqueId("reference"), className, parameters);
		getScript().append(this);
	}

	public JavascriptBuilderObjectReference() {
		super(getScript().generateUniqueId("reference"));
	}

	public JavascriptBuilderObjectReference(String variable, JavascriptInstance instance) {
		super(variable, instance);
		getScript().append(this);
	}

	public JavascriptBuilderObjectReference(String variable, String className, Object... parameters) {
		super(variable, className, parameters);
		getScript().append(this);
	}

	public JavascriptBuilderObjectReference(String variable) {
		super(variable);
		getScript().append(this);
	}

	public String call(String function, Object... parameters) {
		String call = super.call(function, parameters);
		getScript().append(call);
		return call;
	}

	protected static JavascriptBuilderContext getScript() {
		return JavascriptBuilderContext.getContext();
	}

	public static void main(String[] args) {
		JavascriptBuilderObjectReference ref = new JavascriptBuilderObjectReference("teste", "classe", "objetos");
		ref.call("funcao", "param1", "param2", new JavascriptBuilderMap("mapa", "valor"));

		System.out.println(JavascriptBuilderContext.getContext());
	}

}
