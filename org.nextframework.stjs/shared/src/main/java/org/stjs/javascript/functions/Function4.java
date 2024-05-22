package org.stjs.javascript.functions;

import org.stjs.javascript.annotation.JavascriptFunction;

@JavascriptFunction
public interface Function4<P1, P2, P3, P4, R> {

	public R $invoke(P1 p1, P2 p2, P3 p3, P4 p4);

}
