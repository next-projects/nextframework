package org.nextframework.view.js.api;

import org.nextframework.view.js.JavascriptObjectReference;


public interface Html extends Core {

	DocumentReference document = new DocumentReference();
	
	public class DocumentReference extends Ref {
		
		public DocumentReference() {
			super("document");
		}
		
		public JavascriptObjectReference getElementById(String id){
			return new Ref(call("getElementById", false, new Object[]{id}));
		}
	}
}
