package org.nextframework.js.dom;

import org.stjs.javascript.annotation.STJSBridge;
import org.stjs.javascript.dom.Div;

@STJSBridge
public abstract class Popup extends Div {

	public abstract void close();

}