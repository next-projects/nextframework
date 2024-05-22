package org.nextframework.view.js;

import java.util.ArrayList;
import java.util.List;

public class JavascriptArray implements JavascriptInstance {

	List<Object> list = new ArrayList<Object>();
	private boolean inline;

	public JavascriptArray(boolean inline, Object... objects) {
		this(objects);
		this.inline = inline;
	}

	public JavascriptArray(Object... objects) {
		for (Object object : objects) {
			add(object);
		}
	}

	public boolean add(Object o) {
		return list.add(JSBuilderUtils.convertToJavascriptValue(o));
	}

	private String toJs() {
		if (list.size() <= 5) {
			inline = true;
		}
		for (Object o : list) {
			if (o instanceof JavascriptArray) {
				inline = false;
				break;
			}
		}
		String result = "[";
		result += JSBuilderUtils.join(list, inline ? ", " : ",\n        ");
		result += "]";
		return result;
	}

	@Override
	public String toString() {
		return toJs();
	}

	public static void main(String[] args) {
		JavascriptArray array = new JavascriptArray();
		array.add("bagaca");
		array.add("poiasdf");
		System.out.println(array);
	}

}
