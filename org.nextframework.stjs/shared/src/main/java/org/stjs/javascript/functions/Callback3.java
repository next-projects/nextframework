package org.stjs.javascript.functions;

import org.stjs.javascript.annotation.JavascriptFunction;

@JavascriptFunction
public interface Callback3<P1, P2, P3> {
	public void $invoke(P1 p1, P2 p2, P3 p3);
}