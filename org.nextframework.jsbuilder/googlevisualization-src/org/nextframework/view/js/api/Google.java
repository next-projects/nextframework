package org.nextframework.view.js.api;

import org.nextframework.view.js.JavascriptMap;
import org.nextframework.view.js.JavascriptReferenciable;

public interface Google extends Core {

	GoogleReference google = new GoogleReference();

	public class GoogleReference extends Ref {

		public GoogleReference() {
			super("google");
		}

		public void load(String module, String string, JavascriptMap map) {
			call("load", module, string, map);
		}

		public void setOnLoadCallback(JavascriptReferenciable ref) {
			call("setOnLoadCallback", ref);
		}

	}

}
