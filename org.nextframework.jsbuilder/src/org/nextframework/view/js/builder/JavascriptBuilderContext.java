package org.nextframework.view.js.builder;

import java.util.ArrayList;
import java.util.List;

import org.nextframework.view.js.JavascriptCode;

public class JavascriptBuilderContext {

	private static long sequence = 0;

	private static ThreadLocal<List<JavascriptBuilderContext>> tlcode = new ThreadLocal<List<JavascriptBuilderContext>>();

	public static void pushNewContext() {
		getStack().add(new JavascriptBuilderContext());
	}

	public static void popContext() {
		getStack().remove(getStack().size() - 1);
	}

	public static JavascriptBuilderContext getContext() {
		List<JavascriptBuilderContext> stack = getStack();
//		if(stack.isEmpty()){
//			stack.add(new JavascriptBuilderContext());
//		}

		return stack.get(stack.size() - 1);
	}

	private static List<JavascriptBuilderContext> getStack() {
		List<JavascriptBuilderContext> stack;
		if ((stack = tlcode.get()) == null) {
			stack = new ArrayList<JavascriptBuilderContext>();
			tlcode.set(stack);
		}
		return stack;
	}

	JavascriptCode code = new JavascriptCode();

	public void append(Object o) {
		code.append(o);
	}

	@Override
	public String toString() {
		return code.toString();
	}

	public synchronized String generateUniqueId(String string) {
		if (sequence == Long.MAX_VALUE) {
			sequence = 0;
		}
		return string + "_" + (sequence++);
	}

}
