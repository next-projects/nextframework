package org.nextframework.view.js.builder;

import org.nextframework.view.js.JavascriptCode;
import org.nextframework.view.js.JavascriptFunctionReference;

public class JavascriptBuilderFunction extends JavascriptFunctionReference {

	public JavascriptBuilderFunction(JavascriptBuilder code) {
		super(getScript().generateUniqueId("function"), getCode(code));
		getScript().append(this);
	}

	public JavascriptBuilderFunction(String name, JavascriptBuilder code) {
		super(name, getCode(code));
		getScript().append(this);
	}

	
	private static JavascriptCode getCode(JavascriptBuilder builder) {
		JavascriptBuilderContext.pushNewContext();
		String result = builder.toString();
		JavascriptCode code = new JavascriptCode();
		code.setIdentation(1);
		code.append(result);
		JavascriptBuilderContext.popContext();
		return code;
	}
	
	private static JavascriptBuilderContext getScript() {
		return JavascriptBuilderContext.getContext();
	}
}
