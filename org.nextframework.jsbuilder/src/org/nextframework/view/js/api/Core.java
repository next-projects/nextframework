package org.nextframework.view.js.api;

import org.nextframework.view.js.JavascriptInstance;
import org.nextframework.view.js.builder.JavascriptBuilder;
import org.nextframework.view.js.builder.JavascriptBuilderArray;
import org.nextframework.view.js.builder.JavascriptBuilderFunction;
import org.nextframework.view.js.builder.JavascriptBuilderMap;
import org.nextframework.view.js.builder.JavascriptBuilderObjectReference;

public interface Core {

	interface Var {

		String call(String function, Object... parameters);

	}

	class Obj extends JavascriptBuilderObjectReference implements Var {

		public Obj() {
			super();
		}

		public Obj(JavascriptInstance instance) {
			super(instance);
		}

		public Obj(String variable, JavascriptInstance instance) {
			super(variable, instance);
		}

		public Obj(String className, Object... parameters) {
			super(className, parameters);
		}

		public Obj(String variable, String className, Object... parameters) {
			super(variable, className, parameters);
		}

		public Obj(String variable) {
			super(variable);
		}

	}

	class Ref extends JavascriptBuilderObjectReference implements Var {

		public Ref(String refName) {
			super();
			variable = refName;
		}

	}

	class Array extends JavascriptBuilderArray {

		public Array(Object... objects) {
			super(objects);
		}

		public String call(String function, Object... parameters) {
			throw new RuntimeException("Should not call this method in array");
		}

	}

	class Map extends JavascriptBuilderMap {

		public Map() {
			super();
		}

		public Map(java.util.Map<String, Object> map) {
			super(map);
		}

		public Map(Object... props) {
			super(props);
		}

		public String call(String function, Object... parameters) {
			throw new RuntimeException("Should not call this method in map");
		}

	}

	class Function extends JavascriptBuilderFunction {

		public Function(JavascriptBuilder code) {
			super(code);
		}

		public Function(String name, JavascriptBuilder code) {
			super(name, code);
		}

	}

}
